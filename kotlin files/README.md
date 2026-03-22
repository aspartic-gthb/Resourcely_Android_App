# 📚 Study Resource Organizer — Android App

Native Android (Kotlin) + SQLite — no cloud, no login, works fully offline.

---

## ✅ Features

| Feature | Status |
|---|---|
| Add resource (title, link, category) | ✅ |
| Display saved resources (cards) | ✅ |
| Filter by category (chip tabs) | ✅ |
| Open resource links in browser | ✅ |
| Search (title, category, tag) | ✅ |
| Mark important resources (⭐ star) | ✅ |
| Tag system (#hashtag display) | ✅ |
| Favicon preview (Google favicon API) | ✅ |
| Pre-loaded demo data on first install | ✅ |

---

## 📁 Complete File Tree

```
StudyOrganizerApp/
├── build.gradle                          ← project-level
├── settings.gradle
├── gradle.properties
├── gradle/wrapper/
│   └── gradle-wrapper.properties
│
└── app/
    ├── build.gradle                      ← app-level (dependencies here)
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/studyorganizer/
        │   ├── Resource.kt               ← data class (model)
        │   ├── DatabaseHelper.kt         ← SQLite CRUD
        │   ├── ResourceAdapter.kt        ← RecyclerView + Glide favicons
        │   ├── MainActivity.kt           ← home screen + search + chips
        │   ├── AddEditResourceActivity.kt← add/edit form
        │   └── SeedData.kt               ← demo data on first launch
        │
        └── res/
            ├── layout/
            │   ├── activity_main.xml
            │   ├── activity_add_edit.xml
            │   └── item_resource.xml
            ├── menu/
            │   └── main_menu.xml
            ├── drawable/
            │   ├── ic_star_filled.xml
            │   ├── ic_star_outline.xml
            │   └── ic_link.xml
            ├── color/
            │   ├── chip_bg_selector.xml
            │   └── chip_text_selector.xml
            ├── xml/
            │   └── network_security_config.xml
            └── values/
                ├── colors.xml
                ├── strings.xml
                ├── themes.xml
                └── styles.xml
```

---

## 🚀 Setup in Android Studio (step-by-step)

1. Open Android Studio → **New Project** → **Empty Views Activity**
2. Set:
   - Package name: `com.studyorganizer`
   - Language: **Kotlin**
   - Min SDK: **API 24**
   - Build config language: **Groovy DSL (.gradle)**
3. Once project loads, **replace** files at matching paths with the provided files
4. Sync Gradle (Sync Now banner or File → Sync Project with Gradle Files)
5. Run on emulator or physical device

---

## 🗄️ Database Design

Single table: `resources`

| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| title | TEXT | required |
| link | TEXT | auto-prefixed with https:// |
| category | TEXT | used for chip filters |
| tags | TEXT | comma-separated |
| is_important | INTEGER | 0 or 1 (boolean) |
| created_at | INTEGER | Unix timestamp |

---

## 🌐 Favicon Preview

Uses Google's free public favicon service — zero API key, zero setup:
```
https://www.google.com/s2/favicons?domain=<host>&sz=64
```
Loaded via Glide with a fallback to ic_link on failure.

---

## 🏆 Judge Talking Points

- SQLite over SharedPreferences → proper relational storage, scalable, queryable
- ViewBinding → no more findViewById, type-safe UI access
- DiffUtil in RecyclerView → efficient list updates, only redraws changed items
- SeedData → app looks populated and demo-ready from second 1
- Offline-first → zero internet required except for favicon thumbnails
