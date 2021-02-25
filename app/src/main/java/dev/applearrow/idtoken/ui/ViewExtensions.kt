package dev.applearrow.idtoken.ui

/**
 * [View] extension functions
 */
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.InputFilter
import android.text.Spannable
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar


/**
 * Contains definitions for bindings that can be used in our layout files. For example app:booleanText can be bound to a boolean in order to
 * set the visibility of that widget in the xml.
 */

@BindingAdapter("booleanText")
fun bindBooleanText(view: TextView, booleanText: Boolean?) {
    view.text = if (booleanText == true) {
        "YES"
    } else {
        "NO"
    }
}

@BindingAdapter("isVisible")
fun isVisible(view: View, visible: Boolean?) {
    view.visibility = if (visible == true) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun View.getParentActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun TextView.setMaxLength(maxLength: Int) {
    val originalFilters = filters.filter { it !is InputFilter.LengthFilter }
    val newFilters = mutableListOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    newFilters.addAll(originalFilters)
    this.filters = newFilters.toTypedArray()
}

fun TextView.stylePartOfText(allText: String, textToStyle: String, style: Int) {
    if (TextUtils.isEmpty(allText) || TextUtils.isEmpty(textToStyle)) {
        return
    }

    setText(allText, TextView.BufferType.SPANNABLE)

    val span = text as Spannable
    val index = allText.indexOf(textToStyle)
    if (index >= 0) {
        span.setSpan(
            StyleSpan(style),
            index,
            textToStyle.length + index,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

fun <T : View> T.withUniqueId(): T {
    this.id = View.generateViewId()
    return this
}

fun View.showError(error: String) {
    Snackbar.make(this, error, Snackbar.LENGTH_LONG).show()
}

fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}

fun TextView.setStyle(style: Int): TextView {
    if (Build.VERSION.SDK_INT < 23) {
        @Suppress("DEPRECATION")
        setTextAppearance(context, style)
    } else {
        setTextAppearance(style)
    }
    return this
}

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}