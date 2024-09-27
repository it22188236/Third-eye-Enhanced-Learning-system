package com.example.text_to_speech_3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

const val REQUEST_CODE = 200

class MenuActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    lateinit var mic:ImageView
    lateinit var viewOutPut:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        permissionGranted = ActivityCompat.checkSelfPermission(this@MenuActivity,permissions[0])==PackageManager.PERMISSION_GRANTED

        if (!permissionGranted){
            ActivityCompat.requestPermissions(this@MenuActivity,permissions, REQUEST_CODE)
        }

        mic = findViewById(R.id.btnmic)
        viewOutPut = findViewById(R.id.inputView)
        mic.setOnClickListener {
            checkAudioPermission()
            mic.setColorFilter(ContextCompat.getColor(this,R.color.black))
            speechToText()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkAudioPermission(){
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this@MenuActivity,permissions, REQUEST_CODE)
            return
        }
    }

    private fun speechToText(){
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.setRecognitionListener(object:RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onBeginningOfSpeech() {
                TODO("Not yet implemented")
            }

            override fun onRmsChanged(rmsdB: Float) {
                TODO("Not yet implemented")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                TODO("Not yet implemented")
            }

            override fun onEndOfSpeech() {
                mic.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_disable))
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    // Add other cases based on SpeechRecognizer error codes
                    else -> "Unknown error"
                }
                Toast.makeText(applicationContext, "Error occurred: $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle) {
                val result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    viewOutPut.text = result[0]

                    if (result[0].toString()=="open Gemini" || result[0].toString()=="Open Gemini" || result[0].toString()=="Open gemini" || result[0].toString()=="open gemini" || result[0].toString()=="OPEN GEMINI"){
                        val intent = Intent(this@MenuActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@MenuActivity,"Opening Gemini AI",Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                TODO("Not yet implemented")
            }

        })

        speechRecognizer.startListening(speechRecognizerIntent)
    }
}