# Radio Maroc

**Radio Maroc** is a free, open-source and ad-free Android application for listening to Moroccan radio stations worldwide.

Developer **M@ster** created the project to serve Moroccans living abroad by offering a simple, secure and modern way to stay connected with Moroccan radio, culture, music, news and regional voices.

## Radio Maroc 4.0
- English is the default language on first install.
- Arabic and French remain available from the universal ⚙ Settings menu.
- Premium dark Moroccan visual identity with deep navy, polished gold, emerald green and Moroccan red.
- Richer geometric zellige treatment in the header and interface instead of cartoon-style decoration.
- Redesigned Moroccan launcher icon combining zellige, the Moroccan star and a radio motif.
- List, compact-list and thumbnail/grid station views.
- Search, favorites and recent stations.
- Persistent mini-player with play/pause and stop.
- Android notification and lock-screen media controls through MediaSession.
- Sleep timer.
- About page crediting M@ster and the diaspora-service mission.
- Official French/Latin station names are preserved; station names are not translated.

## Radio catalogue
The catalogue follows the current HACA public/private radio service lists and the SNRT public-radio structure. A station is never assigned a fabricated Internet stream. If a distinct secure HTTPS stream cannot be verified for this build, the station remains visible but playback is disabled with a clear message until a verified stream is added.

## Privacy and security
- No advertisements.
- No analytics SDK.
- No trackers.
- No user account.
- No collection of listening history, favorites or personal data by any server.
- Favorites and recent stations are stored only on the device.
- All cleartext HTTP traffic is disabled.
- The app requests only Internet, media foreground-service, notification and wake-lock permissions required for radio playback.
- Backups are disabled in the manifest.
- Release builds are non-debuggable, optimized and resource-shrunk.

## Open source
The source is licensed under **GNU GPL-3.0-or-later**. Anyone can inspect the complete source and reproduce the build.

## App signing and trusted updates
Android requires every APK to be cryptographically signed. Radio Maroc has a dedicated 4096-bit RSA release certificate whose public certificate is committed as `SIGNING_CERTIFICATE.pem`.

Official release certificate SHA-256:

`73:7F:44:24:EE:54:5E:89:26:45:E3:06:A1:67:23:E5:6C:81:B5:9B:F8:0B:05:F9:CE:FE:EB:5B:6A:A8:25:CC`

The private signing key is **not** stored in this public repository. GitHub Actions can build a stable signed release only when the repository secrets `RADIO_MAROC_KEYSTORE_B64` and `RADIO_MAROC_KEYSTORE_PASSWORD` are configured.

This stable certificate ensures that future official updates can be installed only when signed by the same Radio Maroc key. It does not by itself remove Android/Samsung warnings associated with installing apps outside an app store; store distribution is a separate trust signal.

## Build
Requirements: Java 17, Android SDK 35 and Gradle 8.9.

```bash
gradle assembleDebug
```

Signed release builds are produced by the GitHub Actions workflow when the release signing secrets are configured.
