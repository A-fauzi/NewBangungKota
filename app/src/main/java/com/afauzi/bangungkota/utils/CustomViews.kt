package com.afauzi.bangungkota.utils

import android.content.Context
import android.widget.Toast

object CustomViews {
    fun toast(context: Context, message: String?) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}