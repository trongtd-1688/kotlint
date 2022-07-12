
package com.example.common.error

import com.example.common.error.exception.ApiException
import com.example.common.error.exception.LocalizedException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import java.text.MessageFormat
import java.util.stream.Collectors

@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    @Value("\${spring.profiles.active}")
    val activeProfile: String? = null

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {

        pageNotFoundLogger.warn(ex.message)

        ex.supportedHttpMethods?.let { headers.allow = it }

        val localizedException = LocalizedException("http_request_method_not_supported")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = localizedException.getLocalizedMessage()
        )
        val apiError = ApiErrorRes(
            HttpStatus.METHOD_NOT_ALLOWED,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        ex.supportedMediaTypes.let { headers.accept = it }

        val localizedException = LocalizedException("http_media_type_not_supported")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = localizedException.getLocalizedMessage()
        )
        val apiError = ApiErrorRes(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleHttpMediaTypeNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        ex.supportedMediaTypes.let { headers.accept = it }

        val localizedException = LocalizedException("http_media_type_not_supported")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = localizedException.getLocalizedMessage()
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("missing_path_variable")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(
                localizedException.getLocalizedMessage(),
                ex.variableName,
                ex.parameter.nestedParameterType.simpleName
            )
        )
        val apiError = ApiErrorRes(
            HttpStatus.INTERNAL_SERVER_ERROR,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors: MutableList<ErrorObject> =
            ex.bindingResult.fieldErrors.stream().filter { it.code != null && it.defaultMessage?.isNotEmpty() == true }
                .map { fieldError ->
                    val localizedException = LocalizedException("method_argument_not_valid", fieldError.code.toString())
                    val message = MessageFormat.format(fieldError.defaultMessage.toString(), fieldError.field)
                    ErrorObject(
                        errorCode = localizedException.getLocalizedCode(),
                        errorMessage = message
                    )
                }.collect(Collectors.toList())
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = errors,
            debugInfo = getDebugMessage(ex)
        )
        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("missing_parameter")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(localizedException.getLocalizedMessage(), ex.parameterName)
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val cause = ex.cause
        var localizedException = LocalizedException("servlet_request_binding")
        var code = localizedException.getLocalizedCode()
        var message = localizedException.getLocalizedMessage()

        when (cause) {
            is MissingMatrixVariableException -> {
                localizedException = LocalizedException("missing_matrix_variable")
                code = localizedException.getLocalizedCode()
                message = MessageFormat.format(
                    localizedException.getLocalizedMessage(),
                    cause.variableName,
                    cause.parameter.nestedParameterType.simpleName
                )
            }
            is MissingRequestCookieException -> {
                localizedException = LocalizedException("missing_request_cookie")
                code = localizedException.getLocalizedCode()
                message = MessageFormat.format(
                    localizedException.getLocalizedMessage(),
                    cause.cookieName,
                    cause.parameter.nestedParameterType.simpleName
                )
            }
            is MissingRequestHeaderException -> {
                localizedException = LocalizedException("missing_request_header")
                code = localizedException.getLocalizedCode()
                message = MessageFormat.format(
                    localizedException.getLocalizedMessage(),
                    cause.headerName,
                    cause.parameter.nestedParameterType.simpleName
                )
            }
        }

        val error = ErrorObject(
            errorCode = code,
            errorMessage = message
        )

        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )
        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleConversionNotSupported(
        ex: ConversionNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("conversion_not_supported")
        val klassName = ex.value?.javaClass?.name ?: ex.javaClass.name
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(
                localizedException.getLocalizedMessage(),
                klassName,
                ex.requiredType
            )
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("conversion_not_supported")
        val klassName = ex.value?.javaClass?.name ?: ex.javaClass.name
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(
                localizedException.getLocalizedMessage(),
                klassName,
                ex.requiredType
            )
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val cause = ex.cause
        val localizedException = LocalizedException("http_message_not_readable")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = localizedException.getLocalizedMessage()
        )

        if (cause is MissingKotlinParameterException) {
            val violations = createMissingKotlinParameterViolation(cause)
            error.errorMessage = violations
        }

        val apiError = ApiErrorRes(
            HttpStatus.UNPROCESSABLE_ENTITY,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("http_message_not_writable")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(localizedException.getLocalizedMessage(), ex.message).trim()
        )
        val apiError = ApiErrorRes(
            HttpStatus.INTERNAL_SERVER_ERROR,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("missing_servlet_request_part")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(localizedException.getLocalizedMessage(), ex.requestPartName).trim()
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("no_handler_found")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(localizedException.getLocalizedMessage(), ex.httpMethod, ex.requestURL)
                .trim()
        )
        val apiError = ApiErrorRes(
            HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleAsyncRequestTimeoutException(
        ex: AsyncRequestTimeoutException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val localizedException = LocalizedException("async_request_timeout")
        val error = ErrorObject(
            errorCode = localizedException.getLocalizedCode(),
            errorMessage = MessageFormat.format(localizedException.getLocalizedMessage())
        )
        val apiError = ApiErrorRes(
            HttpStatus.SERVICE_UNAVAILABLE,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    @ExceptionHandler(ApiException::class)
    protected fun handleApiException(
        ex: ApiException
    ): ResponseEntity<Any> {
        val error = ErrorObject(
            errorCode = ex.code,
            errorMessage = ex.message
        )
        val apiError = ApiErrorRes(
            status = HttpStatus.BAD_REQUEST,
            errors = listOf(error),
            debugInfo = getDebugMessage(ex)
        )
        return ResponseEntity(apiError, apiError.status)
    }

    private fun createMissingKotlinParameterViolation(cause: MissingKotlinParameterException): String {
        val name = cause.path.fold("") { jsonPath, ref ->
            val suffix = when {
                ref.index > -1 -> "[${ref.index}]"
                else -> ".${ref.fieldName}"
            }
            (jsonPath + suffix).removePrefix(".")
        }

        val localizedException = LocalizedException("missing_parameter")
        return MessageFormat.format(localizedException.getLocalizedMessage(), name)
    }

    private fun getDebugMessage(ex: Exception): String {
        if (activeProfile == "dev") {
            val sw = StringWriter()
            ex.printStackTrace(PrintWriter(sw))

            return sw.toString()
        }

        return ""
    }
}
