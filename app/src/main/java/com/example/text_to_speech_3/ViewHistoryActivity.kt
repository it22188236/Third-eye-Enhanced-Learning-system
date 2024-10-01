package com.example.text_to_speech_3

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.text_to_speech_3.model.db.ChatHistory
import com.example.text_to_speech_3.model.db.DBHelper
import java.util.Locale

class ViewHistoryActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var dbHelper: DBHelper
    private lateinit var userInputText:TextView
    private lateinit var aiResponseText:TextView
    private lateinit var timestampText:TextView
    private lateinit var delete:ImageView
    private lateinit var tts:TextToSpeech
    private lateinit var navigateHistory:ImageView
    private var chatID:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        dbHelper = DBHelper(this@ViewHistoryActivity)
        tts = TextToSpeech(this@ViewHistoryActivity,this@ViewHistoryActivity)

        userInputText = findViewById(R.id.viewUserinput)
        aiResponseText = findViewById(R.id.viewAiResponse)
        timestampText = findViewById(R.id.viewTimeStamp)
        delete = findViewById(R.id.deletebtnView)
        navigateHistory = findViewById(R.id.detailsbackbtn)
        aiResponseText.movementMethod = ScrollingMovementMethod()

        chatID = intent.getIntExtra("chat_id",-1)

        val chatHistory = dbHelper.getAllChatHistory().find { it.id == chatID }
        chatHistory?.let {
            userInputText.text = it.userMessage
            aiResponseText.text = it.aiResponse
            timestampText.text = it.timestamp
        }

        delete.setOnClickListener {
            dbHelper.deleteChatHistory(chatID)
            tts.stop()
            Toast.makeText(this@ViewHistoryActivity,"Chat history deleted",Toast.LENGTH_LONG).show()
            finish()
        }

        navigateHistory.setOnClickListener {
            tts.stop()
            val intent = Intent(this@ViewHistoryActivity,HistoryActivity::class.java)
            startActivity(intent)
        }

//        val userMessage = intent.getStringExtra("User_Message")
//        val aiResponse = intent.getStringExtra("AI_Response")
//        val timestamp = intent.getStringExtra("Timestamp")
//
//        userInputText.text = "User: $userMessage"
//        aiResponseText.text = "AI: $aiResponse"
//        timestampText.text = "TimeStamp : $timestamp"
//
//        delete.setOnClickListener {
//            dbHelper.deleteChatHistory(chatHistory.id)
//            val intent = Intent(this@ViewHistoryActivity,HistoryActivity::class.java)
//            startActivity(intent)
//        }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    this@ViewHistoryActivity,
                    "Language Not Supported",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                speakText(userInputText.text.toString())
                speakText("user says:"+userInputText.text.toString() + "And the "+"AI Response:"+aiResponseText.text.toString())
            }
        } else {
            Toast.makeText(
                this@ViewHistoryActivity,
                "Text to speech Initialization failed!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }


}