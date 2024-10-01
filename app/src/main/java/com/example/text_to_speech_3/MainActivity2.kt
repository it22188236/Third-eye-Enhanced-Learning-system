package com.example.text_to_speech_3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale


class MainActivity2 : AppCompatActivity() {

    lateinit var mic:ImageView
    lateinit var viewOutPut:TextView
    lateinit var instruction:ImageView
    lateinit var link_gemini:Button
    lateinit var link_scanner:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mic = findViewById(R.id.micImage)
        viewOutPut = findViewById(R.id.textViewOutput)
        instruction = findViewById(R.id.instruction_btn)
        link_gemini = findViewById(R.id.link_gemini)
        link_scanner = findViewById(R.id.scannerbtn)

        mic.setOnClickListener {
//            speak()
            checkAudioPermission()
            mic.setColorFilter(ContextCompat.getColor(this,R.color.black))
            startSpeechToText()
        }

        instruction.setOnClickListener {
            instruction()
        }

        link_gemini.setOnClickListener {
            val intent =Intent(this@MainActivity2,ChatActivity::class.java)
            startActivity(intent)
        }

        link_scanner.setOnClickListener {
            val intent = Intent(this@MainActivity2,ScannerActivity::class.java)
            startActivity(intent)
        }
    }

//    fun speak(){
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"SPEAK NOW")
//
//        try {
//            startActivityForResult(intent,99)
//        }
//        catch (e:Exception){
//            Toast.makeText(this," "+e.message,Toast.LENGTH_LONG).show()
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

//        if(requestCode==99){
//            if (resultCode == RESULT_OK){
//                val res: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
//                viewOutPut.setText(Objects.requireNonNull(res)[0])
//                viewOutPut.setText(data!!.getCharArrayExtra(RecognizerIntent.EXTRA_RESULTS)!![0])
//            }
//        }
//
//        if (requestCode==99 && resultCode == RESULT_OK){
//            viewOutPut.setText(data!!.getStringArrayExtra(RecognizerIntent.EXTRA_RESULTS)!![0])
//        }
//    }
    private fun instruction(){
        val intent = Intent(this@MainActivity2,InstructionActivity::class.java)
        startActivity(intent)
    }

    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {
                // changing the color of our mic icon to
                // gray to indicate it is not listening
                mic.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_disable)) // #FF6D6A6A
            }

            override fun onError(errorCode: Int) {
                val message = when (errorCode) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    // Add other cases based on SpeechRecognizer error codes
                    else -> "Unknown error"
                }
                Toast.makeText(applicationContext, "Error occurred: $message", Toast.LENGTH_SHORT).show()
            }


            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    viewOutPut.text = result[0]

//                    if (result[0].toString()=="open Gemini" || result[0].toString()=="Open Gemini" || result[0].toString()=="Open gemini" || result[0].toString()=="open gemini" || result[0].toString()=="OPEN GEMINI"){
                    if (result[0].toString().lowercase()=="open gemini"){
                        val intent = Intent(this@MainActivity2,ChatActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@MainActivity2,"Opening Gemini AI",Toast.LENGTH_LONG).show()
                    }

                    else if (result[0].toString().lowercase()=="open object detection"){
                        val intent = Intent(this@MainActivity2,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@MainActivity2,"Opening Gemini AI",Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}

        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkAudioPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,"android.permission.RECORD_AUDIO")!=PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:com.programmingtech.offlinespeechtotext"))
                    startActivity(intent)
                    Toast.makeText(this,"Allow Microphone Permission",Toast.LENGTH_LONG).show()
            }
        }
    }
}