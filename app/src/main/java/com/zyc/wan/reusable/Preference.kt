package com.zyc.wan.reusable

import android.util.Log
import androidx.datastore.preferences.core.*
import com.zyc.wan.App
import com.zyc.wan.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.reflect.KProperty

class Preference<T>(
    val name: String,
    private val default: T
) {
    @Suppress("PrivatePropertyName")
    private val TAG: String = "Preference"

    companion object {
        fun clearPreference() {
            CoroutineScope(Dispatchers.IO).launch {
                App.instance.dataStore.edit { it.clear() }
            }
        }
    }

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        return getDataStorePreferences(name, default)
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>, value: T
    ) {
        putDataStorePreferences(name, value)
    }

    private fun putDataStorePreferences(
        name: String,
        value: T
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            App.instance.dataStore.edit {
                when (value) {
                    is Long -> it[longPreferencesKey(name)] = value
                    is Int -> it[intPreferencesKey(name)] = value
                    is Boolean -> it[booleanPreferencesKey(name)] = value
                    is Float -> it[floatPreferencesKey(name)] = value
                    is String -> it[stringPreferencesKey(name)] = value
                    else -> Log.e(TAG, "detected an unsupported type when saving")
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDataStorePreferences(
        name: String,
        default: T
    ): T {
        return runBlocking {
            App.instance.dataStore.data
                .catch { exception ->
                    // dataStore.data throws an IOException when an error is encountered when reading data
                    if (exception is IOException) {
                        Log.e(TAG, "Error reading preferences.", exception)
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map {
                    it[when (default) {
                        is Long -> longPreferencesKey(name)
                        is Int -> intPreferencesKey(name)
                        is Boolean -> booleanPreferencesKey(name)
                        is Float -> floatPreferencesKey(name)
                        else -> stringPreferencesKey(name)
                    }] ?: default
                }.first() as T
        }
    }
}
