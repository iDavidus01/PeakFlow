# PeakFlow 🏔️

**PeakFlow** is a gamified Android application for mountain enthusiasts, climbers, and trekkers. It turns your real-life mountaineering achievements into an RPG-style progression system, allowing you to track your conquered peaks, visualize your stats, and pan across a global map of the greatest mountains in the world.

## 🚀 Features

*   **Mountain Database**: Explore a curated list of 45 famous mountains worldwide, complete with their height, region, descriptions, and custom requirement stats.
*   **RPG-style Progression System**: Each mountain holds specific difficulty requirements:
    *   **KND** (Condition)
    *   **TCH** (Technique)
    *   **AKL** (Acclimatization)
    *   **RYZ** (Risk)
    Conquering a mountain upgrades your overall profile XP and maximizes your stats accordingly!
*   **Interactive Global Map**: View an OSM-powered (OpenStreetMap) world map containing markers for all available peaks. Markers are color-coded based on the peak's overall difficulty:
    *   🟢 **Green**: Beginner-friendly
    *   🟡 **Yellow**: Intermediate
    *   🟠 **Orange**: Advanced / Expert
    *   🔴 **Red**: Extreme difficulty
*   **Path of Ascent ("Droga")**: A suggested progression path ordering the mountains from easiest to hardest, guiding you on what to climb next based on your current skill level.
*   **Radar Chart Profiling**: A dynamic spider/radar chart that visually constructs your unique climber profile based on the highest peaks you've reached.

## 🆕 Latest Updates

*   **World Map Integration**: Integrated `osmdroid` to introduce a completely offline-ready, global map tab with interactive, color-coded markers.
*   **Image Handling Improvements**: Replaced slow or broken remote URLs with comprehensive local asset mappings, supporting various extensions like `.jpg` and `.jpeg`.
*   **UI/UX Polishing**: Redesigned the radar chart text-fitting algorithm, ensuring the "AKL" or "X/5" values no longer overlap or get truncated on smaller screens. 

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **UI Components**: XML Layouts, Material Design 3, Auto-loading Recycler Views
*   **Navigation**: Android Jetpack Navigation Component
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
*   **Map API**: [Osmdroid](https://github.com/osmdroid/osmdroid) 

## 💻 Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iDavidus01/PeakFlow.git
    ```
2.  **Open in Android Studio:**
    Select `File > Open`, then locate the cloned `PeakFlow` directory.
3.  **Sync Gradle:**
    Allow Android Studio to download the necessary dependencies (Kotlin, Coil, OSMdroid).
4.  **Run the App:**
    Connect a physical device via USB or deploy using an Android Virtual Device (AVD). Ensure the device runs Android 7.0 (API 24) or newer.
    *Press `Shift + F10` (Windows) or standard Run button to compile.*

## 🤝 Contribution

Feel free to open issues or submit pull requests if you want to add new mountains to `mountains.json`, improve the UI, or add multiple languages.