
package com.example.security

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@Component
class AuthenticationFilter : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authentication = UsernamePasswordAuthenticationToken(null, null, null)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (ex: Exception) {
            log.debug("Could not set user authentication in security context: {}", ex.message)
        }
        filterChain.doFilter(request, response)
    }
}
