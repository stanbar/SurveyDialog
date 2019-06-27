package com.stasbar.feedbackdialog.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.stasbar.feedbackdialog.R

internal fun inferTheme(context : Context):Int{
    val isThemeDark = !inferThemeIsLight(context)
    return if(isThemeDark){
        R.style.FD_Dark
    }else{
        R.style.FD_Light
    }
}

@CheckResult
internal fun inferThemeIsLight(context: Context): Boolean {
    return resolveColor(context = context, attr = android.R.attr.textColorPrimary).isColorDark()
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) @ColorInt
fun resolveColor(
        context: Context,
        @ColorRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: (() -> Int)? = null
): Int {
    if (attr != null) {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            val result = a.getColor(0, 0)
            if (result == 0 && fallback != null) {
                return fallback()
            }
            return result
        } finally {
            a.recycle()
        }
    }
    return ContextCompat.getColor(context, res ?: 0)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun Int.isColorDark(threshold: Double = 0.5): Boolean {
    if (this == Color.TRANSPARENT) {
        return false
    }
    val darkness =
            1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= threshold
}

