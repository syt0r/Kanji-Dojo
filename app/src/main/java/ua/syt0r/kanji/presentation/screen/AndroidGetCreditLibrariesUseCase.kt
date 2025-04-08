package ua.syt0r.kanji.presentation.screen

import android.content.Context
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withJson
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

class AndroidGetCreditLibrariesUseCase(
    private val context: Context
) : GetCreditLibrariesUseCase {

    override suspend fun invoke(): Libs {
        return Libs.Builder().withJson(context, R.raw.aboutlibraries).build()
    }

}