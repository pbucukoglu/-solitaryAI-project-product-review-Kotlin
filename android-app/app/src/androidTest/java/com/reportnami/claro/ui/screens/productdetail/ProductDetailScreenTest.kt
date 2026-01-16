package com.reportnami.claro.ui.screens.productdetail

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productDetailScreen_displaysCorrectly() {
        composeTestRule.setContent {
            ProductDetailScreen(
                productId = 1L,
                onBack = { },
                onOpenSettings = { },
                onOpenReviews = { },
                onAddReview = { }
            )
        }
        
        // Verify loading state or content is displayed
        // This is a basic test to ensure the screen renders
        composeTestRule
            .onNodeWithText("Loading...")
            .assertExists()
    }
    
    @Test
    fun productDetailScreen_backButtonWorks() {
        var backClicked = false
        
        composeTestRule.setContent {
            ProductDetailScreen(
                productId = 1L,
                onBack = { backClicked = true },
                onOpenSettings = { },
                onOpenReviews = { },
                onAddReview = { }
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
