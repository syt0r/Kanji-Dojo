package ua.syt0r.kanji.presentation.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import dagger.hilt.android.AndroidEntryPoint
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            KanjiDojoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }

        }

    }

}
