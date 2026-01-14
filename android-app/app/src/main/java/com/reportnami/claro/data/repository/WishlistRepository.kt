package com.reportnami.claro.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.wishlistDataStore by preferencesDataStore(name = "claro_prefs")

class WishlistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val key = stringSetPreferencesKey("wishlist_ids")

    val favoriteIds: Flow<Set<Long>> = context.wishlistDataStore.data
        .map { prefs ->
            val raw = prefs[key].orEmpty()
            raw.mapNotNull { it.toLongOrNull() }.toSet()
        }

    suspend fun toggle(productId: Long): Set<Long> {
        var next: Set<Long> = emptySet()
        context.wishlistDataStore.edit { prefs ->
            val current = prefs[key].orEmpty().toMutableSet()
            val id = productId.toString()
            if (current.contains(id)) current.remove(id) else current.add(id)
            prefs[key] = current
            next = current.mapNotNull { it.toLongOrNull() }.toSet()
        }
        return next
    }
}
