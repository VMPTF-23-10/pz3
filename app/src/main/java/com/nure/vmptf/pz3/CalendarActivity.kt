package com.nure.vmptf.pz3

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : Activity() {
    private val selectedDate = Calendar.getInstance()
    private val events = mutableMapOf<String, MutableList<String>>()
    private val storage by lazy { getSharedPreferences(PREFS_NAME, MODE_PRIVATE) }
    private val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val dateDisplayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("uk-UA"))

    private lateinit var selectedDateText: TextView
    private lateinit var eventInput: EditText
    private lateinit var eventsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        selectedDateText = findViewById(R.id.selectedDateText)
        eventInput = findViewById(R.id.eventInput)
        eventsContainer = findViewById(R.id.eventsContainer)

        loadEvents()
        updateSelectedDate()

        findViewById<Button>(R.id.pickDateButton).setOnClickListener {
            showDatePicker()
        }

        findViewById<Button>(R.id.addEventButton).setOnClickListener {
            addEvent()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateSelectedDate()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun addEvent() {
        val eventText = eventInput.text.toString().trim()
        if (eventText.isBlank()) {
            eventInput.error = getString(R.string.calendar_empty_event)
            return
        }

        events.getOrPut(currentDateKey()) { mutableListOf() }.add(eventText)
        saveEvents()
        eventInput.text.clear()
        renderEvents()
        Toast.makeText(this, R.string.calendar_event_saved, Toast.LENGTH_SHORT).show()
    }

    private fun updateSelectedDate() {
        selectedDateText.text = getString(
            R.string.calendar_selected_date,
            dateDisplayFormat.format(selectedDate.time)
        )
        renderEvents()
    }

    private fun renderEvents() {
        eventsContainer.removeAllViews()
        val eventsForDate = events[currentDateKey()].orEmpty()

        if (eventsForDate.isEmpty()) {
            eventsContainer.addView(createEventText(getString(R.string.calendar_no_events)))
            return
        }

        eventsForDate.forEachIndexed { index, event ->
            eventsContainer.addView(createEventText("${index + 1}. $event"))
        }
    }

    private fun createEventText(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(getColor(R.color.text_primary))
            setPadding(0, 8, 0, 8)
        }
    }

    private fun currentDateKey(): String = dateKeyFormat.format(selectedDate.time)

    private fun loadEvents() {
        events.clear()
        val rawJson = storage.getString(EVENTS_KEY, "{}").orEmpty()
        val root = runCatching { JSONObject(rawJson) }.getOrElse { JSONObject() }
        val keys = root.keys()

        while (keys.hasNext()) {
            val date = keys.next()
            val jsonEvents = root.optJSONArray(date) ?: JSONArray()
            val dateEvents = mutableListOf<String>()

            for (index in 0 until jsonEvents.length()) {
                val value = jsonEvents.optString(index)
                if (value.isNotBlank()) {
                    dateEvents.add(value)
                }
            }

            if (dateEvents.isNotEmpty()) {
                events[date] = dateEvents
            }
        }
    }

    private fun saveEvents() {
        val root = JSONObject()
        events.forEach { (date, dateEvents) ->
            val jsonEvents = JSONArray()
            dateEvents.forEach { jsonEvents.put(it) }
            root.put(date, jsonEvents)
        }
        storage.edit().putString(EVENTS_KEY, root.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "calendar_events_storage"
        private const val EVENTS_KEY = "events_json"
    }
}
