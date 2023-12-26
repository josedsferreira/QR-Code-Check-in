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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import utilitarios

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
        val editTextStaff = findViewById<TextInputEditText>(R.id.staffcode)
        val buttonRegister = findViewById<Button>(R.id.registerButton)
        val loginNow = findViewById<TextView>(R.id.loginNow)

        loginNow.setOnClickListener {
            // intent to login activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val staff = editTextStaff.text.toString()

            if (email.isEmpty() || password.isEmpty() || staff.isEmpty()) {
                utilitarios.showToast("Insira um email, senha e código válidos", this@Register)
                return@setOnClickListener
            } else if (password.length < 6) {
                utilitarios.showToast("A senha deve ter pelo menos 6 caracteres", this@Register)
                return@setOnClickListener
            }

            checkStaffAndRegister(email, password, staff)
        }// fim do register button



    }

    private fun checkStaffAndRegister(email: String, password: String, staff: String) {
        val database = Firebase.database("https://qrcodereader-d6599-default-rtdb.europe-west1.firebasedatabase.app/")
        val staffRef = database.getReference("staff").child(staff)

        staffRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Staff code exists in the "staff" node
                    val isRegistered = dataSnapshot.child("is_registered").getValue(Boolean::class.java)

                    if (isRegistered == true) {
                        // Staff is already registered
                        utilitarios.showToast("Funcionario já registado", this@Register)
                    } else {
                        // Staff is not registered, update the "is_registered" field to true
                        staffRef.child("is_registered").setValue(true)

                        // Continue with user registration
                        registerUser(email, password)
                    }
                } else {
                    // Staff code não existe
                    utilitarios.showToast("Código de funcionário inválido.", this@Register)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                println("Database error: ${databaseError.message}")
            }
        })
    }



    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // sucesso
                    utilitarios.showToast("Conta Criada!", this@Register)
                    val intent = Intent(this@Register, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // falhou
                    utilitarios.showToast("Registo falhou!", this@Register)
                }
            }
    }

}