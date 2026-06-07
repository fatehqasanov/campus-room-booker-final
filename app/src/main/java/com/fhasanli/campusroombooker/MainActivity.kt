package com.fhasanli.campusroombooker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 2. Session Check
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // 3. Bind UI components
        val welcomeText = findViewById<TextView>(R.id.welcomeTextView)
        val roomNameInput = findViewById<EditText>(R.id.roomNameEditText)
        val dateInput = findViewById<EditText>(R.id.dateEditText)
        val timeInput = findViewById<EditText>(R.id.timeEditText)
        val bookBtn = findViewById<Button>(R.id.bookButton)
        val logoutBtn = findViewById<Button>(R.id.logoutButton)
        val recyclerView = findViewById<RecyclerView>(R.id.bookingsRecyclerView)

        welcomeText.text = "Welcome, ${currentUser.email}"

        // 4. Setup RecyclerView with Delete Callback
        adapter = BookingAdapter(bookingList) { booking ->
            deleteBooking(booking)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 5. Fetch Bookings from Firestore
        fetchBookings()

        // 6. Handle Booking logic
        bookBtn.setOnClickListener {
            val roomName = roomNameInput.text.toString().trim()
            val date = dateInput.text.toString().trim()
            val time = timeInput.text.toString().trim()

            if (roomName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newBooking = Booking(
                roomName = roomName,
                studentEmail = currentUser.email ?: "",
                bookingDate = date,
                bookingTime = time,
                status = "Pending"
            )

            saveBooking(newBooking, roomNameInput, dateInput, timeInput)
        }

        // 7. Handle Logout
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveBooking(booking: Booking, vararg inputs: EditText) {
        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.msg_booking_success), Toast.LENGTH_SHORT).show()
                inputs.forEach { it.text.clear() }
                fetchBookings()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error adding booking", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBookings() {
        val userEmail = auth.currentUser?.email ?: return

        db.collection("bookings")
            .whereEqualTo("studentEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                bookingList.clear()
                for (document in documents) {
                    val booking = document.toObject(Booking::class.java)
                    booking.documentId = document.id // Important: capture the ID for deletion
                    bookingList.add(booking)
                }
                adapter.updateData(bookingList)
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error fetching bookings", e)
                Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteBooking(booking: Booking) {
        if (booking.documentId.isEmpty()) return

        db.collection("bookings")
            .document(booking.documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
                fetchBookings() // Refresh the list
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error deleting booking", e)
                Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
