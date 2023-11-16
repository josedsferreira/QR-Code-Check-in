package com.example.qrapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            } else {
                // Handle no result
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}