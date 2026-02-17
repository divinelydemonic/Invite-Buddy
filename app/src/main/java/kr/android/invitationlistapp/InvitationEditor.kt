package kr.android.invitationlistapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups3
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
fun InvitationEditor(
    isDark: Boolean,                      // Used for theme-based UI styling
    item: InvitationListClass,            // Existing invitee data being edited

    onDismiss: () -> Unit,                // Called when dialog is dismissed

    // Callback to persist updated data
    onSaveClick: (
        isDark: Boolean,
        name: String,
        email: String,
        mobile: String,
        category: String,
        extras: String
    ) -> Unit,

    onCancelClick: () -> Unit             // Cancel button action
) {

    /* ---------------- State ---------------- */

    // Initialize editable state from existing item

    var name by remember { mutableStateOf(item.name) }

    // Convert "NA" back to blank for editing UX
    var email by remember {
        mutableStateOf(
            if (item.email == "NA") "" else item.email
        )
    }

    // Extract country code using regex (1–2 digits after '+')
    // Falls back to +91 if not matched
    var countryCode by remember {
        mutableStateOf(
            Regex("^\\+\\d{1,2}")
                .find(item.mobile)
                ?.value
                ?: "+91"
        )
    }

    // Extract last 10 digits as mobile number
    var mobile by remember {
        mutableStateOf(
            item.mobile.filter { it.isDigit() }.takeLast(10)
        )
    }

    var category by remember { mutableStateOf(item.category) }

    // Convert extras Int to editable string
    var extras by remember {
        mutableStateOf(item.extras.toString())
    }

    // Business rule: maximum allowed extra guests
    val maxExtras = 2


    /* ---------------- Submit Flag ---------------- */

    // Tracks whether save button was attempted
    var triedSubmit by remember { mutableStateOf(false) }


    /* ---------------- Validation ---------------- */

    val isNameValid = name.isNotBlank()

    val isPhoneValid =
        mobile.length == 10 && mobile.all { it.isDigit() }

    val isCategoryValid = category.isNotBlank()

    // Email optional; validate only if not blank
    val isEmailValid =
        email.isBlank() ||
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()


    // Controls Save button enabled state (live validation)
    // Note: Email validation is intentionally excluded here
    val canSubmit =
        isNameValid &&
                isPhoneValid &&
                isCategoryValid


    // Full validation executed on Save click
    val isFormValid =
        isNameValid &&
                isPhoneValid &&
                isCategoryValid &&
                isEmailValid


    // Error flags shown only after Save attempt
    val showNameError =
        triedSubmit && !isNameValid

    val showPhoneError =
        triedSubmit && !isPhoneValid

    val showCategoryError =
        triedSubmit && !isCategoryValid

    val showEmailError =
        triedSubmit && !isEmailValid && email.isNotBlank()


    /* ---------------- Dialog ---------------- */

    AlertDialog(

        modifier = Modifier.fillMaxWidth(),

        // Dialog icon
        icon = {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "edit",
                modifier = Modifier.size(40.dp)
            )
        },

        // Dialog title
        title = {
            Text(
                "Edit Invitee",
                style = MaterialTheme.typography.titleMedium
            )
        },

        // Handles outside tap / back press
        onDismissRequest = onDismiss,


        /* ---------------- Buttons ---------------- */

        confirmButton = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                /* -------- Save -------- */

                Button(

                    // Live enable state (without email validation)
                    enabled = canSubmit,

                    onClick = {

                        triedSubmit = true

                        // Prevent save if full validation fails
                        if (!isFormValid) return@Button

                        // Pass updated values back to parent
                        onSaveClick(
                            isDark,
                            name.trim(),
                            if (email.isBlank()) "NA" else email.trim(),
                            countryCode + mobile,
                            category,
                            extras.ifBlank { "0" }
                        )
                    }
                ) {

                    Text("Save")

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "save"
                    )
                }


                /* -------- Cancel -------- */

                Button(

                    onClick = onCancelClick,

                    // Red cancel styling depending on theme
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
                    .padding(top = 4.dp),

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


                Spacer(Modifier.height(8.dp))


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

                        // Allow digits only and restrict to 10 characters
                        onValueChange = { input ->
                            mobile = input
                                .filter { it.isDigit() }
                                .take(10)
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


                Spacer(Modifier.height(8.dp))


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

                    isError = showEmailError,

                    supportingText = {
                        if (showEmailError) {
                            Text("Invalid email format", color = Color.Red)
                        }
                    }
                )


                Spacer(Modifier.height(8.dp))


                /* -------- Category -------- */

                CategoryDropdown(

                    value = category,

                    onValueChange = {
                        category = it
                    },

                    isDark = isDark,

                    isError = showCategoryError
                )

                // External error message for category
                if (showCategoryError) {
                    Text(
                        "Category is required",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }


                Spacer(Modifier.height(8.dp))


                /* -------- Extras -------- */

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(

                        value = extras,

                        // Accept digits only and enforce maxExtras constraint
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

                    // Informational hint aligned right
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

        // Rounded dialog shape
        shape = RoundedCornerShape(20.dp),

        // Themed background color
        containerColor =
            MaterialTheme.colorScheme.inverseOnSurface
    )
}