

package com.example.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ParameterValue
import com.google.firebase.remoteconfig.Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.util.Base64

@Service
class AppFireBaseRemoteConfig(
    @Value("\${firebase.service-account}")
    private val credentials: String? = null
) {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    init {
        val serviceAccount = ByteArrayInputStream(Base64.getDecoder().decode(credentials))
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        remoteConfig = FirebaseRemoteConfig.getInstance(FirebaseApp.initializeApp(options))
    }

    fun getValueString(parameter: String): String? {
        val template: Template = remoteConfig.templateAsync.get()
        val value = (template.parameters[parameter]?.defaultValue as? ParameterValue.Explicit)?.value
        return value
    }
}
