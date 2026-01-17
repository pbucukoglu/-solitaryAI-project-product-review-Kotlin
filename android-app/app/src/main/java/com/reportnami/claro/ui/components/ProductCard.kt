package com.reportnami.claro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.ui.theme.ClaroTheme

@Composable
fun ProductCard(
    item: ProductDto,
    isFavorite: Boolean,
    onToggleFavorite: (() -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onDeleteClick: (() -> Unit)? = null,
) {
    val extra = ClaroTheme.colorsExtra
    val dimens = ClaroTheme.dimens

    val cardShape = RoundedCornerShape(dimens.radiusCard.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = cardShape,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .background(MaterialTheme.colorScheme.surface, cardShape)
            .border(1.dp, extra.border, cardShape)
            .clip(cardShape)
            .clickable(onClick = onClick),
    ) {
        Column {
            Box {
                val imageUrl = item.imageUrls?.firstOrNull()

                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .background(extra.surfaceAlt),
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!item.category.isNullOrBlank()) {
                        Chip(text = item.category!!, background = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), border = extra.border)
                    }

                    val count = item.reviewCount ?: 0
                    if (count > 0) {
                        Chip(
                            text = "⭐ ${String.format("%.1f", (item.averageRating ?: 0.0))} (${count})",
                            background = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            border = extra.border,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(38.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
                                Color(0xDB121A27)
                            } else {
                                Color(0xEBFFFFFF)
                            },
                            CircleShape,
                        )
                        .border(1.dp, extra.border, CircleShape)
                        .clip(CircleShape)
                        .clickable(enabled = onToggleFavorite != null) { onToggleFavorite?.invoke() },
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) extra.danger else extra.textSecondary,
                    )
                }

                // Admin delete button
                if (isAdmin && onDeleteClick != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .size(32.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                Color(0xFFE74C3C),
                                CircleShape,
                            )
                            .border(1.dp, Color(0xFFC0392B), CircleShape)
                            .clip(CircleShape)
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Product",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$${String.format("%.2f", (item.price?.toDoubleOrNull() ?: 0.0))}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    val count = item.reviewCount ?: 0
                    if (count <= 0) {
                        Text(
                            text = "No reviews",
                            color = extra.textSecondary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(
    text: String,
    background: Color,
    border: Color,
) {
    val shape = RoundedCornerShape(12.dp)
    Text(
        text = text,
        modifier = Modifier
            .background(background, shape)
            .border(1.dp, border, shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelMedium,
    )
}
