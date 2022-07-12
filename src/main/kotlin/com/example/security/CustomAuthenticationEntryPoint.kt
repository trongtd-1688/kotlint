
package com.example.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    companion object {
        const val ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
        const val APPLICATION_JSON = "application/json;charset=UTF-8"
        const val ALL = "*"
    }

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALL)
        response?.contentType = APPLICATION_JSON
        response?.status = HttpStatus.UNAUTHORIZED.value()
    }
}
