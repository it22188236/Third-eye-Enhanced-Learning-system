package com.example.text_to_speech_3

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.text_to_speech_3.model.db.DBHelper
import com.example.text_to_speech_3.model.historyManager.ChatHistoryAdapter

class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var history:EditText
    private lateinit var navigateBack:ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatHistoryAdapter: ChatHistoryAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
        dbHelper = DBHelper(this@HistoryActivity)
        navigateBack = findViewById(R.id.backbtn2)

        retrieveChatHistory()

//        delete.setOnClickListener {
//            dbHelper.deleteChatHistory()
//            Toast.makeText(this@HistoryActivity,"Record Deleted",Toast.LENGTH_SHORT).show()
//        }

        navigateBack.setOnClickListener {
            val intent = Intent(this@HistoryActivity,ChatActivity::class.java)
            startActivity(intent)
        }

    }

    private fun retrieveChatHistory() {
        val chatList = dbHelper.getAllChatHistory()

        if (chatList.isNotEmpty()){
            chatHistoryAdapter = ChatHistoryAdapter(chatList){
                chatHistory ->
                val intent = Intent(this@HistoryActivity,ViewHistoryActivity::class.java)
                intent.putExtra("chat_id",chatHistory.id)
                startActivity(intent)
            }
            recyclerView.adapter = chatHistoryAdapter
        }
        else{
            Toast.makeText(this@HistoryActivity,"No History Found",Toast.LENGTH_LONG).show()
        }
//        chatHistoryAdapter = ChatHistoryAdapter(
//            chatList,
//            onItemClick = {
//                chatHistory ->
//                val intent = Intent(this@HistoryActivity,ViewHistoryActivity::class.java)
//                intent.putExtra("chat_id",chatHistory.id)
//                startActivity(intent)
//            }
//        )
//        recyclerView.adapter = chatHistoryAdapter
//        recyclerView.adapter = chatHistoryAdapter
//        val historyText = StringBuilder()
//        for (chat in chatHistory) {
//            historyText.append("User: ${chat.userMessage}\n")
//            historyText.append("AI: ${chat.aiResponse}\n")
//            historyText.append("Timestamp: ${chat.timestamp}\n\n")
//        }
//        history.setText(historyText.toString().trim())
//
//        if (chatHistory.isEmpty()){
//            findViewById<TextView>(R.id.tv_no_history).visibility = View.VISIBLE
//        }
//        else{
//            findViewById<TextView>(R.id.tv_no_history).visibility = View.GONE
//            chatHistoryAdapter = ChatHistoryAdapter(chatHistory)
//            recyclerView.adapter = chatHistoryAdapter
//
//        }

    }

    override fun onResume() {
        super.onResume()
        retrieveChatHistory()
    }

//    private fun onDeleteClick(chatHistory: ChatHistory) {
//        dbHelper.deleteChatHistory(chatHistory.id)
//        Toast.makeText(this, "Deleted Chat: ${chatHistory.userMessage}", Toast.LENGTH_SHORT).show()
//        retrieveChatHistory() // Refresh the list
//    }
}