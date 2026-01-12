package com.reportnami.claro.ui.screens.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long?,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenReviews: (Long) -> Unit,
    onAddReview: (Long) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        val id = productId ?: return@LaunchedEffect
        viewModel.load(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            when {
                productId == null -> {
                    Text(
                        text = "Product not found",
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                state.isLoading -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    val p = state.product
                    if (p == null) {
                        Text(text = "Product not found", color = MaterialTheme.colorScheme.error)
                        return@Column
                    }

                    Text(text = p.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = p.description ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Category: ${p.category ?: ""}")
                    Text(text = "Price: ${p.price ?: ""}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "⭐ ${p.averageRating ?: "-"} (${p.reviewCount ?: 0})")

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { onOpenReviews(p.id) }) {
                            Text("Reviews")
                        }
                        Button(onClick = { onAddReview(p.id) }) {
                            Text("Add Review")
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(text = "Smart Review Insights", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val s = state.summary
                    if (s == null) {
                        Text(text = "No summary")
                    } else {
                        if (!s.takeaway.isNullOrBlank()) {
                            Text(text = s.takeaway!!)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Section(title = "Pros", items = s.pros)
                        Section(title = "Cons", items = s.cons)
                        Section(title = "Top Topics", items = s.topTopics)
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, items: List<String>?) {
    if (items.isNullOrEmpty()) return
    Text(text = title, style = MaterialTheme.typography.titleSmall)
    Spacer(modifier = Modifier.height(6.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { Text(text = "• $it") }
    }
    Spacer(modifier = Modifier.height(12.dp))
}
