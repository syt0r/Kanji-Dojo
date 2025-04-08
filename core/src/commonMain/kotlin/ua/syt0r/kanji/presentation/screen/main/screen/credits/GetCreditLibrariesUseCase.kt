package ua.syt0r.kanji.presentation.screen.main.screen.credits

import com.mikepenz.aboutlibraries.Libs

interface GetCreditLibrariesUseCase {
    suspend operator fun invoke(): Libs
}