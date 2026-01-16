package com.reportnami.claro.ui.screens.productdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.reportnami.claro.ui.components.ImageCarousel
import com.reportnami.claro.ui.components.ImagePreviewModal
import com.reportnami.claro.ui.components.ReviewCard
import com.reportnami.claro.ui.components.ReviewEditModal
import com.reportnami.claro.ui.components.ReviewSummaryCard
import com.reportnami.claro.ui.theme.ClaroTheme
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.api.model.ReviewDto

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(productId) {
        val id = productId ?: return@LaunchedEffect
        viewModel.load(id)
    }

    // Modal states
    var showImagePreview by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var showReviewEdit by remember { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<com.reportnami.claro.ui.components.Review?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            val p = state.product
            if (p != null) {
                FloatingActionButton(
                    onClick = {
                        editingReview = null
                        showReviewEdit = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ) {
                    Text(text = "＋", fontWeight = FontWeight.Black)
                }
            }
        },
    ) { padding ->
        val listState = rememberLazyListState()
        val headerMaxHeight = 320.dp
        val headerMinHeight = 92.dp
        val collapseRangePx = with(LocalDensity.current) {
            (headerMaxHeight - headerMinHeight).toPx()
        }
        val firstVisibleItemIndex = listState.firstVisibleItemIndex
        val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
        val collapseProgress =
            ((firstVisibleItemIndex * 600 + firstVisibleItemScrollOffset) / collapseRangePx).coerceIn(
                0f,
                1f
            )
        val headerHeight = headerMaxHeight - (headerMaxHeight - headerMinHeight) * collapseProgress

        val stickyAlpha by animateFloatAsState(
            targetValue = ((collapseProgress - 0.3f) / 0.7f).coerceIn(0f, 1f),
            label = "sticky_alpha",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Favorite button overlay
            val currentProduct = state.product
            if (currentProduct != null) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(currentProduct.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, extra.border, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (state.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Sticky header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = stickyAlpha))
                    .border(1.dp, extra.border.copy(alpha = stickyAlpha), RoundedCornerShape(0.dp))
                    .statusBarsPadding(),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = currentProduct?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = stickyAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ ${currentProduct?.averageRating ?: 0}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = stickyAlpha)
                        )
                        Text(
                            text = "${currentProduct?.reviewCount ?: 0} reviews",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = stickyAlpha)
                        )
                    }
                }
            }

            // Main content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = headerMinHeight)
            ) {
                // Image carousel
                item {
                    ImageCarousel(
                        images = currentProduct?.imageUrls ?: emptyList(),
                        loading = state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerMaxHeight),
                        onImageClick = { index ->
                            selectedImageIndex = index
                            showImagePreview = true
                        }
                    )
                }

                // Product info
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (state.isLoading) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .height(22.dp)
                                            .background(
                                                extra.surfaceAlt,
                                                RoundedCornerShape(10.dp)
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.4f)
                                            .height(14.dp)
                                            .background(
                                                extra.surfaceAlt,
                                                RoundedCornerShape(10.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.3f)
                                            .height(28.dp)
                                            .background(
                                                extra.surfaceAlt,
                                                RoundedCornerShape(10.dp)
                                            )
                                    )
                                }
                            } else {
                                Text(
                                    text = currentProduct?.name ?: "",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentProduct?.category ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$${currentProduct?.price ?: 0}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Rating summary
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (state.isLoading) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(extra.surfaceAlt, RoundedCornerShape(12.dp))
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.6f)
                                                .height(14.dp)
                                                .background(
                                                    extra.surfaceAlt,
                                                    RoundedCornerShape(10.dp)
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.45f)
                                                .height(12.dp)
                                                .background(
                                                    extra.surfaceAlt,
                                                    RoundedCornerShape(10.dp)
                                                )
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${currentProduct?.averageRating ?: 0}",
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Average Rating",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${currentProduct?.reviewCount ?: 0} reviews",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Rating distribution (simplified)
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf(5, 4, 3, 2, 1).forEach { stars ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "$stars★",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.width(20.dp)
                                                )
                                                LinearProgressIndicator(
                                                    progress = (stars / 5f).coerceIn(0f, 1f),
                                                    modifier = Modifier
                                                        .width(100.dp)
                                                        .height(6.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    trackColor = extra.surfaceAlt
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Review summary card
                item {
                    ReviewSummaryCard(
                        loading = state.isLoading,
                        summary = state.reviewSummary?.let {
                            com.reportnami.claro.ui.components.ReviewSummary(
                                takeaway = it.takeaway,
                                pros = it.pros ?: emptyList(),
                                cons = it.cons ?: emptyList(),
                                topTopics = it.topTopics ?: emptyList()
                            )
                        },
                        empty = (currentProduct?.reviewCount?.toInt() ?: 0) == 0,
                        source = state.reviewSummarySource,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Description
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (state.isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.3f)
                                        .height(16.dp)
                                        .background(extra.surfaceAlt, RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(if (it == 0) 1f else 0.92f)
                                            .height(12.dp)
                                            .background(
                                                extra.surfaceAlt,
                                                RoundedCornerShape(10.dp)
                                            )
                                    )
                                    if (it < 2) Spacer(modifier = Modifier.height(8.dp))
                                }
                            } else {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = state.translatedDescription ?: currentProduct?.description
                                        ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                // Reviews section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reviews (${currentProduct?.reviewCount ?: 0})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (state.isLoadingReviews) {
                                repeat(3) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .background(extra.surfaceAlt, CircleShape)
                                            )
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.4f)
                                                        .height(12.dp)
                                                        .background(
                                                            extra.surfaceAlt,
                                                            RoundedCornerShape(10.dp)
                                                        )
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.24f)
                                                        .height(10.dp)
                                                        .background(
                                                            extra.surfaceAlt,
                                                            RoundedCornerShape(10.dp)
                                                        )
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .background(
                                                        extra.surfaceAlt,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .background(
                                                    extra.surfaceAlt,
                                                    RoundedCornerShape(10.dp)
                                                )
                                        )
                                    }
                                }
                            } else if (state.reviews.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Be the first to review this product!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                // Show first 3 reviews
                                state.reviews.take(3).forEach { reviewDto ->
                                    ReviewCard(
                                        review = com.reportnami.claro.ui.components.Review(
                                            id = reviewDto.id,
                                            reviewerName = reviewDto.reviewerName,
                                            rating = (reviewDto.rating ?: 0).toInt(),
                                            comment = reviewDto.comment,
                                            helpfulCount = (reviewDto.helpfulCount ?: 0).toInt(),
                                            createdAt = reviewDto.createdAt ?: "",
                                            isMine = false, // TODO: Determine if review belongs to current user
                                            isHelpful = false // TODO: Determine if review is marked as helpful by current user
                                        ),
                                        onEdit = { review ->
                                            editingReview = review
                                            showReviewEdit = true
                                        },
                                        onDelete = { review ->
                                            // TODO: Implement delete
                                        },
                                        onToggleHelpful = { review ->
                                            viewModel.toggleHelpful(review.id)
                                        },
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                if (state.reviews.size > 3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "And ${state.reviews.size - 3} more reviews...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Show "Load More Reviews" button if there are more reviews
                if (state.reviewsHasMore && state.reviews.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMoreReviews() },
                                enabled = !state.isLoadingReviews,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.isLoadingReviews) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Load More Reviews")
                                }
                            }
                        }
                    }
                }

                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Modals
        if (showImagePreview) {
            ImagePreviewModal(
                images = state.product?.imageUrls ?: emptyList(),
                initialIndex = selectedImageIndex,
                onDismiss = { showImagePreview = false }
            )
        }

        if (showReviewEdit) {
            ReviewEditModal(
                visible = showReviewEdit,
                initialReview = editingReview?.let { review ->
                    com.reportnami.claro.ui.components.ReviewEdit(
                        id = review.id,
                        reviewerName = review.reviewerName ?: "",
                        rating = review.rating,
                        comment = review.comment ?: ""
                    )
                } ?: com.reportnami.claro.ui.components.ReviewEdit(),
                onDismiss = { showReviewEdit = false },
                onSave = { reviewEdit ->
                    state.product?.id?.let { productId ->
                        if (reviewEdit.id == null) {
                            viewModel.addReview(
                                productId,
                                reviewEdit.reviewerName,
                                reviewEdit.rating,
                                reviewEdit.comment
                            )
                        } else {
                            // TODO: Implement update review
                        }
                    }
                    showReviewEdit = false
                },
                loading = state.isSubmittingReview
            )
        }
        
        // Show error toast/snackbar for review submission errors
        state.error?.let { error ->
            LaunchedEffect(error) {
                // You could show a snackbar here
                // For now, the error is handled in the ViewModel
            }
        }
    }
}
