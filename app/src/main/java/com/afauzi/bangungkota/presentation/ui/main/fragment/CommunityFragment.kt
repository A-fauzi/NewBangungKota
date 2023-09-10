package com.afauzi.bangungkota.presentation.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.afauzi.bangungkota.presentation.ui.detail.InfoDetailPostActivity
import com.afauzi.bangungkota.presentation.ui.camera.CameraStoryActivity
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.afauzi.bangungkota.utils.CustomViews
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UniqueIdGenerator.generateUniqueId
import com.afauzi.bangungkota.utils.UtilityLibrary.formatedDateToTimeAgo
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapterPagingPost: AdapterPagingPost
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private val postViewModel: PostViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

    }

    override fun onStart() {
        super.onStart()

        adapterPagingPost = AdapterPagingPost { componentListCommunityPostBinding, post ->
            bindDataPostToViews(componentListCommunityPostBinding, post)
        }

        onClickViews()
        setUpRecyclerViewAdapter()
    }

    private fun bindDataPostToViews(
        componentListCommunityPostBinding: ComponentListCommunityPostBinding,
        post: Post
    ) {

        setDataUserPostListToViews(post, componentListCommunityPostBinding)
        setDataPostListToViews(post, componentListCommunityPostBinding)
    }

    private fun setDataUserPostListToViews(
        post: Post,
        componentListCommunityPostBinding: ComponentListCommunityPostBinding
    ) {
        lifecycleScope.launch {
            userViewModel.getUserById(post.uid.toString())
                .addOnSuccessListener {
                    if (it.exists()) {

                        Glide.with(requireActivity())
                            .load(it.getString("photo"))
                            .error(R.drawable.example_profile)
                            .into(componentListCommunityPostBinding.itemIvProfile)

                        componentListCommunityPostBinding.itemNameUser.text = it.getString("name")

                    } else {
                        // tidak ada data uid di list
                    }
                }
                .addOnFailureListener {
                    toast(requireActivity(), it.message)
                }
        }
    }

    private fun setDataPostListToViews(
        post: Post,
        componentListCommunityPostBinding: ComponentListCommunityPostBinding
    ) {

        if (user?.uid != post.uid) componentListCommunityPostBinding.btnMorePost.visibility = View.GONE


        componentListCommunityPostBinding.tvTextPost.text = post.text
        componentListCommunityPostBinding.itemDatePost.text = formatedDateToTimeAgo(post.created_at)


        var isLoved = false
        componentListCommunityPostBinding.btnLove.setOnClickListener {
            val postId = post.id
            val currentUserId = user?.uid.toString()

            if (isLoved) {
                componentListCommunityPostBinding.btnLove.setImageResource(R.drawable.heart_outline)
                toast(requireActivity(), "Batal Menyukai")

                // Handle deleted data like in document
            } else {
                componentListCommunityPostBinding.btnLove.setImageResource(R.drawable.heart_filled)
                toast(requireActivity(), "Kamu Menyukai ini")
            }

            isLoved = !isLoved
        }

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
                progressBar.visibility = View.VISIBLE
                layoutItem.visibility = View.GONE

                // Delete post
                postViewModel.deletePost(post.id)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            toast(requireActivity(), "Post Deleted 🙌")
                            adapterPagingPost.refresh()
                            dialog.dismiss()
                        } else {
                            progressBar.visibility = View.GONE
                            layoutItem.visibility = View.VISIBLE
                            toast(requireActivity(), "Hufftt gagal Delete post!")
                        }
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        layoutItem.visibility = View.VISIBLE
                        toast(requireActivity(), "Ada masalah server 😥")
                    }
            }
            dialog.show()
        }

        componentListCommunityPostBinding.btnComment.setOnClickListener {
            val intent = Intent(requireActivity(), InfoDetailPostActivity::class.java)
            intent.putExtra("post_data", post) // Mengirim objek Post sebagai extra
            startActivity(intent)
        }
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

        lifecycleScope.launch {
            adapterPagingPost.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                val isLoadingAppend = loadStates.append is LoadState.Loading

                if (isLoading) {

                    binding.containerContent.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE

                } else {

                    binding.containerContent.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE

                    // CEK DATA AVAIL IN LIST
                    adapterPagingPost.loadStateFlow.distinctUntilChangedBy {
                        it.refresh
                    }.collect {
                        //you get all the data here
                        val list = adapterPagingPost.snapshot()
                        if (list.isEmpty()) {
                            binding.tvDataEmpty.visibility = View.VISIBLE
                            binding.rvCommunityPost.visibility = View.GONE
                        } else {
                            binding.tvDataEmpty.visibility = View.GONE
                            binding.rvCommunityPost.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setTopAppBar() {
        binding.appBarLayout.topAppBar.title = "Community"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.user).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon = null
    }


    private fun onClickViews() {
        // WHEN BUTTON END ICON POSTING MESSAGE ONCLICK
        binding.outlineTextfieldProductSpec.setEndIconOnClickListener {
            val circularProgressDrawable = CustomViews.circularDrawableToLoadInput(requireActivity())
            binding.outlineTextfieldProductSpec.endIconDrawable = circularProgressDrawable
            circularProgressDrawable.start()

            insertDataPost()
        }

        // button top scroll
        binding.fabUpScroll.setOnClickListener { binding.nestedScrollView.smoothScrollTo(0, 0) }

        binding.cvCameraStory.setOnClickListener {
            startActivity(Intent(requireActivity(), CameraStoryActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun insertDataPost() {

        binding.outlineTextfieldProductSpec.isEnabled = false
        val textPost = binding.etPostText.text.toString()
        val data = Post(
            id = generateUniqueId(),
            uid = user?.uid,
            text = textPost,
        )

        postViewModel.createPost(data, data.id)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    adapterPagingPost.refresh()
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    binding.etPostText.text?.clear()

                    binding.outlineTextfieldProductSpec.endIconDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_paper_plane_top)
                } else {
                    binding.outlineTextfieldProductSpec.isEnabled = true
                    toast(requireActivity(), "Gagal posting")
                    binding.outlineTextfieldProductSpec.endIconDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_paper_plane_top)
                }
            }
            .addOnFailureListener {
                binding.outlineTextfieldProductSpec.isEnabled = true
                toast(requireActivity(), "Error Posting ${it.message}")
                binding.outlineTextfieldProductSpec.endIconDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_paper_plane_top)
            }
    }
}