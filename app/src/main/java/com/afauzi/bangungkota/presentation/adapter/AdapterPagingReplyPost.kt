package com.afauzi.bangungkota.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.bangungkota.databinding.ComponentListReplyPostBinding
import com.afauzi.bangungkota.domain.model.Post

class AdapterPagingReplyPost(private val bindCallback: (ComponentListReplyPostBinding, Post.ReplyPost) -> Unit): PagingDataAdapter<Post.ReplyPost, AdapterPagingReplyPost.ReplyPostViewHolder>(ReplyPostDiffCallback) {
    object ReplyPostDiffCallback : DiffUtil.ItemCallback<Post.ReplyPost>() {
        override fun areItemsTheSame(oldItem: Post.ReplyPost, newItem: Post.ReplyPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post.ReplyPost, newItem: Post.ReplyPost): Boolean {
            return oldItem == newItem
        }
    }

    class ReplyPostViewHolder(val binding: ComponentListReplyPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ReplyPostViewHolder, position: Int) {
        val replyPost = getItem(position) ?: return
        bindCallback(holder.binding, replyPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyPostViewHolder {
        val binding = ComponentListReplyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyPostViewHolder(binding)
    }
}