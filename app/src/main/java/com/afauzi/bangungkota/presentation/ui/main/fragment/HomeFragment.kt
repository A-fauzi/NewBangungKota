package com.afauzi.bangungkota.presentation.ui.main.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.map
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.FragmentHomeBinding
import com.afauzi.bangungkota.presentation.ui.adapter.AdapterPagingEvent
import com.afauzi.bangungkota.presentation.viewmodels.AuthViewModel
import com.afauzi.bangungkota.presentation.viewmodels.EventViewModel
import com.afauzi.bangungkota.utils.CustomViews
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UtilityLibrary
import com.afauzi.bangungkota.utils.UtilityLibrary.currentDate
import com.afauzi.bangungkota.utils.UtilityLibrary.imageGlideForCircle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterPagingEvent: AdapterPagingEvent

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        adapterPagingEvent = AdapterPagingEvent(requireActivity())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataToViews()
        getEventList()

        binding.rvEvent.apply {
            adapter = adapterPagingEvent
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
                    binding.rvEvent.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                } else {
                    binding.rvEvent.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE

                    // CEK DATA AVAIL IN LIST
                    adapterPagingEvent.loadStateFlow.distinctUntilChangedBy {
                        it.refresh
                    }.collect {
                        //you get all the data here
                        val list = adapterPagingEvent.snapshot()
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

        // SET DATA TOOLBAR
        binding.appBarLayout.topAppBar.title = "Hi ${user?.displayName} 🙌"
        imageGlideForCircle(
            context = requireActivity(),
            dataImg = user?.photoUrl.toString(),
            res = resources
        ) { bitmap ->
            binding.appBarLayout.topAppBar.menu.findItem(R.id.user).icon = bitmap
        }

        binding.currentDate.text = currentDate()




    }
}