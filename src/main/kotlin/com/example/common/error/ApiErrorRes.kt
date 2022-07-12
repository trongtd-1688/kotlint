
package com.example.common.error

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus
import java.time.Instant
import java.time.temporal.ChronoField

data class ApiErrorRes(
    @JsonIgnore
    var status: HttpStatus,

    var errorTime: Instant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0),

    var errors: List<ErrorObject>,

    var debugInfo: String = ""
)

data class ErrorObject(
    var errorCode: String = "",

    var errorMessage: String = "",
)
