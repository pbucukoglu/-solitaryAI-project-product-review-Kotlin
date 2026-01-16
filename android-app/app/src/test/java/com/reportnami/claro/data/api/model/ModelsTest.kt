package com.reportnami.claro.data.api.model

import org.junit.Test
import org.junit.Assert.*

class ModelsTest {
    
    @Test
    fun `product dto creation works correctly`() {
        // Given
        val product = ProductDto(
            id = 1L,
            name = "Test Product",
            category = "Electronics",
            price = "999.99",
            imageUrls = listOf("image1.jpg", "image2.jpg"),
            averageRating = 4.5,
            reviewCount = 100L,
            description = "Test Description",
            createdAt = "2023-01-01T00:00:00Z",
            updatedAt = "2023-01-01T00:00:00Z"
        )
        
        // Then
        assertEquals(1L, product.id)
        assertEquals("Test Product", product.name)
        assertEquals("Electronics", product.category)
        assertEquals("999.99", product.price)
        assertEquals(2, product.imageUrls?.size)
        assertEquals(4.5, product.averageRating)
        assertEquals(100L, product.reviewCount)
        assertEquals("Test Description", product.description)
    }
    
    @Test
    fun `review dto creation works correctly`() {
        // Given
        val review = ReviewDto(
            id = 1L,
            productId = 1L,
            comment = "Great product!",
            rating = 5,
            reviewerName = "John Doe",
            deviceId = "device123",
            helpfulCount = 10L,
            createdAt = "2023-01-01T00:00:00Z"
        )
        
        // Then
        assertEquals(1L, review.id)
        assertEquals(1L, review.productId)
        assertEquals("Great product!", review.comment)
        assertEquals(5, review.rating)
        assertEquals("John Doe", review.reviewerName)
        assertEquals("device123", review.deviceId)
        assertEquals(10L, review.helpfulCount)
        assertEquals("2023-01-01T00:00:00Z", review.createdAt)
    }
    
    @Test
    fun `create review request dto creation works correctly`() {
        // Given
        val request = CreateReviewRequestDto(
            productId = 1L,
            comment = "Amazing product!",
            rating = 5,
            reviewerName = "Jane Doe",
            deviceId = "device456"
        )
        
        // Then
        assertEquals(1L, request.productId)
        assertEquals("Amazing product!", request.comment)
        assertEquals(5, request.rating)
        assertEquals("Jane Doe", request.reviewerName)
        assertEquals("device456", request.deviceId)
    }
}
