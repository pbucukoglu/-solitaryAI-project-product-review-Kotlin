package com.reportnami.claro.data.repository

import com.reportnami.claro.data.api.model.ProductDto
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

class ProductRepositoryTest {
    
    @Test
    fun `product price parsing works correctly`() {
        // Given
        val product = ProductDto(
            id = 1L,
            name = "Test Product",
            category = "Test",
            price = "99.99",
            imageUrls = null,
            averageRating = null,
            reviewCount = null,
            description = null,
            createdAt = null,
            updatedAt = null
        )
        
        // When
        val parsedPrice = product.price?.toDoubleOrNull()
        
        // Then
        assertEquals(99.99, parsedPrice, 0.001)
    }
    
    @Test
    fun `product price handles invalid format`() {
        // Given
        val product = ProductDto(
            id = 1L,
            name = "Test Product",
            category = "Test",
            price = "invalid_price",
            imageUrls = null,
            averageRating = null,
            reviewCount = null,
            description = null,
            createdAt = null,
            updatedAt = null
        )
        
        // When
        val parsedPrice = product.price?.toDoubleOrNull()
        
        // Then
        assertNull(parsedPrice)
    }
    
    @Test
    fun `product rating null handling`() {
        // Given
        val product = ProductDto(
            id = 1L,
            name = "Test Product",
            category = "Test",
            price = "99.99",
            imageUrls = null,
            averageRating = null,
            reviewCount = null,
            description = null,
            createdAt = null,
            updatedAt = null
        )
        
        // When
        val rating = product.averageRating ?: 0.0
        
        // Then
        assertEquals(0.0, rating, 0.001)
    }
}
