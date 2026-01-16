package com.reportnami.claro.ui.screens.reviews

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reviewsScreen_displaysCorrectly() {
        composeTestRule.setContent {
            ReviewsScreen(
                productId = 1L,
                onBack = { },
                onAddReview = { }
            )
        }
        
        // Verify reviews title is displayed
        composeTestRule
            .onNodeWithText("Reviews")
            .assertExists()
    }
    
    @Test
    fun reviewsScreen_addReviewButtonWorks() {
        var addReviewClicked = false
        
        composeTestRule.setContent {
            ReviewsScreen(
                productId = 1L,
                onBack = { },
                onAddReview = { addReviewClicked = true }
            )
        }
        
        // Find and click add review button
        composeTestRule
            .onNodeWithContentDescription("Add review")
            .performClick()
        
        // Verify callback was triggered
        assert(addReviewClicked)
    }
    
    @Test
    fun reviewsScreen_backButtonWorks() {
        var backClicked = false
        
        composeTestRule.setContent {
            ReviewsScreen(
                productId = 1L,
                onBack = { backClicked = true },
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
