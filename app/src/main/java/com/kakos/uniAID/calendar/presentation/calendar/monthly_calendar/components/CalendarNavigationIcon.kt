package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Composable for displaying calendar navigation icons.
 *
 * Encapsulates navigation controls for calendar views, rendering directional
 * icons with appropriate rotation and interaction handling for consistent
 * navigation experiences.
 *
 * @param imageVector The vector image to display as the navigation icon.
 * @param contentDescription Accessibility description of the icon's function.
 * @param isHorizontal Whether to display the icon in horizontal orientation (true) or vertical (false).
 * @param onClick Callback invoked when the navigation icon is clicked.
 */
@Composable
fun CalendarNavigationIcon(
    imageVector: ImageVector,
    contentDescription: String,
    isHorizontal: Boolean = true,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    val rotation by animateFloatAsState(
        targetValue = if (isHorizontal) 0f else 90f,
        label = "CalendarNavigationIconAnimation",
    )
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .align(Alignment.Center)
            .rotate(rotation),
        imageVector = imageVector,
        contentDescription = contentDescription,
    )
}