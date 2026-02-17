package kr.android.invitationlistapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.android.invitationlistapp.ui.theme.textFieldColorsDark
import kr.android.invitationlistapp.ui.theme.textFieldColorsLight

// Opting into experimental API because ExposedDropdownMenuBox
// is still marked experimental in Material3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeDropdown(
    value: String,                     // Currently selected country code
    onValueChange: (String) -> Unit,   // Callback when user selects a code
    isDark: Boolean                    // Used for theming the text field
){

    // Controls whether dropdown is expanded or collapsed
    var expanded by remember { mutableStateOf(false) }

    // Scroll state for long country code list inside dropdown
    val scrollState = rememberScrollState()

    // Large static list of international dialing codes
    // Wrapped in remember to avoid recreating list on recomposition
    val countryCodeList = remember {
        listOf(
            "+1", "+7", "+20", "+27", "+30", "+31", "+32", "+33", "+34", "+36", "+39", "+40", "+41", "+43",
            "+44", "+45", "+46", "+47", "+48", "+49", "+52", "+53", "+54", "+55", "+56", "+57", "+58", "+60",
            "+61", "+62", "+63", "+64", "+65", "+66", "+81", "+82", "+84", "+86", "+90", "+91", "+92", "+93",
            "+94", "+95", "+98", "+211", "+212", "+213", "+216", "+218", "+220", "+221", "+222", "+223", "+224",
            "+225", "+226", "+227", "+228", "+229", "+230", "+231", "+232", "+233", "+234", "+235", "+236",
            "+237", "+238", "+239", "+240", "+241", "+242", "+243", "+244", "+245", "+246", "+248", "+249",
            "+250", "+251", "+252", "+253", "+254", "+255", "+256", "+257", "+258", "+260", "+261", "+262",
            "+263", "+264", "+265", "+266", "+267", "+268", "+269", "+290", "+291", "+297", "+298", "+299",
            "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+357", "+358", "+359", "+370", "+371",
            "+372", "+373", "+374", "+375", "+376", "+377", "+378", "+380", "+381", "+382", "+383", "+385",
            "+386", "+387", "+389", "+420", "+421", "+423", "+500", "+501", "+502", "+503", "+504", "+505",
            "+506", "+507", "+508", "+509", "+590", "+591", "+592", "+593", "+594", "+595", "+596", "+597",
            "+598", "+599", "+670", "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679", "+680",
            "+681", "+682", "+683", "+685", "+686", "+687", "+688", "+689", "+690", "+691", "+692", "+850",
            "+852", "+853", "+855", "+856", "+880", "+886", "+960", "+961", "+962", "+963", "+964", "+965",
            "+966", "+967", "+968", "+970", "+971", "+972", "+973", "+974", "+975", "+976", "+977", "+992",
            "+993", "+994", "+995", "+996", "+998"
        )
    }

    // Container that links the text field and dropdown together
    ExposedDropdownMenuBox(
        expanded = expanded,
        // Toggle dropdown when text field is clicked
        onExpandedChange = { expanded = !expanded }
    ) {

        // Read-only field used as dropdown trigger
        OutlinedTextField(
            value = value,
            onValueChange = {},  // Disabled because selection happens via dropdown
            readOnly = true,     // Prevents manual editing
            placeholder = { Text("+91") }, // Default visual hint
            trailingIcon = {
                // Default Material3 dropdown arrow icon
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true,
            // menuAnchor ensures dropdown positions correctly relative to this field
            modifier = Modifier.menuAnchor(),
            colors = if (isDark)
                textFieldColorsDark()
            else
                textFieldColorsLight(),
        )

        // Dropdown content
        ExposedDropdownMenu(
            expanded = expanded,
            // Close dropdown when user taps outside
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp) // Rounded menu corners
        ) {

            // Wrapping items in Column to apply scroll behavior
            Column(
                modifier = Modifier
                    // Prevent dropdown from becoming too tall
                    .heightIn(max = 400.dp)
                    // Enable vertical scrolling for long list
                    .verticalScroll(scrollState),

                ) {

                // Render each country code as selectable item
                countryCodeList.forEach { code ->

                    DropdownMenuItem(
                        text = { Text(code) },
                        onClick = {
                            // Update parent state with selected code
                            onValueChange(code)

                            // Collapse dropdown after selection
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}