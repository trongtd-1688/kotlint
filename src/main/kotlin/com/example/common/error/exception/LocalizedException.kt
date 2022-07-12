
package com.example.common.error.exception

import java.util.*

object Messages {
    fun getMessageForLocale(messageKey: String): String {
        return ResourceBundle.getBundle("messages", Locale.JAPANESE).getString(messageKey)
    }
}
class LocalizedException(
    private val exceptionKey: String,
    private val keyCode: String = "default"
) {
    fun getLocalizedMessage(): String {
        return Messages.getMessageForLocale("api_error.$exceptionKey.$keyCode.message")
    }

    fun getLocalizedCode(): String {
        return Messages.getMessageForLocale("api_error.$exceptionKey.$keyCode.code")
    }
}

fun String.toSnakeCase() = replace(humps, "_").lowercase()
private val humps = "(?<=.)(?=\\p{Upper})".toRegex()
