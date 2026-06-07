package com.fhasanli.campusroombooker

import com.google.firebase.firestore.Exclude

/**
 * Data class representing a Room Booking.
 * It includes a no-argument constructor (via default values) for Firestore compatibility.
 */
data class Booking(
    @get:Exclude var documentId: String = "", // This field won't be saved in the document itself
    val roomName: String = "",
    val studentEmail: String = "",
    val bookingDate: String = "",
    val bookingTime: String = "",
    val status: String = "Pending"
)
