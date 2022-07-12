
package com.example.extension

import java.time.Instant
import java.time.temporal.ChronoField

object InstantExtension {
    fun fromString(
        time: String?,
        defaultTime: Instant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0),
    ): Instant {
        return try {
            Instant.parse(time).with(ChronoField.NANO_OF_SECOND, 0)
        } catch (e: Exception) {
            defaultTime
        }
    }

    fun fromInstant(time: Instant = Instant.now()): Instant {
        return time.with(ChronoField.NANO_OF_SECOND, 0)
    }
}
