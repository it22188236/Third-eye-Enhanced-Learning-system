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
import com.example.text_to_speech_3.model.db.DBHelper
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.text_to_speech_3.MainActivity2 as mainActivity2

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    lateinit var inputText:TextView
    lateinit var editTextOutput:EditText
    lateinit var mic:ImageView
    lateinit var chat:Chat
    private lateinit var tts:TextToSpeech
    private lateinit var resetbtn:Button
    private lateinit var stopgeneration:Button
    private var generateJob:Job?=null
    lateinit var editbutton:ImageView
    private var textToRead = String
    lateinit var historyDirection:ImageView
    var isFirstConversation = true
    private lateinit var dbHelper:DBHelper


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
        historyDirection = findViewById(R.id.historybtn)

        tts  = TextToSpeech(this@MainActivity, this@MainActivity)

        mic.setOnClickListener{
            startSpeechToText()
        }

        resetbtn.setOnClickListener{
            editTextOutput.text.clear()
            chat.history.clear()
            inputText.text = ""
            tts.stop()
        }

        stopgeneration.setOnClickListener{
            stopGenerating()
        }

        editbutton.setOnClickListener{
            stopGenerating()
            startSpeechToText()
            inputText.text=""
        }

        historyDirection.setOnClickListener {
            val intent = Intent(this@MainActivity,HistoryActivity::class.java)
            startActivity(intent)
        }

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
//        editTextOutput.setText(textToRead.toString())
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
            override fun onBeginningOfSpeech() {
                mic.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_disable))
            }
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {
                mic.setColorFilter(ContextCompat.getColor(applicationContext, R.color.black))
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
                    speechRecognizer.destroy()
                    speakText("you says "+inputText.text.toString())

                    MainScope().launch {
                        val results = chat.sendMessage(inputText.text.toString())
                        stringBuilder.append("AI Assistant : ",results.text,"\n\n")
                        inputText.text = ""
                        editTextOutput.setText(stringBuilder.toString())

//                        inputText.text = inputText.text.toString()


//                        if(!editbutton.callOnClick()){
//                            if (editTextOutput.text.toString().trim().isNotEmpty()){
//                                tts.speak(editTextOutput.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,
//                                    null,
//                                    null)
//                            }
//                            else{
//                                Toast.makeText(this@MainActivity,"Required",Toast.LENGTH_LONG).show()
//                            }
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



    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Language Not Supported",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                speakText(textToRead.toString())
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "Text to speech Initialization failed!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
        generateJob?.cancel()
    }

    private fun speakText(text: String) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }


}