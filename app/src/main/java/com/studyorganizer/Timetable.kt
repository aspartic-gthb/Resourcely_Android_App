package com.studyorganizer

data class Timetable(
    val id: Int = 0,
    val subject: String,
    val day: String,       // Monday, Tuesday, etc.
    val startTime: String, // HH:mm
    val endTime: String,   // HH:mm
    val type: String       // Class or Lab
)
