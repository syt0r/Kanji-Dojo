package ua.syt0r.kanji

object PlatformFeature {

    var supported: Boolean = true
        private set

    fun disableSupport() {
        supported = false
    }

}