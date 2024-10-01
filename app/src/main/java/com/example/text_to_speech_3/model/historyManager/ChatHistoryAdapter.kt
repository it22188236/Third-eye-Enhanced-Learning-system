package com.example.text_to_speech_3.model.historyManager


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.text_to_speech_3.R
import com.example.text_to_speech_3.model.db.ChatHistory


class ChatHistoryAdapter(
    private val chatHistoryList: List<ChatHistory>,
    private val onItemClick: (ChatHistory) ->Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {

    inner class ChatHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessage: TextView = view.findViewById(R.id.userInput)
        val aiResponse: TextView = view.findViewById(R.id.aiResponse)
        val timestamp:TextView = view.findViewById(R.id.timestamp)

        fun bind(chatHistory: ChatHistory){
            userMessage.text = chatHistory.userMessage
            aiResponse.text = chatHistory.aiResponse
            timestamp.text = chatHistory.timestamp

            itemView.setOnClickListener {
                onItemClick(chatHistory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view, parent, false)
        return ChatHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        val chatHistory = chatHistoryList[position]

//        holder.userMessage.text = "User: ${chatHistory.userMessage}"
//        holder.aiResponse.text = "AI: ${chatHistory.aiResponse}"
//
//        holder.deleteButton.setOnClickListener {
//            onDeleteClick(chatHistory)
//        }
        holder.bind(chatHistory)
    }

    override fun getItemCount(): Int {
        return chatHistoryList.size
    }
}

//class ChatHistoryAdapter(var data:List<ChatHistory>):RecyclerView.Adapter<ChatHistoryAdapter.viewHolder>(){
//
//    class viewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
//        var userInput = itemView.findViewById<TextView>(R.id.userInput)
//        var aiResponse = itemView.findViewById<TextView>(R.id.aiResponse)
//        var timestamp = itemView.findViewById<TextView>(R.id.timestamp)
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
//        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.view,parent,false)
//        return viewHolder(itemView)
//    }
//
//    override fun getItemCount(): Int {
//        return data.size
//    }
//
//    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        holder.userInput.text = data[position].userMessage
//        holder.aiResponse.text = data[position].aiResponse
//        holder.timestamp.text = data[position].timestamp
//            val intent = Intent(holder.itemView.context,HistoryActivity::class.java)
//            intent.putExtra("id",position)
//            holder.itemView.context.startActivity(intent)
//    }
//
//}