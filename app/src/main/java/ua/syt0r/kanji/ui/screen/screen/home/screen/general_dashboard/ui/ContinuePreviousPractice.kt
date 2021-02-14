package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import ua.syt0r.kanji.ui.theme.red

@Composable
fun ContinuePreviousPractice(
    practiceSetName: String
) {

    Column(
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {

        Text(
            text = "Continue were you left",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = practiceSetName,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        LinearProgressIndicator(
            color = red,
            progress = 0.7f
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = red
            )
        ) {

            Text(text = "Learn", color = Color.White)

        }


    }

}

@Preview(showSystemUi = true)
@Composable
fun ContinuePreviousPracticePreview() {

    KanjiDojoTheme {
        ContinuePreviousPractice(
            practiceSetName = "Custom 1"
        )
    }

}