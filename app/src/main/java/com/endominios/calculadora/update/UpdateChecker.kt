package com.endominios.calculadora.update

import com.endominios.calculadora.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

/**
 * Consulta Firebase Remote Config para saber si la versión instalada quedó
 * por debajo de la mínima permitida. El valor "min_version_code" se controla
 * desde la consola de Firebase sin necesidad de publicar una nueva build:
 * subirlo por encima del versionCode actual fuerza la actualización.
 */
object UpdateChecker {

    private const val KEY_MIN_VERSION_CODE = "min_version_code"
    private const val KEY_FORCE_UPDATE_MESSAGE = "force_update_message"
    private const val MIN_FETCH_INTERVAL_SECONDS = 3600L

    private val remoteConfig by lazy {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings { minimumFetchIntervalInSeconds = MIN_FETCH_INTERVAL_SECONDS }
            )
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    /**
     * @param currentVersionCode versionCode de la build instalada.
     * @param onResult se invoca con `true` si se debe bloquear la app hasta
     * actualizar, junto con el mensaje a mostrar. Si la consulta a Firebase
     * falla (sin red, etc.) se usan los valores por defecto locales, por lo
     * que nunca bloquea a un usuario sin conexión por error de red.
     */
    fun checkForForcedUpdate(
        currentVersionCode: Long,
        onResult: (required: Boolean, message: String) -> Unit
    ) {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val minVersionCode = remoteConfig.getLong(KEY_MIN_VERSION_CODE)
            val message = remoteConfig.getString(KEY_FORCE_UPDATE_MESSAGE)
            onResult(currentVersionCode < minVersionCode, message)
        }
    }
}
