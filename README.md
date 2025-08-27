# Junun MP3 Player

A simple and elegant MP3 player for Android devices.

## Features

- 🎵 Play MP3 files from your device storage
- ⏯️ Full playback controls (Play, Pause, Next, Previous)
- 🔀 Shuffle and Repeat modes
- 📱 Background playback with notification controls
- 🎨 Modern Material Design dark theme
- 📋 Playlist management
- 🔍 Music library scanning
- ⏰ Real-time progress tracking

## Screenshots

*Add screenshots here when the app is built*

## Technical Details

- **Minimum SDK**: Android 5.0 (API level 21)
- **Target SDK**: Android 14 (API level 34)
- **Language**: Java
- **Architecture**: Service-based background playback
- **UI**: Material Design 3 components

## Permissions

The app requires the following permissions:
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_AUDIO` - To access music files
- `FOREGROUND_SERVICE` - For background music playback
- `WAKE_LOCK` - To prevent device sleep during playback
- `POST_NOTIFICATIONS` - For playback control notifications

## Installation

1. Open the project in Android Studio
2. Build and run the project on your Android device or emulator
3. Grant the required permissions when prompted

## Usage

1. Launch the app
2. Grant storage permissions to access your music files
3. Browse your music library
4. Tap on any song to start playing
5. Use the player controls at the bottom
6. Control playback from the notification panel when the app is in background

## Development

### Project Structure
```
app/
├── src/main/java/com/jununmp3/player/
│   ├── MainActivity.java
│   ├── adapter/
│   │   └── MusicAdapter.java
│   ├── model/
│   │   ├── Music.java
│   │   └── Playlist.java
│   ├── service/
│   │   └── MusicService.java
│   └── utils/
│       ├── MusicLibrary.java
│       └── PlaylistManager.java
└── src/main/res/
    ├── layout/
    ├── values/
    └── drawable/
```

### Building

```bash
./gradlew assembleDebug
```

### Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is open source and available under the MIT License.

## Credits

Developed by the Junun Team

## Version History

- v1.0.0 - Initial release
  - Basic MP3 playback functionality
  - Background service implementation
  - Notification controls
  - Playlist management
  - Material Design UI
