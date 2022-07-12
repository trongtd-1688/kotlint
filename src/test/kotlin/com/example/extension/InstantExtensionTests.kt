
package com.example.extension

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.time.temporal.ChronoField

@ExtendWith(SpringExtension::class)
class InstantExtensionTests {
    private val extension = InstantExtension

    @Test
    fun from_string_with_time_valid_return_time() {
        val time = "2021-02-01T17:32:05Z"
        val result = extension.fromString(time)
        val timeParse = Instant.parse(time)

        Assertions.assertEquals(timeParse, result)
    }

    @Test
    fun from_string_with_time_invalid_return_time_now() {
        val now = Instant.now()
        val time = "time"
        val result = extension.fromString(time, now)

        Assertions.assertEquals(now, result)
    }

    @Test
    fun from_string_with_time_null_return_time_now() {
        val now = Instant.now()
        val time = null
        val result = extension.fromString(time, now)

        Assertions.assertEquals(now, result)
    }

    @Test
    fun from_instant_with_time_valid_return_time() {
        val time = Instant.now()
        val result = extension.fromInstant(time)
        val timeParse = time.with(ChronoField.NANO_OF_SECOND, 0)

        Assertions.assertEquals(timeParse, result)
    }
}
