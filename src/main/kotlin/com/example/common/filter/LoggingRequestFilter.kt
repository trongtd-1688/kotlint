
package com.example.common.filter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class LoggingRequestFilter : Filter {
    private val log: Logger = LoggerFactory.getLogger(LoggingRequestFilter::class.java)

    companion object {
        const val REQUEST_ID = "request-id"
        const val START_TIME = "start-time"
    }

    private var requestId: String? = null

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val multiReadRequest = MultiReadHttpServletRequest(request as HttpServletRequest?)
        this.requestId = UUID.randomUUID().toString()
        multiReadRequest.setAttribute(REQUEST_ID, requestId)
        multiReadRequest.setAttribute(START_TIME, System.currentTimeMillis())
        logRequest(multiReadRequest)

        chain?.doFilter(multiReadRequest, response)
    }

    private fun logRequest(request: MultiReadHttpServletRequest) {
        val data = StringBuilder()
        data.append("LOGGING REQUEST => ")
            .append("[REQUEST-ID]: ").append(requestId)
            .append(" - [PATH]: ").append(request.requestURI)
            .append(" - [METHOD]: ").append(request.method)
            .append(" - [QUERIES]: ").append(request.queryString)
            .append(" - [HEADERS]: ")

        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement() as String
            val value = request.getHeader(key)
            data.append(key).append(": ").append(value).append(" ; ")
        }

        val bodyInput = getStringFromInputStream(request.inputStream)
        data.append("\n - [BODY]: ").append(bodyInput)
        log.info(data.toString())
    }

    private fun getStringFromInputStream(input: ServletInputStream): String {
        val stringBuilder = StringBuilder()
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(InputStreamReader(input))
            val charBuffer = CharArray(128)
            var bytesRead = -1
            while (bufferedReader.read(charBuffer).also { bytesRead = it } > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead)
            }
        } catch (ex: IOException) {
            throw ex
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (ex: IOException) {
                    throw ex
                }
            }
        }

        return stringBuilder.toString()
    }
}
