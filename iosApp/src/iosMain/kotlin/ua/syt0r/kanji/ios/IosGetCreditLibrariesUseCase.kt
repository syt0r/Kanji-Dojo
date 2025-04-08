package ua.syt0r.kanji.ios

import com.mikepenz.aboutlibraries.Libs
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

object IosGetCreditLibrariesUseCase : GetCreditLibrariesUseCase {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun invoke(): Libs {
        val json = Res.readBytes("files/aboutlibraries.json").decodeToString()
        return Libs.Builder().withJson(json).build()
    }

}