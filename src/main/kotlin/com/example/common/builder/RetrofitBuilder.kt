
package com.example.common.builder

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder {
    private var connectionTimeout = com.example.Constants.HttpClient.CONNECT_TIMEOUT
    private var writeTimeout = com.example.Constants.HttpClient.WRITE_TIMEOUT
    private var readTimeout = com.example.Constants.HttpClient.READ_TIMEOUT
    private var okHttpClientBuilder: OkHttpClient.Builder? = null
    private var interceptors = mutableListOf<Interceptor>()
    private var logEnable: Boolean = false
    private var logging = HttpLoggingInterceptor()
    private var baseUrl = ""
    private var isGsonEnable = true
    private var connectionSubscriptionKey = ""

    /**
     * Customize time out
     * @param connectionTimeout timeout for connection OK Http client
     * @param writeTimeout timeout for write data
     * @param readTimeout timeout for read data
     */
    fun setTimeout(
        connectionTimeout: Long = com.example.Constants.HttpClient.CONNECT_TIMEOUT,
        writeTimeout: Long = com.example.Constants.HttpClient.WRITE_TIMEOUT,
        readTimeout: Long = com.example.Constants.HttpClient.READ_TIMEOUT
    ): RetrofitBuilder {
        this.connectionTimeout = connectionTimeout
        this.writeTimeout = writeTimeout
        this.readTimeout = readTimeout
        return this
    }

    /**
     * User customize ok http client
     * @param okHttpClientBuilder
     */
    fun setOkHttpClientBuilder(okHttpClientBuilder: OkHttpClient.Builder): RetrofitBuilder {
        this.okHttpClientBuilder = okHttpClientBuilder
        return this
    }

    /**
     * add custom interceptor for ok http client
     * @param interceptor is a interceptor for ok http client
     */
    fun addInterceptors(vararg interceptor: Interceptor): RetrofitBuilder {
        interceptors.addAll(interceptor)
        return this
    }

    /**
     * Customize show or hide logging
     * @param enable is status for logs
     */
    fun loggingEnable(enable: Boolean): RetrofitBuilder {
        this.logEnable = enable
        return this
    }

    /**
     * Customize base url
     * @param baseUrl is base url for ok http client
     */
    fun setBaseURL(baseUrl: String): RetrofitBuilder {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * Customize enable or disable gson
     * @param enable is status for gson
     */
    fun enableGson(enable: Boolean): RetrofitBuilder {
        this.isGsonEnable = enable
        return this
    }

    /**
     * Customize set header connection subscription key
     * @param connectionSubscriptionKey is for azure api management
     */
    fun setConnectionSubscriptionKey(connectionSubscriptionKey: String): RetrofitBuilder {
        this.connectionSubscriptionKey = connectionSubscriptionKey
        return this
    }

    /**
     * Make a Retrofit
     */
    fun build(): Retrofit {
        val clientBuilder = okHttpClientBuilder ?: OkHttpClient.Builder().apply {
            connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            writeTimeout(writeTimeout, TimeUnit.SECONDS)
            readTimeout(readTimeout, TimeUnit.SECONDS)
            if (logEnable) {
                addInterceptor(
                    logging.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
            if (connectionSubscriptionKey.isNotBlank()) {
                addInterceptor { chain ->
                    chain.request()
                        .newBuilder()
                        .addHeader("ocp-apim-subscription-key", connectionSubscriptionKey)
                        .addHeader("ocp-apim-trace", "true")
                        .build()
                        .let(chain::proceed)
                }
            }
            interceptors.forEach { addInterceptor(it) }
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .apply {
                if (isGsonEnable) {
                    val gson = GsonBuilder().setLenient()
                        .enableComplexMapKeySerialization()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                    addConverterFactory(GsonConverterFactory.create(gson))
                }
            }
            .build()
    }
}
