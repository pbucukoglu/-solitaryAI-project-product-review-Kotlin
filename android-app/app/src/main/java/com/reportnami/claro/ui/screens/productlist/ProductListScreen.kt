package com.reportnami.claro.ui.screens.productlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.ui.components.ProductCard
import com.reportnami.claro.ui.theme.ClaroTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProductListScreen(
    onOpenProduct: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val extra = ClaroTheme.colorsExtra
    val scope = rememberCoroutineScope()

    var showFilters by rememberSaveable { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    LaunchedEffect(showFilters) {
        if (showFilters) sheetState.show() else sheetState.hide()
    }
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            showFilters = false
        }
    }

    var tempCategory by remember(showFilters) { mutableStateOf(state.selectedCategory) }
    var tempSortBy by remember(showFilters) { mutableStateOf(state.sortBy) }
    var tempSortDir by remember(showFilters) { mutableStateOf(state.sortDir) }
    var tempMinRating by remember(showFilters) { mutableStateOf(state.minRating) }
    var tempMinPrice by remember(showFilters) { mutableStateOf(state.minPrice) }
    var tempMaxPrice by remember(showFilters) { mutableStateOf(state.maxPrice) }

    val categories = remember {
        listOf(
            "Electronics",
            "Clothing",
            "Books",
            "Home & Kitchen",
            "Sports & Outdoors",
        )
    }

    val sortOptions = remember {
        listOf(
            SortOption("Most reviewed", "reviewCount", "DESC"),
            SortOption("Newest", "createdAt", "DESC"),
            SortOption("Price: low to high", "price", "ASC"),
            SortOption("Price: high to low", "price", "DESC"),
            SortOption("Rating: high to low", "averageRating", "DESC"),
            SortOption("Name: A-Z", "name", "ASC"),
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Filters & Sort",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showFilters = false }) {
                        Icon(
                            imageVector = Icons.Filled.Close, 
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "Category", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    categories.forEach { c ->
                        SelectChip(
                            text = c,
                            selected = tempCategory == c,
                            onClick = { tempCategory = if (tempCategory == c) null else c },
                        )
                    }
                }

                Text(
                    text = "Sort by", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    sortOptions.forEach { opt ->
                        SelectChip(
                            text = opt.label,
                            selected = tempSortBy == opt.sortBy && tempSortDir == opt.sortDir,
                            onClick = {
                                tempSortBy = opt.sortBy
                                tempSortDir = opt.sortDir
                            },
                            fill = true,
                        )
                    }
                }

                Text(
                    text = "Minimum rating", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    listOf(4, 3, 2, 1).forEach { r ->
                        SelectChip(
                            text = "${r}+",
                            selected = tempMinRating == r,
                            onClick = { tempMinRating = if (tempMinRating == r) null else r },
                        )
                    }
                }

                Text(
                    text = "Price range", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    androidx.compose.material.OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = tempMinPrice,
                        onValueChange = { tempMinPrice = it },
                        singleLine = true,
                        placeholder = { Text("Min") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = extra.surfaceAlt,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extra.border,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    androidx.compose.material.OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = tempMaxPrice,
                        onValueChange = { tempMaxPrice = it },
                        singleLine = true,
                        placeholder = { Text("Max") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = extra.surfaceAlt,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extra.border,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.applyFilters(
                            selectedCategory = tempCategory,
                            sortBy = tempSortBy,
                            sortDir = tempSortDir,
                            minRating = tempMinRating,
                            minPrice = tempMinPrice,
                            maxPrice = tempMaxPrice,
                        )
                        scope.launch {
                            showFilters = false
                        }
                    },
                ) {
                    Text("Apply")
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Products") })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .padding(0.dp),
                ) {
                    androidx.compose.material.OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.search,
                        onValueChange = { viewModel.setSearch(it) },
                        singleLine = true,
                        placeholder = { Text("Search") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = extra.textSecondary,
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = extra.surfaceAlt,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extra.border,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = { showFilters = true }),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filters",
                        tint = Color.White,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(extra.surfaceAlt)
                        .border(1.dp, extra.border, MaterialTheme.shapes.medium)
                        .clickable(onClick = onOpenSettings),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = extra.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SegmentedToggle(
                leftLabel = "All",
                rightLabel = "Favorites",
                selectedRight = state.showFavorites,
                onSelectLeft = { viewModel.setShowFavorites(false) },
                onSelectRight = { viewModel.setShowFavorites(true) },
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActiveFiltersRow(
                selectedCategory = state.selectedCategory,
                minRating = state.minRating,
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                sortLabel = sortOptions.firstOrNull { it.sortBy == state.sortBy && it.sortDir == state.sortDir }?.label
                    ?: "Most reviewed",
                hasNonDefaultSort = !(state.sortBy == "reviewCount" && state.sortDir == "DESC"),
                onClearCategory = { viewModel.applyFilters(null, state.sortBy, state.sortDir, state.minRating, state.minPrice, state.maxPrice) },
                onClearMinRating = { viewModel.applyFilters(state.selectedCategory, state.sortBy, state.sortDir, null, state.minPrice, state.maxPrice) },
                onClearPrice = { viewModel.applyFilters(state.selectedCategory, state.sortBy, state.sortDir, state.minRating, "", "") },
                onResetSort = { viewModel.applyFilters(state.selectedCategory, "reviewCount", "DESC", state.minRating, state.minPrice, state.maxPrice) },
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                state.isLoading && state.items.isEmpty() -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && state.items.isEmpty() -> {
                    Column {
                        Text(
                            text = state.error ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(state.items) { item ->
                            val isFav = state.favoriteIds.contains(item.id)
                            ProductCard(
                                item = item,
                                isFavorite = isFav,
                                onToggleFavorite = { viewModel.toggleFavorite(item.id) },
                                onClick = { onOpenProduct(item.id) },
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (!state.showFavorites && state.hasMore) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { viewModel.loadMore() },
                                    enabled = !state.isLoading,
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
}

private data class SortOption(
    val label: String,
    val sortBy: String,
    val sortDir: String,
)

@Composable
private fun SegmentedToggle(
    leftLabel: String,
    rightLabel: String,
    selectedRight: Boolean,
    onSelectLeft: () -> Unit,
    onSelectRight: () -> Unit,
) {
    val extra = ClaroTheme.colorsExtra

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(extra.surfaceAlt)
            .border(1.dp, extra.border, MaterialTheme.shapes.medium)
            .padding(2.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val w: Dp = maxWidth / 2
            val offsetX by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (selectedRight) w else 0.dp,
                label = "seg_offset",
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .width(w)
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary),
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable(onClick = onSelectLeft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = leftLabel,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (!selectedRight) Color.White else MaterialTheme.colorScheme.onSurface,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable(onClick = onSelectRight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = rightLabel,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selectedRight) Color.White else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    selectedCategory: String?,
    minRating: Int?,
    minPrice: String,
    maxPrice: String,
    sortLabel: String,
    hasNonDefaultSort: Boolean,
    onClearCategory: () -> Unit,
    onClearMinRating: () -> Unit,
    onClearPrice: () -> Unit,
    onResetSort: () -> Unit,
) {
    val extra = ClaroTheme.colorsExtra

    val hasAny = !selectedCategory.isNullOrBlank() || minRating != null || minPrice.isNotBlank() || maxPrice.isNotBlank() || hasNonDefaultSort
    if (!hasAny) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!selectedCategory.isNullOrBlank()) {
            FilterChip(text = "Category: $selectedCategory", onClose = onClearCategory)
        }
        if (minRating != null) {
            FilterChip(text = "Min rating: ${minRating}+", onClose = onClearMinRating)
        }
        if (minPrice.isNotBlank() || maxPrice.isNotBlank()) {
            val min = if (minPrice.isNotBlank()) minPrice else "0"
            val max = if (maxPrice.isNotBlank()) maxPrice else "∞"
            FilterChip(text = "Price: $min - $max", onClose = onClearPrice)
        }
        FilterChip(text = "Sort: $sortLabel", onClose = onResetSort)
    }
}

@Composable
private fun FilterChip(
    text: String,
    onClose: () -> Unit,
) {
    val extra = ClaroTheme.colorsExtra
    val shape = MaterialTheme.shapes.medium

    Row(
        modifier = Modifier
            .clip(shape)
            .background(extra.surfaceAlt)
            .border(1.dp, extra.border, shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
        Text(
            text = "✕",
            modifier = Modifier.clickable(onClick = onClose),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    fill: Boolean = false,
) {
    val extra = ClaroTheme.colorsExtra
    val shape = MaterialTheme.shapes.medium
    val bg = if (selected) MaterialTheme.colorScheme.primary else extra.surfaceAlt
    val border = if (selected) MaterialTheme.colorScheme.primary else extra.border
    val textColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .then(if (fill) Modifier.fillMaxWidth() else Modifier)
            .clip(shape)
            .background(bg)
            .border(1.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProductRow(
    item: ProductDto,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.category ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "⭐ ${item.averageRating ?: "-"} (${item.reviewCount ?: 0})",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
