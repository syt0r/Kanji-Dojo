package ua.syt0r.kanji.core.notification

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.time.TimeUtils
import java.util.concurrent.TimeUnit

class ReminderNotificationScheduler(
    private val workManger: WorkManager,
    private val timeUtils: TimeUtils
) : ReminderNotificationContract.Scheduler {

    companion object {
        private const val WORK_NAME = "handle_reminder"
    }

    override fun scheduleNotification(time: LocalTime) {
        val now = timeUtils.getCurrentTime()
        val desiredScheduleTime = now.date.atTime(time)
            .takeIf { it > now }
            ?: now.date
                .plus(1, DateTimeUnit.DAY)
                .atTime(time)

        Logger.d("Desired time [$desiredScheduleTime]")

        val timeZone = TimeZone.currentSystemDefault()
        val delay = desiredScheduleTime.toInstant(timeZone) - now.toInstant(timeZone)

        workManger.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                .setInitialDelay(delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
                .build()
        )
    }

    override fun unscheduleNotification() {
        workManger.cancelUniqueWork(WORK_NAME)
    }

}