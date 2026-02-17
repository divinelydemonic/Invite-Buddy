package kr.android.invitationlistapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppTopBar(
    isDark: Boolean,                    // Current theme state
    onToggleDark: (Boolean) -> Unit     // Callback to toggle theme
) {

    // Simple top bar container using Box for flexible alignment
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Outer spacing for breathing room
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        /* -------- App Title -------- */

        Text(
            text = "InviteBuddy",
            style = MaterialTheme.typography.titleLarge,
            // Align title to left
            modifier = Modifier.align(Alignment.CenterStart)
        )

        /* -------- Theme Toggle -------- */

        // Custom animated theme switch placed at right
        ThemeToggleSwitch(
            isDark = isDark,
            onToggleDark = onToggleDark,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}