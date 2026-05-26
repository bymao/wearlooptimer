# wearlooptimer

A simple countdown timer app for Android Wear OS watches.

## Features

- Black background with a white countdown display occupying the top ~2/3 of the screen (default `9:20`)
- Tap the hour or minute to switch selection, then swipe to adjust the value
- Three rounded-square action buttons at the bottom: **Start / Pause / End**

## Preview

![UI Preview](ui-preview.png)

## Build

```bash
./gradlew :app:assembleDebug
```

The debug APK will be generated at `app/build/outputs/apk/debug/`.

## Requirements

- Android SDK 34
- JDK 17
- Wear OS device or emulator (minSdk 30)
