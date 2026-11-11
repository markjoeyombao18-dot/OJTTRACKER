package com.example.ojttrackerbyteambalagbag

data class JournalEntry(
    val id: Long,
    val date: String,
    val time: String,
    val title: String,
    val content: String,
    val hours: Double,
    val imageUri: String = "",
    val accountEmail: String = ""
)