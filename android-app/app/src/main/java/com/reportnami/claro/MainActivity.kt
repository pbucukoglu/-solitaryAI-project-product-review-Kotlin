package com.reportnami.claro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.reportnami.claro.ui.navigation.AppRoot
import com.reportnami.claro.ui.components.OfflineBanner
import com.reportnami.claro.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.reportnami.claro.data.network.NetworkMonitor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppRoot()
                        OfflineBanner(isOffline = !isOnline)
                    }
                }
            }
        }
    }
}