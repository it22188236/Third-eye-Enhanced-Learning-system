package com.example.text_to_speech_3.model.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):
    SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "THIRDEYE.db"
        private const val DATABASE_VERSION = 1
        private const val CHATHISTORYTABLE = "chatHistory"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_MESSAGE = "userMessage"
        private const val COLUMN_AI_RESPONSE = "aiResponse"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val IMAGETABLE = "imageTable"
        private const val COLUMN_IDENTIFY_TEXT = "text"


        private const val SQL_CREATE_ENTRIES = "CREATE TABLE $CHATHISTORYTABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_MESSAGE TEXT," +
                "$COLUMN_AI_RESPONSE TEXT," +
                "$COLUMN_TIMESTAMP TEXT)"

        private const val SQL_CREATE_IMAGE_ENTRIES = "CREATE TABLE $IMAGETABLE("+
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "$COLUMN_IDENTIFY_TEXT TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $CHATHISTORYTABLE"

        private const val SQL_DELETE_IMAGE_ENTRIES = "DROP TABLE IF EXISTS $IMAGETABLE"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_IMAGE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_IMAGE_ENTRIES)
        onCreate(db)
    }

    fun insertHistory(
        userMessage: String,
        aiResponse: String,
        timestamp: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_MESSAGE, userMessage)
            put(COLUMN_AI_RESPONSE, aiResponse)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(CHATHISTORYTABLE, null, values)

        db.close()
    }

    fun getAllChatHistory(): List<ChatHistory> {
        val chatList = mutableListOf<ChatHistory>()

        val selectQuery = "SELECT * FROM $CHATHISTORYTABLE ORDER BY $COLUMN_TIMESTAMP DESC"

        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            db.execSQL(selectQuery)
            return ArrayList()

        }
        var id:Int
        var userMessage: String
        var aiResponse: String
        var timestamp: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                userMessage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_MESSAGE))
                aiResponse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AI_RESPONSE))
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                chatList.add(ChatHistory(id,userMessage,aiResponse,timestamp))



            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return chatList


    }

    fun getDataUsingID(id:Int):Boolean{
        val db = readableDatabase
        val cursor:Cursor = db.rawQuery(
            "SELECT * FROM $CHATHISTORYTABLE WHERE $COLUMN_ID=? ",
            arrayOf(id.toString())
        )

        val exists = cursor.count>0
        cursor.close()
        return exists
    }

    fun deleteChatHistory(id: Int){
        val db = this.writableDatabase
        db.delete(CHATHISTORYTABLE,"$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun insertText(recognizedText: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_IDENTIFY_TEXT, recognizedText)
        }
        db.insert(IMAGETABLE, null, contentValues)
        db.close()
    }

    fun getAllText(): List<String> {
        val textList = ArrayList<String>()
        val db = this.readableDatabase
        val cursor = db.query(IMAGETABLE, arrayOf(COLUMN_IDENTIFY_TEXT), null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                textList.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IDENTIFY_TEXT)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return textList
    }


}




data class ChatHistory(
    val id:Int,
    val userMessage: String,
    val aiResponse: String,
    val timestamp: String

)

