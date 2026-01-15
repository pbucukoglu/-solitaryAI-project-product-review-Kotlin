package com.reportnami.claro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reportnami.claro.ui.theme.ClaroTheme

data class ReviewSummary(
    val takeaway: String? = null,
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val topTopics: List<String> = emptyList(),
)

@Composable
fun ReviewSummaryCard(
    loading: Boolean,
    summary: ReviewSummary?,
    empty: Boolean,
    source: String?,
    modifier: Modifier = Modifier,
) {
    val extra = ClaroTheme.colorsExtra
    val title = "Smart Review Insights"

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (source?.lowercase() == "local") {
                    Box(
                        modifier = Modifier
                            .background(
                                color = extra.surfaceAlt,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Local",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when {
                loading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(
                                    color = extra.surfaceAlt,
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.84f)
                                .height(12.dp)
                                .background(
                                    color = extra.surfaceAlt,
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.96f)
                                    .height(10.dp)
                                    .background(
                                        color = extra.surfaceAlt,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            )
                        }
                    }
                }
                
                empty -> {
                    Text(
                        text = "No reviews yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                summary == null -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                else -> {
                    summary.takeaway?.let { takeaway ->
                        Text(
                            text = takeaway,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (summary.pros.isNotEmpty()) {
                        Text(
                            text = "Pros",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        summary.pros.forEachIndexed { idx, pro ->
                            Text(
                                text = "• $pro",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (summary.cons.isNotEmpty()) {
                        Text(
                            text = "Cons",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        summary.cons.forEachIndexed { idx, con ->
                            Text(
                                text = "• $con",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (summary.topTopics.isNotEmpty()) {
                        Text(
                            text = "Top Topics",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        summary.topTopics.forEachIndexed { idx, topic ->
                            Text(
                                text = "• $topic",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
