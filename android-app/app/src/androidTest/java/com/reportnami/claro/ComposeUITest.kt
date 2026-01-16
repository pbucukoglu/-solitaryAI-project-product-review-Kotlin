package com.reportnami.claro

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProductCardDisplay() {
        composeTestRule.setContent {
            // Test content would go here
            // For now, just a simple text test
            androidx.compose.material3.Text("Test Product")
        }
        
        composeTestRule
            .onNodeWithText("Test Product")
            .assertExists()
    }
    
    @Test
    fun testButtonClick() {
        var clicked = false
        
        composeTestRule.setContent {
            androidx.compose.material3.Button(
                onClick = { clicked = true }
            ) {
                androidx.compose.material3.Text("Click me")
            }
        }
        
        composeTestRule
            .onNodeWithText("Click me")
            .performClick()
        
        assert(clicked)
    }
}
