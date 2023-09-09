package com.afauzi.bangungkota.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.afauzi.bangungkota.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object UtilityLibrary {
    fun imageGlideForCircle(
        context: Context,
        dataImg: String,
        res: Resources,
        viewBindingBitmap: (iconDrawable: BitmapDrawable) -> Unit
    ) {
        Glide.with(context)
            .asBitmap()
            .load(dataImg)
            .placeholder(R.drawable.image_profile_place_holder)
            .error(R.drawable.image_error)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val iconDrawable = BitmapDrawable(res, resource)
                    viewBindingBitmap(iconDrawable)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }

            })
    }

    fun currentDate(): String {
        val currentTime = Calendar.getInstance().time
        val desiredFormat = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)
        return desiredFormat.format(currentTime)
    }

    fun timeStamp(): String {
        // Timestamp yang akan diubah ke dalam bentuk string
        val timestamp = Timestamp.now()

// Konversi Timestamp ke Date
        val date = timestamp.toDate()

// Format tanggal ke dalam string dengan menggunakan SimpleDateFormat
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())

        return dateFormat.format(date)
    }
}