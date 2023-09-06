package com.afauzi.bangungkota.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.bangungkota.databinding.ComponentListCommunityPostBinding
import com.afauzi.bangungkota.domain.model.Post

class AdapterPagingPost: PagingDataAdapter<Post, AdapterPagingPost.PostViewHolder>(PostDiffCallback) {

    object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    inner class PostViewHolder(private val binding: ComponentListCommunityPostBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvTextPost.text = post.text
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post.let {
            if (it != null) holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ComponentListCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }
}