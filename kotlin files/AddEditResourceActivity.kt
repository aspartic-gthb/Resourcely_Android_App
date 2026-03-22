package com.studyorganizer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studyorganizer.databinding.ActivityAddEditBinding

class AddEditResourceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "resource_id"
    }

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var db: DatabaseHelper
    private var editingResource: Resource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            editingResource = db.getAllResources().find { it.id == id }
            editingResource?.let { fill(it) }
            supportActionBar?.title = "Edit Resource"
        } else {
            supportActionBar?.title = "Add Resource"
        }

        binding.btnSave.setOnClickListener   { trySave() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    // ── Populate fields when editing ─────────────────────────────────────────

    private fun fill(r: Resource) {
        binding.etTitle.setText(r.title)
        binding.etLink.setText(r.link)
        binding.etCategory.setText(r.category)
        binding.etTags.setText(r.tags)
        binding.switchImportant.isChecked = r.isImportant
    }

    // ── Validation + save ────────────────────────────────────────────────────

    private fun trySave() {
        val title    = binding.etTitle.text.toString().trim()
        val link     = binding.etLink.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val tags     = binding.etTags.text.toString().trim()
        val important= binding.switchImportant.isChecked

        if (title.isEmpty())    { binding.tilTitle.error    = "Required"; return }
        if (link.isEmpty())     { binding.tilLink.error     = "Required"; return }
        if (category.isEmpty()) { binding.tilCategory.error = "Required"; return }

        // Auto-prepend https:// so Glide & Intent don't choke
        val finalLink = if (!link.startsWith("http://") && !link.startsWith("https://"))
            "https://$link" else link

        if (editingResource != null) {
            db.updateResource(editingResource!!.copy(
                title = title, link = finalLink, category = category,
                tags = tags, isImportant = important
            ))
            Toast.makeText(this, "✅ Updated!", Toast.LENGTH_SHORT).show()
        } else {
            db.addResource(Resource(
                title = title, link = finalLink, category = category,
                tags = tags, isImportant = important
            ))
            Toast.makeText(this, "✅ Saved!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
