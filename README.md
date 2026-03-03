# Godot Android Push Notifications Plugin

A Godot 4.x Android plugin (v2) that provides Firebase Cloud Messaging (FCM) device registration for push notifications. Designed to mirror the interface of the [Godot iOS APN plugin](https://github.com/godot-sdk-integrations/godot-ios-plugins/tree/master/plugins/apn).

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
3. In Godot, go to **Project > Project Settings > Plugins** and enable **GodotAndroidPushNotifications**. This step is required — without it the AAR won't be bundled into the APK and `Engine.has_singleton()` will return `false` at runtime.

## Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with your Godot project's package name.
3. Download `google-services.json` and place it in your Godot project's `android/build/` directory (created when you install the Android build template via **Project > Install Android Build Template**).
4. Add the Google Services plugin to your Godot project's Android build files:

   In `android/build/settings.gradle`, add to the `pluginManagement > plugins` block:
   ```groovy
   id 'com.google.gms.google-services' version '4.4.2' apply false
   ```

   In `android/build/build.gradle`, add to the `plugins` block:
   ```groovy
   id 'com.google.gms.google-services'
   ```

   > **Note:** Use the `plugins {}` block syntax (not `apply plugin:`). The older `apply plugin:` syntax will fail with "Plugin not found" because Godot's Android build uses `pluginManagement` in `settings.gradle` for plugin resolution.

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
    print("Registration failed: " + error)
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

## Troubleshooting

### `Engine.has_singleton("GodotAndroidPushNotifications")` returns `false`

Make sure the plugin is enabled in **Project > Project Settings > Plugins**. The editor plugin must be active for the AAR to be included in the Android export.

### `Plugin with id 'com.google.gms.google-services' not found`

Use the `plugins {}` block syntax in `build.gradle` instead of `apply plugin:`. See the [Firebase Setup](#firebase-setup) section.

### `Could not find com.google.firebase:firebase-messaging:`

This means a Firebase dependency was declared without an explicit version. The plugin's `export_plugin.gd` uses pinned versions (e.g., `firebase-messaging:24.1.0`) because Godot's export system does not support Gradle BOM (`platform()`) dependencies. If you've modified the export plugin, ensure all dependencies have explicit versions.

## License

MIT
