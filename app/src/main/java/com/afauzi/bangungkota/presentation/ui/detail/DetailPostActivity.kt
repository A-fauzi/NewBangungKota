package com.afauzi.bangungkota.presentation.ui.detail

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.adapter.AdapterCommentPost
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.afauzi.bangungkota.utils.UniqueIdGenerator
import com.afauzi.bangungkota.utils.UtilityLibrary.currentDate
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var replyPostAdapterCommentPost: AdapterCommentPost
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private fun init() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        setDetailDataPost()

        Glide.with(this@DetailPostActivity)
            .load(user?.photoUrl)
            .placeholder(R.drawable.image_profile_place_holder)
            .error(R.drawable.image_error)
            .into(binding.inputReply.ivCurrentUserMessage)

    }

    private fun setDetailDataPost() {
        val receivedData = intent.getParcelableExtra("post_data") as? Post

        if (receivedData != null) {

            getListComment(receivedData.id)

            getUser(receivedData)


            binding.inputReply.outlineTextfieldCommentMessage.setEndIconOnClickListener {

                val etTextComment = binding.inputReply.etPostComment

//                insertComment(
//                    "comments",
//                    UniqueIdGenerator.generateUniqueId(),
//                    receivedData.id,
//                    user?.uid.toString(),
//                    etTextComment.text.toString().trim(),
//                )

                Toast.makeText(this, "input parent", Toast.LENGTH_SHORT).show()

                etTextComment.text?.clear()

                // Window input text down
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(etTextComment.windowToken, 0)
            }

        }
    }

    private fun getUser(receivedData: Post) {
        lifecycleScope.launch {
            userViewModel.getUserById(receivedData.uid.toString())
                .addOnSuccessListener {
                    if (it.exists()) {
                        Glide.with(this@DetailPostActivity)
                            .load(it.getString("photo"))
                            .placeholder(R.drawable.image_profile_place_holder)
                            .error(R.drawable.image_error)
                            .into(binding.itemPost.itemIvProfile)
                        binding.itemPost.itemNameUser.text = it.getString("name")
                        binding.itemPost.itemEmailUser.text = it.getString("email")
                        binding.itemPost.tvTextPost.text = receivedData.text

                        binding.itemPost.btnMorePost.isVisible = receivedData.uid == user?.uid
                    } else {
                        // Handle data not exists
                    }
                }.addOnFailureListener {
                    // Handle request failure
                }
        }
    }

    private fun insertComment(
        childParent: String,
        commentId: String,
        postId: String,
        currentUserId: String,
        text: String
    ) {

        val data = Post.ReplyPost(commentId, postId, currentUserId, text, currentDate())

        postViewModel.createReplyPost(data, postId, childParent)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "success reply", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "not success reply", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "error ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun getListComment(postId: String) {

        lifecycleScope.launch {
            postViewModel.replyPostData.observe(this@DetailPostActivity) {
                replyPostAdapterCommentPost = AdapterCommentPost(it) { viewBinding, data ->

                    viewBinding.replyPostParent.tvTextPost.text = data.text

                    lifecycleScope.launch {

                        userViewModel.getUserById(data.userId.toString())
                            .addOnSuccessListener { user ->

                                Glide.with(this@DetailPostActivity)
                                    .load(user.getString("photo"))
                                    .into(viewBinding.replyPostParent.itemIvProfile)
                                viewBinding.replyPostParent.itemNameUser.text =
                                    user.getString("name")

                                viewBinding.replyPostParent.btnComment.setOnClickListener {

                                    binding.inputReply.outlineTextfieldCommentMessage.isVisible = true

                                    // Berikan fokus ke EditText
                                    binding.inputReply.etPostComment.requestFocus()

                                    // Tampilkan keyboard secara otomatis
                                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.showSoftInput(
                                        binding.inputReply.etPostComment,
                                        InputMethodManager.SHOW_IMPLICIT
                                    )

                                    binding.inputReply.etPostComment.setText(user.getString("name"))
                                }

                            }
                            .addOnFailureListener { }
                    }
                }

                binding.rvReply.apply {
                    layoutManager = LinearLayoutManager(
                        this@DetailPostActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = replyPostAdapterCommentPost
                }

                replyPostAdapterCommentPost.notifyDataSetChanged()
            }
            postViewModel.getReplyPostList(postId)
        }

    }
}