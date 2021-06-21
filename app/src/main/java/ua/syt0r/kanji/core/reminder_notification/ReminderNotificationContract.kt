package ua.syt0r.kanji.core.reminder_notification

interface ReminderNotificationContract {

    interface Scheduler {
        fun schedule()
    }

    interface Dispatcher {
        fun dispatch()
    }

}