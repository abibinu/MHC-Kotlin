package com.mhc.app.data.model

data class CrisisHelpline(
    val id: String,
    val title: String,
    val category: String,
    val phoneNumber: String,
    val hours: String = "24/7 Available",
    val description: String
)

data class EmergencyContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val relationship: String,
    val phoneNumber: String
)
