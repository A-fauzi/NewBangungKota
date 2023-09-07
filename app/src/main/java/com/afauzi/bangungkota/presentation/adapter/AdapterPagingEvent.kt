package com.afauzi.bangungkota.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.bangungkota.databinding.ComponentListEventBinding
import com.afauzi.bangungkota.domain.model.Event
import com.bumptech.glide.Glide
import com.skydoves.transformationlayout.TransformationLayout

class AdapterPagingEvent(private val context: Context, private val listenerAdapterEvent: ListenerAdapterEvent): PagingDataAdapter<Event, AdapterPagingEvent.EventViewHolder>(
    EventDiffCallback
) {

    inner class EventViewHolder(private val binding: ComponentListEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.itemTitle.text = event.title
            binding.itemAddress.text = event.address
            binding.itemDate.text = event.date
            Glide.with(context)
                .load(event.image)
                .into(binding.itemImage)
            binding.cvItem.setOnClickListener {
                listenerAdapterEvent.onClickItemDetail(event, binding.transformLayout)
            }
            binding.btnMorePost.setOnClickListener {
                listenerAdapterEvent.clickItemMore(event)
            }
        }
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        event.let {
            if (it != null) {
                holder.bind(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ComponentListEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    companion object {
        private val EventDiffCallback = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface ListenerAdapterEvent {
        fun onClickItemDetail(data: Event, transformLayoutItem: TransformationLayout)
        fun clickItemMore(data: Event)
    }
}