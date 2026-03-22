package com.studyorganizer

/**
 * Seeds the database with sample resources on first install.
 * Now updated with general subjects like Maths, Physics, and Chemistry.
 */
object SeedData {

    fun populate(db: DatabaseHelper) {
        if (db.getAllResources().isNotEmpty()) return  // already seeded, don't double-insert

        val samples = listOf(
            Resource(
                title       = "Khan Academy - Calculus",
                link        = "https://www.khanacademy.org/math/calculus-1",
                category    = "Maths",
                tags        = "calculus,math,online-course",
                isImportant = true
            ),
            Resource(
                title    = "Physics Classroom",
                link     = "https://www.physicsclassroom.com",
                category = "Physics",
                tags     = "mechanics,waves,notes"
            ),
            Resource(
                title    = "Chemistry LibreTexts",
                link     = "https://chem.libretexts.org",
                category = "Chemistry",
                tags     = "organic,inorganic,reference"
            ),
            Resource(
                title       = "WolframAlpha",
                link        = "https://www.wolframalpha.com",
                category    = "Maths",
                tags        = "computational,solver",
                isImportant = true
            ),
            Resource(
                title    = "Feynman Lectures on Physics",
                link     = "https://www.feynmanlectures.caltech.edu",
                category = "Physics",
                tags     = "quantum,classical,feynman"
            ),
            Resource(
                title    = "PubChem",
                link     = "https://pubchem.ncbi.nlm.nih.gov",
                category = "Chemistry",
                tags     = "compounds,molecules,data"
            ),
            Resource(
                title    = "MIT OpenCourseWare - Biology",
                link     = "https://ocw.mit.edu/courses/biology",
                category = "Biology",
                tags     = "genetics,cell-biology"
            ),
            Resource(
                title       = "Desmos Graphing Calculator",
                link        = "https://www.desmos.com/calculator",
                category    = "Maths",
                tags        = "graphing,math,tools",
                isImportant = false
            )
        )

        samples.forEach { db.addResource(it) }
    }
}
