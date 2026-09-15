# Radio Maroc — Security & Privacy Model

Radio Maroc is intentionally small, open and auditable. The project aims to provide radio playback without advertising, profiling or unnecessary collection of user information.

## Security guarantees of the official source

- No advertising SDKs.
- No analytics, telemetry or behavioural-tracking SDKs.
- No account system and no user-profile database.
- No contacts, SMS, microphone, camera, location, storage, accessibility, VPN, device-admin, package-install or overlay permissions.
- Android application backup is disabled.
- Internal Activity/Service components are not exported; only the launcher Activity is exported as required by Android.
- Cleartext HTTP is denied globally. A short explicit allow-list exists only for legacy radio hosts that still publish their public audio stream over HTTP.
- HTTPS is used for the app-owned remote stream directory and public EasyBroadcast token resolution.
- Local preferences are limited to functions such as language, favourites and display mode.
- Stream addresses and resolver logic remain visible in the source tree for independent review.
- GitHub Actions builds publish an APK and SHA-256 checksum so releases can be verified.

## Network connections

Internet access is used only for functionality visible to the user: retrieving radio audio, station artwork, the public stream directory, and short-lived public EasyBroadcast stream tokens required by some SNRT streams. Radio Maroc does not operate an analytics or advertising endpoint.

## Independent verification

Security researchers are encouraged to inspect the source, review Android permissions and network destinations, build the application independently, and compare release hashes. Open source does not make malicious modification mathematically impossible; it makes behaviour inspectable and unauthorized changes easier to detect.

A third-party APK should not be trusted merely because it uses the Radio Maroc name or icon. Verify its origin and SHA-256 when a checksum is provided.
