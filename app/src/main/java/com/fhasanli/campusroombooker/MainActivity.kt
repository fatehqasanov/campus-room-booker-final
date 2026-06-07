package com.fhasanli.campusroombooker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var realtimeDb: DatabaseReference
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()

    private lateinit var roomNameInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var bookBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var availabilityText: TextView
    private var availabilityListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        realtimeDb = FirebaseDatabase.getInstance().reference

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
        roomNameInput = findViewById(R.id.roomNameEditText)
        dateInput = findViewById(R.id.dateEditText)
        timeInput = findViewById(R.id.timeEditText)
        bookBtn = findViewById(R.id.bookButton)
        val logoutBtn = findViewById<Button>(R.id.logoutButton)
        val recyclerView = findViewById<RecyclerView>(R.id.bookingsRecyclerView)
        progressBar = findViewById(R.id.bookingProgressBar)
        availabilityText = findViewById(R.id.roomAvailabilityText)

        welcomeText.text = "CONNECTED: ${currentUser.email}"

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

            // PHASE 2: Use Validator for Business Logic
            if (!BookingValidator.validateBooking(roomName, date, time)) {
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

            saveBooking(newBooking)
        }

        // Realtime DB: Listen for status of a room when focus is lost (user finishes typing)
        roomNameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val room = roomNameInput.text.toString().trim()
                if (room.isNotEmpty()) {
                    listenToRoomAvailability(room)
                }
            }
        }

        // 7. Handle Logout
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Option C: Listen for real-time room status updates using Realtime Database.
     */
    private fun listenToRoomAvailability(roomName: String) {
        availabilityListener?.let {
            realtimeDb.child("room_status").child(roomName).removeEventListener(it)
        }

        availabilityListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: "Available"
                availabilityText.text = "Room Status ($roomName): $status"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Realtime DB Error", error.toException())
            }
        }

        realtimeDb.child("room_status").child(roomName).addValueEventListener(availabilityListener!!)
    }

    private fun saveBooking(booking: Booking) {
        // PHASE 3: Feedback & Polish - Show loading state
        progressBar.visibility = View.VISIBLE
        bookBtn.isEnabled = false
        bookBtn.text = "Booking..."

        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener {
                // Option C: Update Realtime DB status to "Occupied"
                realtimeDb.child("room_status").child(booking.roomName).setValue("Occupied")

                Toast.makeText(this, getString(R.string.msg_booking_success), Toast.LENGTH_SHORT).show()
                roomNameInput.text.clear()
                dateInput.text.clear()
                timeInput.text.clear()
                
                resetBookingButton()
                fetchBookings()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error adding booking", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                resetBookingButton()
            }
    }

    private fun resetBookingButton() {
        progressBar.visibility = View.GONE
        bookBtn.isEnabled = true
        bookBtn.text = getString(R.string.btn_book_room)
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
                    booking.documentId = document.id
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
                // Option C: Reset status in Realtime DB when booking is cancelled
                realtimeDb.child("room_status").child(booking.roomName).setValue("Available")
                
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
                fetchBookings()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error deleting booking", e)
                Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
