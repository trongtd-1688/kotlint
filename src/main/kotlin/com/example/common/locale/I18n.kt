
package com.example.common.locale

import java.text.MessageFormat
import java.util.*

object I18n {
    fun t(key: String, locale: Locale = Locale.JAPANESE, args: Array<String> = arrayOf()): String {
        return try {
            MessageFormat.format(ResourceBundle.getBundle("I18n", locale).getString(key), *args)
        } catch (e: MissingResourceException) {
            return "Translation missing key <\"$key\">"
        }
    }
}
