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

            if (email.isEmpty() || password.isEmpty()) {
                val message = "Insira um email e senha válidos"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(this, message, duration)
                toast.show()
                return@setOnClickListener
            }
            else if (password.length < 6) {
                val message = "A senha deve ter pelo menos 6 caracteres"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(this, message, duration)
                toast.show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Conta Criada!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Registo falhou.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        } //end of login button caller



    }
}