package se.bylenny.quiz.extensions

import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.ITALIC
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan

enum class Style {
    Bold,
    Italic,
    Large,
    Small,
    Muted,
    Highlighted
}

fun SpannableStringBuilder.append(
    sequence: CharSequence,
    vararg styles: Style
): SpannableStringBuilder {
    val start = this.length
    val end = start + sequence.length
    append(sequence)
    styles.mapNotNull<Style, CharacterStyle> { style ->
        when (style) {
            Style.Bold -> StyleSpan(BOLD)
            Style.Italic -> StyleSpan(ITALIC)
            Style.Muted -> ForegroundColorSpan(Color.parseColor("#1E88E5"))
            Style.Highlighted -> ForegroundColorSpan(Color.parseColor("#BF360C"))
            Style.Large -> RelativeSizeSpan(2.0f)
            Style.Small -> RelativeSizeSpan(0.7f)
        }
    }.forEach { span ->
        setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

fun SpannableStringBuilder.appendLineBreak(): SpannableStringBuilder {
    appendln()
    return this
}
