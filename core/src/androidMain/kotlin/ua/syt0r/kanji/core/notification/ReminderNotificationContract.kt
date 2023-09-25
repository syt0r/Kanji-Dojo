package ua.syt0r.kanji.core.notification

import kotlinx.datetime.LocalTime

interface ReminderNotificationContract {

    interface Scheduler {
        fun scheduleNotification(time: LocalTime)
        fun unscheduleNotification()
    }

    interface Manager {
        fun showNotification(learn: Int, review: Int)
        fun dismissNotification()
    }

    interface HandleScheduledActionUseCase {
        suspend fun handle()
    }

}

data class ReminderNotificationConfiguration(
    val enabled: Boolean,
    val time: LocalTime
)