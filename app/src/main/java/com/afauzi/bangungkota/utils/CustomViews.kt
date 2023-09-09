package com.afauzi.bangungkota.utils

import android.content.Context
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.afauzi.bangungkota.R

object CustomViews {
    fun toast(context: Context, message: String?) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun circularDrawableToLoadInput(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            setColorSchemeColors(R.color.purple_700)
        }
    }


}