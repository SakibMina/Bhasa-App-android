# Bhasha Android MVP

A starter Android project for a Bangla-English dictionary with a floating lookup bubble.

## Current MVP
- Jetpack Compose dictionary search screen.
- Small bundled sample dictionary (not a full lexical database).
- English-to-Bangla and Bangla-to-English direction toggle.
- User-enabled Android overlay bubble.
- Tap bubble to look up copied text in the sample dictionary.

## Important limitations
- Sentence/paragraph translation is not integrated yet; it requires a translation provider/API or a local model.
- Dictionary data is only a small sample. Replace/extend it with a properly licensed dataset.
- Android restricts background clipboard access. This build deliberately does not continuously monitor clipboard. The user copies text and explicitly taps the floating bubble to trigger lookup.
- Overlay permission is required for the bubble. The user must grant it in Android Settings.
- The overlay panel is a starter implementation and should be polished/tested across Android versions before release.

## Build
1. Open this folder in Android Studio.
2. Allow Gradle sync.
3. Connect an Android device or start an emulator.
4. Run the `app` configuration.
5. To test the floating bubble: open Bhasha, tap Enable floating bubble, grant "Display over other apps", return to Bhasha, enable the bubble, copy a supported word in another app, then tap the bubble.

Package: `com.bhasha.dictionary`
Minimum Android: 8.0 (API 26)
