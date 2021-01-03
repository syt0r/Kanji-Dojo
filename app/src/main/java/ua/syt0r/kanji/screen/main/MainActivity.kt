package ua.syt0r.kanji.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.setContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.syt0r.kanji.screen.theme.KanjiDrawerTheme
import ua.syt0r.kanji_model.TmpKanjiData

object DataSource {
    lateinit var data: List<TmpKanjiData>
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = assets.open("data.json").bufferedReader().use { it.readText() }
        DataSource.data = Gson().fromJson(json, object : TypeToken<List<TmpKanjiData>>() {}.type)


        setContent {
            KanjiDrawerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }

    }
}