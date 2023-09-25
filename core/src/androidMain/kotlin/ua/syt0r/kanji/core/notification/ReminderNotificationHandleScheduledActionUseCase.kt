package ua.syt0r.kanji.core.notification

import android.app.ActivityManager
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

class ReminderNotificationHandleScheduledActionUseCase(
    private val activityManager: ActivityManager,
    private val appStateManager: AppStateManager,
    private val notificationManager: ReminderNotificationContract.Manager,
    private val repository: UserPreferencesRepository,
    private val scheduler: ReminderNotificationContract.Scheduler
) : ReminderNotificationContract.HandleScheduledActionUseCase {

    override suspend fun handle() {
        showNotification()
        scheduleNextNotification()
    }

    private suspend fun showNotification() {
        val isInForeground = activityManager.appTasks.isNotEmpty()
        if (isInForeground) return

        val appState = appStateManager.appStateFlow
            .filter { !it.isLoading }
            .take(1)
            .first()
            .lastData!!

        val learn = appState.run { dailyGoalConfiguration.learnLimit - dailyProgress.studied }
        val review = appState.run { dailyGoalConfiguration.reviewLimit - dailyProgress.reviewed }

        if (learn > 0 || review > 0) {
            notificationManager.showNotification(learn, review)
        }
    }

    private suspend fun scheduleNextNotification() {
        val time = repository.getReminderTime()!!
        scheduler.scheduleNotification(time)
    }

}