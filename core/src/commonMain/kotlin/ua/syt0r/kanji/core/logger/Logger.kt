package ua.syt0r.kanji.core.logger

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Logger : KoinComponent {

    private val configuration by inject<LoggerConfiguration>()

    fun d(message: String) {
        if (configuration.isEnabled) platformLogMessage(message)
    }

    fun logMethod() {
        if (configuration.isEnabled) platformLogMethod()
    }

}

expect fun platformLogMessage(message: String)
expect fun platformLogMethod()

data class LoggerConfiguration(
    val isEnabled: Boolean
)