package com.example.podcastapp.core.ui.utils

import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

fun String.htmlToAnnotatedString(): AnnotatedString {
    // 1. 先用系统 API 把 HTML 字符串转为 Spanned (它会自动处理 <p> <br> 等换行逻辑)
    val spanned = Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)

    return buildAnnotatedString {
        append(spanned.toString())

        // 2. 遍历 Spanned 中的样式区间，映射到 Compose 的 SpanStyle
        val spans = spanned.getSpans(0, spanned.length, Any::class.java)
        spans.forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        android.graphics.Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    }
                }
                is ForegroundColorSpan -> {
                    addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
                }
                is UnderlineSpan -> {
                    addStyle(SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline), start, end)
                }
                // 如果有链接 <a> 标签，可以处理 ClickableText
                is android.text.style.URLSpan -> {
                    addStringAnnotation(tag = "URL", annotation = span.url, start = start, end = end)
                    addStyle(SpanStyle(color = Color.Blue, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline), start, end)
                }
            }
        }
    }
}