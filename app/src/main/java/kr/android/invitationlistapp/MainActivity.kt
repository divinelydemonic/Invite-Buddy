package kr.android.invitationlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kr.android.invitationlistapp.ui.theme.DarkPrimaryMuted
import kr.android.invitationlistapp.ui.theme.InvitationListAppTheme
import kr.android.invitationlistapp.ui.theme.LightPrimarySoft

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Detect system-level dark mode preference
            val systemDarkTheme = isSystemInDarkTheme()

            // Persist user theme preference across configuration changes
            var isDarkTheme by rememberSaveable {
                mutableStateOf(systemDarkTheme)
            }

            // App-level state holding entire invitation list
            // This acts as the single source of truth
            var invitations by remember {
                mutableStateOf(listOf<InvitationListClass>())
            }

            // Apply custom theme wrapper
            InvitationListAppTheme(
                darkTheme = isDarkTheme
            ) {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    /* -------- Top Bar -------- */

                    topBar = {
                        AppTopBar(
                            isDark = isDarkTheme,
                            // Toggle theme from top bar
                            onToggleDark = { isDarkTheme = it }
                        )
                    },

                    /* -------- FAB Configuration -------- */

                    floatingActionButtonPosition = FabPosition.End,

                    floatingActionButton = {

                        // Controls visibility of AddDialogBox
                        var showAddDialog by remember { mutableStateOf(false) }

                        // Add invitation dialog
                        AddDialogBox(
                            showAddDialog = showAddDialog,

                            onDismiss = {
                                showAddDialog = false
                            },

                            isDark = isDarkTheme,

                            // When new invitee is created
                            onInvite = { newItem ->
                                invitations = invitations + newItem
                            },

                            onCancel = {
                                showAddDialog = false
                            }
                        )

                        // Floating Action Button
                        FloatingActionButton(
                            onClick = {
                                showAddDialog = true
                            },

                            shape = CircleShape,

                            // Theme-based coloring
                            containerColor =
                                if (isDarkTheme)
                                    Color(0xFFD26004)
                                else
                                    LightPrimarySoft,

                            contentColor =
                                if (isDarkTheme)
                                    Color.White
                                else
                                    Color.Black,

                            // Elevated press feedback
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 20.dp
                            ),

                            // Custom offset positioning for visual alignment
                            modifier = Modifier
                                .size(80.dp)
                                .offset(
                                    x = (-12).dp, // shift slightly toward center
                                    y = (-40).dp  // raise above default position
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GroupAdd,
                                contentDescription = "Add",
                                modifier = Modifier.size(33.dp)
                            )
                        }
                    }
                ) { innerPadding ->

                    // Main content screen
                    InvitationList(
                        modifier = Modifier.padding(innerPadding),
                        isDark = isDarkTheme,
                        invitations = invitations,

                        // Replace entire list when updates occur
                        onUpdate = { invitations = it }
                    )
                }
            }
        }
    }
}