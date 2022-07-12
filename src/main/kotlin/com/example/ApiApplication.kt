
package com.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
class ApiApplication {
    @Value("\${app.tz}")
    val timeZone: String? = null

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timeZone)))
        Locale.setDefault(Locale.JAPAN)
    }
}

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
