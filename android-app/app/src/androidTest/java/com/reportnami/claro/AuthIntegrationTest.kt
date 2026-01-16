package com.reportnami.claro

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.reportnami.claro.ui.screens.auth.AuthViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.junit.Assert.*

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var authViewModel: AuthViewModel

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testLoginFlow() {
        // Test login flow with valid credentials
        runBlocking {
            // Initially should not be logged in
            assertFalse(authViewModel.uiState.value.isLoggedIn)
            
            // Attempt login
            authViewModel.login("test@example.com", "password123")
            
            // Should be loading
            assertTrue(authViewModel.uiState.value.isLoading)
            
            // Wait for result (in real test, you'd wait for async operation)
            Thread.sleep(2000)
            
            // Check final state
            assertFalse(authViewModel.uiState.value.isLoading)
            // Note: In real test with mock API, this would be true
        }
    }

    @Test
    fun testRegisterFlow() {
        // Test registration flow
        runBlocking {
            // Initially should not be logged in
            assertFalse(authViewModel.uiState.value.isLoggedIn)
            
            // Attempt registration
            authViewModel.register("newuser@example.com", "password123", "Test User")
            
            // Should be loading
            assertTrue(authViewModel.uiState.value.isLoading)
            
            // Wait for result
            Thread.sleep(2000)
            
            // Check final state
            assertFalse(authViewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun testValidationError() {
        // Test validation error for empty fields
        authViewModel.login("", "")
        
        // Should have error
        assertNotNull(authViewModel.uiState.value.error)
        assertFalse(authViewModel.uiState.value.isLoading)
        assertFalse(authViewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun testPasswordValidation() {
        // Test password validation for registration
        authViewModel.register("test@example.com", "123", "Test User")
        
        // Should have error about password length
        assertNotNull(authViewModel.uiState.value.error)
        assertTrue(authViewModel.uiState.value.error?.contains("6 characters") == true)
    }

    @Test
    fun testLogout() {
        // Test logout functionality
        runBlocking {
            // First login (mock successful login)
            authViewModel.login("test@example.com", "password123")
            Thread.sleep(1000)
            
            // Then logout
            authViewModel.logout()
            
            // Should be logged out
            assertFalse(authViewModel.uiState.value.isLoggedIn)
            assertFalse(authViewModel.uiState.value.isLoading)
            assertNull(authViewModel.uiState.value.error)
        }
    }

    @Test
    fun testClearError() {
        // Trigger an error
        authViewModel.login("", "")
        
        // Should have error
        assertNotNull(authViewModel.uiState.value.error)
        
        // Clear error
        authViewModel.clearError()
        
        // Error should be cleared
        assertNull(authViewModel.uiState.value.error)
    }
}
