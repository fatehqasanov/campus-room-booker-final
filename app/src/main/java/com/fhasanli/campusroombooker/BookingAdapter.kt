package com.fhasanli.campusroombooker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onDeleteClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.roomNameText)
        val dateTime: TextView = view.findViewById(R.id.dateTimeText)
        val status: TextView = view.findViewById(R.id.statusText)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.roomName.text = booking.roomName
        holder.dateTime.text = "${booking.bookingDate} at ${booking.bookingTime}"
        holder.status.text = booking.status

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(booking)
        }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
