package com.afauzi.bangungkota.presentation.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class InfoDetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var inputCommentMessageLayout: TextInputLayout
    private lateinit var inputCommentMessageEdiText: EditText

    private var user: FirebaseUser? = null

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private fun init() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        inputCommentMessageLayout = binding.inputReply.outlineTextfieldCommentMessage
        inputCommentMessageEdiText = binding.inputReply.etPostComment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setDetailDataPost()
        thisActivityBehaviour()

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

    private fun setDetailDataPost() {
        val post = intent.getParcelableExtra("post_data") as? Post

        if (post != null) {

            // Get user live data
            userDetailPostLiveData(post)

            binding.itemPost.tvTextPost.text = post.text

        } else {
            // if data null
        }
    }

    private fun userDetailPostLiveData(post: Post) {
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
            userViewModel.getUserByIdLiveData(post.uid.toString())
        }
    }
}