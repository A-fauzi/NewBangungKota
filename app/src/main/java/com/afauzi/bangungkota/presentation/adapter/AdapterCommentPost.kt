package com.afauzi.bangungkota.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.bangungkota.databinding.ComponentListCommunityPostBinding
import com.afauzi.bangungkota.domain.model.Post

class AdapterCommentPost(private var commentList: List<Post.ReplyPost>, private val bindCallBack: (ComponentListCommunityPostBinding, Post.ReplyPost) -> Unit): RecyclerView.Adapter<AdapterCommentPost.CommentViewHolder>() {
    inner class CommentViewHolder(val binding: ComponentListCommunityPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ComponentListCommunityPostBinding.inflate(inflater, parent, false)
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