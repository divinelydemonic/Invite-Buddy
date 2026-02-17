package kr.android.invitationlistapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups3
import androidx.compose.material.icons.filled.InsertInvitation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kr.android.invitationlistapp.ui.theme.textFieldColorsDark
import kr.android.invitationlistapp.ui.theme.textFieldColorsLight

@Composable
fun AddDialogBox(
    showAddDialog: Boolean,        // Controls visibility of dialog
    onDismiss: () -> Unit,         // Triggered when dialog is dismissed externally
    isDark: Boolean,               // Used for theme-specific styling
    onInvite: (InvitationListClass) -> Unit, // Callback when a valid invite is submitted
    onCancel: () -> Unit           // Explicit cancel button action
) {

    /* ---------------- State ---------------- */

    // Form field states (remembered across recompositions while dialog is visible)
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var extras by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") } // Default country code (India)

    // Business rule: maximum allowed extra guests
    val maxExtras = 2

    /* ---------------- Submit Flag ---------------- */

    // Tracks whether user attempted submission (used to control error visibility)
    var triedSubmit by remember { mutableStateOf(false) }

    /* ---------------- Validation ---------------- */

    // Basic non-empty validation for required name
    val isNameValid = name.isNotBlank()

    // Phone must be exactly 10 digits and contain only numeric characters
    val isPhoneValid =
        mobile.length == 10 && mobile.all { it.isDigit() }

    // Category must be selected
    val isCategoryValid = category.isNotBlank()

    // Email is optional; validate only if not blank
    val isEmailValid =
        email.isBlank() ||
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()

    // Error visibility flags (shown only after user tries submitting)

    val showNameError =
        triedSubmit && !isNameValid

    val showPhoneError =
        triedSubmit && !isPhoneValid

    val showCategoryError =
        triedSubmit && !isCategoryValid

    val showEmailError =
        triedSubmit && !isEmailValid && email.isNotBlank()

    // Final validation state used to enable/disable Invite button
    val isFormValid =
        isNameValid &&
                isPhoneValid &&
                isCategoryValid &&
                isEmailValid


    /* ---------------- Reset On Open ---------------- */

    // When dialog opens, reset all form fields to initial state
    // Ensures fresh form each time dialog is shown
    LaunchedEffect(showAddDialog) {
        if (showAddDialog) {

            name = ""
            mobile = ""
            email = ""
            extras = ""
            category = ""
            countryCode = "+91"

            triedSubmit = false
        }
    }


    /* ---------------- UI ---------------- */

    if (showAddDialog) {

        AlertDialog(

            modifier = Modifier.fillMaxWidth(),

            // Top icon inside dialog
            icon = {
                Icon(
                    imageVector = Icons.Filled.PersonAddAlt1,
                    contentDescription = "Add",
                    modifier = Modifier.size(40.dp)
                )
            },

            // Dialog title
            title = {
                Text(
                    "Add Invitee",
                    style = MaterialTheme.typography.titleMedium
                )
            },

            // Handles outside click or back press dismissal
            onDismissRequest = onDismiss,


            /* ---------------- Buttons ---------------- */

            confirmButton = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    /* -------- Invite -------- */

                    Button(

                        // Button is interactable only when form is valid
                        enabled = isFormValid,

                        onClick = {

                            // Mark that user attempted submission
                            triedSubmit = true

                            // Create new invitation object
                            val newItem = InvitationListClass(

                                // Using current timestamp as temporary unique ID
                                id = System.currentTimeMillis().toInt(),

                                name = name.trim(),

                                // Store "NA" if email not provided
                                email = if (email.isBlank())
                                    "NA"
                                else
                                    email.trim(),

                                // Concatenate country code with mobile number
                                mobile = countryCode + mobile,

                                category = category,

                                // Safe parsing of extras; fallback to 0 if invalid
                                extras = extras.toIntOrNull() ?: 0,

                                // Default status on creation
                                status = "PENDING"
                            )

                            // Pass new item to parent
                            onInvite(newItem)

                            // Close dialog after successful submission
                            onDismiss()
                        }
                    ) {

                        Text("Invite")

                        Spacer(Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Filled.InsertInvitation,
                            contentDescription = "invite"
                        )
                    }


                    /* -------- Cancel -------- */

                    Button(

                        onClick = onCancel,

                        // Custom red color depending on theme
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (isDark)
                                    Color(0xFFF65555)
                                else
                                    Color(0xFFEA1414)
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            },


            /* ---------------- Form ---------------- */

            text = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        // Enables scrolling when content exceeds dialog height
                        .verticalScroll(rememberScrollState()),

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    /* -------- Name -------- */

                    OutlinedTextField(

                        value = name,

                        // Limit name length to 50 characters
                        onValueChange = {
                            name = it.take(50)
                        },

                        leadingIcon = {
                            Icon(Icons.Filled.Person, "name")
                        },

                        label = { Text("Name *") },

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),

                        colors = if (isDark)
                            textFieldColorsDark()
                        else
                            textFieldColorsLight(),

                        isError = showNameError,

                        supportingText = {
                            if (showNameError) {
                                Text("Name is required", color = Color.Red)
                            }
                        }
                    )


                    /* -------- Phone -------- */

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Country code selector
                        Box(
                            modifier = Modifier.width(108.dp)
                        ) {
                            CountryCodeDropdown(
                                value = countryCode,
                                onValueChange = { countryCode = it },
                                isDark = isDark
                            )
                        }

                        Spacer(Modifier.width(6.dp))

                        OutlinedTextField(

                            value = mobile,

                            // Accept only digits and limit to 10
                            onValueChange = { input ->

                                val digits =
                                    input.filter { it.isDigit() }

                                mobile = digits.take(10)
                            },

                            label = { Text("Phone *") },

                            singleLine = true,

                            modifier = Modifier.weight(1f).padding(top = 8.dp),

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),

                            colors = if (isDark)
                                textFieldColorsDark()
                            else
                                textFieldColorsLight(),

                            isError = showPhoneError,

                            supportingText = {
                                if (showPhoneError) {
                                    Text("Must be 10 digits", color = Color.Red)
                                }
                            }
                        )
                    }


                    /* -------- Email -------- */

                    OutlinedTextField(

                        value = email,

                        // Limit email length to 100 characters
                        onValueChange = {
                            email = it.take(100)
                        },

                        leadingIcon = {
                            Icon(Icons.Filled.Email, "email")
                        },

                        label = { Text("Email (Optional)") },

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),

                        colors = if (isDark)
                            textFieldColorsDark()
                        else
                            textFieldColorsLight(),

                        isError = triedSubmit && !isEmailValid && email.isNotBlank(),

                        supportingText = {
                            if (showEmailError) {
                                Text("Invalid email format", color = Color.Red)
                            }
                        }
                    )


                    /* -------- Category -------- */

                    CategoryDropdown(

                        value = category,

                        // Update selected category
                        onValueChange = {
                            category = it
                        },

                        isDark = isDark,

                        isError = showCategoryError
                    )

                    if (showCategoryError) {
                        Text(
                            "Category is required",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }


                    /* -------- Extras -------- */

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(

                            value = extras,

                            // Accept only digits and enforce maxExtras constraint
                            onValueChange = { input ->

                                val filtered =
                                    input.filter { it.isDigit() }

                                val number =
                                    filtered.toIntOrNull()

                                if (filtered.isEmpty()) {
                                    extras = ""
                                } else if (
                                    number != null &&
                                    number <= maxExtras
                                ) {
                                    extras = filtered
                                }
                            },

                            label = { Text("Extras") },

                            singleLine = true,

                            leadingIcon = {
                                Icon(Icons.Filled.Groups3, "extras")
                            },

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),

                            colors = if (isDark)
                                textFieldColorsDark()
                            else
                                textFieldColorsLight(),

                            modifier = Modifier.fillMaxWidth()
                        )


                        Spacer(Modifier.height(4.dp))


                        // Hint text aligned to the right
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            Text(
                                text = "Max $maxExtras extras",
                                style = MaterialTheme.typography.bodySmall,
                                color =
                                    if (isDark)
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                            .copy(alpha = 0.6f)
                                    else
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                            .copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            },

            // Rounded corners for dialog
            shape = RoundedCornerShape(20.dp),

            // Custom container background
            containerColor =
                MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}