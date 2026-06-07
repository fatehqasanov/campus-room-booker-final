package com.fhasanli.campusroombooker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Bind UI elements
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLinkTextView)

        // Registration Logic
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Input Validation using string resources
            if (email.isEmpty()) {
                emailEditText.error = getString(R.string.error_email_required)
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordEditText.error = getString(R.string.error_password_short)
                return@setOnClickListener
            }

            // Create User in Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success
                        Toast.makeText(this, getString(R.string.msg_register_success), Toast.LENGTH_SHORT).show()
                        
                        // Navigate to MainActivity after successful registration
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Registration failure
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Navigate to Login screen
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
