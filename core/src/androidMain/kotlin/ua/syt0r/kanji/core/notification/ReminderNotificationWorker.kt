package ua.syt0r.kanji.core.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.syt0r.kanji.core.logger.Logger

class ReminderNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val useCase: ReminderNotificationContract.HandleScheduledActionUseCase by inject()

    override suspend fun doWork(): Result {
        Logger.d("Reminder Notification Worker")
        useCase.handle()
        return Result.success()
    }

}