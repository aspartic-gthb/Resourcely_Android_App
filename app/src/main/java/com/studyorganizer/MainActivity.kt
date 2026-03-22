package com.studyorganizer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.studyorganizer.databinding.ActivityMainBinding
import com.studyorganizer.databinding.ItemCategoryCardBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        SeedData.populate(db)
        
        setupDrawer()
        setupAdapter()
        setupStaticCategories()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditResourceActivity::class.java))
        }

        // Header click resets to All
        binding.tvCategoriesHeader.setOnClickListener {
            currentCategory = "All"
            loadResources()
            syncChipChecked()
        }

        // Handle back press to close drawer
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.app_name, R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadResources()
        buildCategoryChips()
    }

    // ── Static Categories ────────────────────────────────────────────────────

    private fun setupStaticCategories() {
        setupCategoryCard(binding.catMaths, "Maths", R.drawable.ic_maths)
        setupCategoryCard(binding.catChemistry, "Chemistry", R.drawable.ic_chemistry)
        setupCategoryCard(binding.catPhysics, "Physics", R.drawable.ic_link)
        setupCategoryCard(binding.catBiology, "Biology", R.drawable.ic_file)
    }

    private fun setupCategoryCard(cardBinding: ItemCategoryCardBinding, name: String, iconRes: Int) {
        cardBinding.tvCategoryName.text = name
        cardBinding.ivCategoryIcon.setImageResource(iconRes)
        cardBinding.root.setOnClickListener {
            val intent = Intent(this, CategoryViewerActivity::class.java)
            intent.putExtra("EXTRA_CATEGORY", name)
            startActivity(intent)
        }
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
        
        if (list.isEmpty()) {
            binding.tvEmpty.text = if (currentCategory == "All") "No resources added" else "No resources added for $currentCategory"
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
        }
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

    // ── Navigation Drawer ────────────────────────────────────────────────────

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home      -> { currentCategory = "All"; loadResources(); syncChipChecked() }
            R.id.nav_timetable -> { startActivity(Intent(this, TimetableActivity::class.java)) }
            R.id.nav_attendance -> { startActivity(Intent(this, AttendanceActivity::class.java)) }
            R.id.nav_settings  -> { startActivity(Intent(this, SettingsActivity::class.java)) }
            R.id.nav_about     -> { startActivity(Intent(this, AboutActivity::class.java)) }
            R.id.nav_maths     -> { openCategory("Maths") }
            R.id.nav_chemistry -> { openCategory("Chemistry") }
            R.id.nav_physics   -> { openCategory("Physics") }
            R.id.nav_biology   -> { openCategory("Biology") }
            R.id.nav_important -> {
                val list = db.getAllResources().filter { it.isImportant }
                adapter.submitList(list)
                currentCategory = "Important"
                syncChipChecked()
                binding.tvEmpty.text = "No important resources"
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openCategory(name: String) {
        val intent = Intent(this, CategoryViewerActivity::class.java)
        intent.putExtra("EXTRA_CATEGORY", name)
        startActivity(intent)
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
