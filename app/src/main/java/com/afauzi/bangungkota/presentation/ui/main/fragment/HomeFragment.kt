package com.afauzi.bangungkota.presentation.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ComponentBottomSheetMorePostBinding
import com.afauzi.bangungkota.databinding.FragmentHomeBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.presentation.adapter.AdapterPagingEvent
import com.afauzi.bangungkota.presentation.ui.auth.SignInActivity
import com.afauzi.bangungkota.presentation.ui.detail.InfoDetailEventActivity
import com.afauzi.bangungkota.presentation.ui.event.CreateEventActivity
import com.afauzi.bangungkota.presentation.viewmodels.EventViewModel
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UtilityLibrary.currentDate
import com.afauzi.bangungkota.utils.UtilityLibrary.imageGlideForCircle
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), AdapterPagingEvent.ListenerAdapterEvent {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterPagingEvent: AdapterPagingEvent

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.containerContent.visibility = View.GONE
        binding.progressbar.visibility = View.VISIBLE

        adapterPagingEvent = AdapterPagingEvent(requireActivity(), this)

        binding.rvEvent.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterPagingEvent
        }

    }

    override fun onStart() {
        super.onStart()

        setDataToViews()
        getEventList()

        binding.fabCreateEvent.setOnClickListener {
            val intent = Intent(requireActivity(), CreateEventActivity::class.java)
            TransformationCompat.startActivity(binding.transformLayout, intent)
        }
    }

    private fun getEventList() {
        lifecycleScope.launch {
            eventViewModel.getEvents.collectLatest { pagingData ->
                adapterPagingEvent.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapterPagingEvent.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                val isLoadingAppend = loadStates.append is LoadState.Loading

                if (isLoading) {
                    // data is load
                } else {
                    binding.containerContent.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE

                    // CEK DATA AVAIL IN LIST
                    adapterPagingEvent.loadStateFlow.distinctUntilChangedBy {
                        it.refresh
                    }.collect {
                        //you get all the data here
                        val list = adapterPagingEvent.snapshot()

                        Log.d("HomeFragment", list.toString())

                        if (list.isEmpty()) {
                            binding.tvDataEmpty.visibility = View.VISIBLE
                            binding.rvEvent.visibility = View.GONE
                        } else {
                            binding.tvDataEmpty.visibility = View.GONE
                            binding.rvEvent.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setDataToViews() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.appBarLayout.topAppBar.navigationIcon = null
        binding.appBarLayout.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.user -> {
                    binding.drawerLayout.open()
                    true
                }
                else -> false
            }
        }

        val headerView = binding.navigationView.getHeaderView(0)
        val topAppBar = headerView.findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.arrow_small_left, null)
        topAppBar.menu.findItem(R.id.user).icon = ResourcesCompat.getDrawable(resources, R.drawable.pencil, null)
        topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.close()
        }
        topAppBar.setBackgroundColor(resources.getColor(R.color.purple_500))

        val imageView = headerView.findViewById<ImageView>(R.id.iv_profile_header)
        val username = headerView.findViewById<TextView>(R.id.tv_name_user_drawer_header)
        val email = headerView.findViewById<TextView>(R.id.tv_email_user_drawer_header)

        username.text = user?.displayName
        email.text = user?.email
        Glide.with(requireActivity())
            .load(user?.photoUrl)
            .into(imageView)

        val menu = binding.navigationView.menu.findItem(R.id.logout)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(requireActivity(), SignInActivity::class.java))
                    activity?.finish()
                    true // Menyatakan bahwa tindakan sudah ditangani
                }
                else -> false // Tidak ada tindakan yang ditentukan
            }
        }

        binding.appBarLayout.topAppBar.title = "Hi ${user?.displayName} 🙌"
        imageGlideForCircle(
            context = requireActivity(),
            dataImg = user?.photoUrl.toString(),
            res = resources
        ) { bitmap ->
            binding.appBarLayout.topAppBar.menu.findItem(R.id.user).icon = bitmap
        }

    }

    override fun onClickItemDetail(data: Event, transformLayoutItem: TransformationLayout) {
        val intent = Intent(requireActivity(), InfoDetailEventActivity::class.java)
        intent.putExtra("event_data", data) // Mengirim objek Event sebagai extra
        TransformationCompat.startActivity(transformLayoutItem, intent)
    }

    override fun clickItemMore(data: Event) {
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
            dialog.setCancelable(false)

            eventViewModel.deleteEvent(data.id.toString(), data.image) { isSuccessDeleted ->

                if (isSuccessDeleted) {
                    adapterPagingEvent.refresh()
                    dialog.dismiss()
                    toast(requireActivity(), "Data deleted 👌")
                } else {
                    progressBar.visibility = View.GONE
                    layoutItem.visibility = View.VISIBLE
                    dialog.setCancelable(true)
                    toast(requireActivity(), "Ada kesalahan saat menghapus🤦‍♀️")
                }

            }
        }
        dialog.show()
    }

}