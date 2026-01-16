package com.reportnami.claro.ui.screens.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.reportnami.claro.ui.screens.productlist.ProductListScreen
import com.reportnami.claro.ui.screens.productdetail.ProductDetailScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppFlowIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testCompleteAppFlow() {
        // Test complete app flow from login to product details
        var loginSuccess = false
        var registerClicked = false
        var productClicked = false
        var settingsClicked = false
        var backClicked = false

        // Start with login screen
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { loginSuccess = true },
                onNavigateToRegister = { registerClicked = true }
            )
        }

        // Verify login screen
        composeTestRule
            .onNodeWithText("Welcome Back")
            .assertExists()

        // Navigate to register
        composeTestRule
            .onNodeWithText("Don't have an account? Register")
            .performClick()

        assert(registerClicked)

        // Now show register screen
        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { loginSuccess = true },
                onNavigateToLogin = { }
            )
        }

        // Verify register screen
        composeTestRule
            .onNodeWithText("Create Account")
            .assertExists()

        // Fill registration form
        composeTestRule
            .onNodeWithText("Name")
            .performTextInput("Test User")

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("password123")

        // Click register
        composeTestRule
            .onNodeWithText("Register")
            .performClick()

        // After successful login, show product list
        composeTestRule.setContent {
            ProductListScreen(
                onProductClick = { productClicked = true },
                onOpenSettings = { settingsClicked = true }
            )
        }

        // Verify product list
        composeTestRule
            .onNodeWithText("Search products...")
            .assertExists()

        // Click on settings
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        assert(settingsClicked)

        // Show product detail screen
        composeTestRule.setContent {
            ProductDetailScreen(
                productId = 1L,
                onBack = { backClicked = true },
                onOpenSettings = { settingsClicked = true },
                onOpenReviews = { },
                onAddReview = { }
            )
        }

        // Verify product detail
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertExists()

        // Click back
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun testSearchAndFilterFlow() {
        // Test search and filter functionality
        var productClicked = false
        var settingsClicked = false

        composeTestRule.setContent {
            ProductListScreen(
                onProductClick = { productClicked = true },
                onOpenSettings = { settingsClicked = true }
            )
        }

        // Enter search query
        composeTestRule
            .onNodeWithText("Search products...")
            .performTextInput("iPhone")

        // Click filter button
        composeTestRule
            .onNodeWithContentDescription("Filter")
            .performClick()

        // Verify filter options appear
        composeTestRule
            .onNodeWithText("Electronics")
            .assertExists()

        // Select category
        composeTestRule
            .onNodeWithText("Electronics")
            .performClick()

        // Apply filter
        composeTestRule
            .onNodeWithText("Apply")
            .performClick()

        // Verify search and filter are applied
        // In real test, you'd verify the filtered results
    }

    @Test
    fun testProductDetailInteractions() {
        var backClicked = false
        var reviewsClicked = false
        var addReviewClicked = false

        composeTestRule.setContent {
            ProductDetailScreen(
                productId = 1L,
                onBack = { backClicked = true },
                onOpenSettings = { },
                onOpenReviews = { reviewsClicked = true },
                onAddReview = { addReviewClicked = true }
            )
        }

        // Wait for content to load
        Thread.sleep(2000)

        // Click on reviews section
        composeTestRule
            .onNodeWithText("See All")
            .performClick()

        assert(reviewsClicked)

        // Click add review button
        composeTestRule
            .onNodeWithContentDescription("Add review")
            .performClick()

        assert(addReviewClicked)

        // Click back
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun testErrorHandlingFlow() {
        // Test error handling throughout the app
        var loginSuccess = false
        var registerClicked = false

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { loginSuccess = true },
                onNavigateToRegister = { registerClicked = true }
            )
        }

        // Try to login with empty fields
        composeTestRule
            .onNodeWithText("Login")
            .performClick()

        // Should show error
        composeTestRule
            .onNodeWithText("Email and password cannot be empty")
            .assertExists()

        // Navigate to register
        composeTestRule
            .onNodeWithText("Don't have an account? Register")
            .performClick()

        // Try to register with short password
        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { },
                onNavigateToLogin = { }
            )
        }

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("123")

        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("123")

        composeTestRule
            .onNodeWithText("Register")
            .performClick()

        // Should show password validation error
        composeTestRule
            .onNodeWithText("Password must be at least 6 characters")
            .assertExists()
    }
}
