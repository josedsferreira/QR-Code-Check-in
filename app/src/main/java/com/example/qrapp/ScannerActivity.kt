package com.example.qrapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.zxing.integration.android.IntentIntegrator

class ScannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        // Initialize the scanner
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR Code")
        integrator.setCameraId(0)  // camara traseira
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)

        // Start scanning
        integrator.initiateScan()
    }

    // Handle the result in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedData = result.contents
                // Handle the scanned data as needed
                // For now, you can log it
                println("Scanned data: $scannedData")
                val database = Firebase.database("https://qrcodereader-d6599-default-rtdb.europe-west1.firebasedatabase.app/")
                val guestsRef: DatabaseReference = database.getReference("guests")

                // Método para atualizar o campo 'is_in' de um convidado
                fun updateIsInStatus(guestId: String, isIn: Boolean) {
                    // Referência ao nó do convidado específico
                    val guestNodeRef = guestsRef.child(guestId)

                    // Obter os valores de 'company' e 'name'
                    guestNodeRef.child("company").get().addOnSuccessListener { companySnapshot ->
                        val company = companySnapshot.value as? String

                        guestNodeRef.child("name").get().addOnSuccessListener { nameSnapshot ->
                            val name = nameSnapshot.value as? String

                            // Atualizar as TextViews com os valores obtidos
                            val textn = findViewById<TextView>(R.id.name)
                            val textc = findViewById<TextView>(R.id.company)
                            textn.text = name
                            textc.text = company

                            // Atualize o valor do campo 'is_in'
                            guestNodeRef.child("is_in").setValue(isIn)
                                .addOnSuccessListener {
                                    // A operação foi bem-sucedida
                                    println("Status 'is_in' atualizado com sucesso.")
                                }
                                .addOnFailureListener { e ->
                                    // A operação falhou
                                    println("Erro ao atualizar o status 'is_in': ${e.message}")
                                }
                        }
                    }
                }
                updateIsInStatus(scannedData,true)
            } else {
                // Handle no result
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}