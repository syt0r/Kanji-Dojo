package ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract

@Composable
fun KanjiInfoScreenUI(
    state: KanjiInfoScreenContract.State
) {

    when (state) {

        KanjiInfoScreenContract.State.Init,
        KanjiInfoScreenContract.State.Loading -> {
            CircularProgressIndicator()
        }

        is KanjiInfoScreenContract.State.Loaded -> {

            Column {

                Text(text = state.kanji, fontSize = 64.sp)

                AutoBreakRow(Modifier.fillMaxSize()) {
                    state.meanings.forEach {
                        Text(
                            text = it.uppercase(),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(secondary)
                                .padding(vertical = 2.dp, horizontal = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


            }
        }

    }

}