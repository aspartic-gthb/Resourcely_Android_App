package com.studyorganizer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.studyorganizer.databinding.ActivityCategoryViewerBinding

class CategoryViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryViewerBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ResourceAdapter
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra("EXTRA_CATEGORY") ?: "All"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = category

        db = DatabaseHelper(this)
        setupAdapter()
        setupTabs()
        loadResources(ResourceType.LINK) // Default tab
    }

    private fun setupAdapter() {
        adapter = ResourceAdapter(
            onEdit = { }, // Disable edit/delete for simplicity in this view or implement if needed
            onDelete = { },
            onToggleImportant = { res ->
                db.toggleImportant(res.id, !res.isImportant)
                val currentTabType = when(binding.tabLayout.selectedTabPosition) {
                    0 -> ResourceType.LINK
                    1 -> ResourceType.DRIVE
                    2 -> ResourceType.LOCAL
                    else -> ResourceType.LINK
                }
                loadResources(currentTabType)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("URLs"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Google Drive"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Local Files"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val type = when (tab?.position) {
                    0 -> ResourceType.LINK
                    1 -> ResourceType.DRIVE
                    2 -> ResourceType.LOCAL
                    else -> ResourceType.LINK
                }
                loadResources(type)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadResources(type: ResourceType) {
        val allInCategory = db.getByCategory(category)
        val filtered = allInCategory.filter { it.type == type }
        adapter.submitList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
