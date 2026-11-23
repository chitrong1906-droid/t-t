# FF ESP Overlay

A Java Android overlay shell targeting Android 6.0 (API 23) and newer.

## Included
- Overlay permission flow (`SYSTEM_ALERT_WINDOW`)
- Floating, draggable menu
- Independent ON/OFF controls for Player, Box, Skeleton, Health and Distance display modules
- Android foreground service on Android 8+

## Important implementation boundary
This project currently provides the overlay UI only. It does **not** read Free Fire memory, inspect the game process, or obtain live player coordinates. Therefore the toggles currently change UI state only; they do not display live opponents. A renderer needs a legitimate, explicitly available target-data provider before those ESP elements can be drawn.

## Build
- Android Studio with JDK 17
- Android SDK Platform 34
- Gradle plugin 8.2.2

Open the project folder in Android Studio, allow Gradle sync, then use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
The APK output is normally `app/build/outputs/apk/debug/app-debug.apk`.

`minSdk 23` means Android 6.0 and newer. `targetSdk 28` is intentional for this initial compatibility-oriented overlay prototype; review platform distribution requirements before publishing.
