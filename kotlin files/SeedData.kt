package com.studyorganizer

/**
 * Seeds the database with sample resources on first install.
 * Judges see a populated app immediately — much better than staring at an empty list! 😅
 */
object SeedData {

    fun populate(db: DatabaseHelper) {
        if (db.getAllResources().isNotEmpty()) return  // already seeded, don't double-insert

        val samples = listOf(
            Resource(
                title       = "Android Developer Docs",
                link        = "https://developer.android.com/docs",
                category    = "Android",
                tags        = "official,reference",
                isImportant = true
            ),
            Resource(
                title    = "Kotlin Language Guide",
                link     = "https://kotlinlang.org/docs/home.html",
                category = "Kotlin",
                tags     = "kotlin,language,official"
            ),
            Resource(
                title    = "SQLite Tutorial",
                link     = "https://www.sqlitetutorial.net",
                category = "Database",
                tags     = "sqlite,sql,beginner"
            ),
            Resource(
                title       = "Material Design 3",
                link        = "https://m3.material.io",
                category    = "UI/UX",
                tags        = "design,material,google",
                isImportant = true
            ),
            Resource(
                title    = "RecyclerView Guide",
                link     = "https://developer.android.com/guide/topics/ui/layout/recyclerview",
                category = "Android",
                tags     = "recyclerview,list,adapter"
            ),
            Resource(
                title    = "LeetCode",
                link     = "https://leetcode.com",
                category = "DSA",
                tags     = "dsa,problems,interview"
            ),
            Resource(
                title    = "Glide Image Library",
                link     = "https://bumptech.github.io/glide",
                category = "Android",
                tags     = "images,glide,library"
            ),
            Resource(
                title       = "GeeksForGeeks Android",
                link        = "https://www.geeksforgeeks.org/android-tutorial",
                category    = "Android",
                tags        = "tutorial,beginner",
                isImportant = false
            )
        )

        samples.forEach { db.addResource(it) }
    }
}
