# Godot Android Push Notifications Plugin

A Godot 4.x Android plugin that provides Firebase Cloud Messaging (FCM) device registration for push notifications. Designed to mirror the interface of the [Godot iOS APN plugin](https://github.com/godot-sdk-integrations/godot-ios-plugins/tree/master/plugins/apn).

## Features

- Register device for push notifications via Firebase Cloud Messaging
- Retrieve FCM token to send to your backend server
- Requests `POST_NOTIFICATIONS` permission automatically on Android 13+
- Badge number getter/setter (stub implementation for iOS interface parity)

## Requirements

- Godot 4.3+
- Android SDK with minimum API 24
- Java 17+
- A Firebase project with Cloud Messaging enabled

## Building the Plugin

```bash
./gradlew assemble
```

This builds the AAR and copies it along with the export scripts to `plugin/demo/addons/GodotAndroidPushNotifications/`.

## Installation

1. Build the plugin or download a release.
2. Copy the `GodotAndroidPushNotifications` folder into your Godot project's `addons/` directory:
   ```
   your-godot-project/
   └── addons/
       └── GodotAndroidPushNotifications/
           ├── bin/
           │   ├── debug/GodotAndroidPushNotifications-debug.aar
           │   └── release/GodotAndroidPushNotifications-release.aar
           ├── export_plugin.gd
           └── plugin.cfg
   ```
3. In Godot, go to **Project > Project Settings > Plugins** and enable **GodotAndroidPushNotifications**.

## Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with your Godot project's package name.
3. Download `google-services.json` and place it in your Godot project's `android/build/` directory (created when you install the Android build template via **Project > Install Android Build Template**).
4. Add the Google Services plugin to your Android build. In `android/build/build.gradle`, add:
   ```groovy
   apply plugin: 'com.google.gms.google-services'
   ```
   And in the project-level `android/build/settings.gradle` or buildscript, ensure the plugin classpath is available:
   ```groovy
   id 'com.google.gms.google-services' version '4.4.2' apply false
   ```

## Usage

```gdscript
var _plugin

func _ready():
    if Engine.has_singleton("GodotAndroidPushNotifications"):
        _plugin = Engine.get_singleton("GodotAndroidPushNotifications")
        _plugin.WillSendDeviceToken.connect(_on_token_received)
        _plugin.RegistrationError.connect(_on_registration_error)

func register():
    if _plugin:
        _plugin.registerPushNotifications()

func _on_token_received(token: String):
    print("FCM Token: " + token)
    # Send this token to your server

func _on_registration_error(error: String):
    printerr("Registration failed: " + error)
```

## API Reference

### Methods

| Method | Description |
|--------|-------------|
| `registerPushNotifications()` | Requests notification permission (Android 13+) and retrieves the FCM device token. Emits `WillSendDeviceToken` on success or `RegistrationError` on failure. |
| `setBadgeNumber(value: int)` | Stores a badge number value. Stub implementation — Android does not have native badge support like iOS. |
| `getBadgeNumber() -> int` | Returns the stored badge number value. |

### Signals

| Signal | Parameter | Description |
|--------|-----------|-------------|
| `WillSendDeviceToken` | `token: String` | Emitted when the FCM registration token is successfully retrieved. Send this token to your server to target this device with push notifications. |
| `RegistrationError` | `error: String` | Emitted when FCM token retrieval fails. |

## License

MIT
