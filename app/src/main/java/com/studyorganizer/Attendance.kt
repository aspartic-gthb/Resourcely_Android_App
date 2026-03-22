package com.studyorganizer

data class Attendance(
    val id: Int = 0,
    val subject: String,
    val totalClasses: Int,
    val attendedClasses: Int
) {
    val percentage: Float
        get() = if (totalClasses > 0) (attendedClasses.toFloat() / totalClasses) * 100 else 0f

    val bunkedClasses: Int
        get() = totalClasses - attendedClasses

    fun getStatusMessage(): String {
        return if (percentage < 75f) {
            val needed = Math.ceil((0.75 * totalClasses - attendedClasses) / 0.25).toInt()
            "Warning: Attendance is below 75%. You need to attend at least $needed more classes to reach 75%."
        } else {
            "Status: You are safe. Attendance is above 75%."
        }
    }
}
