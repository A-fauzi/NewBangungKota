package com.afauzi.bangungkota.presentation.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.adapter.AdapterPagingReplyPost
import com.afauzi.bangungkota.presentation.viewmodels.PostReplyViewModel
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.afauzi.bangungkota.utils.UniqueIdGenerator.generateUniqueId
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class InfoDetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var inputCommentMessageLayout: TextInputLayout
    private lateinit var inputCommentMessageEdiText: EditText
    private lateinit var adapterPagingReplyPost: AdapterPagingReplyPost

    private var user: FirebaseUser? = null

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val postReplyViewModel: PostReplyViewModel by viewModels()

    private fun init() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        inputCommentMessageLayout = binding.inputReply.outlineTextfieldCommentMessage
        inputCommentMessageEdiText = binding.inputReply.etPostComment

        adapterPagingReplyPost = AdapterPagingReplyPost {viewBind, dataReply ->

            viewBind.replyPostParent.tvTextPost.text = dataReply.text

            lifecycleScope.launch {
                userViewModel.getUserById(dataReply.userId.toString())
                    .addOnSuccessListener {
                        if (it.exists()) {
                            Glide.with(this@InfoDetailPostActivity)
                                .load(it.getString("photo"))
                                .into(viewBind.replyPostParent.itemIvProfile)

                            viewBind.replyPostParent.itemNameUser.text = it.getString("name")
                        } else {

                        }
                    }
                    .addOnFailureListener {

                    }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        thisActivityBehaviour()

        setDetailDataPost { post ->
            lifecycleScope.launch {
                postReplyViewModel.getReplyPost(post.id).collectLatest { pagingData ->
                    adapterPagingReplyPost.submitData(pagingData)
                }
            }
        }

        binding.rvReply.apply {
            layoutManager = LinearLayoutManager(this@InfoDetailPostActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterPagingReplyPost
        }

    }

    private fun thisActivityBehaviour() {
        // Image current user in input column
        Glide.with(this@InfoDetailPostActivity)
            .load(user?.photoUrl)
            .placeholder(R.drawable.image_profile_place_holder)
            .error(R.drawable.image_error)
            .into(binding.inputReply.ivCurrentUserMessage)


        // show if start activity
        inputBehaviour(inputCommentMessageEdiText, true)
    }

    private fun inputBehaviour(editTextView: EditText, isShowing: Boolean) {
        // Window input text down
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        if (isShowing) {
            editTextView.requestFocus()
            inputMethodManager.showSoftInput( editTextView, InputMethodManager.SHOW_IMPLICIT)
        } else {
            inputMethodManager.hideSoftInputFromWindow(editTextView.windowToken, 0)
        }
    }

    private fun setDetailDataPost(postData: (Post) -> Unit) {
        val post = intent.getParcelableExtra("post_data") as? Post

        if (post != null) {

            postData(post)


            // Get user live data
            userDetailPostLiveData(post.uid.toString())

            binding.itemPost.tvTextPost.text = post.text

            onClickMessageReply(post)

        } else {
            // if data null
        }
    }

    private fun onClickMessageReply(post: Post) {
        inputCommentMessageLayout.setEndIconOnClickListener {

            viewModels(post)

            inputBehaviour(inputCommentMessageEdiText, false)
            inputCommentMessageEdiText.text.clear()
        }
    }

    private fun viewModels(post: Post) {
        val data = Post.ReplyPost(
            id = generateUniqueId(),
            postId = post.id,
            userId = user?.uid,
            text = inputCommentMessageEdiText.text.toString().trim(),

            )

        postReplyViewModel.createPostReply(data, data.id.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    adapterPagingReplyPost.refresh()
                } else {
                    Toast.makeText(this, "reply not send", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "error ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun userDetailPostLiveData(uid: String) {
        lifecycleScope.launch {
            userViewModel.userLiveData.observe(this@InfoDetailPostActivity) {
                Glide.with(this@InfoDetailPostActivity)
                    .load(it.photo)
                    .placeholder(R.drawable.image_profile_place_holder)
                    .error(R.drawable.image_error)
                    .into(binding.itemPost.itemIvProfile)

                binding.itemPost.itemNameUser.text = it.name
                binding.itemPost.itemEmailUser.text = it.email
            }
            userViewModel.getUserByIdLiveData(uid)
        }
    }
}