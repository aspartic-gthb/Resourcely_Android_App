package com.studyorganizer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.studyorganizer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnExport.setOnClickListener {
            Toast.makeText(this, "Exporting database to Downloads folder...", Toast.LENGTH_SHORT).show()
            // Logic to copy DB file to public folder could go here
        }

        binding.btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear All Data")
                .setMessage("This will delete all resources, attendance, and timetable. This action cannot be undone.")
                .setPositiveButton("Delete Everything") { _, _ ->
                    // For simplicity, we can delete and recreate the DB tables
                    applicationContext.deleteDatabase(DatabaseHelper.DB_NAME)
                    Toast.makeText(this, "All data wiped. Please restart the app.", Toast.LENGTH_LONG).show()
                    finishAffinity() 
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Reminders $status", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
