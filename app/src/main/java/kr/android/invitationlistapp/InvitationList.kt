package kr.android.invitationlistapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InvitationList(
    modifier: Modifier,                           // External modifier (currently unused internally)
    isDark: Boolean,                              // Theme flag passed down to children
    invitations: List<InvitationListClass>,       // Source of truth list
    onUpdate: (List<InvitationListClass>) -> Unit // Callback to update entire list
){

    // Event metadata (local UI state, not persisted)
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }

    // Tracks whether any card is in editing mode (unused currently)
    var isEditing by remember { mutableStateOf(false) }

    // Holds the ID of the item currently being edited
    // If null → no editor visible
    var editingId by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Top padding likely accounts for status bar / toolbar spacing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Spacer(Modifier.height(8.dp))

                    // Derived stats computed from current list
                    val total = invitations.size
                    val accepted = invitations.count { it.status == "ACCEPTED" }
                    val pending = invitations.count { it.status == "PENDING" }
                    val rejected = invitations.count { it.status == "REJECTED" }

                    // Sum of all extra guests
                    val totalExtras = invitations.sumOf { it.extras }

                    // Total headcount including extras
                    val totalWithExtras = total + totalExtras

                    // Dashboard header section
                    InvitationDashboard(
                        eventName = eventName,
                        onEventNameChange = { eventName = it },
                        eventDate = eventDate,
                        onEventDateChange = { eventDate = it },
                        isDark = isDark,
                        total = totalWithExtras,
                        accepted = accepted,
                        pending = pending,
                        rejected = rejected,
                        totalExtras = totalExtras
                    )

                    Spacer(Modifier.height(8.dp))

                    // Empty state UI when there are no invitations
                    if (invitations.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No invitations yet 📭\nPress the button to invite 📬",
                                textAlign = TextAlign.Center,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }

                    } else {

                        // Scrollable list of invitation items
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            items(invitations) { item ->

                                // If this item matches editingId, show editor instead of card
                                if (editingId == item.id) {

                                    InvitationEditor(
                                        isDark = isDark,
                                        item = item,

                                        // Close editor dialog
                                        onDismiss = {
                                            editingId = null
                                        },

                                        // Save updated values
                                        onSaveClick = { _, name, email, mobile, category, extras ->

                                            // Create updated list with modified item
                                            val updated = invitations.map {

                                                if (it.id == item.id)
                                                    it.copy(
                                                        name = name,
                                                        email = email,
                                                        mobile = mobile,
                                                        category = category,
                                                        extras = extras.toIntOrNull() ?: 0,
                                                        isEditing = false
                                                    )
                                                else it
                                            }

                                            // Push updated list upstream
                                            onUpdate(updated)

                                            // Close editor
                                            editingId = null
                                        },

                                        // Cancel editing
                                        onCancelClick = {
                                            editingId = null
                                        }
                                    )

                                } else {

                                    // Default list card view
                                    InvitationView(
                                        isDark = isDark,
                                        status = InvitationStatus.valueOf(item.status),
                                        item = item,

                                        // Update status of item
                                        onStatusChange = { newStatus ->

                                            val updated = invitations.map {
                                                if (it.id == item.id)
                                                    it.copy(status = newStatus.name)
                                                else it
                                            }

                                            onUpdate(updated)
                                        },

                                        // Enter editing mode
                                        onEditClick = {
                                            editingId = item.id
                                        },

                                        // Remove item from list
                                        onDeleteClick = {

                                            val updated = invitations.filter {
                                                it.id != item.id
                                            }

                                            onUpdate(updated)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}