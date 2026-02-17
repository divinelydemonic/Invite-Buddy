package kr.android.invitationlistapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kr.android.invitationlistapp.ui.theme.DarkPrimaryMuted
import kr.android.invitationlistapp.ui.theme.cardColorDark
import kr.android.invitationlistapp.ui.theme.cardColorLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.List
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationDashboard(
    eventName: String,                      // Current event title
    onEventNameChange: (String) -> Unit,    // Callback when event name changes
    eventDate: String,                      // Selected event date (dd-MM-yyyy)
    onEventDateChange: (String) -> Unit,    // Callback when date is selected
    isDark: Boolean,                        // Theme flag
    total: Int,                             // Total attendees including extras
    accepted: Int,                          // Accepted count
    pending: Int,                           // Pending count
    rejected: Int,                          // Rejected count
    totalExtras: Int                        // Total extra guests
) {

    // Card styling
    val shape = RoundedCornerShape(20.dp)
    val containerColor = if (isDark) cardColorDark else cardColorLight

    // Controls visibility of DatePickerDialog
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        shape = shape,
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(2.dp, DarkPrimaryMuted),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = if (isDark) Color.White else Color.Black
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            /* -------- Event Name -------- */

            // Editable title using BasicTextField for custom centered styling
            BasicTextField(
                value = eventName,
                onValueChange = { onEventNameChange(it) },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder shown when event name is empty
                        if (eventName.isEmpty()) {
                            Text(
                                "Event Name",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            /* -------- Event Date -------- */

            // Clickable date display that opens date picker
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
                            eventDate.isEmpty() ->
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            else ->
                                MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /* -------- Date Picker Dialog -------- */

            if (showDatePicker) {

                // Prevent selecting today or past dates (starts from tomorrow)
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

                val datePickerState = rememberDatePickerState(
                    selectableDates = object : SelectableDates {

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
                        containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White,
                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                        selectedDayContentColor = Color.White,
                        todayDateBorderColor = MaterialTheme.colorScheme.primary,
                        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedMillis = datePickerState.selectedDateMillis
                                if (selectedMillis != null) {

                                    // Format selected date as dd-MM-yyyy
                                    val formattedDate = SimpleDateFormat(
                                        "dd-MM-yyyy",
                                        Locale.getDefault()
                                    ).format(Date(selectedMillis))

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

            Spacer(Modifier.height(8.dp))

            // Show countdown only when date exists
            if (eventDate.isNotEmpty()) {
                EventCountdown(eventDate, isDark)
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- Stats Card -------- */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // First row of stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("🟢", accepted.toString(), "Accepted")
                        StatItem("🟡", pending.toString(), "Pending")
                        StatItem("🔴", rejected.toString(), "Rejected")
                        StatItem("➕", totalExtras.toString(), "Extras")
                    }

                    // Small visual divider
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                    )

                    // Second row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StatItem("👤", total.toString(), "Total")
                    }
                }
            }
        }
    }
}

/* -------- Stat Item Component -------- */

@Composable
fun StatItem(
    icon: String,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {

    // Highlight extras when non-zero
    val highlightExtras =
        label == "Extras" && count != "0"

    val countColor =
        if (highlightExtras)
            Color(0xFF389B3D)
        else
            LocalContentColor.current

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = countColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/* -------- Countdown Logic -------- */

@Composable
fun EventCountdown(
    eventDate: String,
    isDark: Boolean
) {

    // Remaining milliseconds until event
    var remainingMillis by remember { mutableStateOf<Long?>(null) }

    // Controls confetti visibility
    var showConfetti by remember { mutableStateOf(false) }

    // Runs whenever eventDate changes
    LaunchedEffect(eventDate) {

        showConfetti = false
        if (eventDate.isEmpty()) return@LaunchedEffect

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formatter.isLenient = false

        val parsedDate = formatter.parse(eventDate) ?: return@LaunchedEffect

        // Set event to end of selected day
        val calendar = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val eventMillis = calendar.timeInMillis

        // Infinite loop updating countdown every second
        while (true) {

            val diff = eventMillis - System.currentTimeMillis()

            if (diff <= 0) {
                remainingMillis = 0
                showConfetti = true
                break
            }

            remainingMillis = diff
            delay(1000)
        }
    }

    // UI rendering logic continues unchanged below...
}