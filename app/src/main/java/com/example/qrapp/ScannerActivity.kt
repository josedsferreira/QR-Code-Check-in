package com.example.qrapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
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
        integrator.setPrompt("")
        integrator.setCameraId(0)  // camara traseira
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        // Start scanning
        integrator.initiateScan()

        layoutState(3) // iniciar com layout de loading
    } // fim do onCreate

    // Handle the result in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedData = result.contents
                println("Scanned data: $scannedData")

                val database = Firebase.database("https://qrcodereader-d6599-default-rtdb.europe-west1.firebasedatabase.app/")
                val guestsRef: DatabaseReference = database.getReference("guests")

                updateIsInStatus(guestsRef, scannedData,true)

                val lqrbutton = findViewById<Button>(R.id.lerqr)
                lqrbutton.setOnClickListener {
                    val intent = Intent(this, ScannerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                // Handle no result da db
                layoutState(1)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            layoutState(1)
        }
    } // fim do onActivityResult


    // Método para atualizar o campo 'is_in' de um convidado
    fun updateIsInStatus(guestsRef: DatabaseReference, guestId: String, isIn: Boolean) {
        // Referência ao nó do convidado específico
        val guestNodeRef = guestsRef.child(guestId)

        // Obter os valores de 'company' e 'name'
        guestNodeRef.child("company").get().addOnSuccessListener { companySnapshot ->
            val company = companySnapshot.value as? String

            guestNodeRef.child("name").get().addOnSuccessListener { nameSnapshot ->
                val name = nameSnapshot.value as? String

                guestNodeRef.child("is_in").get().addOnSuccessListener { isInSnapshot ->
                    val isGuestIn = isInSnapshot.value as? Boolean

                    // verificar que o guest existe na db
                    if (name != null && company != null && isGuestIn == false) {
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
                                layoutState(0)
                            }
                            .addOnFailureListener { e ->
                                // A operação falhou
                                println("Erro ao atualizar o status 'is_in': ${e.message}")
                            }
                    }
                    else if(name != null && company != null && isGuestIn == true) {
                        // convidado existe mas já deu entrada
                        val textn = findViewById<TextView>(R.id.name)
                        val textc = findViewById<TextView>(R.id.company)
                        textn.text = name
                        textc.text = company
                        layoutState(2)
                    } else {
                        // Erro, o convidado não existe na db
                        println("Guest not found in the database.")
                        layoutState(1)
                    }
                }
            }
        }
    } // fim do updateIsInStatus

    private fun layoutState(state: Int = 0) {
        //state = 0 -> não existe erro, esconder o layout de erro
        //state = 1 -> convidado não existe
        //state = 2 -> convidado já deu entrada
        //state = 3 -> mostrar o layout de loading

        val progbar = findViewById<View>(R.id.progressBar)
        val erroView = findViewById<TextView>(R.id.errorView)
        val titulo = findViewById<TextView>(R.id.tituloView)
        val nomeView = findViewById<TextView>(R.id.nomeView)
        val nameText = findViewById<TextView>(R.id.name)
        val empresaView = findViewById<TextView>(R.id.empresaView)
        val empresaText = findViewById<TextView>(R.id.company)
        val erroJaEntrou = findViewById<TextView>(R.id.erroJaEntrou)
        val lqrbutton = findViewById<Button>(R.id.lerqr)

        if (state == 0) { // não existe erro
            progbar.visibility = View.GONE
            erroView.visibility = View.GONE
            erroJaEntrou.visibility = View.GONE
            titulo.visibility = View.VISIBLE
            nomeView.visibility = View.VISIBLE
            nameText.visibility = View.VISIBLE
            empresaView.visibility = View.VISIBLE
            empresaText.visibility = View.VISIBLE
            lqrbutton.visibility = View.VISIBLE
        } else if(state == 1){ // convidado não existe
            progbar.visibility = View.GONE
            erroView.visibility = View.VISIBLE
            erroJaEntrou.visibility = View.GONE
            titulo.visibility = View.GONE
            nomeView.visibility = View.GONE
            nameText.visibility = View.GONE
            empresaView.visibility = View.GONE
            empresaText.visibility = View.GONE
            lqrbutton.visibility = View.VISIBLE
        } else if (state == 2) { // convidado já deu entrada
            progbar.visibility = View.GONE
            erroJaEntrou.visibility = View.VISIBLE
            erroView.visibility = View.GONE
            titulo.visibility = View.VISIBLE
            nomeView.visibility = View.VISIBLE
            nameText.visibility = View.VISIBLE
            empresaView.visibility = View.VISIBLE
            empresaText.visibility = View.VISIBLE
            lqrbutton.visibility = View.VISIBLE
        } else if (state == 3) { // mostrar o layout de loading
            progbar.visibility = View.VISIBLE
            erroView.visibility = View.GONE
            erroJaEntrou.visibility = View.GONE
            titulo.visibility = View.GONE
            nomeView.visibility = View.GONE
            nameText.visibility = View.GONE
            empresaView.visibility = View.GONE
            empresaText.visibility = View.GONE
            lqrbutton.visibility = View.GONE
        }
    } // fim do layoutErro


}