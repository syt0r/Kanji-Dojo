package ua.syt0r.kanji.presentation.screen.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import ua.syt0r.kanji.presentation.KanjiDojoApp

class GooglePlayMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KanjiDojoApp()
        }
    }

}