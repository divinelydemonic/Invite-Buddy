package kr.android.invitationlistapp.ui.theme

import androidx.compose.material3.CardColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/* -------------------- */
/* Light Mode Colors    */
/* -------------------- */

val LightBackground = Color(0xFFFFFFFF)      // Clean white
val LightSurface = Color(0xFFF9F7F5)         // Warm off-white
val LightSurfaceVariant = Color(0xFFF2E6DA)  // Warm elevated surface

val LightPrimary = Color(0xFFFF7A00)         // Vivid orange
val LightPrimarySoft = Color(0xFFFACB9D)     // Soft peach container

val LightTextPrimary = Color(0xFF1C1C1C)     // Strong dark text
val LightTextSecondary = Color(0xFF5E5E5E)   // Softer secondary text

val LightOutline = Color(0xFFDDC3A5)          // Warm divider


/* -------------------- */
/* Dark Mode Colors     */
/* -------------------- */

val DarkBackground = Color(0xFF000000)        // True black
val DarkSurface = Color(0xFF121212)           // Dark cards
val DarkSurfaceVariant = Color(0xFF2A2A2A)    // Elevated dark surface

val DarkPrimary = Color(0xFFF89029)           // Glowing orange
val DarkPrimaryMuted = Color(0xFFEF801F)      // Burnt orange container

val DarkTextPrimary = Color(0xFFEDEDED)       // Soft white
val DarkTextSecondary = Color(0xFFEFEAEA)     // Muted text

val DarkOutline = Color(0xFF6B4A2D)            // Warm dark divider


/* -------------------- */
/* Semantic Colors      */
/* -------------------- */

val SuccessGreen = Color(0xFF2ECC71)
val ErrorRed = Color(0xFFE53935)
val WarningYellow = Color(0xFFFBC02D)


val lightGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFCC680),
        Color(0xFFFAB593)
    )
)

val darkGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFB253),
        Color(0xFFFF8C52)
    )
)

val cardColorLight = Color(0xFFF0C397)
val cardColorDark = Color(0xFFD26004)

@Composable
fun textFieldColorsLight() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LightPrimary,
        unfocusedBorderColor = LightOutline,
        focusedLabelColor = Color.Black.copy(alpha = 0.6f),
        unfocusedLabelColor = Color.Black.copy(alpha = 0.4f),
        cursorColor = LightPrimary,
        focusedContainerColor = LightSurface,
        unfocusedContainerColor = LightSurface
    )

@Composable
fun textFieldColorsDark() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DarkPrimary,
        unfocusedBorderColor = DarkOutline,
        focusedLabelColor = Color.White.copy(alpha = 0.6f),
        unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
        cursorColor = DarkPrimary,
        focusedContainerColor = DarkSurfaceVariant,
        unfocusedContainerColor = DarkSurface
    )
