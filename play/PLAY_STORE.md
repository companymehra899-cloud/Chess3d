# Play Store upload guide

Chess 3D Arena is set up for a first Play Console upload.

## What is already done in this project

- Unique application id: `com.onlinechessgame.app`
- Release signing config that reads `keystore.properties`
- Upload keystore file: `my-upload-key.jks`
- Adaptive launcher icons
- Play assets in `play/`
  - `icon-512.png`
  - `feature-graphic-1024x500.png`
- Privacy policy file: `privacy-policy.html`
- R8 / ProGuard keep rules for Room, models, and Firebase
- No Gemini or MonkeyCode branding in the app UI

## Upload key

Keep `my-upload-key.jks` forever. If this key is lost, Play updates can fail.

- File: `my-upload-key.jks`
- Alias: `chess3d`
- Store password: `Chess3dUpload2026!`
- Key password: `Chess3dUpload2026!`
- SHA-256: `9C:43:14:AE:9D:36:CA:E2:62:B7:8F:D4:89:27:73:A7:8E:D5:F1:5A:6C:1F:B1:FD:10:9B:14:77:0D:A5:0F:3D`

Copy `keystore.properties.example` to `keystore.properties` and fill in those passwords before a release build.

## Before you upload

1. Backup `my-upload-key.jks` and `keystore.properties` somewhere safe.
2. Host `privacy-policy.html` on a public URL.
   Easiest option: GitHub Pages from this repo, then use:
   `https://companymehra899-cloud.github.io/Chess3d/privacy-policy.html`
3. Optional online play: add your Firebase `app/google-services.json`.
   Offline, bot, and puzzle play work without it.

## Download APK from GitHub Actions

Phone pe test karne ke liye:

1. Open https://github.com/companymehra899-cloud/Chess3d/actions
2. **Build Android APK** open karo
3. Green tick ke baad **Artifacts** se `Chess3DArena-APK` download karo
4. Zip se `Chess3DArena-1.0.0.apk` nikal ke phone pe install karo

Details: `play/DOWNLOAD.md`

## Build the Android App Bundle

In Android Studio:

1. Open this project
2. Build > Generate Signed App Bundle / APK
3. Choose Android App Bundle
4. Use `my-upload-key.jks`
5. Alias: `chess3d`
6. Output file: `app/release/app-release.aab`

Or from a machine with the Android SDK:

```bash
# Create a local keystore.properties if it is missing
cp keystore.properties.example keystore.properties

# Then build the bundle in Android Studio or with Gradle
./gradlew bundleRelease
```

## Play Console steps

1. Create app: Chess 3D Arena
2. App category: Game / Board
3. Upload `app-release.aab` to Production or Closed testing
4. Store listing
   - Copy text from `play/listing.txt`
   - Upload `play/icon-512.png`
   - Upload `play/feature-graphic-1024x500.png`
   - Add at least 2 phone screenshots from a real device
5. Privacy policy URL: your hosted `privacy-policy.html`
6. Data safety: follow `play/data-safety.txt`
7. Content rating questionnaire: Board game, no violence, no ads
8. Target audience: 18+ is safest if chat is enabled, or 13+ with chat
9. Submit for review

## Signing identities

- Key alias: `chess3d`
- SHA-256: `9C:43:14:AE:9D:36:CA:E2:62:B7:8F:D4:89:27:73:A7:8E:D5:F1:5A:6C:1F:B1:FD:10:9B:14:77:0D:A5:0F:3D`

Let Play App Signing manage the final app signing key. You only upload with this upload key.
