package za.co.ishmaelt.examplecompe

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object Injector {

    private val context: Context
        get() = App.applicationContext

    val analyticsService: AnalyticsService
        get() = AnalyticsServiceImpl(context = context)

    val remoteConfigService: RemoteConfigService
        get() = RemoteConfigServiceImpl(context = context)

}

interface AnalyticsService {

    fun logEvent(name: String, parameters: Map<String, String>)
}

class AnalyticsServiceImpl(
    context: Context
) : AnalyticsService {

    private val firebaseAnalytics by lazy(LazyThreadSafetyMode.NONE) {
        FirebaseAnalytics.getInstance(context)
    }

    override fun logEvent(name: String, parameters: Map<String, String>) {
        val bundle = Bundle().apply {
            for (parameter in parameters) {
                putString(parameter.key, parameter.value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}

interface RemoteConfigService {
    fun fetchAndActiveRemoteConfig()
    fun getDebugToken()
    fun getString(key: String): String
}

class RemoteConfigServiceImpl(
    private val context: Context
) : RemoteConfigService {

    private val firebaseRemoteConfig: FirebaseRemoteConfig
        get() = FirebaseRemoteConfig.getInstance()

    private val firebaseInstallations: FirebaseInstallations
        get() = FirebaseInstallations.getInstance()

    init {
        if(isDebug()) {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        }
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    override fun fetchAndActiveRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(ContextCompat.getMainExecutor(context)) {
                if (it.isSuccessful) {
                    Log.d(javaClass.name, "Success: ${it.result}")
                }
            }
    }

    override fun getDebugToken() {
        firebaseInstallations.getToken(false)
            .addOnCompleteListener(ContextCompat.getMainExecutor(context)) {
                if (it.isSuccessful) {
                    Log.d(javaClass.name, "getToken Success: ${it.result.token}")
                }
            }
    }

    override fun getString(key: String): String {
        return firebaseRemoteConfig.getString(key)
    }
}

fun isDebug() = BuildConfig.DEBUG