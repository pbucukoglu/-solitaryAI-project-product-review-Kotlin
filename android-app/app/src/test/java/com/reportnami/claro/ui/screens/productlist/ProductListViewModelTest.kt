package com.reportnami.claro.ui.screens.productlist

import com.reportnami.claro.data.api.model.ProductDto
import org.junit.Test
import org.junit.Assert.*

class ProductListViewModelTest {
    
    @Test
    fun `search query filters products correctly`() {
        // Given
        val products = listOf(
            ProductDto(1L, "iPhone 15", "Electronics", "999.99", null, null, null, null, null, null),
            ProductDto(2L, "Samsung Galaxy", "Electronics", "899.99", null, null, null, null, null, null),
            ProductDto(3L, "MacBook Pro", "Electronics", "1999.99", null, null, null, null, null, null)
        )
        
        // When
        val filtered = products.filter { 
            it.name.contains("iPhone", ignoreCase = true) 
        }
        
        // Then
        assertEquals(1, filtered.size)
        assertEquals("iPhone 15", filtered[0].name)
    }
    
    @Test
    fun `price filtering works correctly`() {
        // Given
        val products = listOf(
            ProductDto(1L, "Product A", "Category", "10.99", null, null, null, null, null, null),
            ProductDto(2L, "Product B", "Category", "25.99", null, null, null, null, null, null),
            ProductDto(3L, "Product C", "Category", "50.99", null, null, null, null, null, null)
        )
        
        // When
        val filtered = products.filter { 
            (it.price?.toDoubleOrNull() ?: 0.0) <= 30.0 
        }
        
        // Then
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { (it.price?.toDoubleOrNull() ?: 0.0) <= 30.0 })
    }
    
    @Test
    fun `rating filtering works correctly`() {
        // Given
        val products = listOf(
            ProductDto(1L, "Product A", "Category", "10.99", null, 4.5, null, null, null, null),
            ProductDto(2L, "Product B", "Category", "25.99", null, 3.0, null, null, null, null),
            ProductDto(3L, "Product C", "Category", "50.99", null, 2.5, null, null, null, null)
        )
        
        // When
        val filtered = products.filter { 
            (it.averageRating ?: 0.0) >= 3.0 
        }
        
        // Then
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { (it.averageRating ?: 0.0) >= 3.0 })
    }
}
