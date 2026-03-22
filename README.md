📚 Resourcely: Streamline Your Learning Workflow
Resourcely is an all-in-one productivity companion built for the modern student. It transforms the chaotic mess of bookmarks, drive links, and scattered notes into a structured "Study Vault," while simultaneously keeping your attendance and weekly schedule on track with a premium, aesthetic interface.
🌟 Key Features
1. The Study Vault (Resource Management)
•
Centralized Hub: Save Web Links, Google Drive documents, and Local File paths in one place.
•
Intelligent Categorization: Automatically group materials by subject (Maths, Physics, etc.).
•
Favicon Integration: Powered by Glide, the app automatically fetches website icons for a professional, visual browsing experience.
•
Live Search: Instant discovery across titles, categories, and tags.
•
Importance Tracker: Star your "must-study" materials to pin them to the top.
2. Smart Attendance Tracker
•
One-Tap Updates: Dedicated "Attended" and "Missed" buttons for frictionless tracking.
•
Dynamic Analytics: Real-time percentage calculation with a color-coded LinearProgressIndicator.
•
Status Advisor: The app calculates exactly how many more classes you need to attend to safely hit the 75% threshold.
•
Visual Feedback: Cards turn Red (Warning) or Green (Safe) based on your current standing.
3. Weekly Timetable
•
Timeline View: A clean, tabbed interface to view your daily classes and labs.
•
Auto-Day Detection: Opens to the current day of the week automatically.
•
Precision Entry: Integrated native Time Pickers for managing class slots.
4. Aesthetic UI/UX
•
Branding: A witty, student-centric personality with relatable empty-state messages.
•
Modern Design: Built using Material Design 3 with a smooth, "Dreamy Sky" color palette.
⚖️ Instructions for Judges
To evaluate the application, please follow these steps:
🚀 Running the App
1.
Environment: Open the project in Android Studio Hedgehog (or newer).
2.
Sync: Allow Gradle to sync dependencies (ViewBinding, Glide, Material Components).
3.
Build: Run the app on an Emulator (API 26+) or a physical device.
4.
Seed Data: Upon first launch, the app automatically seeds the "Study Vault" with sample resources to demonstrate the UI.
🔍 Key Implementation Files
•
Database Architecture: com.studyorganizer.DatabaseHelper (Manages 3 distinct tables: Resources, Attendance, Timetable).
•
Logic Hubs:
◦
AttendanceActivity.kt: Handles the math for the 75% attendance logic.
◦
TimetableActivity.kt: Manages the tabbed daily schedule system.
◦
ResourceAdapter.kt: Implements the Glide favicon fetching logic.
🛠️ Functionality Check
•
Navigate to Weekly Timetable via the sidebar to see the current day's schedule.
•
Visit Attendance Tracker to interact with the +/- buttons and see the progress bars update in real-time.
•
Use the Search Bar on the Home Screen to filter the seeded sample data.
🛠 Technical Stack
•
Language: Kotlin
•
Database: SQLite (Local-first, no cloud dependency)
•
View Architecture: ViewBinding
•
UI Components: Material Design 3, CoordinatorLayout, RecyclerView
•
Libraries: Glide (Favicon rendering), Material Components
✍️ Developed By
Anirudh & Tinklejit
National Institute of Technology (NIT), Silchar
Made with ❤️ for the SmartDroid Competition(Under ECS's Spectrum Tech Module)
