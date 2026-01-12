package com.reportnami.claro.ui.screens.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reportnami.claro.data.api.model.ReviewDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    productId: Long?,
    onBack: () -> Unit,
    onAddReview: (Long) -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        val id = productId ?: return@LaunchedEffect
        viewModel.load(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    val id = productId
                    if (id != null) {
                        IconButton(onClick = { onAddReview(id) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                            )
                        }
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            if (productId == null) {
                Text(text = "Invalid product selection", color = MaterialTheme.colorScheme.error)
                return@Column
            }

            when {
                state.isLoading && state.items.isEmpty() -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && state.items.isEmpty() -> {
                    Text(text = state.error ?: "Failed to load reviews", color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.items) { r ->
                            ReviewItem(
                                item = r,
                                onHelpful = { viewModel.toggleHelpful(r.id) },
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (state.hasMore) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isLoading,
                                    onClick = { viewModel.loadMore() },
                                ) {
                                    Text(if (state.isLoading) "Loading..." else "Load more")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(
    item: ReviewDto,
    onHelpful: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = item.reviewerName ?: "Anonymous", style = MaterialTheme.typography.titleSmall)
        Text(text = "Rating: ${item.rating ?: "-"}")
        if (!item.comment.isNullOrBlank()) {
            Text(text = item.comment!!)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = item.createdAt ?: "")
            Button(onClick = onHelpful) {
                Text(text = "Helpful (${item.helpfulCount ?: 0})")
            }
        }
    }
}
