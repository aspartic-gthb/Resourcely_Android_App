package com.studyorganizer

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.studyorganizer.databinding.ActivityAttendanceBinding

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupSubjectAutocomplete()

        binding.btnCalculate.setOnClickListener {
            addOrUpdateSubject()
        }

        refreshList()
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(
            list = emptyList(),
            onUpdate = { updatedAttendance ->
                db.upsertAttendance(updatedAttendance)
                refreshList()
            },
            onDelete = { attendance ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Subject")
                    .setMessage("Are you sure you want to delete ${attendance.subject}?")
                    .setPositiveButton("Delete") { _, _ ->
                        db.deleteAttendance(attendance.id)
                        refreshList()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        binding.rvAttendance.adapter = adapter
    }

    private fun setupSubjectAutocomplete() {
        val subjects = (db.getCategories() + listOf("Maths", "Physics", "Chemistry", "Biology")).distinct()
        val autocompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects)
        binding.etSubject.setAdapter(autocompleteAdapter)
    }

    private fun refreshList() {
        val list = db.getAllAttendance()
        adapter.updateData(list)
    }

    private fun addOrUpdateSubject() {
        val subject = binding.etSubject.text.toString().trim()
        val total = binding.etTotal.text.toString().toIntOrNull() ?: 0
        val attended = binding.etAttended.text.toString().toIntOrNull() ?: 0

        if (subject.isEmpty()) {
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show()
            return
        }

        if (attended > total) {
            Toast.makeText(this, "Attended classes cannot be more than total", Toast.LENGTH_SHORT).show()
            return
        }

        val attendance = Attendance(subject = subject, totalClasses = total, attendedClasses = attended)
        db.upsertAttendance(attendance)
        
        // Clear inputs
        binding.etSubject.text?.clear()
        binding.etTotal.text?.clear()
        binding.etAttended.text?.clear()
        binding.etSubject.clearFocus()

        refreshList()
        Toast.makeText(this, "Subject $subject added/updated", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
