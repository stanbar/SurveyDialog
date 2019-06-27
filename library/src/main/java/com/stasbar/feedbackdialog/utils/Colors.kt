package com.stasbar.feedbackdialog.utils

import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.stasbar.feedbackdialog.FeedBackDialog

@ColorInt
@CheckResult
internal fun FeedBackDialog.resolveColor(
        @ColorRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: (() -> Int)? = null
): Int = resolveColor(windowContext, res, attr, fallback)