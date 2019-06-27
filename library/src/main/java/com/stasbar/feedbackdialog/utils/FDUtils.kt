package com.stasbar.feedbackdialog.utils

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.stasbar.feedbackdialog.DialogCallback
import com.stasbar.feedbackdialog.FeedBackDialog

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun assertOneSet(
        method: String,
        b: Any?,
        a: Int?
) {
    if (a == null && b == null) {
        throw IllegalArgumentException("$method: You must specify a resource ID or literal value")
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun View.show() {
    visibility = View.VISIBLE
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun View.hide() {
    visibility = View.GONE
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun resolveDimen(
        context: Context,
        @AttrRes attr: Int,
        defaultValue: Float = 0f
): Float {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getDimension(0, defaultValue)
    } finally {
        a.recycle()
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun resolveDrawable(
        context: Context,
        @DrawableRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: Drawable? = null
): Drawable? {
    if (attr != null) {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            var d = a.getDrawable(0)
            if (d == null && fallback != null) {
                d = fallback
            }
            return d
        } finally {
            a.recycle()
        }
    }
    if (res == null) return fallback
    return ContextCompat.getDrawable(context, res)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun resolveString(
        materialDialog: FeedBackDialog,
        @StringRes res: Int? = null,
        @StringRes fallback: Int? = null,
        html: Boolean = false
): CharSequence? = resolveString(
        context = materialDialog.windowContext,
        res = res,
        fallback = fallback,
        html = html
)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun resolveString(
        context: Context,
        @StringRes res: Int? = null,
        @StringRes fallback: Int? = null,
        html: Boolean = false
): CharSequence? {
    val resourceId = res ?: (fallback ?: 0)
    if (resourceId == 0) return null
    val text = context.resources.getText(resourceId)
    if (html) {
        @Suppress("DEPRECATION")
        return Html.fromHtml(text.toString())
    }
    return text
}


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) fun WindowManager.getWidthAndHeight(): Pair<Int, Int> {
    val size = Point()
    defaultDisplay.getSize(size)
    return Pair(size.x, size.y)
}

internal fun MutableList<DialogCallback>.invokeAll(dialog: FeedBackDialog) {
    for (callback in this) {
        callback.invoke(dialog)
    }
}