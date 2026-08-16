package com.mhc.app.ui.emergency

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mhc.app.data.model.CrisisHelpline
import com.mhc.app.data.model.EmergencyContact

class EmergencyViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mhc_emergency_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    val nationalHelplines = listOf(
        CrisisHelpline(
            id = "h1",
            title = "National Emergency",
            category = "Police / Medical / Fire",
            phoneNumber = "112",
            hours = "24/7 Toll-Free",
            description = "Immediate emergency medical, police, and distress response."
        ),
        CrisisHelpline(
            id = "h2",
            title = "Kiran Mental Health Helpline",
            category = "Government Helpline",
            phoneNumber = "18005990019",
            hours = "24/7 Toll-Free",
            description = "Ministry of Social Justice 24/7 mental health counseling."
        ),
        CrisisHelpline(
            id = "h3",
            title = "Tele-MANAS Helpline",
            category = "Mental Health",
            phoneNumber = "14416",
            hours = "24/7 Toll-Free",
            description = "Government of India 24/7 tele-mental health services."
        ),
        CrisisHelpline(
            id = "h4",
            title = "AASRA Helpline",
            category = "Crisis Intervention",
            phoneNumber = "9820466726",
            hours = "24/7 Available",
            description = "Suicide prevention, emotional distress, and crisis support."
        ),
        CrisisHelpline(
            id = "h5",
            title = "Snehi India",
            category = "Psychosocial Support",
            phoneNumber = "9152987821",
            hours = "10 AM - 10 PM",
            description = "Crisis intervention and emotional health support."
        ),
        CrisisHelpline(
            id = "h6",
            title = "iCall (TISS)",
            category = "Psychological Counselling",
            phoneNumber = "9152987820",
            hours = "Mon-Sat: 8 AM - 10 PM",
            description = "Tata Institute of Social Sciences professional counselling."
        ),
        CrisisHelpline(
            id = "h7",
            title = "Vandrevala Foundation",
            category = "Mental Health",
            phoneNumber = "9999666555",
            hours = "24/7 Available",
            description = "Free 24/7 mental health support and crisis response."
        )
    )

    val customContacts = mutableStateListOf<EmergencyContact>()

    var isAddContactDialogOpen by mutableStateOf(false)
    var contactNameInput by mutableStateOf("")
    var contactRelationInput by mutableStateOf("")
    var contactPhoneInput by mutableStateOf("")

    init {
        loadCustomContacts()
    }

    private fun loadCustomContacts() {
        val savedJson = prefs.getString("user_emergency_contacts", null)
        if (!savedJson.isNull_blank()) {
            try {
                val type = object : TypeToken<List<EmergencyContact>>() {}.type
                val savedList: List<EmergencyContact> = gson.fromJson(savedJson, type)
                customContacts.clear()
                customContacts.addAll(savedList)
            } catch (e: Exception) {
                // Ignore load error
            }
        }
    }

    private fun String?.isNull_blank(): Boolean = this == null || this.trim().isEmpty()

    private fun saveCustomContacts() {
        val json = gson.toJson(customContacts.toList())
        prefs.edit().putString("user_emergency_contacts", json).apply()
    }

    fun openAddContactDialog() {
        contactNameInput = ""
        contactRelationInput = ""
        contactPhoneInput = ""
        isAddContactDialogOpen = true
    }

    fun closeAddContactDialog() {
        isAddContactDialogOpen = false
    }

    fun saveNewContact() {
        val name = contactNameInput.trim()
        val relation = contactRelationInput.trim().ifEmpty { "Trusted Contact" }
        val phone = contactPhoneInput.trim()

        if (name.isNotBlank() && phone.isNotBlank()) {
            customContacts.add(
                EmergencyContact(
                    name = name,
                    relationship = relation,
                    phoneNumber = phone
                )
            )
            saveCustomContacts()
            isAddContactDialogOpen = false
        }
    }

    fun deleteContact(contactId: String) {
        customContacts.removeAll { it.id == contactId }
        saveCustomContacts()
    }
}
