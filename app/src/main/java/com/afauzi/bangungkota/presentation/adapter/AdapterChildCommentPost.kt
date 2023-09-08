package com.afauzi.bangungkota.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.bangungkota.databinding.ComponentListReplyPostBinding
import com.afauzi.bangungkota.databinding.LayoutReplyPostBinding
import com.afauzi.bangungkota.domain.model.Post

class AdapterChildCommentPost(private var commentList: List<Post.ReplyPost>, private val bindCallBack: (LayoutReplyPostBinding, Post.ReplyPost) -> Unit): RecyclerView.Adapter<AdapterChildCommentPost.CommentViewHolder>() {
    inner class CommentViewHolder(val binding: LayoutReplyPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutReplyPostBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comments = commentList[position]
        bindCallBack(holder.binding, comments)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}