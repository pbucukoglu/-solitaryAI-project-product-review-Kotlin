package com.reportnami.claro.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.reportnami.claro.ui.theme.ClaroTheme

@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
) {
    val extra = ClaroTheme.colorsExtra

    val targetY = if (isOffline) 0.dp else (-60).dp
    val translateY by animateDpAsState(
        targetValue = targetY,
        animationSpec = tween(durationMillis = 220),
        label = "offline_banner_y",
    )

    Box(
        modifier = modifier
            .offset(y = translateY)
            .padding(top = 10.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
    ) {
        val shape = RoundedCornerShape(14.dp)
        Row(
            modifier = Modifier
                .shadow(6.dp, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(1.dp, extra.border, shape)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(extra.offline, CircleShape),
            )
            Text(
                text = "You are offline",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.labelLarge,
                color = extra.textSecondary,
            )
        }
    }
}
