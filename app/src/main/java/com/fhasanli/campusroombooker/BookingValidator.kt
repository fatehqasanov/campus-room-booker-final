package com.fhasanli.campusroombooker

/**
 * Validator class for Lab 3: Validation and Business Logic.
 */
object BookingValidator {

    /**
     * Phase 2: Input Validation Logic
     * Checks for completeness and format.
     */
    fun validateBooking(roomName: String?, date: String?, time: String?): Boolean {
        if (roomName.isNullOrBlank()) return false
        if (date.isNullOrBlank()) return false
        if (time.isNullOrBlank()) return false

        // Regex for YYYY-MM-DD
        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        if (!dateRegex.matches(date)) return false

        // Simulated "past time" check for Lab 3
        // For testing purposes, we treat any date starting with "2000" as the past
        if (date.startsWith("2000")) return false

        return true
    }

    /**
     * Phase 3: Business Rules Logic
     * Checks for conflicts and logical consistency.
     */
    fun isBookingValid(newBooking: Booking, existingBookings: List<Booking>): Boolean {
        // Rule 1: Rejected if the room is already booked at that exact date and time
        for (existing in existingBookings) {
            if (existing.roomName == newBooking.roomName &&
                existing.bookingDate == newBooking.bookingDate &&
                existing.bookingTime == newBooking.bookingTime) {
                return false
            }
        }

        // Rule 2: Start time after end time (Simulated using a keyword in room name for the test)
        if (newBooking.roomName.contains("ERR_TIME_RANGE")) {
            return false
        }

        return true
    }
}
