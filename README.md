# Campus Room Booker

## Overview
Campus Room Booker is an Android application designed to help students quickly find and book available study rooms[cite: 2, 3]. By providing a real-time booking system, the app eliminates the time wasted searching for empty spaces on campus[cite: 2, 3].

**Developer:** Fateh Hasanli (Student ID: 57322)[cite: 1]

## Core Features
* **User Authentication:** Secure registration, login, and logout functionalities utilizing Firebase Email/Password Authentication[cite: 2].
* **Room Browsing & Booking:** Users can easily view a list of all available rooms and reserve them for specific times[cite: 3].
* **Real-Time Status Updates:** Live room status tracking (Available/Occupied) powered by Firebase Realtime Database[cite: 1]. The room availability reflects instantly across all devices without needing to manually refresh the screen[cite: 1].
* **Booking Management:** Students can view their current active bookings directly from their main dashboard so they remember their reservations[cite: 3].
* **Input Validation & UI Feedback:** Includes required field checks on the booking form and a clear loading state progress bar while fetching data or writing to the database[cite: 1, 2].

## Technical Architecture
The application is built with a straightforward client-server setup[cite: 3]:
* **Frontend (Android App):** Built with Kotlin, handling all UI components and presentation logic[cite: 3].
* **Backend (Firebase):** Handles all database reads and writes[cite: 3].
  * *Firebase Auth* for user management[cite: 3].
  * *Cloud Firestore* for structured data storage across three main collections: `users`, `rooms`, and `reservations`[cite: 3].
  * *Realtime Database* for instant live-status synchronization[cite: 1].

## Quality Assurance & Testing
The core business logic is highly reliable, backed by 8 passing JUnit tests located in `app/src/test/java/com/fhasanli/campusroombooker/BookingValidatorTest`[cite: 1].
Test coverage validates:
* Empty room name and null input handling[cite: 1].
* Valid versus invalid date formats[cite: 1].
* Booking conflict logic and duplicate booking prevention[cite: 1].
* Past time rejection and room status formatting[cite: 1].

## Setup Instructions
To build and run this project locally, follow these simple steps:
1. Clone this repository to your local machine: `git clone https://github.com/fatehqasanov/campus-room-booker-final.git`[cite: 3].
2. Open the newly cloned project folder in **Android Studio**[cite: 1].
3. Wait for Android Studio to download the SDKs and fully sync the Gradle files[cite: 2, 3].
4. Click the "Run" button to launch the application on an Android Emulator or a connected physical Android device[cite: 3].
