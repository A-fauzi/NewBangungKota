package com.afauzi.bangungkota.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
}