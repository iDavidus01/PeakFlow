# PeakFlow 🏔️

**PeakFlow** is a gamified Android application for mountain enthusiasts, climbers, and trekkers. It turns your real-life mountaineering achievements into an RPG-style progression system, allowing you to track your conquered peaks, visualize your stats, and pan across a global map of the greatest mountains in the world.

## 🚀 Features

*   **Mountain Database**: Explore a curated list of 45 famous mountains worldwide, complete with height, region, descriptions, GPS coordinates, and custom requirement stats.
*   **RPG-style Progression System**: Each mountain holds specific difficulty requirements:
    *   **KND** (Condition)
    *   **TCH** (Technique)
    *   **AKL** (Acclimatization)
    *   **RYZ** (Risk)

    Conquering a mountain upgrades your overall profile XP and maximizes your stats accordingly. Stats are derived from the highest value across all conquered peaks.
*   **XP & Leveling**: Earn XP equal to a mountain's total difficulty on conquest. Level thresholds follow a quadratic curve — higher levels require exponentially more XP.
*   **Interactive Global Map**: An OSM-powered (OpenStreetMap) world map with markers for all available peaks, color-coded by difficulty:
    *   🟢 **Green**: Beginner (difficulty < 10)
    *   🟡 **Yellow**: Intermediate (10–14)
    *   🟠 **Orange**: Advanced (15–18)
    *   🔴 **Red**: Extreme (19+)
*   **Path of Ascent ("Droga")**: A suggested progression path ordering mountains from easiest to hardest. The next recommended peak is automatically highlighted based on your current progress.
*   **Radar Chart Profiling**: A dynamic spider/radar chart that visually constructs your unique climber profile across all four stat axes.
*   **Home Filters**: Search, sort, and filter the mountain list by name, region, height, or difficulty in real time using reactive StateFlow pipelines.
*   **Next Goal Widget**: Identifies the next reachable unconquered mountain based on your current level — displayed both in-app and on the home screen widget.
*   **Live Weather on Detail Screen**: Fetches real-time weather data from the Open-Meteo API (no API key required) for each peak using its GPS coordinates and classifies conditions as Ideal, Acceptable, Winter, or Dangerous.
*   **Readiness Score**: Calculates a 0–100% readiness score for any mountain based on the gap between your current stats and the mountain's requirements.
*   **Achievements System**: Unlock eight milestones with XP rewards and progress tracking:
    | Achievement | Requirement | XP |
    |---|---|---|
    | First Summit | Conquer any mountain | 50 |
    | Five Summits | Conquer 5 mountains | 150 |
    | Ten Summits | Conquer 10 mountains | 300 |
    | Himalayan | Conquer a peak ≥ 8000 m | 500 |
    | Total Height | Accumulate 20 000 m total | 200 |
    | Max Difficulty | Conquer a difficulty 18+ peak | 400 |
    | Globetrotter | Conquer peaks in 3+ regions | 250 |
    | Level Five | Reach level 5 | 300 |
*   **Profile Sub-tabs**: The Profile screen splits into a **Stats** tab (level, XP bar, total height climbed, hardest/highest peak, regions conquered, conquest history) and an **Achievements** tab with per-item progress bars.
*   **Conquest History**: Timestamped log of every conquered peak, sorted newest-first, displayed in the Stats sub-tab.
*   **Home Screen Widget**: Shows the next goal mountain name and your current readiness percentage; tapping it opens the app.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **Architecture**: MVVM (Model-View-ViewModel) with a singleton `MountainRepository`
*   **Reactive State**: Kotlin `StateFlow` / `MutableStateFlow` — all UI state is observed via `combine` and `stateIn` operators (no LiveData)
*   **Coroutines**: `viewModelScope` with `Dispatchers.IO` for network calls
*   **UI Components**: XML Layouts, View Binding, Material Design 3, RecyclerView, TabLayout + ViewPager2
*   **Navigation**: Android Jetpack Navigation Component
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
*   **Map**: [Osmdroid](https://github.com/osmdroid/osmdroid) — fully offline-capable OpenStreetMap rendering
*   **Weather API**: [Open-Meteo](https://open-meteo.com/) — free, no API key required
*   **Persistence**: `SharedPreferences` — stores conquered IDs with Unix timestamps; migrates legacy data automatically
*   **Domain layer**: Sealed classes (`Achievement`, `ReadinessLevel`, `WeatherCondition`) and an enum (`SortOrder`) encapsulating all business logic
*   **Home Screen Widget**: `AppWidgetProvider` (`PeakFlowWidget`) with `RemoteViews`
*   **Custom Views**: Hand-drawn `RadarChartView` (Canvas API) with adaptive text sizing

## 💻 Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iDavidus01/PeakFlow.git
    ```
2.  **Open in Android Studio:**
    Select `File > Open`, then locate the cloned `PeakFlow` directory.
3.  **Sync Gradle:**
    Allow Android Studio to download the necessary dependencies (Kotlin, Coil, Osmdroid).
4.  **Run the App:**
    Connect a physical device via USB or deploy using an Android Virtual Device (AVD). The app requires Android 7.0 (API 24) or newer.
    *Press `Shift + F10` (Windows) or the standard Run button to compile.*

## 🤝 Contribution

Feel free to open issues or submit pull requests if you want to add new mountains to `mountains.json`, improve the UI, or add multiple languages.
