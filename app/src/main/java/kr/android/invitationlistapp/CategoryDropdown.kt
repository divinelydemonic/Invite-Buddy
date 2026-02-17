package kr.android.invitationlistapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kr.android.invitationlistapp.ui.theme.textFieldColorsDark
import kr.android.invitationlistapp.ui.theme.textFieldColorsLight

// Using ExperimentalMaterial3Api because ExposedDropdownMenuBox
// is still marked experimental in Material3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    value: String,                     // Currently selected category
    onValueChange: (String) -> Unit,   // Callback when a category is selected
    isDark: Boolean,                   // Used for theming the text field
    isError: Boolean                   // Controls error UI state
) {

    // Controls dropdown expansion state
    var expanded by remember { mutableStateOf(false) }

    // Static list of available categories
    // Can later be externalized or made dynamic if needed
    val categoriesList = listOf(
        "Family",
        "Friend",
        "Relative",
        "Colleague",
        "Acquaintance"
    )

    // ExposedDropdownMenuBox handles anchor + dropdown behavior together
    ExposedDropdownMenuBox(
        expanded = expanded,
        // Toggle dropdown visibility when text field is clicked
        onExpandedChange = { expanded = !expanded }
    ) {

        // Read-only text field acting as dropdown trigger
        OutlinedTextField(
            value = value,
            onValueChange = {}, // No-op since selection happens via dropdown
            readOnly = true,    // Prevents manual text input
            label = { Text("Category") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Category,
                    contentDescription = "category"
                )
            },
            // Material3 default trailing arrow icon
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true,

            // Apply theme-specific text field colors
            colors = if (isDark)
                textFieldColorsDark()
            else
                textFieldColorsLight(),

            // menuAnchor() connects this field to the dropdown positioning
            modifier = Modifier.menuAnchor().fillMaxWidth(),

            // Show error border if validation fails
            isError = isError,

            // Supporting error message shown below text field
            supportingText = {
                if (isError) {
                    Text("Category is required", color = Color.Red)
                }
            },
        )

        // Dropdown menu content
        ExposedDropdownMenu(
            expanded = expanded,
            // Close dropdown when clicking outside
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp) // Slightly rounded dropdown corners
        ) {

            // Render each category as selectable menu item
            categoriesList.forEach {

                DropdownMenuItem(
                    text = { Text(it) },

                    // When user selects an item:
                    // 1. Update parent state
                    // 2. Collapse dropdown
                    onClick = {
                        onValueChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}