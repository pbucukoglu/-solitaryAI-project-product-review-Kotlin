package com.reportnami.claro.data.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Global singleton DataStore extension
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "claro_prefs")
