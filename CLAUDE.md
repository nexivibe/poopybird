# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PoopyBird is a 3D game where the player controls a flying bird with simplified physics, attempting to poop on things and people. Built with libGDX 1.13.1 as a multi-platform project supporting Desktop (LWJGL3), Android, and iOS.

**Package:** `ape.poopybird`

## Build Commands

```bash
# Run desktop game (primary development target)
./gradlew lwjgl3:run

# Build runnable desktop JAR (output: lwjgl3/build/libs/)
./gradlew lwjgl3:jar

# Build platform-specific JARs
./gradlew jarWin        # Windows
./gradlew jarMac        # macOS
./gradlew jarLinux      # Linux

# Android
./gradlew android:build
./gradlew android:lint

# iOS (macOS only)
./gradlew ios:launchIPhoneSimulator
./gradlew ios:launchIOSDevice

# Run tests
./gradlew test
./gradlew core:test     # Core module only

# Clean build
./gradlew clean
```

## Architecture

**Multi-module Gradle project with platform-specific launchers:**

```
core/           → Shared game logic (Main.java extends ApplicationAdapter)
lwjgl3/         → Desktop launcher (Windows/Mac/Linux)
android/        → Android launcher
ios/            → iOS launcher (RoboVM)
assets/         → Shared game assets (auto-listed to assets.txt)
```

**Launcher Pattern:** Each platform launcher creates its platform-specific Application instance and passes the shared `Main` class from core.

- Desktop entry: `ape.poopybird.lwjgl3.Lwjgl3Launcher`
- Android entry: `ape.poopybird.android.AndroidLauncher`
- iOS entry: `ape.poopybird.IOSLauncher`

## Key Configuration

- **Window size:** 640x480 (configured in `Lwjgl3Launcher.java`)
- **Android:** minSdk 19, targetSdk 35, landscape orientation
- **libGDX version:** Defined in `gradle.properties` as `gdxVersion`

## Assets

Place all game assets in `/assets/`. The build automatically generates `assets/assets.txt` listing all files for streaming optimization.

## Code Style

- 4 spaces indentation (no tabs)
- LF line endings
- See `.editorconfig` for details
