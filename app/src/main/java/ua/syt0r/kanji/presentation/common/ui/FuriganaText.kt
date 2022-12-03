package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.AppTheme

@Composable
fun FuriganaText(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {

//    textStyle.fontFamily!!.ty

    val px = with(LocalDensity.current) { fontScale * density * 12 }

    val textPaint = Paint().asFrameworkPaint().apply {
        textSize = px
    }

    val furiganaTextPaint = Paint().apply {

    }

    val rect = android.graphics.Rect()
    val text: String = "かんじ"
    textPaint.getTextBounds(text, 0, text.length, rect)

    val inlineContent = mapOf(
        "test" to InlineTextContent(
            Placeholder(
                40.sp,
                (textStyle.lineHeight.value + 12.sp.value).sp,
                PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "かんじ", fontSize = 8.sp, maxLines = 1)
                Text(text = "漢字", fontSize = LocalTextStyle.current.fontSize, maxLines = 1)
            }
        }
    )

    BasicText(
        text = buildAnnotatedString {
            append("This is furigana test 感じ")
            appendInlineContent("test")
            append("and hopefully it works")
        },
        inlineContent = inlineContent
    )

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        FuriganaText(text = "")
    }
}