package com.afauzi.bangungkota.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.afauzi.bangungkota.data.repository.post.PostRepository
import com.afauzi.bangungkota.data.repository.post.PostRepositoryImpl
import com.afauzi.bangungkota.data.repository.post.reply.ReplyPostRepository
import com.afauzi.bangungkota.domain.model.Post
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val replyPostRepository: ReplyPostRepository
) : ViewModel() {

    val getEvents = postRepository.postPagingSource().cachedIn(viewModelScope)

    fun createPost(data: Post, documentId: String): Task<Void> {
        return postRepository.createPost(data, documentId)
    }

    fun deletePost(documentId: String): Task<Void> {
        return postRepository.deletePost(documentId)
    }

    // REPLY POST
    fun createReplyPost(data: Post.ReplyPost, postId: String, childParent: String): Task<Void> {
        return replyPostRepository.createReplyPost(data, postId, childParent)
    }

    private val _replyPostData = MutableLiveData<List<Post.ReplyPost>>()
    val replyPostData: LiveData<List<Post.ReplyPost>> get() = _replyPostData

    suspend fun getReplyPostList(childParent: String, postId: String) {
        val commentPost = mutableListOf<Post.ReplyPost>()
        replyPostRepository.getReplyPost(childParent, postId, {
            // Bersihkan daftar sebelum mengisi ulang
            commentPost.clear()

            // Mengambil data dari snapshot
            for (comment in it.children) {
                // Konversi snapshot menjadi objek UserData
                val dataComment = comment.getValue(Post.ReplyPost::class.java)

                // Jika userData tidak null, tambahkan ke daftar
                dataComment?.let { replyPost ->
                    commentPost.add(replyPost)
                }
            }

            commentPost.reverse()

            // Mengupdate _replyPostData LiveData dengan data yang baru
            _replyPostData.postValue(commentPost)

        }){

        }
    }

    private val _replyPostDataChild = MutableLiveData<List<Post.ReplyPost>>()
    val replyPostDataChild: LiveData<List<Post.ReplyPost>> get() = _replyPostDataChild

    suspend fun getReplyPostListChild(childParent: String, postId: String) {
        val commentPostChild = mutableListOf<Post.ReplyPost>()
        replyPostRepository.getReplyPost(childParent, postId, {
            // Bersihkan daftar sebelum mengisi ulang
            commentPostChild.clear()

            // Mengambil data dari snapshot
            for (comment in it.children) {
                // Konversi snapshot menjadi objek UserData
                val dataComment = comment.getValue(Post.ReplyPost::class.java)

                // Jika userData tidak null, tambahkan ke daftar
                dataComment?.let { replyPost ->
                    commentPostChild.add(replyPost)
                }
            }

            // Mengupdate _replyPostData LiveData dengan data yang baru
            _replyPostDataChild.postValue(commentPostChild)

        }){

        }
    }
}