package ua.syt0r.kanji.ui.screen.screen.kanji_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.ui.common.AutoBreakRow
import ua.syt0r.kanji.ui.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji.ui.theme.secondary

@Composable
fun KanjiInfoScreen(
    viewModel: KanjiInfoScreenContract.ViewModel = hiltViewModel<KanjiInfoViewModel>(),
    kanji: String
) {

    val state = viewModel.state.observeAsState()
    viewModel.loadKanjiInfo(kanji)

    when (val value = state.value!!) {
        State.Init, State.Loading -> {
            CircularProgressIndicator()
        }
        is State.Loaded -> {

            Column {

                Text(text = kanji, fontSize = 64.sp)

                AutoBreakRow(Modifier.fillMaxSize()) {
                    value.meanings.forEach {
                        Text(
                            text = it.toUpperCase(),
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