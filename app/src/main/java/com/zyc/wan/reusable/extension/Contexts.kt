package com.zyc.wan.reusable.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * @author devzyc
 */
fun Context.toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(
    @StringRes resId: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, resources.getText(resId), duration).show()
}

