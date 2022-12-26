package ua.syt0r.kanji.core.analytics

import androidx.compose.runtime.compositionLocalOf


val LocalAnalytics = compositionLocalOf<AnalyticsManager> {
    error("Analytics manager is not set")
}

