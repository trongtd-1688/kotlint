
package com.example.common.error.exception

abstract class ApiException(
    keyCode: String = "default"
) : RuntimeException() {
    var code: String
    final override var message: String

    init {
        val klassName = javaClass.simpleName.replace("Exception", "").toSnakeCase()
        val localizedException = LocalizedException(klassName, keyCode)
        this.code = localizedException.getLocalizedCode()
        this.message = localizedException.getLocalizedMessage()
    }
}
