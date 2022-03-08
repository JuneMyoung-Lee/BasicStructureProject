package com.jmp.basicstructureproject.presentation.extension

import android.content.Context
import android.content.MutableContextWrapper
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import net.gotev.cookiestore.InMemoryCookieStore
import net.gotev.cookiestore.SharedPreferencesCookieStore

fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun Context.getActivityContext() : Context {
    return when (this) {
        is MutableContextWrapper -> this.baseContext
        else -> this
    }
}

// Example extension function to demonstrate how to create both cookie stores
fun Context.createCookieStore(name: String, persistent: Boolean) = if (persistent) {
    SharedPreferencesCookieStore(applicationContext, name)
} else {
    InMemoryCookieStore(name)
}