package com.reportnami.claro.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.reportnami.claro.ui.theme.ClaroTheme

data class ReviewEdit(
    val id: Long? = null,
    val reviewerName: String = "",
    val rating: Int = 5,
    val comment: String = "",
)

@Composable
fun ReviewEditModal(
    visible: Boolean,
    initialReview: ReviewEdit = ReviewEdit(),
    onDismiss: () -> Unit,
    onSave: (ReviewEdit) -> Unit,
    loading: Boolean = false,
) {
    val extra = ClaroTheme.colorsExtra
    
    var review by remember(initialReview) { 
        mutableStateOf(initialReview) 
    }

    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (review.id == null) "Add Review" else "Edit Review",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Name field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your Name (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    
                    OutlinedTextField(
                        value = review.reviewerName,
                        onValueChange = { review = review.copy(reviewerName = it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Rating
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            val starValue = index + 1
                            val scale by animateFloatAsState(
                                targetValue = if (review.rating >= starValue) 1.2f else 1f,
                                animationSpec = tween(durationMillis = 150),
                                label = "star_scale_$starValue"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { review = review.copy(rating = starValue) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (index < review.rating) "★" : "☆",
                                    fontSize = 28.sp,
                                    color = if (index < review.rating) Color(0xFFFFB800) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = androidx.compose.ui.draw.scale(scale)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "${review.rating} / 5",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Comment
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Review Comment (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    
                    OutlinedTextField(
                        value = review.comment,
                        onValueChange = { review = review.copy(comment = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Write your review (optional)") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Save button
                Button(
                    onClick = { onSave(review) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (review.id == null) "Submit Review" else "Save Changes",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
