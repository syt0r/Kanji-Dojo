package ua.syt0r.kanji.desktopApp

import com.mikepenz.aboutlibraries.Libs
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

object JvmGetCreditLibrariesUseCase : GetCreditLibrariesUseCase {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun invoke(): Libs {
        val json = Res.readBytes("files/aboutlibraries.json").toString(Charsets.UTF_8)
        return Libs.Builder().withJson(json).build()
    }

}