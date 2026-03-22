package com.studyorganizer

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.studyorganizer.databinding.ActivityTimetableBinding
import com.studyorganizer.databinding.DialogAddTimetableBinding
import java.util.*

class TimetableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimetableBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: TimetableAdapter
    private val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private var selectedDay = "Monday"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupTabs()
        setupRecyclerView()

        binding.fabAdd.setOnClickListener { showAddDialog() }

        // Set initial day based on current day of week
        val calendar = Calendar.getInstance()
        val dayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Adjust to Monday = 0
        binding.tabLayout.getTabAt(dayIndex)?.select()
    }

    private fun setupTabs() {
        days.forEach { day ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(day))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedDay = tab?.text.toString()
                refreshList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = TimetableAdapter(emptyList()) { item ->
            db.deleteTimetable(item.id)
            refreshList()
        }
        binding.rvTimetable.layoutManager = LinearLayoutManager(this)
        binding.rvTimetable.adapter = adapter
    }

    private fun refreshList() {
        val list = db.getTimetableByDay(selectedDay)
        adapter.updateData(list)
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddTimetableBinding.inflate(layoutInflater)
        
        // Setup Subject Autocomplete
        val subjects = (db.getCategories() + listOf("Maths", "Physics", "Chemistry", "Biology")).distinct()
        dialogBinding.etSubject.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects))

        // Setup Type Spinner
        val types = listOf("Class", "Lab", "Seminar", "Other")
        dialogBinding.etType.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types))

        // Time pickers
        dialogBinding.etStartTime.setOnClickListener { showTimePicker { time -> dialogBinding.etStartTime.setText(time) } }
        dialogBinding.etEndTime.setOnClickListener { showTimePicker { time -> dialogBinding.etEndTime.setText(time) } }

        AlertDialog.Builder(this)
            .setTitle("Add Schedule")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val subject = dialogBinding.etSubject.text.toString()
                val startTime = dialogBinding.etStartTime.text.toString()
                val endTime = dialogBinding.etEndTime.text.toString()
                val type = dialogBinding.etType.text.toString()

                if (subject.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                    db.addTimetable(Timetable(subject = subject, day = selectedDay, startTime = startTime, endTime = endTime, type = type))
                    refreshList()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            onTimeSelected(String.format("%02d:%02d", hour, minute))
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
