package com.reportnami.claro.ui.screens.productdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.reportnami.claro.ui.theme.ClaroTheme

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
    val extra = ClaroTheme.colorsExtra

    LaunchedEffect(productId) {
        val id = productId ?: return@LaunchedEffect
        viewModel.load(id)
    }

    Scaffold(
        floatingActionButton = {
            val p = state.product
            if (p != null) {
                FloatingActionButton(
                    onClick = { onAddReview(p.id) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ) {
                    Text(text = "＋", fontWeight = FontWeight.Black)
                }
            }
        },
    ) { padding ->
        val scrollState = rememberScrollState()

        val headerMaxHeight = 320.dp
        val headerMinHeight = 92.dp
        val collapseRangePx = with(androidx.compose.ui.platform.LocalDensity.current) {
            (headerMaxHeight - headerMinHeight).toPx()
        }
        val collapseProgress = (scrollState.value / collapseRangePx).coerceIn(0f, 1f)
        val headerHeight = headerMaxHeight - (headerMaxHeight - headerMinHeight) * collapseProgress

        val stickyAlpha by animateFloatAsState(
            targetValue = ((collapseProgress - 0.3f) / 0.7f).coerceIn(0f, 1f),
            label = "sticky_alpha",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    val images = state.product?.imageUrls.orEmpty()
                    if (state.isLoading) {
                        Box(modifier = Modifier.fillMaxSize().background(extra.surfaceAlt))
                    } else {
                        val first = images.firstOrNull()
                        if (first != null) {
                            AsyncImage(
                                model = first,
                                contentDescription = "Product image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(0.dp)),
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(extra.surfaceAlt))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                }

                when {
                    productId == null -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Product not found", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    state.isLoading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.error != null -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    else -> {
                        val p = state.product
                        if (p == null) return@Column

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(20.dp),
                        ) {
                            Text(text = p.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (p.category ?: "").uppercase(),
                                color = extra.textSecondary,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "$${(p.price?.toDoubleOrNull() ?: 0.0)}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                            )
                        }

                        RatingSummaryCard(
                            averageRating = p.averageRating ?: 0.0,
                            reviewCount = p.reviewCount ?: 0,
                        )

                        ReviewSummaryCard(summary = state.summary)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(20.dp),
                        ) {
                            Text(text = "Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.translatedDescription ?: (p.description ?: ""),
                                color = extra.textSecondary,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(20.dp),
                        ) {
                            Text(
                                text = "Reviews (${p.reviewCount ?: state.reviews.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            when {
                                state.reviewsError != null && state.reviews.isEmpty() -> {
                                    Text(text = state.reviewsError ?: "Failed to load reviews", color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(onClick = { viewModel.load(p.id) }) { Text("Retry") }
                                }

                                state.reviews.isEmpty() -> {
                                    Text(text = "Be the first to review", color = extra.textSecondary)
                                }

                                else -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        state.reviews.forEach { r ->
                                            ReviewCard(
                                                reviewerName = r.reviewerName,
                                                rating = r.rating,
                                                comment = state.translatedCommentsById[r.id] ?: r.comment,
                                                helpfulCount = r.helpfulCount ?: 0,
                                                onHelpful = { viewModel.toggleHelpful(r.id) },
                                            )
                                        }

                                        if (state.reviewsHasMore) {
                                            Button(
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = !state.reviewsLoadingMore,
                                                onClick = { viewModel.loadMoreReviews() },
                                            ) {
                                                Text(if (state.reviewsLoadingMore) "Loading..." else "Load more")
                                            }
                                        }

                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = { onOpenReviews(p.id) },
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text("See all reviews")
                                                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "All reviews")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }

            val p = state.product
            if (p != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 56.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, extra.border, CircleShape)
                        .clickable(onClick = { viewModel.toggleFavorite() }),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (state.isFavorite) extra.danger else extra.textSecondary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = stickyAlpha))
                    .border(1.dp, extra.border.copy(alpha = stickyAlpha), RoundedCornerShape(0.dp)),
            ) {
                val title = if (state.isLoading) "" else (state.product?.name ?: "")
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp),
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Black,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⭐ ${String.format("%.1f", (state.product?.averageRating ?: 0.0))}",
                            color = extra.textSecondary,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${state.product?.reviewCount ?: 0} reviews",
                            color = extra.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingSummaryCard(
    averageRating: Double,
    reviewCount: Long,
) {
    val extra = ClaroTheme.colorsExtra
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = "Average rating", color = extra.textSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$reviewCount total reviews", color = extra.textSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    summary: com.reportnami.claro.data.api.model.ReviewSummaryResponseDto?,
) {
    val extra = ClaroTheme.colorsExtra
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text = "Smart Review Insights", fontWeight = FontWeight.Black)
            if (summary == null) {
                Text(text = "No summary", color = extra.textSecondary)
                return@Column
            }

            if (!summary.takeaway.isNullOrBlank()) {
                Text(text = summary.takeaway!!, color = extra.textSecondary)
            }
            BulletSection(title = "Pros", items = summary.pros)
            BulletSection(title = "Cons", items = summary.cons)
            BulletSection(title = "Top Topics", items = summary.topTopics)
        }
    }
}

@Composable
private fun BulletSection(title: String, items: List<String>?) {
    val extra = ClaroTheme.colorsExtra
    if (items.isNullOrEmpty()) return
    Text(text = title, fontWeight = FontWeight.Black)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { Text(text = "• $it", color = extra.textSecondary) }
    }
}

@Composable
private fun ReviewCard(
    reviewerName: String?,
    rating: Int?,
    comment: String?,
    helpfulCount: Long,
    onHelpful: () -> Unit,
) {
    val extra = ClaroTheme.colorsExtra
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(extra.surfaceAlt)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1F2A3A) else Color(0xFFECE7FF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val ch = ((reviewerName ?: "Anonymous").trim().firstOrNull() ?: 'A').uppercaseChar()
                        Text(text = ch.toString(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = reviewerName ?: "Anonymous", fontWeight = FontWeight.Black)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(text = "⭐ ${rating ?: "-"}/5", color = Color.White, fontWeight = FontWeight.Black)
                }
            }

            if (!comment.isNullOrBlank()) {
                Text(text = comment, color = extra.textSecondary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onHelpful) {
                    Text(text = "Helpful")
                }
                Text(text = helpfulCount.toString(), color = extra.textSecondary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
