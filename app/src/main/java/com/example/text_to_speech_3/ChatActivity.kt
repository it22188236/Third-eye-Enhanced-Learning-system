package com.example.text_to_speech_3

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.text_to_speech_3.model.db.DBHelper
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale

class ChatActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private lateinit var micbtn:ImageView
    private lateinit var ChatHistory:ImageView
    private lateinit var chatHistoryView : TextView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var navigateBack:ImageView
    private lateinit var resetFunction:Button
    private lateinit var textRecognizer:TextView
    private lateinit var sendToAi:ImageView

    private lateinit var chat: Chat
    
    private lateinit var dbHelper: DBHelper

    var stringBuilder: StringBuilder = java.lang.StringBuilder()

    companion object{
        private  var textRecognizerText = String()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        micbtn = findViewById(R.id.clickMic)
        resetFunction = findViewById(R.id.resetPrompt)
        ChatHistory = findViewById(R.id.historybtn)
        chatHistoryView = findViewById(R.id.outputtext)
        navigateBack = findViewById(R.id.backbtn)
        textRecognizer = findViewById(R.id.recognizerCapture)
        sendToAi = findViewById(R.id.sendProcess)
        chatHistoryView.movementMethod = ScrollingMovementMethod()
        textRecognizer.movementMethod = ScrollingMovementMethod()
        
        dbHelper = DBHelper(this@ChatActivity)
        textToSpeech = TextToSpeech(this@ChatActivity,this@ChatActivity)
        
        micbtn.setOnClickListener {
            textToSpeech.stop()
            startVoiceRecognition()
        }
        
        ChatHistory.setOnClickListener {
            textToSpeech.stop()
            val intent = Intent(this@ChatActivity,HistoryActivity::class.java)
            startActivity(intent)
        }

        navigateBack.setOnClickListener {
            textToSpeech.stop()
            val intent = Intent(this@ChatActivity,MainActivity2::class.java)
            startActivity(intent)
        }

        resetFunction.setOnClickListener {
            chatHistoryView.text = ""
            textRecognizer.text = ""
            textToSpeech.stop()
        }

        sendToAi.setOnClickListener {
            sendRequestForAI()
        }

        val recognizedTextFromRecognizer = intent.getStringExtra("Recognized_Text")

        val textView = findViewById<TextView>(R.id.recognizerCapture).apply {
            text = recognizedTextFromRecognizer
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
    }

//    private fun retrieveChatHistory() {
//        val chatHistory = dbHelper.getAllChatHistory()
//        val historyText = StringBuilder()
//        for (chat in chatHistory) {
//            historyText.append("User: ${chat.userMessage}\n")
//            historyText.append("AI: ${chat.aiResponse}\n")
//            historyText.append("Timestamp: ${chat.timestamp}\n\n")
//        }
//        chatHistoryView.text = historyText.toString().trim()
//    }

    private fun startVoiceRecognition() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(errorCode: Int) {
                Toast.makeText(applicationContext, "Speech Recognition Error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    val userInput = result[0]

                    speechRecognizer.destroy()
                    textToSpeech.speak(
                        "you says " + userInput.toString().trim(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    MainScope().launch {
                        val results = chat.sendMessage(result.toString())
                        stringBuilder.append("AI Assistant : ", results.text, "\n\n")
//                        chatHistoryView.text = results.text.toString().trim()
                        chatHistoryView.setText(results.text.toString().trim())
                        textToSpeech.speak(
                            "AI Response " + results.text.toString().trim(),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        if (userInput != null) {
                            dbHelper.insertHistory(
                                userInput,
                                results.text.toString(),
                                getCurrentTimeStamp()
                            )
                        }

                    }
                }


//                val text = bundle.getStringArrayList(getIntent().toString())
//                val textValue = textRecognizer.text.toString()
//                if (textValue == textRecognizer.text){
//                    speechRecognizer.destroy()
//                    textToSpeech.speak("you says "+textRecognizer.text .toString().trim(),TextToSpeech.QUEUE_FLUSH,null,null)
//
//
//                    MainScope().launch {
//                        val results = chat.sendMessage(textRecognizer.text .toString())
//                        stringBuilder.append("AI Assistant : ", results.text, "\n\n")
////                        chatHistoryView.text = results.text.toString().trim()
//                        chatHistoryView.setText(results.text.toString().trim())
//                        textToSpeech.speak("AI Response "+results.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,null,null)
//                        if (textRecognizer.text  != null) {
//                            dbHelper.insertHistory(textRecognizer.text.toString(),results.text.toString(),getCurrentTimeStamp())
//                        }
//
//                    }
//                }
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    private fun getCurrentTimeStamp(): String {
        return System.currentTimeMillis().toString()

    }

//    private fun sendToAI(userInput:String){
//        MainScope().launch {
//            try {
//                val aiResponse = callAiAPI(userInput)
//                textToSpeech.speak(aiResponse,TextToSpeech.QUEUE_FLUSH,null,null)
//
//                dbHelper.insertHistory(userInput,aiResponse,getCurrentTimeStamp())
//            }
//            catch (e:Exception){
//                Toast.makeText(this@ChatActivity,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    private fun callAiAPI(userInput: String): String {
//
//        return "Ai Response to '$userInput"
//
//    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    this@ChatActivity,
                    "Language Not Supported",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

            }
        } else {
            Toast.makeText(
                this@ChatActivity,
                "Text to speech Initialization failed!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun sendRequestForAI(){
//        val textView:TextView = findViewById(R.id.recognizeText)
//        val textValue = textView.text.toString()
//            MainScope().launch {
//                val result = chat.sendMessage(textValue)
//                stringBuilder.append("AI Assistant : ", result.text, "\n\n")
//                chatHistoryView.text = result.text.toString().trim()
//                textToSpeech.speak("AI Response "+result.text.toString().trim(),TextToSpeech.QUEUE_FLUSH,null,null)
//
//                dbHelper.insertHistory(textValue,result.text.toString(),getCurrentTimeStamp())
//            }
        }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }
}

