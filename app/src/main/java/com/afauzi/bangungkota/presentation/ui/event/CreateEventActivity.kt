package com.afauzi.bangungkota.presentation.ui.event

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.afauzi.bangungkota.R
import com.afauzi.bangungkota.databinding.ActivityCreateEventBinding
import com.afauzi.bangungkota.domain.model.Event
import com.afauzi.bangungkota.presentation.ui.main.MainActivity
import com.afauzi.bangungkota.presentation.viewmodels.EventViewModel
import com.afauzi.bangungkota.utils.Constant
import com.afauzi.bangungkota.utils.Constant.RC_IMAGE_GALLERY
import com.afauzi.bangungkota.utils.CustomViews
import com.afauzi.bangungkota.utils.CustomViews.toast
import com.afauzi.bangungkota.utils.UniqueIdGenerator
import com.afauzi.bangungkota.utils.UniqueIdGenerator.generateUniqueId
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private lateinit var filePathImageGallery: Uri

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewAppBar()
        setViewFormTextField()
        onClickViews()
    }

    private fun onClickViews() {
        binding.cvUploadImage.setOnClickListener {
            getImageFromGalerry()
        }
        binding.eventDate.outlinedTextFieldEvent.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date event 📅")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(supportFragmentManager, "17:00")
            datePicker.addOnPositiveButtonClickListener {
                val selectDate = Date(it)
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDate)
                binding.eventDate.outlinedTextFieldEvent.editText?.setText(formattedDate)
                binding.eventDate.editTextCreateEvent.setTextColor(resources.getColor(R.color.black))
            }
        }
        binding.eventTime.outlinedTextFieldEvent.setEndIconOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select time event ⏲")
                    .build()
            picker.show(supportFragmentManager, "time")
            picker.addOnPositiveButtonClickListener {
                val selectedHour = picker.hour
                val selectedMinute = picker.minute
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                binding.eventTime.outlinedTextFieldEvent.editText?.setText(formattedTime)
                binding.eventTime.editTextCreateEvent.setTextColor(resources.getColor(R.color.black))

            }
        }
        binding.btnCreateEvent.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            val data = Event(
                id = generateUniqueId(),
                title = binding.eventName.editTextCreateEvent.text.toString(),
                address = binding.eventLocation.editTextCreateEvent.text.toString(),
                date = binding.eventDate.editTextCreateEvent.text.toString(),
                time = binding.eventTime.editTextCreateEvent.text.toString(),
                createdBy = user?.uid.toString(),
                description = binding.etDescription.text.toString()
            )
            if (this::filePathImageGallery.isInitialized) {
                // JIka semua validasi pada create event sudah memenuhi
                binding.btnCreateEvent.visibility = View.GONE
                binding.progressbar.visibility = View.VISIBLE
                enabledStateForm(false)

                eventViewModel.createEvent(filePathImageGallery, data) { isSuccessfullyTransactionDb ->
                    if (isSuccessfullyTransactionDb)  {
                        val intent = Intent(this@CreateEventActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        toast(this, "Operasi gagal, ada kesalahan😥")
                        binding.btnCreateEvent.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.GONE
                    }
                }
            } else {
                toast(this, "Upload Gambar dulu 🤦‍♂️")
            }
        }
    }

    private fun enabledStateForm(state: Boolean) {
        binding.eventName.outlinedTextFieldEvent.isEnabled = state
        binding.eventLocation.outlinedTextFieldEvent.isEnabled = state
        binding.outlinedTextFieldEventDesc.isEnabled = state
    }

    private fun getImageFromGalerry() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RC_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == RC_IMAGE_GALLERY) {
            try {
                if (data != null) {
                    filePathImageGallery = data.data!!
                    binding.tvSetFillPath.text = filePathImageGallery.path.toString()
                } else {
                    toast(this, "Data photo is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast(this, e.message)
            }
        }
    }

    private fun setViewAppBar() {
        binding.appBarLayout.topAppBar.title = "Create Event"
        binding.appBarLayout.topAppBar.menu.findItem(R.id.user).isVisible = false
        binding.appBarLayout.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_angle_left, null)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setViewFormTextField() {
        binding.eventLocation.tvInputName.text = getString(R.string.choose_location)
        binding.eventDate.tvInputName.text = getString(R.string.set_data_event)
        binding.eventTime.tvInputName.text = getString(R.string.set_time_event)
        binding.eventName.tvInputName.text = getString(R.string.event_name)

        binding.eventLocation.editTextCreateEvent.hint = "Bekasi, Jawabarat"
        binding.eventName.editTextCreateEvent.hint = "Charity child people"
        binding.eventDate.editTextCreateEvent.hint = "2023-08-31"
        binding.eventTime.editTextCreateEvent.hint = "18:10"

        binding.eventLocation.outlinedTextFieldEvent.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.eventDate.outlinedTextFieldEvent.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.eventTime.outlinedTextFieldEvent.endIconMode = TextInputLayout.END_ICON_CUSTOM

        binding.eventLocation.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.map_marker_filled, null)
        binding.eventDate.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_calendar, null)
        binding.eventTime.outlinedTextFieldEvent.endIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_clock, null)

        binding.eventTime.editTextCreateEvent.isEnabled = false
        binding.eventDate.editTextCreateEvent.isEnabled = false

    }

}