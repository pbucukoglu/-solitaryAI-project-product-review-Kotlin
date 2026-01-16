package com.reportnami.claro

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.reportnami.claro.ui.screens.auth.LoginScreen
import com.reportnami.claro.ui.screens.auth.RegisterScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthUITest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testLoginScreenDisplays() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { },
                onNavigateToRegister = { }
            )
        }

        // Verify login screen elements
        composeTestRule
            .onNodeWithText("Welcome Back")
            .assertExists()

        composeTestRule
            .onNodeWithText("Email")
            .assertExists()

        composeTestRule
            .onNodeWithText("Password")
            .assertExists()

        composeTestRule
            .onNodeWithText("Login")
            .assertExists()

        composeTestRule
            .onNodeWithText("Don't have an account? Register")
            .assertExists()
    }

    @Test
    fun testLoginScreenInput() {
        var loginClicked = false
        var registerClicked = false

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { loginClicked = true },
                onNavigateToRegister = { registerClicked = true }
            )
        }

        // Enter email
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        // Enter password
        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        // Click login button
        composeTestRule
            .onNodeWithText("Login")
            .performClick()

        // Verify login was attempted
        // Note: In real test, you'd verify the ViewModel was called
    }

    @Test
    fun testLoginScreenNavigation() {
        var registerClicked = false

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { },
                onNavigateToRegister = { registerClicked = true }
            )
        }

        // Click register link
        composeTestRule
            .onNodeWithText("Don't have an account? Register")
            .performClick()

        // Verify navigation was triggered
        assert(registerClicked)
    }

    @Test
    fun testRegisterScreenDisplays() {
        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { },
                onNavigateToLogin = { }
            )
        }

        // Verify register screen elements
        composeTestRule
            .onNodeWithText("Create Account")
            .assertExists()

        composeTestRule
            .onNodeWithText("Name")
            .assertExists()

        composeTestRule
            .onNodeWithText("Email")
            .assertExists()

        composeTestRule
            .onNodeWithText("Password")
            .assertExists()

        composeTestRule
            .onNodeWithText("Confirm Password")
            .assertExists()

        composeTestRule
            .onNodeWithText("Register")
            .assertExists()

        composeTestRule
            .onNodeWithText("Already have an account? Login")
            .assertExists()
    }

    @Test
    fun testRegisterScreenInput() {
        var registerClicked = false
        var loginClicked = false

        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { registerClicked = true },
                onNavigateToLogin = { loginClicked = true }
            )
        }

        // Enter name
        composeTestRule
            .onNodeWithText("Name")
            .performTextInput("Test User")

        // Enter email
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        // Enter password
        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        // Enter confirm password
        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("password123")

        // Click register button
        composeTestRule
            .onNodeWithText("Register")
            .performClick()

        // Verify registration was attempted
        // Note: In real test, you'd verify the ViewModel was called
    }

    @Test
    fun testRegisterScreenNavigation() {
        var loginClicked = false

        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { },
                onNavigateToLogin = { loginClicked = true }
            )
        }

        // Click login link
        composeTestRule
            .onNodeWithText("Already have an account? Login")
            .performClick()

        // Verify navigation was triggered
        assert(loginClicked)
    }

    @Test
    fun testRegisterButtonDisabledWithMismatchedPasswords() {
        composeTestRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { },
                onNavigateToLogin = { }
            )
        }

        // Enter different passwords
        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("different456")

        // Register button should be disabled
        composeTestRule
            .onNodeWithText("Register")
            .assertIsNotEnabled()
    }

    @Test
    fun testLoginButtonDisabledWithEmptyFields() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { },
                onNavigateToRegister = { }
            )
        }

        // Login button should be disabled initially
        composeTestRule
            .onNodeWithText("Login")
            .assertIsNotEnabled()

        // Enter email only
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        // Still should be disabled
        composeTestRule
            .onNodeWithText("Login")
            .assertIsNotEnabled()

        // Enter password
        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        // Now should be enabled
        composeTestRule
            .onNodeWithText("Login")
            .assertIsEnabled()
    }
}
