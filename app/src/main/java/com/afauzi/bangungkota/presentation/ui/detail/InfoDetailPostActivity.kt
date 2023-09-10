package com.afauzi.bangungkota.presentation.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityDetailPostBinding
import com.afauzi.bangungkota.databinding.ComponentListReplyPostBinding
import com.afauzi.bangungkota.domain.model.Post
import com.afauzi.bangungkota.presentation.adapter.AdapterPagingReplyPost
import com.afauzi.bangungkota.presentation.viewmodels.PostReplyViewModel
import com.afauzi.bangungkota.presentation.viewmodels.PostViewModel
import com.afauzi.bangungkota.presentation.viewmodels.UserViewModel
import com.afauzi.bangungkota.utils.CustomViews.circularDrawableToLoadInput
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UniqueIdGenerator.generateUniqueId
import com.afauzi.bangungkota.utils.UtilityLibrary.formatedDateToTimeAgo
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class InfoDetailPostActivity : AppCompatActivity() {

    companion object {
        const val END_ICON_MODE_SEND = 0
        const val END_ICON_MODE_CLEAR_PREFIX = 1
    }

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var inputCommentMessageLayout: TextInputLayout
    private lateinit var inputCommentMessageEdiText: EditText
    private lateinit var adapterPagingReplyPost: AdapterPagingReplyPost

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val postReplyViewModel: PostReplyViewModel by viewModels()

    private var user: FirebaseUser? = null
    private var isPrefix: Boolean = false
    private var endIconMode: Int = 0

    private fun init() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        inputCommentMessageLayout = binding.inputReply.outlineTextfieldCommentMessage
        inputCommentMessageEdiText = binding.inputReply.etPostComment

        adapterPagingReplyPost = AdapterPagingReplyPost { viewBind, dataReply ->
            getUserReplyAndSetDataBinding(dataReply, viewBind)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        thisActivityBehaviour()

        setDetailDataPost { post ->

            inputPrefixValidation(post)


            if (post.uid.toString() != user?.uid.toString()) binding.itemPost.btnMorePost.isVisible =
                false

            // Get user live data
            userDetailPostLiveData(post.uid.toString())

            binding.itemPost.tvTextPost.text = post.text
            binding.itemPost.itemDatePost.text = formatedDateToTimeAgo(post.created_at)

            lifecycleScope.launch {
                postReplyViewModel.getReplyPost(post.id).collectLatest { pagingData ->
                    adapterPagingReplyPost.submitData(pagingData)
                }
            }
        }

        binding.rvReply.apply {
            layoutManager = LinearLayoutManager(
                this@InfoDetailPostActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = adapterPagingReplyPost
        }


    }

    private fun getUserReplyAndSetDataBinding(
        dataReply: Post.ReplyPost,
        viewBind: ComponentListReplyPostBinding
    ) {

        viewBind.replyPostParent.tvTextPost.text = dataReply.text

        lifecycleScope.launch {
            userViewModel.getUserById(dataReply.userId.toString())
                .addOnSuccessListener { user ->
                    if (user.exists()) {
                        Glide.with(this@InfoDetailPostActivity)
                            .load(user.getString("photo"))
                            .into(viewBind.replyPostParent.itemIvProfile)

                        viewBind.replyPostParent.itemNameUser.text = user.getString("name")

                        viewBind.replyPostParent.btnComment.setOnClickListener {
                            isPrefix = true
                            inputCommentMessageLayout.prefixText = "@${user.getString("name")}"

                            if (inputCommentMessageEdiText.text.toString().isBlank()) {
                                endIconMode = END_ICON_MODE_CLEAR_PREFIX
                                inputCommentMessageLayout.endIconDrawable =
                                    ContextCompat.getDrawable(
                                        this@InfoDetailPostActivity,
                                        R.drawable.cross_circle
                                    )
                            }
                        }

                    } else {

                    }
                }
                .addOnFailureListener {

                }
        }
    }

    private fun inputPrefixValidation(post: Post) {

        // OnClick send message reply
        inputCommentMessageLayout.setEndIconOnClickListener {

            if (endIconMode == END_ICON_MODE_SEND) {
                if (isPrefix && inputCommentMessageEdiText.text.toString().isNotBlank()) toast(
                    this,
                    "reply child"
                )

                if (!isPrefix) {

                    if (inputCommentMessageEdiText.text.toString().isNotBlank()) {
                        createReplyPost(post)
                    } else {
                        toast(this, "minimal isi dulu 🤦‍♀️")
                    }

                }
            }

            if (endIconMode == END_ICON_MODE_CLEAR_PREFIX) {
                inputCommentMessageLayout.endIconDrawable = ContextCompat.getDrawable(
                    this@InfoDetailPostActivity,
                    R.drawable.ic_paper_plane_top
                )
                inputCommentMessageLayout.prefixText = null
                isPrefix = false
                endIconMode = END_ICON_MODE_SEND
            }

            inputBehaviour(inputCommentMessageEdiText, false)
            inputCommentMessageEdiText.text.clear()
        }

        inputCommentMessageEdiText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()

                if (isPrefix) {
                    if (text.isNotBlank()) {
                        endIconMode = END_ICON_MODE_SEND
                        inputCommentMessageLayout.endIconDrawable = ContextCompat.getDrawable(
                            this@InfoDetailPostActivity,
                            R.drawable.ic_paper_plane_top
                        )
                    } else {
                        endIconMode = END_ICON_MODE_CLEAR_PREFIX
                        inputCommentMessageLayout.endIconDrawable = ContextCompat.getDrawable(
                            this@InfoDetailPostActivity,
                            R.drawable.cross_circle
                        )
                    }
                }
            }

        })
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
            inputMethodManager.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT)
        } else {
            inputMethodManager.hideSoftInputFromWindow(editTextView.windowToken, 0)
        }
    }

    private fun setDetailDataPost(postData: (Post) -> Unit) {
        val post = intent.getParcelableExtra("post_data") as? Post

        if (post != null) {
            postData(post)
        } else {
            // if data null
        }
    }

    private fun createReplyPost(post: Post) {

        val circularProgressDrawable = circularDrawableToLoadInput(this)
        inputCommentMessageLayout.endIconDrawable = circularProgressDrawable
        circularProgressDrawable.start()

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

                    inputCommentMessageLayout.endIconDrawable =
                        ContextCompat.getDrawable(this, R.drawable.ic_paper_plane_top)
                } else {
                    Toast.makeText(this, "reply not send", Toast.LENGTH_SHORT).show()
                    inputCommentMessageLayout.endIconDrawable =
                        ContextCompat.getDrawable(this, R.drawable.ic_paper_plane_top)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "error ${it.message}", Toast.LENGTH_SHORT).show()
                inputCommentMessageLayout.endIconDrawable =
                    ContextCompat.getDrawable(this, R.drawable.ic_paper_plane_top)
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
            }
            userViewModel.getUserByIdLiveData(uid)
        }
    }
}