# Security Policy

RadioMaroc is intentionally small and auditable.

## Security guarantees of the official source
- No advertising SDKs.
- No analytics SDKs.
- No tracking SDKs.
- No account system or telemetry endpoint.
- No contacts, SMS, microphone, camera, location, storage, accessibility, VPN, device-admin, package-install, or overlay permissions.
- Station artwork is never bundled from third parties; it is requested from broadcaster websites and falls back to a locally generated badge.
- Cleartext HTTP is blocked globally except for explicitly listed legacy stream hosts.
- GitHub Actions builds with read-only repository permissions and publishes a SHA-256 checksum beside the APK.

## Verifying an APK
Use the APK created by the official GitHub Actions workflow and compare its SHA-256 with `SHA256SUMS.txt` from the same build artifact.

Open source cannot make malicious modification mathematically impossible. It makes unauthorized changes reviewable and detectable. A third-party APK should not be trusted merely because it uses the RadioMaroc name or icon.
