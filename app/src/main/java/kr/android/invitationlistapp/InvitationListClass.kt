package kr.android.invitationlistapp

// Data model representing a single invitee entry
// This acts as the core state unit for the invitation list
data class InvitationListClass (

    var id: Int,              // Unique identifier (currently generated using timestamp)

    var name: String,        // Invitee name (required field)

    var email: String,       // Email address (optional; stored as "NA" if not provided)

    var mobile: String,      // Full mobile number including country code (e.g., +919876543210)

    var category: String,    // Category classification (Family, Friend, etc.)

    var extras: Int,         // Number of additional guests accompanying invitee

    var status: String,      // Invitation status ("PENDING", "ACCEPTED", "REJECTED")

    // Flag to indicate whether this item is currently being edited
    // Not heavily used in current implementation (editing is handled via editingId in list)
    var isEditing: Boolean = false
)