package com.studyorganizer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.chip.Chip
import com.studyorganizer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ResourceAdapter
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        db = DatabaseHelper(this)
        SeedData.populate(db) // Ensure data is seeded
        setupAdapter()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditResourceActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadResources()
        buildCategoryChips()
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    private fun setupAdapter() {
        adapter = ResourceAdapter(
            onEdit = { res ->
                val i = Intent(this, AddEditResourceActivity::class.java)
                i.putExtra(AddEditResourceActivity.EXTRA_ID, res.id)
                startActivity(i)
            },
            onDelete = { res ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Resource")
                    .setMessage("Remove \"${res.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        db.deleteResource(res.id)
                        loadResources()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onToggleImportant = { res ->
                db.toggleImportant(res.id, !res.isImportant)
                loadResources()
            }
        )
        binding.recyclerView.adapter = adapter
    }

    // ── Data loading ─────────────────────────────────────────────────────────

    private fun loadResources(query: String = "") {
        val list = when {
            query.isNotBlank()       -> db.search(query)
            currentCategory == "All" -> db.getAllResources()
            else                     -> db.getByCategory(currentCategory)
        }
        adapter.submitList(list)
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    // ── Category chips ───────────────────────────────────────────────────────

    private fun buildCategoryChips() {
        binding.chipGroup.removeAllViews()

        val categories = listOf("All") + db.getCategories()
        categories.forEach { cat ->
            val chip = Chip(this).apply {
                text        = cat
                isCheckable = true
                isChecked   = (cat == currentCategory)
                setOnClickListener {
                    currentCategory = cat
                    syncChipChecked()
                    loadResources()
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun syncChipChecked() {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            chip.isChecked = (chip.text == currentCategory)
        }
    }

    // ── Search menu ──────────────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val sv = searchItem.actionView as SearchView
        sv.queryHint = "Search title, category, tag…"
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                loadResources(q.orEmpty())
                return true
            }
        })
        return true
    }
}
