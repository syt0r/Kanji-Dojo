package ua.syt0r.kanji.core.user_data.database.migration

import app.cash.sqldelight.db.SqlDriver

actual fun SqlDriver.ensureMigrationTransactionEnabled() {
    // TODO remove after sqldelight 2.0.3? release that enables it by default
    if (currentTransaction() == null) newTransaction().value
}