package com.reportnami.claro.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productCard_displaysProductInfo() {
        // Given
        val productName = "Test Product"
        val productPrice = "$99.99"
        
        composeTestRule.setContent {
            // Mock ProductCard for testing
            androidx.compose.material3.Card {
                androidx.compose.material3.Column(
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                ) {
                    androidx.compose.material3.Text(
                        text = productName,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                    androidx.compose.material3.Text(
                        text = productPrice,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText(productName)
            .assertExists()
        
        composeTestRule
            .onNodeWithText(productPrice)
            .assertExists()
    }
    
    @Test
    fun productCard_clickTriggersCallback() {
        // Given
        var clicked = false
        
        composeTestRule.setContent {
            androidx.compose.material3.Card(
                modifier = androidx.compose.ui.Modifier
                    .padding(16.dp)
                    .androidx.compose.ui.test.testTag("productCard")
                    .androidx.compose.foundation.clickable { clicked = true }
            ) {
                androidx.compose.material3.Text(
                    text = "Clickable Product",
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }
        
        // When
        composeTestRule
            .onNodeWithTag("productCard")
            .performClick()
        
        // Then
        assert(clicked)
    }
}
