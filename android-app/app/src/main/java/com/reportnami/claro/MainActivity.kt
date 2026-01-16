package com.reportnami.claro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.reportnami.claro.data.preferences.SettingsPreferences
import com.reportnami.claro.ui.navigation.AppRoot
import com.reportnami.claro.ui.components.OfflineBanner
import com.reportnami.claro.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up system UI visibility
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val isDarkMode by settingsPreferences.darkMode.collectAsState(initial = false)
            val isSystemDarkMode = isSystemInDarkTheme()
            
            // Use user preference or system theme
            val useDarkTheme = isDarkMode
            
            MyApplicationTheme(
                darkTheme = useDarkTheme
            ) {
                // Set system bar colors based on theme
                val window = this@MainActivity.window
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}
