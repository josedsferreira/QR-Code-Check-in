package com.example.qrapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Register_qr : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_qr)

        auth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val editTextEmail = findViewById<TextInputEditText>(R.id.email)
        val editTextPassword = findViewById<TextInputEditText>(R.id.password)
        val buttonRegister = findViewById<Button>(R.id.registerButton)


        buttonRegister.setOnClickListener {
            val name = editTextEmail.text.toString()
            val company = editTextPassword.text.toString()

            if (name.isEmpty() || company.isEmpty()) {
                val message = "Insira um nome e uma empresa valida"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(this, message, duration)
                toast.show()
                return@setOnClickListener
            }else
            {
                insertDataToFirebase(name,company)
            }




        } //end of login button caller



    }
    fun insertDataToFirebase(name: String, company: String) {
        // Get a reference to the "guests" node in your Firebase Realtime Database
        val database = Firebase.database("https://qrcodereader-d6599-default-rtdb.europe-west1.firebasedatabase.app/")
        val guestsRef: DatabaseReference = database.getReference("guests")

        // Generate a unique key for the new guest
        val newGuestRef = guestsRef.push()

        // Create a map with the data to be inserted
        val guestData = mapOf(
            "name" to name,
            "company" to company,
            "is_in" to false // Assuming initially the guest is not in
        )

        // Set the data to the new guest reference
        newGuestRef.setValue(guestData)
            .addOnSuccessListener {
                // Data insertion successful
                // Handle success, if needed
            }
            .addOnFailureListener {
                // Data insertion failed
                // Handle failure, if needed
            }
    }
}