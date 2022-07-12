
package com.example.common.filter

import org.apache.tomcat.util.http.fileupload.IOUtils
import java.io.*
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class MultiReadHttpServletRequest(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArrayOutputStream? = null

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (cachedBytes == null) cacheInputStream()

        return CustomServletInputStream(cachedBytes?.toByteArray())
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    private fun cacheInputStream() {
        cachedBytes = ByteArrayOutputStream()
        IOUtils.copy(super.getInputStream(), cachedBytes)
    }

    private inner class CustomServletInputStream constructor(byte: ByteArray?) : ServletInputStream() {
        private val byteArrayInput: ByteArrayInputStream = ByteArrayInputStream(byte)

        override fun read(): Int {
            return byteArrayInput.read()
        }

        override fun isFinished(): Boolean {
            return byteArrayInput.available() == 0
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(listener: ReadListener?) {
            throw RuntimeException("Not implemented")
        }
    }
}
