package com.stasbar.feedbackdialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.core.graphics.drawable.DrawableCompat
import com.stasbar.feedbackdialog.utils.*
import com.stasbar.feedbackdialog.utils.inferTheme
import com.stasbar.feedbackdialog.utils.resolveColor

typealias DialogCallback = (FeedBackDialog) -> Unit

public class FeedBackDialog(val windowContext: Context) : Dialog(windowContext, inferTheme(windowContext)) {

    /** Returns true if auto dismiss is enabled. */
    var autoDismissEnabled: Boolean = true
        internal set
    var titleFont: Typeface? = null
        internal set
    var bodyFont: Typeface? = null
        internal set
    var buttonFont: Typeface? = null
        internal set
    var cancelOnTouchOutside: Boolean = true
        internal set
    var cornerRadius: Float? = null
        internal set
    @Px
    private var maxWidth: Int? = null

    @ColorRes
    var iconsColor: Int? = null
        internal set

    /** The root layout of the dialog. */
    val view: View

    private val positiveListeners = mutableListOf<DialogCallback>()
    private val negativeListeners = mutableListOf<DialogCallback>()
    private val neutralListeners = mutableListOf<DialogCallback>()
    private val cancelListeners = mutableListOf<DialogCallback>()
    private var constructiveFeedbackListener: ((FeedBackDialog, String) -> Unit)? = null

    lateinit var layoutBody: RelativeLayout

    lateinit var ivTitleIcon: ImageView
    lateinit var tvTitle: TextView
    lateinit var tvDescription: TextView
    lateinit var tvQuestion: TextView


    lateinit var layoutPositive: LinearLayout
    lateinit var layoutNegative: LinearLayout
    lateinit var layoutNeutral: LinearLayout


    lateinit var tvPositive: TextView
    lateinit var tvNegative: TextView
    lateinit var tvNeutral: TextView

    lateinit var ivPositiveIcon: ImageView
    lateinit var ivNegativeIcon: ImageView
    lateinit var ivNeutralIcon: ImageView

    lateinit var etConstructiveFeedback: EditText

    lateinit var layoutConstructiveCriticism: LinearLayout
    lateinit var layoutActions: LinearLayout

    lateinit var btnConstructiveFeedback: Button

    init {
        val layoutInflater = LayoutInflater.from(windowContext)

        val rootView = layoutInflater.inflate(R.layout.review_dialog_base, null, false)
        setContentView(rootView)
        this.view = rootView
        initiateAllViews()

        invalidateBackgroundColorAndRadius()
        initiateListeners()
    }


    private fun initiateAllViews() {
        layoutConstructiveCriticism = view.findViewById(R.id.layoutConstructiveCriticism)
        layoutActions = view.findViewById(R.id.layoutActions)
        ivTitleIcon = view.findViewById(R.id.ivTitleIcon) as ImageView
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvQuestion = view.findViewById(R.id.tvQuestion)


        layoutPositive = view.findViewById(R.id.layoutPositive)
        tvPositive = view.findViewById(R.id.tvPositive)
        ivPositiveIcon = view.findViewById(R.id.ivPositiveIcon) as ImageView

        layoutNegative = view.findViewById(R.id.layoutNegative)
        tvNegative = view.findViewById(R.id.tvNegative)
        ivNegativeIcon = view.findViewById(R.id.ivNegativeIcon) as ImageView

        etConstructiveFeedback = view.findViewById(R.id.etConstructiveFeedback)
        btnConstructiveFeedback = view.findViewById(R.id.btnConstructiveFeedback)

        tvNeutral = view.findViewById(R.id.not_sure_feedback_text)
        layoutNeutral = view.findViewById(R.id.not_sure_feedback_layout)
        ivNeutralIcon = view.findViewById(R.id.not_sure_feedback_icon) as ImageView

        layoutBody = view.findViewById(R.id.layoutBody)


    }

    private fun initiateListeners() {
        layoutPositive.setOnClickListener { onPositiveClicked(it) }
        layoutNegative.setOnClickListener { onNegativeClicked(it) }
        layoutNeutral.setOnClickListener { onNeutralClicked(it) }
        setOnCancelListener { dialog -> onCancelListener(dialog) }
        btnConstructiveFeedback.setOnClickListener { onConstructiveFeedbackClick(it) }
    }

    /**
     * Set icon on the top of dialog
     *
     * @param res The drawable resource to display as the drawable.
     * @param drawable The drawable to display as the drawable.
     */
    fun icon(
            @DrawableRes iconRes: Int? = null,
            icon: Drawable? = null,
            @ColorRes colorRes: Int? = null
    ): FeedBackDialog {
        assertOneSet("icon", icon, iconRes)
        val drawable = resolveDrawable(windowContext, res = iconRes, fallback = icon)
        if (drawable != null) {
            ivTitleIcon.show()
            val layerDrawable = resolveDrawable(windowContext, res = R.drawable.fd_round_icon) as LayerDrawable
            val gradientDrawable = layerDrawable.findDrawableByLayerId(R.id.round_background) as GradientDrawable
            gradientDrawable.setColor(Color.parseColor("#FFFFFF"))
            layerDrawable.setDrawableByLayerId(R.id.round_background, gradientDrawable)

            val color = iconsColor ?: resolveColor(colorRes, fallback = { R.color.colorPrimary })
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(drawable.mutate(), color)
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }


            layerDrawable.setDrawableByLayerId(R.id.drawable_image, drawable)
            ivTitleIcon.setImageDrawable(layerDrawable)
        } else {
            ivTitleIcon.hide()
        }


        return this
    }

    /**
     * Set color of icon on the top of dialog
     *
     * @param res The color resource to display as the drawable.
     * @param drawable The drawable to display as the drawable.
     */
    fun iconsColor(
            @ColorRes colorRes: Int
    ): FeedBackDialog {
        @ColorInt val color = resolveColor(windowContext, res = colorRes)
        iconsColor = color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(ivTitleIcon.drawable.mutate(), color) //TODO should we mutate() here ?
            DrawableCompat.setTint(ivPositiveIcon.drawable.mutate(), color) //TODO should we mutate() here ?
            DrawableCompat.setTint(ivNegativeIcon.drawable.mutate(), color) //TODO should we mutate() here ?
            DrawableCompat.setTint(ivNeutralIcon.drawable.mutate(), color) //TODO should we mutate() here ?
        } else {
            //TODO or maybe color filter is sufficient ?
            ivTitleIcon.drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            ivPositiveIcon.drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            ivNegativeIcon.drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            ivNeutralIcon.drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        return this
    }

    /**
     * Shows a title, or header, at the top of the dialog.
     *
     * @param res The string resource to display as the title.
     * @param text The literal string to display as the title.
     */
    fun title(
            @StringRes res: Int? = null,
            text: String? = null
    ): FeedBackDialog {
        assertOneSet("title", text, res)
        val value = text ?: resolveString(this, res)
        if (value != null) {
            (tvTitle.parent as View).visibility = View.VISIBLE
            tvTitle.show()
            tvTitle.text = value
        } else {
            tvTitle.hide()
        }
        return this
    }

    /**
     * Shows a description, below the title, and above the question.
     *
     * @param res The string resource to display as the message.
     * @param text The literal string to display as the message.
     */
    fun description(
            @StringRes res: Int? = null,
            text: CharSequence? = null
    ): FeedBackDialog {
        assertOneSet("description", text, res)
        val value = text ?: resolveString(this, res)
        if (value != null) {
            (tvDescription.parent as View).visibility = View.VISIBLE
            tvDescription.show()
            tvDescription.text = value
        } else {
            tvDescription.hide()
        }
        return this
    }

    /**
     * Shows a question, below the title and description, and above the action buttons (and checkbox prompt).
     *
     * @param res The string resource to display as the message.
     * @param text The literal string to display as the message.
     */
    fun question(
            @StringRes res: Int? = null,
            text: CharSequence? = null
    ): FeedBackDialog {
        assertOneSet("question", text, res)
        val value = text ?: resolveString(this, res)
        if (value != null) {
            (tvQuestion.parent as View).visibility = View.VISIBLE
            tvQuestion.show()
            tvQuestion.text = value
        } else {
            tvQuestion.hide()
        }
        return this
    }

    /**
     * Shows a positive action button, in the far right at the bottom of the dialog.
     *
     * @param res The string resource to display on the title.
     * @param text The literal string to display on the button.
     * @param iconRes The icon resource to display above text.
     * @param click A listener to invoke when the button is pressed.
     */
    fun positiveButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            @DrawableRes iconRes: Int? = null,
            click: DialogCallback? = null
    ): FeedBackDialog {
        if (click != null) {
            positiveListeners.add(click)
        }

        if (res == null && text == null && layoutPositive.visibility == View.VISIBLE) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }
        assertOneSet("positive", text, res)
        val value = text ?: resolveString(this, res, android.R.string.ok)

        tvPositive.text = value
        resolveDrawable(windowContext, res = iconRes)?.let { drawable ->
            ivPositiveIcon.setImageDrawable(drawable)
            iconsColor?.let { color -> ivPositiveIcon.setColorFilter(color) }
        }
        return this
    }

    /**
     * Shows a negative action button, in the middle at the bottom of the dialog.
     *
     * @param res The string resource to display on the title.
     * @param text The literal string to display on the button.
     * @param iconRes The icon resource to display above text.
     * @param click A listener to invoke when the button is pressed.
     */
    fun negativeButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            @DrawableRes iconRes: Int? = null,
            click: DialogCallback? = null
    ): FeedBackDialog {
        if (click != null) {
            negativeListeners.add(click)
        }

        if (res == null && text == null && layoutPositive.visibility == View.VISIBLE) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }
        assertOneSet("negative", text, res)
        val value = text ?: resolveString(this, res, android.R.string.ok)
        tvNegative.text = value
        resolveDrawable(windowContext, res = iconRes)?.let { drawable ->
            ivPositiveIcon.setImageDrawable(drawable)
            iconsColor?.let { color -> ivPositiveIcon.setColorFilter(color) }
        }
        return this
    }

    var showConstructiveFeedback: Boolean = false
    /**
     * Shows a edit text for entering constructive critics
     * @param callback A callback to invoke when send button is pressed.
     */
    fun constructiveFeedback(
            callback: (FeedBackDialog, String) -> Unit
    ): FeedBackDialog {
        showConstructiveFeedback = true
        constructiveFeedbackListener = callback
        return this
    }

    /**
     * Shows a neutral action button, in the middle at the bottom of the dialog.
     *
     * @param res The string resource to display on the title.
     * @param text The literal string to display on the button.
     * @param iconRes The icon resource to display above text.
     * @param click A listener to invoke when the button is pressed.
     */
    fun neutralButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            @DrawableRes iconRes: Int? = null,
            click: DialogCallback? = null
    ): FeedBackDialog {
        if (click != null) {
            neutralListeners.add(click)
        }

        if (res == null && text == null && layoutPositive.visibility == View.VISIBLE) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }
        assertOneSet("neutral", text, res)
        val value = text ?: resolveString(this, res, android.R.string.ok)

        tvNeutral.text = value
        resolveDrawable(windowContext, res = iconRes)?.let { drawable ->
            ivPositiveIcon.setImageDrawable(drawable)
            iconsColor?.let { color -> ivPositiveIcon.setColorFilter(color) }
        }
        return this
    }


    /**
     * Sets the corner radius for the dialog, or the rounding of the corners. Dialogs can choose
     * how they want to handle this, e.g. bottom sheets will only round the top left and right
     * corners.
     */
    fun cornerRadius(
            literalDp: Float? = null,
            @DimenRes res: Int? = null
    ): FeedBackDialog {
        assertOneSet("cornerRadius", literalDp, res)
        this.cornerRadius = if (res != null) {
            windowContext.resources.getDimension(res)
        } else {
            val displayMetrics = windowContext.resources.displayMetrics
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, literalDp!!, displayMetrics)
        }
        invalidateBackgroundColorAndRadius()
        return this
    }


    /** Opens the dialog. */
    override fun show() {
        setWindowConstraints()
        super.show()
    }

    private fun setWindowConstraints() {
        if (maxWidth == 0) {
            // Postpone
            return
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val lp = WindowManager.LayoutParams().apply {
            copyFrom(window.attributes)
            width = (windowContext.resources.displayMetrics.widthPixels * 0.90).toInt()
            height = (windowContext.resources.displayMetrics.heightPixels * 0.50).toInt()
        }
        window.attributes = lp
    }

    private fun onPositiveClicked(view: View) {
        positiveListeners.invokeAll(this)
    }

    private fun onNegativeClicked(view: View) {
        negativeListeners.invokeAll(this)
        if (showConstructiveFeedback) {
            showConstructiveFeedbackLayout()
        }
    }

    private fun showConstructiveFeedbackLayout() {
        layoutActions.hide()
        layoutConstructiveCriticism.show()
        //TODO animate
    }

    private fun hideConstructiveFeedbackLayout() {
        etConstructiveFeedback.text.clear()
        layoutConstructiveCriticism.hide()
        layoutActions.show()
        //TODO animate
    }

    private fun onNeutralClicked(view: View) {
        neutralListeners.invokeAll(this)
    }

    private fun onConstructiveFeedbackClick(view: View) {
        val suggestion = etConstructiveFeedback.text.toString()
        constructiveFeedbackListener?.invoke(this, suggestion)
        hideConstructiveFeedbackLayout()

    }

    private fun onCancelListener(dialog: DialogInterface) {
        cancelListeners.invokeAll(this)
    }

    private fun invalidateBackgroundColorAndRadius() {
        val backgroundColor = resolveColor(attr = R.attr.colorBackgroundFloating)
        val cornerRounding =
                cornerRadius ?: resolveDimen(windowContext, attr = R.attr.fd_corner_radius)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutBody.background = GradientDrawable().apply {
            cornerRadius = cornerRounding
            setColor(backgroundColor)
        }

    }
}

