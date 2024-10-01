package com.example.text_to_speech_3


import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class InstructionActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var textToRead: String
    private lateinit var screen: ConstraintLayout
    lateinit var navigate:EditText
    private var ttsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_instruction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        screen = findViewById(R.id.main)
        navigate = findViewById(R.id.navigate)

        textToRead =
            "Third eye is an innovative Android application designed to assist blind people by leveraging the power of Gemini AI. The app provides advanced functionalities to enhance accessibility and support daily tasks through voice commands and AI-powered features. It provides four main functionalities:\" +\n" +
                    "                \"1) Using speech to text feature solve math problems.\" +\n" +
                    "                \"2) Using speech to text describe images.\" +\n" +
                    "                \"3) using speech to text identify realtime object detection.\" +\n" +
                    "                \"4) using text to speech feature for read pdf files stored in mobile memory.\" +\n" +
                    "                \"\" +\n" +
                    "                \"1. Using speech to text feature for controlling this application and solve maths problems\" +\n" +
                    "                \"How to use\" +\n" +
                    "                \"1. Open the app and then you are in instruction page. \" +\n" +
                    "                \"2. Touch the page and then you are in Menu page. use voice recognition button.\" +\n" +
                    "                \"3. That voice recognition button on the bottom part of the screen.\" +\n" +
                    "                \"4. Then touch the voice recognition button and say 'open gemini'\" +\n" +
                    "                \"5. Application wil recognise command and re-direct to the page with ai prompt.\" +\n" +
                    "                \"6. That page also has a voice button and touch it and say what do you know about."

        tts = TextToSpeech(this@InstructionActivity, this@InstructionActivity)

        if (tts.isSpeaking) {
            tts.stop()
        }


//        screen.setOnClickListener{
//            val intent = Intent(this@InstructionActivity,MainActivity2::class.java)
//            startActivity(intent)
//            tts.stop()
//        }
        navigate.setOnClickListener{
            val intent = Intent(this@InstructionActivity,MainActivity2::class.java)
            startActivity(intent)
            tts.stop()
        }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    this@InstructionActivity,
                    "Language Not Supported",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                speakText(textToRead)
                ttsInitialized = true
            }
        } else {
            Toast.makeText(
                this@InstructionActivity,
                "Text to speech Initialization failed!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        speakText(textToRead)
    }

    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

}
