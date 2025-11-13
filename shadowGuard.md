# Shadow Guard — Summary

Shadow Guard is the mobile client for an asset protection and monitoring system built as part of ProjectDAM. It focuses on secure user access and real-time alerting while providing a compact, easy-to-use UI implemented with Jetpack Compose.

Key points

- Purpose: Protect and monitor devices and applications by scanning, reporting suspicious activity, and surfacing alerts to end users and administrators.
- Primary flows: user authentication (login, OTP, reset), onboarding (signup), a Home dashboard, Scans (device/app scanning), Alerts (viewing and acknowledging alerts), and Settings.
- UI: Fully implemented with Jetpack Compose — screens and components live under `app/src/main/java/tn/esprit/dam_android/screens` and `app/src/main/java/tn/esprit/dam_android/ui/components`.
- Backend integration: Uses Retrofit (`app/src/main/java/tn/esprit/dam_android/api/RetrofitClient.kt`) and token management (`TokenManager.kt`) for API calls. Confirm API endpoints and contracts against `shadowGuard.pdf` and the `models/*/services` interfaces.
- Security: Token-based auth, secure handling of secrets (keep `google-services.json`, keystores and env files out of VCS), and guidance about storing signing credentials in CI.

Developer notes

- The included `shadowGuard.pdf` contains the full product brief, feature list, and API integration notes. Use it as the canonical source for expected behavior and backend contract details.
- For UI work, use Android Studio's Compose tooling (Previews and Layout Inspector) to iterate quickly.
- Verify `android:exported` attributes in `AndroidManifest.xml` if targeting Android 12+ and ensure Kotlin / Compose versions are compatible in `build.gradle.kts`.

Reference files

- App entry and navigation: `app/src/main/java/tn/esprit/dam_android/MainActivity.kt` and `navigation` package.
- Auth services and models: `app/src/main/java/tn/esprit/dam_android/models/auth`
- Screens: `app/src/main/java/tn/esprit/dam_android/screens`

---

This summary is intentionally short — let me know if you want a longer, sectioned briefing extracted from `shadowGuard.pdf` (I can create a more detailed markdown version).
