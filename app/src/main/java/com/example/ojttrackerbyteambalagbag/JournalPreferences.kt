package com.example.ojttrackerbyteambalagbag

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class JournalPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "OJT_JOURNAL_PREFS",
        Context.MODE_PRIVATE
    )

    private val journalKey = "journal_entries"

    fun saveJournal(entry: JournalEntry) {

        val entries = getJournals().toMutableList()

        entries.add(entry)

        saveAllJournals(entries)
    }

    fun getJournals(): List<JournalEntry> {

        val jsonString = preferences.getString(journalKey, null)
            ?: return emptyList()

        return try {

            val jsonArray = JSONArray(jsonString)
            val entries = mutableListOf<JournalEntry>()

            for (i in 0 until jsonArray.length()) {

                val item = jsonArray.getJSONObject(i)

                entries.add(
                    JournalEntry(
                        id = item.getLong("id"),
                        date = item.getString("date"),
                        time = item.getString("time"),
                        title = item.getString("title"),
                        content = item.getString("content"),
                        hours = item.getDouble("hours"),
                        imageUri = item.optString("imageUri", "")
                    )
                )
            }

            entries

        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveAllJournals(
        entries: List<JournalEntry>
    ) {

        val jsonArray = JSONArray()

        entries.forEach { entry ->

            val jsonObject = JSONObject()

            jsonObject.put("id", entry.id)
            jsonObject.put("date", entry.date)
            jsonObject.put("time", entry.time)
            jsonObject.put("title", entry.title)
            jsonObject.put("content", entry.content)
            jsonObject.put("hours", entry.hours)
            jsonObject.put("imageUri", entry.imageUri)

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(
                journalKey,
                jsonArray.toString()
            )
            .apply()
    }

    fun deleteJournal(id: Long) {

        val updatedEntries = getJournals()
            .filter { it.id != id }

        saveAllJournals(updatedEntries)
    }

    fun getTotalHours(): Double {

        return getJournals().sumOf {
            it.hours
        }
    }

    fun getJournalCount(): Int {

        return getJournals().size
    }
}