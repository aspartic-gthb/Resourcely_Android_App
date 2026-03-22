package com.studyorganizer

enum class ResourceType {
    LINK, DRIVE, LOCAL
}

data class Resource(
    val id: Int = 0,
    val title: String,
    val link: String,              // Can be URL, Drive Link, or Local File Path
    val category: String,
    val tags: String = "",
    val isImportant: Boolean = false,
    val type: ResourceType = ResourceType.LINK,
    val createdAt: Long = System.currentTimeMillis()
)
