# 📚 RESOURCELY
### Streamline Your Learning Workflow

Resourcely is an all-in-one productivity companion designed for modern students. It transforms scattered bookmarks, drive links, and notes into a structured Study Vault, while also helping you stay on top of attendance and weekly schedules — all wrapped in a clean, aesthetic interface.

🌟 Key Features
1. 📂 Study Vault (Resource Management)
Centralized Hub
Store web links, Google Drive documents, and local file paths in one place.
Intelligent Categorization
Automatically organizes resources by subject (Maths, Physics, etc.).
Favicon Integration
Uses Glide to fetch website icons for a polished browsing experience.
Live Search
Instantly search across titles, categories, and tags.
Importance Tracker
Star important resources and pin them to the top.
2. 📊 Smart Attendance Tracker
One-Tap Updates
Simple Attended and Missed buttons for quick logging.
Dynamic Analytics
Real-time attendance percentage with a color-coded progress bar.
Status Advisor
Calculates how many more classes are needed to reach the 75% threshold.
Visual Feedback
🔴 Red → Warning
🟢 Green → Safe
3. 🗓️ Weekly Timetable
Timeline View
Clean tab-based interface for daily schedules.
Auto-Day Detection
Automatically opens the current day.
Precision Entry
Built-in time pickers for accurate scheduling.
4. 🎨 Aesthetic UI/UX
Brand Personality
Student-friendly tone with relatable empty-state messages.
Modern Design
Built using Material Design 3 with a Dreamy Sky color palette.
⚖️ Instructions for Judges
🚀 Running the App
Open the project in Android Studio Hedgehog (or newer)
Let Gradle sync dependencies
Run the app on:
Emulator (API 26+) OR
Physical device
On first launch, sample data is auto-seeded in the Study Vault
🔍 Key Implementation Files
Database Architecture
DatabaseHelper
→ Manages:
Resources Table
Attendance Table
Timetable Table
Core Logic Files
AttendanceActivity.kt → Attendance calculation logic (75% rule)
TimetableActivity.kt → Daily schedule + tab system
ResourceAdapter.kt → Glide-based favicon handling
🛠️ Functionality Checklist
Navigate to Weekly Timetable → View current day schedule
Open Attendance Tracker → Use buttons and observe real-time updates
Use Search Bar → Filter resources instantly
🛠️ Technical Stack
Category	Technology Used
Language	Kotlin
Database	SQLite (Local-first)
Architecture	ViewBinding
UI Components	Material Design 3, RecyclerView, CoordinatorLayout
Libraries	Glide (Favicon Rendering), Material Components
👨‍💻 Developed By

Anirudh & Tinklejit
National Institute of Technology (NIT), Silchar

🏆 Built For

SmartDroid Competition
Under ECS Spectrum Tech Module

❤️ Acknowledgment

Made with dedication for students who want a cleaner, smarter way to manage their academic workflow.
