package kr.android.invitationlistapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ThemeToggleSwitch(
    isDark: Boolean,                    // Current theme state
    onToggleDark: (Boolean) -> Unit,    // Callback when switch is toggled
    modifier: Modifier = Modifier
) {

    // Transition object to animate between light and dark states
    val transition = updateTransition(
        targetState = isDark,
        label = "ThemeTransition"
    )

    /* -------- Track Color Animation -------- */

    // Smooth animated color change for switch track
    val trackColor by transition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = 1000,
                easing = LinearOutSlowInEasing
            )
        },
        label = "TrackColor"
    ) { dark ->
        if (dark) Color(0xFFD26004) else Color(0xFFF0C397)
    }

    /* -------- Thumb Rotation Animation -------- */

    // Rotate icon when switching to dark mode
    val rotation by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 1100,
                easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
            )
        },
        label = "Rotation"
    ) { dark ->
        if (dark) -360f else 0f
    }

    /* -------- Thumb Scale Animation -------- */

    // Slight scale boost for playful interaction feel
    val scale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            )
        },
        label = "Scale"
    ) { dark ->
        if (dark) 1.12f else 1.04f
    }

    /* -------- Switch Component -------- */

    Switch(
        checked = isDark,
        onCheckedChange = onToggleDark,

        // Slight height adjustment for visual balance
        modifier = modifier
            .height(10.dp)
            .padding(end = 4.dp),

        // Custom track + thumb colors
        colors = SwitchDefaults.colors(
            checkedTrackColor = trackColor,
            uncheckedTrackColor = trackColor,
            checkedThumbColor = Color.Black,
            uncheckedThumbColor = Color.White
        ),

        /* -------- Custom Thumb Content -------- */

        thumbContent = {

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        // Apply animated rotation + scale
                        rotationZ = rotation
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {

                // Animate icon morph between sun and moon
                AnimatedContent(
                    targetState = isDark,
                    transitionSpec = {
                        fadeIn(
                            tween(
                                durationMillis = 500,
                                delayMillis = 250
                            )
                        ) + scaleIn(
                            tween(500)
                        ) togetherWith
                                fadeOut(
                                    tween(400)
                                ) + scaleOut(
                            tween(400)
                        )
                    },
                    label = "IconMorph"
                ) { dark ->

                    Icon(
                        imageVector =
                            if (dark)
                                Icons.Filled.NightsStay
                            else
                                Icons.Filled.WbSunny,

                        contentDescription = null,

                        // Warm yellow moon / soft sun tint
                        tint =
                            if (dark)
                                Color(0xFFFFB700)
                            else
                                Color(0xFFF0C397),

                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )
}