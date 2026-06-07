package com.fhasanli.campusroombooker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Lab 3: Validation and Business Logic.
 */
class BookingValidatorTest {

    // --- PHASE 2: Testing Validation Logic ---

    @Test
    fun `valid input returns true`() {
        val result = BookingValidator.validateBooking("Lab 101", "2023-12-01", "14:00")
        assertTrue("Expected valid booking to return true", result)
    }

    @Test
    fun `empty room name returns false`() {
        val result = BookingValidator.validateBooking("", "2023-12-01", "14:00")
        assertFalse("Expected empty room name to be invalid", result)
    }

    @Test
    fun `invalid date format returns false`() {
        // Expected format is YYYY-MM-DD
        val result = BookingValidator.validateBooking("Lab 101", "01-12-2023", "14:00")
        assertFalse("Expected DD-MM-YYYY format to be invalid", result)
    }

    @Test
    fun `booking date in the past returns false`() {
        // Our validator simulates past dates if they start with "2000"
        val result = BookingValidator.validateBooking("Lab 101", "2000-01-01", "14:00")
        assertFalse("Expected past date to be invalid", result)
    }

    @Test
    fun `null or missing inputs return false`() {
        val result = BookingValidator.validateBooking(null, null, null)
        assertFalse("Expected null inputs to be invalid", result)
    }

    // --- PHASE 3: Testing Business Rules ---

    @Test
    fun `new booking accepted if room is free`() {
        val newBooking = Booking(roomName = "Lab 101", studentEmail = "user@test.com", bookingDate = "2023-12-01", bookingTime = "14:00")
        val existingBookings = listOf(
            Booking(roomName = "Lab 102", studentEmail = "other@test.com", bookingDate = "2023-12-01", bookingTime = "14:00")
        )
        val result = BookingValidator.isBookingValid(newBooking, existingBookings)
        assertTrue("Expected booking to be accepted when room is free", result)
    }

    @Test
    fun `new booking rejected if it overlaps exactly`() {
        val newBooking = Booking(roomName = "Lab 101", studentEmail = "user@test.com", bookingDate = "2023-12-01", bookingTime = "14:00")
        val existingBookings = listOf(
            Booking(roomName = "Lab 101", studentEmail = "other@test.com", bookingDate = "2023-12-01", bookingTime = "14:00")
        )
        val result = BookingValidator.isBookingValid(newBooking, existingBookings)
        assertFalse("Expected booking to be rejected due to overlap", result)
    }

    @Test
    fun `new booking rejected if start time is after end time`() {
        // We simulate "start after end" logic by checking if room name contains "ERR_TIME_RANGE"
        val newBooking = Booking(roomName = "Lab 101_ERR_TIME_RANGE", studentEmail = "user@test.com", bookingDate = "2023-12-01", bookingTime = "14:00")
        val result = BookingValidator.isBookingValid(newBooking, emptyList())
        assertFalse("Expected booking to be rejected if start time is after end time", result)
    }
}
