package io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Components

object Logger {

    enum class LogLevel {
        DEBUG, INFO, WARNING, ERROR, SECURITY
    }

    fun logDebug(message: Any?, title: String = "") {
        log(LogLevel.DEBUG, message, title)
    }

    fun logInfo(message: Any?, title: String = "") {
        log(LogLevel.INFO, message, title)
    }

    fun logWarning(message: Any?, title: String = "") {
        log(LogLevel.WARNING, message, title)
    }

    fun logError(message: Any?, title: String = "") {
        log(LogLevel.ERROR, message, title)
    }

    fun logSecurity(message: Any?, title: String = "") {
        log(LogLevel.SECURITY, message, title)
    }

    fun logException(throwable: Throwable, title: String = "") {
        val message = "${throwable::class.simpleName}: ${throwable.message}\n${throwable.stackTraceToString()}"
        log(LogLevel.ERROR, message, title)
    }

    private fun log(level: LogLevel, message: Any?, title: String) {
        val timestamp = DateTimer.getNowYmdHis()
        val prefix = if (title.isNotEmpty()) "[$title] " else ""
        println("[$timestamp] [${level.name}] $prefix$message")
    }
}
