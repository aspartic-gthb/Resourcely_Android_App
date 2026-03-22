package com.studyorganizer

data class Resource(
    val id: Int = 0,
    val title: String,
    val link: String,
    val category: String,
    val tags: String = "",          // comma-separated, e.g. "kotlin,android"
    val isImportant: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
