# wearlooptimer

A simple countdown timer app for Android Wear OS watches.

## Features

- Black background with a white countdown display occupying the top ~2/3 of the screen (default `9:20`)
- Tap the hour or minute to switch selection, then swipe to adjust the value with a single unit marker on the right
- The seconds field is greyed out and display-only (not selectable)
- Three rounded-square action buttons at the bottom: **Start / Pause / End**
- A 5-second vibration alert is triggered when the countdown reaches zero

## Build

```bash
./gradlew :app:assembleDebug
```

The debug APK will be generated at `app/build/outputs/apk/debug/`.

## Requirements

- Android SDK 34
- JDK 17
- Wear OS device or emulator (minSdk 30)
