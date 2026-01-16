package com.reportnami.claro.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "claro_prefs")

@Singleton
class DeviceIdRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val key = stringPreferencesKey("device_id")

    suspend fun getOrCreate(): String {
        val existing = context.dataStore.data
            .map { it[key] }
            .first()

        if (!existing.isNullOrBlank()) return existing

        val created = UUID.randomUUID().toString()
        context.dataStore.edit { prefs ->
            prefs[key] = created
        }
        return created
    }
}
