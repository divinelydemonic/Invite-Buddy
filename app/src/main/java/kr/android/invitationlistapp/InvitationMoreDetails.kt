package kr.android.invitationlistapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InvitationMoreDetails(
    isDark: Boolean,   // Theme flag to adjust text color intensity
    email: String,     // Invitee email to display
    mobile: String     // Invitee phone number to display
) {

    // Container holding additional invitee information
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Provides spacing around the details section
            .padding(vertical = 18.dp, horizontal = 2.dp)
    ) {

        // Email display text
        Text(
            text = "Email: $email",
            fontSize = 16.sp,
            // Slightly muted text tone depending on theme
            color = if (isDark)
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
        )

        // Phone number display text
        Text(
            text = "Phone: $mobile",
            fontSize = 16.sp,
            // Same tonal styling for consistency
            color = if (isDark)
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
        )
    }
}