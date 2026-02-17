package kr.android.invitationlistapp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kr.android.invitationlistapp.ui.theme.DarkPrimaryMuted
import kr.android.invitationlistapp.ui.theme.cardColorDark
import kr.android.invitationlistapp.ui.theme.cardColorLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationDashboard(
    eventName: String,                      // Current event title (state hoisted from parent)
    onEventNameChange: (String) -> Unit,    // Propagates title changes upward
    eventDate: String,                      // Selected event date in "dd-MM-yyyy" format
    onEventDateChange: (String) -> Unit,    // Propagates date changes upward
    isDark: Boolean,                        // Theme flag for styling
    total: Int,                             // Total attendees including extras
    accepted: Int,                          // RSVP accepted count
    pending: Int,                           // RSVP pending count
    rejected: Int,                          // RSVP rejected count
    totalExtras: Int                        // Total additional guests
) {

    /* =====================================================
       Dashboard Container Configuration
       ===================================================== */

    // Rounded card shape for visual consistency
    val shape = RoundedCornerShape(20.dp)

    // Theme-based background color
    val containerColor = if (isDark) cardColorDark else cardColorLight

    // Controls DatePickerDialog visibility lifecycle
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),

        // Card appearance configuration
        shape = shape,
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(2.dp, DarkPrimaryMuted),

        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = if (isDark) Color.White else Color.Black
        )
    ) {

        /* =====================================================
           Main Vertical Layout
           ===================================================== */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

//            Spacer(modifier = Modifier.height(8.dp))

            /* =====================================================
               Event Name Input
               ===================================================== */

            // Using BasicTextField instead of OutlinedTextField
            // to fully control layout + centered placeholder behavior.
            BasicTextField(
                value = eventName,
                onValueChange = { onEventNameChange(it) },

                singleLine = true,

                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark)
                        Color.White
                    else
                        Color.Black.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),

                modifier = Modifier.fillMaxWidth(),

                // Custom placeholder logic via decorationBox
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        // Placeholder appears only when title is empty
                        if (eventName.isEmpty()) {
                            Text(
                                "Event Name",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme
                                        .colorScheme
                                        .onSurface
                                        .copy(alpha = 0.5f)
                                )
                            )
                        }

                        // Actual text field content
                        innerTextField()
                    }
                }
            )

//            Spacer(modifier = Modifier.height(12.dp))

            /* =====================================================
               Event Date Display
               ===================================================== */

            // Acts like a button but visually minimal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clickable { showDatePicker = true },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = eventDate.ifEmpty { "Event Date" },

                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            // Muted style when no date selected
                            eventDate.isEmpty() ->
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                                    .copy(alpha = 0.6f)

                            else ->
                                MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),

                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /* =====================================================
               Date Picker Dialog
               ===================================================== */

            if (showDatePicker) {

                // Compute tomorrow's start (00:00) to disallow today & past
                val tomorrowStart = remember {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    calendar.timeInMillis
                }

                // Custom selectableDates logic
                val datePickerState = rememberDatePickerState(
                    selectableDates = object : SelectableDates {

                        // Allow only dates >= tomorrow
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= tomorrowStart
                        }

                        override fun isSelectableYear(year: Int): Boolean {
                            return true
                        }
                    }
                )

                DatePickerDialog(
                    shape = RoundedCornerShape(24.dp),

                    colors = DatePickerDefaults.colors(
                        containerColor =
                            if (isDark) Color(0xFF1E1E1E)
                            else Color.White,

                        selectedDayContainerColor =
                            MaterialTheme.colorScheme.primary,

                        selectedDayContentColor = Color.White,

                        todayDateBorderColor =
                            MaterialTheme.colorScheme.primary,

                        weekdayContentColor =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ),

                    onDismissRequest = { showDatePicker = false },

                    confirmButton = {
                        TextButton(
                            onClick = {

                                val selectedMillis =
                                    datePickerState.selectedDateMillis

                                if (selectedMillis != null) {

                                    // Convert millis to formatted string
                                    val formattedDate = SimpleDateFormat(
                                        "dd-MM-yyyy",
                                        Locale.getDefault()
                                    ).format(Date(selectedMillis))

                                    // Hoist result upward
                                    onEventDateChange(formattedDate)
                                }

                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }

//            Spacer(Modifier.height(8.dp))

            /* =====================================================
               Countdown Section
               ===================================================== */

            // Only render countdown when date is present
            if (eventDate.isNotEmpty()) {
                EventCountdown(eventDate, isDark)
            }

//            Spacer(modifier = Modifier.height(12.dp))

            /* =====================================================
               RSVP Statistics Card
               ===================================================== */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 8.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    // Primary breakdown row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceEvenly
                    ) {

                        StatItem("🟢", accepted.toString(), "Accepted")
                        StatItem("🟡", pending.toString(), "Pending")
                        StatItem("🔴", rejected.toString(), "Rejected")
                        StatItem("➕", totalExtras.toString(), "Extras")
                    }

                    // Subtle divider for visual hierarchy
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),

                        thickness = 1.dp,

                        color = MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.15f)
                    )

                    // Total row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.Center
                    ) {
                        StatItem("👤", total.toString(), "Invited")
                    }
                }
            }
        }
    }
}

/* -------- Stat Item Component -------- */

@Composable
fun StatItem(
    icon: String,                // Emoji or symbol representing the stat
    count: String,               // Numeric value to display
    label: String,               // Label below the value
    modifier: Modifier = Modifier
) {

    /* -------------------------------------------------------
       Conditional Highlight Logic
       ------------------------------------------------------- */

    // Only highlight "Extras" when its count is non-zero.
    // This draws subtle attention to additional attendees.
    val highlightExtras =
        label == "Extras" && count != "0"

    // Use green accent when highlighting, otherwise inherit
    // the default content color from parent composition.
    val countColor =
        if (highlightExtras)
            Color(0xFF389B3D)
        else
            LocalContentColor.current

    /* -------------------------------------------------------
       Layout Structure
       ------------------------------------------------------- */

    Column(
        modifier = modifier     // Internal spacing to avoid crowding between stats
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .wrapContentSize(),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        /* -------- Icon + Count Row -------- */

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Emoji indicator
            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium
            )

            // Count value (bold for emphasis)
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = countColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        /* -------- Label -------- */

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            // Slightly muted tone for hierarchy separation
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/* -------- Countdown Logic -------- */

@Composable
fun EventCountdown(
    eventDate: String,   // Event date in "dd-MM-yyyy" format
    isDark: Boolean      // Theme flag for styling adjustments
) {

    /* -------------------------------------------------------
       State
       ------------------------------------------------------- */

    // Holds remaining time in milliseconds.
    // Null means countdown hasn't initialized yet.
    var remainingMillis by remember { mutableStateOf<Long?>(null) }

    // Controls confetti visibility on event day.
    var showConfetti by remember { mutableStateOf(false) }

    /* -------------------------------------------------------
       Countdown Calculation Side-Effect
       ------------------------------------------------------- */

    // Re-runs whenever eventDate changes.
    // This safely cancels previous coroutine if date updates.
    LaunchedEffect(eventDate) {

        if (eventDate.isEmpty()) return@LaunchedEffect

        // Strict date parsing (prevents invalid rollover like 32-01-2025)
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formatter.isLenient = false

        val parsedDate = try {
            formatter.parse(eventDate)
        } catch (e: Exception) {
            null
        } ?: return@LaunchedEffect

        // Normalize today's date to midnight
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Normalize event date to midnight
        val eventCalendar = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val eventMillis = eventCalendar.timeInMillis
        val todayMillis = todayCalendar.timeInMillis

        // 🎉 If today equals event day, trigger celebration immediately
        if (todayMillis == eventMillis) {
            remainingMillis = 0
            showConfetti = true
            return@LaunchedEffect
        }

        /* -------------------------------------------------------
           Countdown Loop
           ------------------------------------------------------- */

        // Runs until composable leaves composition (isActive ensures safety)
        while (isActive) {

            val diff = eventMillis - System.currentTimeMillis()

            if (diff <= 0) {
                remainingMillis = 0
                break
            }

            // Prevent negative values due to scheduling jitter
            remainingMillis = diff.coerceAtLeast(0)

            // Align updates with real-world second boundaries
            // This avoids drift from naive delay(1000)
            val nextTick = 1000 - (System.currentTimeMillis() % 1000)
            delay(nextTick)
        }
    }

    /* -------------------------------------------------------
       Confetti Auto-Stop
       ------------------------------------------------------- */

    // Separate side effect tied only to showConfetti state.
    // Keeps logic clean and avoids mixing responsibilities.
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(8000)          // Confetti duration (8 seconds)
            showConfetti = false
        }
    }

    /* -------------------------------------------------------
       UI Rendering
       ------------------------------------------------------- */

    remainingMillis?.let { millis ->

        // Convert milliseconds into human-readable components
        val totalSeconds = millis / 1000
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val oneHourMillis = 60 * 60 * 1000L
        val oneDayMillis = 24 * oneHourMillis

        val isEventHappening = millis <= 0
        val isLastHour = millis in 1..oneHourMillis
        val isLastDay = millis in (oneHourMillis + 1)..oneDayMillis

        // Dynamic color logic for urgency feedback
        val countdownColor = when {
            isEventHappening -> Color(0xFF4CAF50)    // Celebration green
            isLastHour -> Color(0xFFE53935)          // High urgency red
            isLastDay -> if (isDark)
                Color(0xFF621E09)
            else
                Color(0xFFFF3C00)                   // Orange warning
            else -> if (isDark)
                Color.White
            else
                Color.Black
        }

        /* -------------------------------------------------------
           Urgency Pulse Animation
           ------------------------------------------------------- */

        // Only animate scale during last hour
        val scale = if (isLastHour) {
            val transition = rememberInfiniteTransition(label = "pulse")

            transition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAnim"
            ).value
        } else 1f

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isEventHappening) {

                // Celebration layout container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {

                    // Confetti renders behind text
                    if (showConfetti) {
                        ConfettiAnimation(
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    Text(
                        "🎉 It's Event Day!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = countdownColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {

                // Standard countdown display
                Text(
                    "$days d  $hours h  $minutes m  $seconds s",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color =
                        if (isDark)
                            countdownColor.copy(alpha = 0.5f)
                        else
                            countdownColor.copy(alpha = 0.6f),
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        }
    }
}

// Represents a single confetti particle.
// xFactor  → horizontal position as percentage of screen width (0f–1f)
// depth    → pseudo-depth value used for size & motion variance
// colorIndex → index into themeColors list
// offset   → vertical offset to stagger particle start positions

data class Particle(
    val xFactor: Float,
    val depth: Float,
    val colorIndex: Int,
    val offset: Float
)

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier
) {

    /* -------------------------------------------------------
       Infinite Animations
       ------------------------------------------------------- */

    // Drives all repeating animations inside this composable.
    val infinite = rememberInfiniteTransition(label = "confetti")

    // Controls vertical falling progress.
    // Starts slightly above screen (-0.2f) and ends below screen (1.2f)
    // so particles fully enter and exit the viewport.
    val dropProgress by infinite.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiDrop"
    )

    // Rotational spin applied to each particle.
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    /* -------------------------------------------------------
       Color Palette
       ------------------------------------------------------- */

    // Mix of theme-based colors and fixed celebratory colors.
    val themeColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        Color(0xFFFFC107),
        Color(0xFFE91E63),
        Color(0xFF4CAF50),
        Color(0xFF2196F3)
    )

    /* -------------------------------------------------------
       Particle Generation (Stable Across Recompositions)
       ------------------------------------------------------- */

    // Generated once per composition.
    // Using remember prevents particles from regenerating every frame.
    val particles = remember {
        List(100) {
            Particle(
                xFactor = Random.nextFloat(),                 // Horizontal placement
                depth = Random.nextFloat(),                   // Motion & size variance
                colorIndex = Random.nextInt(themeColors.size),
                offset = Random.nextFloat()                   // Vertical stagger
            )
        }
    }

    /* -------------------------------------------------------
       Rendering Layer
       ------------------------------------------------------- */

    Canvas(modifier = modifier) {

        val width = size.width
        val height = size.height

        particles.forEach { particle ->

            // Larger depth → larger particle size (fake perspective)
            val sizeFactor = 6f + (particle.depth * 12f)

            // Adds side-to-side floating motion using sine wave.
            // Depth influences drift intensity.
            val horizontalDrift =
                kotlin.math.sin(dropProgress * 10f + particle.depth * 5f) *
                        (20f + particle.depth * 40f)

            // Calculates vertical position.
            // offset staggers particles so they don't all start at top simultaneously.
            val yPos =
                ((dropProgress + particle.offset) % 1.2f) * height +
                        (particle.depth * -600f)

            // Rotate each particle individually around its own center.
            rotate(
                degrees = rotation + particle.depth * 180f,
                pivot = Offset(
                    x = particle.xFactor * width + horizontalDrift,
                    y = yPos
                )
            ) {
                // Draw rectangle-shaped confetti piece.
                drawRect(
                    color = themeColors[particle.colorIndex],
                    topLeft = Offset(
                        x = particle.xFactor * width + horizontalDrift,
                        y = yPos
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = sizeFactor,
                        height = sizeFactor * 1.5f   // Slightly taller than wide
                    )
                )
            }
        }
    }
}