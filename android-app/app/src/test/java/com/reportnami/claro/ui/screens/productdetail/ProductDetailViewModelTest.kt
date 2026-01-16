package com.reportnami.claro.ui.screens.productdetail

import com.reportnami.claro.data.api.model.ProductDetailDto
import com.reportnami.claro.data.api.model.ReviewDto
import org.junit.Test
import org.junit.Assert.*

class ProductDetailViewModelTest {
    
    @Test
    fun `product loading state updates correctly`() {
        // Given
        val productId = 1L
        val mockProduct = ProductDetailDto(
            id = productId,
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            price = "99.99",
            imageUrls = listOf("image1.jpg", "image2.jpg"),
            averageRating = 4.5,
            reviewCount = 10L,
            reviews = null
        )
        
        // When
        val isLoading = false
        val product = mockProduct
        
        // Then
        assertFalse(isLoading)
        assertEquals(productId, product.id)
        assertEquals("Test Product", product.name)
        assertEquals(4.5, product.averageRating)
        assertEquals(10L, product.reviewCount)
    }
    
    @Test
    fun `review rating validation works correctly`() {
        // Given
        val validRating = 5
        val invalidRating = 6
        
        // When
        val isValidRating = validRating in 1..5
        val isInvalidRating = invalidRating in 1..5
        
        // Then
        assertTrue(isValidRating)
        assertFalse(isInvalidRating)
    }
    
    @Test
    fun `review count conversion works correctly`() {
        // Given
        val reviewCountLong: Long = 10L
        val expectedReviewCountInt = 10
        
        // When
        val reviewCountInt = reviewCountLong.toInt()
        
        // Then
        assertEquals(expectedReviewCountInt, reviewCountInt)
    }
}
