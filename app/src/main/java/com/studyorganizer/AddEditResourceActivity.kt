package com.studyorganizer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.studyorganizer.databinding.ActivityAddEditBinding

class AddEditResourceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "resource_id"
    }

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var db: DatabaseHelper
    private var editingResource: Resource? = null
    private var selectedType = ResourceType.LINK

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                // Keep the content URI for access
                binding.etLink.setText(it.toString())
                if (binding.etTitle.text.toString().isEmpty()) {
                    // Try to extract a name if title is empty
                    binding.etTitle.setText("Local File")
                }
            }
        }
    }

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

        setupTypeSelection()
        
        binding.btnBrowse.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*" // Allow all file types
            }
            filePickerLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener   { trySave() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun setupTypeSelection() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnTypeLink -> {
                        selectedType = ResourceType.LINK
                        binding.tilLink.hint = "Web URL"
                        binding.btnBrowse.visibility = View.GONE
                        binding.tilLink.setEndIconDrawable(R.drawable.ic_link)
                    }
                    R.id.btnTypeDrive -> {
                        selectedType = ResourceType.DRIVE
                        binding.tilLink.hint = "Google Drive Link"
                        binding.btnBrowse.visibility = View.GONE
                        binding.tilLink.setEndIconDrawable(R.drawable.ic_drive)
                    }
                    R.id.btnTypeLocal -> {
                        selectedType = ResourceType.LOCAL
                        binding.tilLink.hint = "File URI"
                        binding.btnBrowse.visibility = View.VISIBLE
                        binding.tilLink.setEndIconDrawable(R.drawable.ic_file)
                    }
                }
            }
        }
    }

    private fun fill(r: Resource) {
        binding.etTitle.setText(r.title)
        binding.etLink.setText(r.link)
        binding.etCategory.setText(r.category)
        binding.etTags.setText(r.tags)
        binding.switchImportant.isChecked = r.isImportant
        
        selectedType = r.type
        val btnId = when(r.type) {
            ResourceType.LINK -> R.id.btnTypeLink
            ResourceType.DRIVE -> R.id.btnTypeDrive
            ResourceType.LOCAL -> R.id.btnTypeLocal
        }
        binding.toggleGroup.check(btnId)
    }

    private fun trySave() {
        val title    = binding.etTitle.text.toString().trim()
        val link     = binding.etLink.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val tags     = binding.etTags.text.toString().trim()
        val important= binding.switchImportant.isChecked

        if (title.isEmpty())    { binding.tilTitle.error    = "Required"; return }
        if (link.isEmpty())     { binding.tilLink.error     = "Required"; return }
        if (category.isEmpty()) { binding.tilCategory.error = "Required"; return }

        // Auto-prepend https:// for links if needed
        val finalLink = if (selectedType != ResourceType.LOCAL && 
            !link.startsWith("http://") && !link.startsWith("https://"))
            "https://$link" else link

        val newResource = Resource(
            id = editingResource?.id ?: 0,
            title = title,
            link = finalLink,
            category = category,
            tags = tags,
            isImportant = important,
            type = selectedType
        )

        if (editingResource != null) {
            db.updateResource(newResource)
            Toast.makeText(this, "✅ Updated!", Toast.LENGTH_SHORT).show()
        } else {
            db.addResource(newResource)
            Toast.makeText(this, "✅ Saved!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
