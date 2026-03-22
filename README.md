# Study Organizer - Mid-Evaluation Report

Study Organizer is a smart Android application designed to help students manage and organize their learning resources efficiently. It allows users to categorize study materials, track important links (Google Drive, Web links, Local files), and search through them with ease.

## Key Features Implemented

### 1. Resource Management
- **CRUD Operations**: Users can add, view, update, and delete study resources.
- **Resource Types**: Supports three types of resources:
  - **LINK**: Direct web URLs.
  - **DRIVE**: Google Drive or other cloud storage links.
  - **LOCAL**: References to local files on the device.
- **Importance Tracking**: Ability to mark resources as "Important" for quick access.

### 2. Intelligent Categorization
- **Dynamic Categories**: Resources are grouped into categories (e.g., Maths, Physics, Chemistry).
- **Chip-based Filtering**: A dynamic UI using Material Chips to filter resources by category on the main dashboard.
- **Tags**: Support for adding searchable tags to each resource.

### 3. Modern UI/UX
- **Material 3 Design**: Clean and modern interface using Google's Material Components.
- **CoordinatorLayout & AppBarLayout**: Smooth scrolling behavior with a sticky toolbar and hero section.
- **RecyclerView with ViewBinding**: Efficient list rendering with a custom adapter.
- **Empty States**: Intuitive "No resources found" display when lists are empty.

### 4. Search & Discovery
- **Live Search**: Real-time searching across titles, categories, and tags via a `SearchView` in the toolbar.
- **Data Seeding**: Automatic population of initial sample data for demonstration purposes.

## Technical Stack
- **Language**: Kotlin
- **Database**: SQLite (via `SQLiteOpenHelper`)
- **View Architecture**: ViewBinding
- **UI Components**: Material Components (FAB, Chips, ConstraintLayout, RecyclerView, CoordinatorLayout)
- **Image Loading**: Glide

## Recent Fixes & Improvements
- Resolved ViewBinding issues related to the empty state view (`tvEmpty`).
- Optimized database schema to include `type` and `created_at` fields.
- Implemented category-specific filtering logic in the main activity.
- Integrated `strings.xml` for better localization and resource management.

---
**Developed for SmartDroid Hackathon**
