package com.example.text_to_speech_3

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.text_to_speech_3.MainActivity2 as mainActivity2

class MainActivity : AppCompatActivity() {

    lateinit var inputText:TextView
    lateinit var editTextOutput:EditText
    lateinit var mic:ImageView
    lateinit var chat:Chat
    private lateinit var texttospeech:TextToSpeech
    lateinit var resetbtn:Button
    lateinit var stopgeneration:Button
    private var generateJob:Job?=null
    lateinit var editbutton:ImageView

    var stringBuilder: StringBuilder = java.lang.StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mic = findViewById(R.id.clickMic)
        inputText = findViewById(R.id.inputtext)
        editTextOutput = findViewById(R.id.outputtext)
        resetbtn = findViewById(R.id.resetPrompt)
        stopgeneration = findViewById(R.id.stopGeneration)
        editbutton = findViewById(R.id.editbtn)

        texttospeech = TextToSpeech(this){status->
            if (status == TextToSpeech.SUCCESS){
                val result = texttospeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA|| result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this,"Language is not supported",Toast.LENGTH_LONG).show()
                }
            }
        }

        mic.setOnClickListener{
            startSpeechToText()
            mic.setColorFilter(ContextCompat.getColor(this,R.color.black))

        }

        resetbtn.setOnClickListener{
            editTextOutput.text.clear()
            chat.history.clear()
            inputText.setText("")
        }

        stopgeneration.setOnClickListener{
            stopGenerating()
        }

        editbutton.setOnClickListener{
            stopGenerating()
            startSpeechToText()
            inputText.text=""
        }
//        if (editbutton.isActivated){
//            editfunction()
//        }


        val generativeModel = GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = "AIzaSyApiz1AVg0BLnPdsggnI3Q_0BjMVft_Ct8"
        )

        chat = generativeModel.startChat(
            history = listOf(
                content(role = "model") {text("Hi!! I'm Gemini, What would you like to know?")},
                content(role = "user") { text("Hello, I want know something!!") },
            )
        )

//        stringBuilder.append("Hello, I want know something!!\n")
        stringBuilder.append("Hi!! I'm Gemini, What would you like to know?\n\n")

        editTextOutput.setText(stringBuilder.toString())
    }

    fun buttonSendChat(view: android.view.View) {
//        stringBuilder.append(inputText.text.toString())
//        MainScope().launch {
//            var result = chat.sendMessage(inputText.text.toString())
//            stringBuilder.append(result.text)
//
//            editTextOutput.setText(stringBuilder.toString())
//            inputText.setText("")
//        }

    }

    fun GotoHome(view: android.view.View) {
        val intent = Intent(this@MainActivity, mainActivity2::class.java)
        startActivity(intent)
        finish()
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
                    inputText.text = result[0]

                    stringBuilder.append("User : ",inputText.text.toString(),"\n")
                    MainScope().launch {
                        val result = chat.sendMessage(inputText.text.toString())
                        stringBuilder.append("AI Assistant : ",result.text,"\n\n")

                        editTextOutput.setText(stringBuilder.toString())
                        inputText.setText(inputText.text.toString())

                        if (result.text.toString()!=null){
                            if(editbutton.callOnClick()!=true){
                                if (editTextOutput.text.toString().trim().isNotEmpty()){
                                    texttospeech.speak(editTextOutput.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        null)
                                }
                                else{
                                    Toast.makeText(this@MainActivity,"Required",Toast.LENGTH_LONG).show()
                                }
                            }
//                            if (editTextOutput.text.toString().trim().isNotEmpty()){
//                                texttospeech.speak(editTextOutput.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,
//                                    null,
//                                    null)
//                            }
//                            else{
//                                Toast.makeText(this@MainActivity,"Required",Toast.LENGTH_LONG).show()
//                            }
                            }

//                        if (editTextOutput.text.toString().trim().isNotEmpty()){
//                            texttospeech.speak(editTextOutput.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,
//                                null,
//                                null)
//                        }
//                        else{
//                            Toast.makeText(this@MainActivity,"Required",Toast.LENGTH_LONG).show()
//                        }

                    }
                }
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}

        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun stopGenerating(){
        generateJob?.cancel()
        println("Generate Cancel")
    }

    override fun onDestroy() {
        super.onDestroy()
        generateJob?.cancel()
    }

//    private fun editfunction(){
//        editbutton.setOnClickListener{
//            stopGenerating()
//            startSpeechToText()
//            inputText.text=""
//        }
//    }

}