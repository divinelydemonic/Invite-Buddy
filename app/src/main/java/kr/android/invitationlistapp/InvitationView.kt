package kr.android.invitationlistapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InvitationView(
    isDark: Boolean,                          // Theme flag
    status: InvitationStatus,                 // Current invitation status
    item: InvitationListClass,                // Data model for this row
    onStatusChange: (InvitationStatus) -> Unit, // Callback when status changes
    onEditClick: () -> Unit,                  // Edit action
    onDeleteClick: () -> Unit                 // Delete action
) {

    // Controls status dropdown visibility
    var expanded by remember { mutableStateOf(false) }

    // Controls 3-dot menu visibility
    var menuExpanded by remember { mutableStateOf(false) }

    // Controls expandable details section
    var detailsExpanded by remember { mutableStateOf(false) }

    /* -------- Animations -------- */

    // Rotate details arrow when expanding
    val detailsRotation by animateFloatAsState(
        targetValue = if (detailsExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "detailsArrowRotation"
    )

    // Rotate status arrow
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "arrowRotation"
    )

    // Slight scale bump for status pill when dropdown is open
    val pillScale by animateFloatAsState(
        targetValue = if (expanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = 0.2f,
            stiffness = 400f
        ),
        label = "pillScale"
    )

    // Elevation increase when details are expanded
    val cardElevation by animateDpAsState(
        targetValue = if (detailsExpanded) 10.dp else 6.dp,
        animationSpec = tween(200),
        label = "cardElevation"
    )

    // Slight scale effect for expanded card
    val cardScale by animateFloatAsState(
        targetValue = if (detailsExpanded) 1.015f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "cardScale"
    )

    // Slight upward offset when expanded
    val cardOffset by animateDpAsState(
        targetValue = if (detailsExpanded) (-2).dp else 0.dp,
        animationSpec = tween(200),
        label = "cardOffset"
    )

    /* -------- Main Card -------- */

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .offset(y = cardOffset) // Animated lift
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        /* -------- Top Row -------- */

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Name + category section
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 24.dp)
            ) {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp
                )

                // Category + extras summary
                Text(
                    text = "${item.category} • +${item.extras}",
                    fontSize = 18.sp,
                    color = Color(0xFF85858A)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                /* -------- Status Badge -------- */

                Box {
                    val backgroundColor = when (status) {
                        InvitationStatus.ACCEPTED ->
                            if (isDark) Color(0xFF5CBE60) else Color(0xFF009D0C)

                        InvitationStatus.PENDING ->
                            if (isDark) Color(0xFFE8A120) else Color(0xFFF5A000)

                        InvitationStatus.REJECTED ->
                            if (isDark) Color(0xFFFF5D52) else Color(0xFFFF2D1E)
                    }

                    // Clickable pill badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = pillScale
                                scaleY = pillScale
                            }
                            .clip(RoundedCornerShape(50))
                            .background(backgroundColor)
                            .clickable { expanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = status.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Change status",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.rotate(rotation)
                        )
                    }

                    // Status selection dropdown
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        shape = RoundedCornerShape(12.dp),
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface
                    ) {
                        InvitationStatus.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option.name.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                },
                                onClick = {
                                    onStatusChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                /* -------- More Options (3-dot menu) -------- */

                Box {

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier
                            .clickable { menuExpanded = true }
                            .padding(4.dp)
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface
                    ) {

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "edit"
                                )
                            },
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                onEditClick()
                            }
                        )

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "delete"
                                )
                            },
                            text = { Text("Delete") },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }
        }

        /* -------- Expandable Details Toggle -------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { detailsExpanded = !detailsExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (detailsExpanded) "Hide details" else "More details",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle details",
                modifier = Modifier.rotate(detailsRotation),
                tint = if (isDark)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            )
        }

        /* -------- Expandable Details Section -------- */

        AnimatedVisibility(
            visible = detailsExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        )  {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InvitationMoreDetails(
                    isDark,
                    email = item.email,
                    mobile = item.mobile
                )
            }
        }
    }
}

/* -------- Status Enum -------- */

// Restricts valid invitation status values
enum class InvitationStatus {
    ACCEPTED,
    PENDING,
    REJECTED
}