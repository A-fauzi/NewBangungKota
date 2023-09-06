package com.afauzi.bangungkota.presentation.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ComponentBottomSheetMorePostBinding
import com.afauzi.bangungkota.databinding.ComponentListCommunityPostBinding
import com.afauzi.bangungkota.databinding.FragmentCommunityBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.adapter.AdapterPagingPost
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UniqueIdGenerator.generateUniqueId
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapterPagingPost: AdapterPagingPost

    private val postViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)

        adapterPagingPost = AdapterPagingPost{componentListCommunityPostBinding, post ->
            bindDataPostToViews(componentListCommunityPostBinding, post)
        }

        return binding.root
    }

    private fun bindDataPostToViews(
        componentListCommunityPostBinding: ComponentListCommunityPostBinding,
        post: Post
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        db.collection("users").document(post.uid.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {

                    if (user?.uid != post.uid) componentListCommunityPostBinding.btnMorePost.visibility = View.GONE

                    componentListCommunityPostBinding.tvTextPost.text = post.text
                    componentListCommunityPostBinding.itemNameUser.text = it.getString("name")
                    componentListCommunityPostBinding.itemEmailUser.text = it.getString("email")
                    Glide.with(requireActivity())
                        .load(it.getString("photo"))
                        .error(R.drawable.example_profile)
                        .into(componentListCommunityPostBinding.itemIvProfile)

                    // GET VALUE AND SET TO ITEM POSTING COMMUNITY
                    componentListCommunityPostBinding.btnMorePost.setOnClickListener {
                        // Handle button more post
                        // BottomSheet
                        val dialog = BottomSheetDialog(requireActivity())
                        val sheetBinding = ComponentBottomSheetMorePostBinding.inflate(layoutInflater)
                        val view = sheetBinding.root
                        dialog.setContentView(view)

                        val progressBar = sheetBinding.progressbar
                        val layoutItem = sheetBinding.layoutItem
                        sheetBinding.btnDelete.setOnClickListener {
//                            deleteEventPost(progressBar, layoutItem, dialog, post)
                        }
                        dialog.show()
                    }
                } else {
                    // tidak ada data uid di list
                }
            }
            .addOnFailureListener {
                toast(requireActivity(), it.message)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()
        onClickViews()
        setUpRecyclerViewAdapter()
    }

    private fun setUpRecyclerViewAdapter() {
        binding.rvCommunityPost.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterPagingPost
        }

        lifecycleScope.launch {
            postViewModel.getEvents.collectLatest { pagingData ->
                adapterPagingPost.submitData(pagingData)
            }
        }
    }

    private fun setTopAppBar() {
        binding.appBarLayout.topAppBar.title = "Community"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.user).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_angle_left, null)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(), "Menu Clicked", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onClickViews() {
        // WHEN BUTTON END ICON POSTING MESSAGE ONCLICK
        binding.outlineTextfieldProductSpec.setEndIconOnClickListener { insertDataPost() }

        // button top scroll
        binding.fabUpScroll.setOnClickListener { binding.nestedScrollView.smoothScrollTo(0, 0) }
    }

    private fun insertDataPost() {

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // TEXTFIELD FALSE
        binding.outlineTextfieldProductSpec.isEnabled = false

        // GET TEXT IN EDITTEXT POST
        val textPost = binding.etPostText.text.toString()

        // SET DATA POSTING
        val data = Post(
            id = generateUniqueId(),
            uid = user?.uid,
            text = textPost,
            created_at = Timestamp.now()
        )

        db.collection("posts")
            .document(data.id)
            .set(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    adapterPagingList.refresh()
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    binding.etPostText.text?.clear()
                } else {
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    toast(requireActivity(), "Gagal posting")
                }
            }
            .addOnFailureListener {
                binding.outlineTextfieldProductSpec.isEnabled = true
                toast(requireActivity(), "Error Posting ${it.message}")
            }
    }
}