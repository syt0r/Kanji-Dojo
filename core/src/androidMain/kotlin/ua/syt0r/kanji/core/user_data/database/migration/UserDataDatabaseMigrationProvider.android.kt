package ua.syt0r.kanji.core.user_data.database.migration

import app.cash.sqldelight.db.SqlDriver

actual fun SqlDriver.ensureMigrationTransactionEnabled() {
    // No-op, on Android migration callback already has transaction enabled
}