package com.reportnami.claro.ui.screens.productlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productListScreen_displaysCorrectly() {
        composeTestRule.setContent {
            ProductListScreen(
                onProductClick = { },
                onOpenSettings = { }
            )
        }
        
        // Verify search functionality is available
        composeTestRule
            .onNodeWithText("Search products...")
            .assertExists()
    }
    
    @Test
    fun productListScreen_settingsButtonWorks() {
        var settingsClicked = false
        
        composeTestRule.setContent {
            ProductListScreen(
                onProductClick = { },
                onOpenSettings = { settingsClicked = true }
            )
        }
        
        // Find and click settings button
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()
        
        // Verify callback was triggered
        assert(settingsClicked)
    }
}
