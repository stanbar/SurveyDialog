package com.stasbar.feedbackdialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.DrawableCompat

public class FeedBackDialog(private val context: Context) {

    @DrawableRes
    private var mIcon: Int = 0

    @ColorRes
    private var mIconColor: Int = 0

    @StringRes
    private var mTitle: Int = 0

    @DrawableRes
    private var mBackgroundColor: Int = 0

    @StringRes
    private var mDescription: Int = 0

    @StringRes
    private var mReviewQuestion: Int = 0


    lateinit var titleImageView: ImageView
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var reviewQuestionTextView: TextView

    lateinit var positiveFeedbackLayout: LinearLayout
    lateinit var negativeFeedbackLayout: LinearLayout
    lateinit var notSureFeedbackLayout: LinearLayout
    lateinit var feedbackBodyLayout: LinearLayout

    lateinit var positiveFeedbackTextView: TextView
    lateinit var negativeFeedbackTextView: TextView
    lateinit var notSureFeedbackTextView: TextView

    lateinit var positiveFeedbackIconView: ImageView
    lateinit var negativeFeedbackIconView: ImageView
    lateinit var notSureFeedbackIconView: ImageView
    lateinit var etConstructiveCriticism: EditText

    lateinit var layoutConstructiveCriticism: LinearLayout
    lateinit var layoutActions: LinearLayout

    @StringRes
    private var mPositiveFeedbackText: Int = 0

    @DrawableRes
    private var mPositiveFeedbackIcon: Int = 0

    @StringRes
    private var mNegativeFeedbackText: Int = 0

    @DrawableRes
    private var mNegativeFeedbackIcon: Int = 0

    @StringRes
    private var mnotSureFeedbackText: Int = 0

    @DrawableRes
    private var mnotSureFeedbackIcon: Int = 0

    private var positiveActionListener: ((Dialog) -> Unit)? = null
    private var constructiveActionListener: ((Dialog, String) -> Unit)? = null
    private var neutralActionListener: ((Dialog) -> Unit)? = null
    lateinit var dialog: Dialog
    private fun initiateAllViews(dialog: Dialog) {
        layoutConstructiveCriticism = dialog.findViewById(R.id.layoutConstructiveCriticism)
        layoutActions = dialog.findViewById(R.id.layoutActions)
        titleImageView = dialog.findViewById(R.id.review_icon) as ImageView
        titleTextView = dialog.findViewById(R.id.review_title)
        descriptionTextView = dialog.findViewById(R.id.review_description)
        reviewQuestionTextView = dialog.findViewById(R.id.review_questions)

        feedbackBodyLayout = dialog.findViewById(R.id.feedback_body_layout)
        etConstructiveCriticism = dialog.findViewById(R.id.etConstructiveCriticism)

        positiveFeedbackLayout = dialog.findViewById(R.id.postive_feedback_layout)
        positiveFeedbackTextView = dialog.findViewById(R.id.positive_feedback_text)
        positiveFeedbackIconView = dialog.findViewById(R.id.postive_feedback_icon) as ImageView

        negativeFeedbackLayout = dialog.findViewById(R.id.negative_feedback_layout)
        negativeFeedbackTextView = dialog.findViewById(R.id.negative_feedback_text)
        negativeFeedbackIconView = dialog.findViewById(R.id.negative_feedback_icon) as ImageView

        this.etConstructiveCriticism = dialog.findViewById(R.id.etConstructiveCriticism)

        notSureFeedbackTextView = dialog.findViewById(R.id.not_sure_feedback_text)
        notSureFeedbackLayout = dialog.findViewById(R.id.not_sure_feedback_layout)
        notSureFeedbackIconView = dialog.findViewById(R.id.not_sure_feedback_icon) as ImageView

    }

    private fun initiateListeners(dialog: Dialog) {
        positiveFeedbackLayout.setOnClickListener { onPositiveFeedbackClicked(it) }
        negativeFeedbackLayout.setOnClickListener {
            onNegativeFeedbackClicked(it)
        }
        notSureFeedbackLayout.setOnClickListener { onNotSureFeedbackClicked(it) }
        dialog.setOnCancelListener { dialog -> onCancelListener(dialog) }
    }

    fun show(): FeedBackDialog {
        val dialog = Dialog(context, R.style.FeedbackDialog_Theme_Dialog)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.review_dialog_base)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
            val height = (context.resources.displayMetrics.heightPixels * 0.50).toInt()
            dialog.window?.setLayout(width, height)
        }

        initiateAllViews(dialog)
        initiateListeners(dialog)

        val layerDrawable = context.resources.getDrawable(R.drawable.reviewdialog_round_icon) as LayerDrawable
        val gradientDrawable = layerDrawable.findDrawableByLayerId(R.id.round_background) as GradientDrawable
        gradientDrawable.setColor(Color.parseColor("#FFFFFF"))
        layerDrawable.setDrawableByLayerId(R.id.round_background, gradientDrawable)

        val drawable = context.resources.getDrawable(this.mIcon)
        val wrappedDrawable = DrawableCompat.wrap(drawable)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable.mutate(), context.resources.getColor(mIconColor))
        } else {
            drawable.setColorFilter(context.resources.getColor(mIconColor), PorterDuff.Mode.SRC_IN)
        }


        layerDrawable.setDrawableByLayerId(R.id.drawable_image, drawable)

        titleImageView.setImageDrawable(layerDrawable)
        titleTextView.text = context.getString(this.mTitle)
        descriptionTextView.text = context.getString(this.mDescription)
        reviewQuestionTextView.text = context.getString(this.mReviewQuestion)

        positiveFeedbackTextView.setText(this.mPositiveFeedbackText)
        positiveFeedbackIconView.setImageResource(this.mPositiveFeedbackIcon)
        positiveFeedbackIconView.setColorFilter(context.resources.getColor(mIconColor))

        negativeFeedbackTextView.setText(this.mNegativeFeedbackText)
        negativeFeedbackIconView.setImageResource(this.mNegativeFeedbackIcon)
        negativeFeedbackIconView.setColorFilter(context.resources.getColor(mIconColor))

        notSureFeedbackTextView.setText(this.mnotSureFeedbackText)
        notSureFeedbackIconView.setImageResource(this.mnotSureFeedbackIcon)
        notSureFeedbackIconView.setColorFilter(context.resources.getColor(mIconColor))

        feedbackBodyLayout.setBackgroundResource(this.mBackgroundColor)

        dialog.show()
        this.dialog = dialog
        return this
    }


    fun setIcon(mIcon: Int): FeedBackDialog {
        this.mIcon = mIcon
        return this
    }


    fun setTitle(mTitle: Int): FeedBackDialog {
        this.mTitle = mTitle
        return this
    }


    fun setDescription(mDescription: Int): FeedBackDialog {
        this.mDescription = mDescription
        return this
    }


    fun setPositiveFeedbackText(@StringRes mPositiveFeedbackText: Int): FeedBackDialog {
        this.mPositiveFeedbackText = mPositiveFeedbackText
        return this
    }


    fun setPositiveFeedbackIcon(@DrawableRes mPositiveFeedbackIcon: Int): FeedBackDialog {
        this.mPositiveFeedbackIcon = mPositiveFeedbackIcon
        return this
    }


    fun setNegativeFeedbackText(@StringRes mNegativeFeedbackText: Int): FeedBackDialog {
        this.mNegativeFeedbackText = mNegativeFeedbackText
        return this
    }

    fun setNegativeFeedbackIcon(@DrawableRes mNegativeFeedbackIcon: Int): FeedBackDialog {
        this.mNegativeFeedbackIcon = mNegativeFeedbackIcon
        return this
    }

    fun setNotSureFeedbackText(@StringRes mnotSureFeedbackText: Int): FeedBackDialog {
        this.mnotSureFeedbackText = mnotSureFeedbackText
        return this
    }

    fun setNotSureFeedbackIcon(@DrawableRes mnotSureFeedbackIcon: Int): FeedBackDialog {
        this.mnotSureFeedbackIcon = mnotSureFeedbackIcon
        return this
    }

    fun setBackgroundColor(@ColorRes mBackgroundColor: Int): FeedBackDialog {
        this.mBackgroundColor = mBackgroundColor
        return this
    }

    fun setIconColor(@ColorRes mIconColor: Int): FeedBackDialog {
        this.mIconColor = mIconColor
        return this
    }

    fun setReviewQuestion(mReviewQuestion: Int): FeedBackDialog {
        this.mReviewQuestion = mReviewQuestion
        return this
    }

    fun setOnPositiveClickListener(positiveActionListener: (Dialog) -> Unit) {
        this.positiveActionListener = positiveActionListener
    }

    fun setOnConstructiveClickListener(constructiveActionListener: (Dialog, String) -> Unit) {
        this.constructiveActionListener = constructiveActionListener
    }

    fun setOnNeutralClickListener(neutralActionListener: (Dialog) -> Unit) {
        this.neutralActionListener = neutralActionListener
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun onPositiveFeedbackClicked(view: View) {
        positiveActionListener?.invoke(dialog)
    }

    private fun onNegativeFeedbackClicked(view: View) {
        layoutActions.visibility = View.GONE
        layoutConstructiveCriticism.visibility = View.VISIBLE
    }

    private fun onConstructiveFeedbackClick(view: View) {
        val suggestion = etConstructiveCriticism.text.toString()
        constructiveActionListener?.invoke(dialog, suggestion)
    }

    private fun onNotSureFeedbackClicked(view: View) {
        neutralActionListener?.invoke(dialog)
    }

    private fun onCancelListener(dialog: DialogInterface) {


    }
}
