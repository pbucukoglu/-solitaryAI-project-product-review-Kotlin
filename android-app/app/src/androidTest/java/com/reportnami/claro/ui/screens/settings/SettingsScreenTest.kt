package com.reportnami.claro.ui.screens.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysCorrectly() {
        composeTestRule.setContent {
            SettingsScreen(
                onBack = { }
            )
        }
        
        // Verify settings title is displayed
        composeTestRule
            .onNodeWithText("Settings")
            .assertExists()
    }
    
    @Test
    fun settingsScreen_backButtonWorks() {
        var backClicked = false
        
        composeTestRule.setContent {
            SettingsScreen(
                onBack = { backClicked = true }
            )
        }
        
        // Find and click back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
        
        // Verify callback was triggered
        assert(backClicked)
    }
}
