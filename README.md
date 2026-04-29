# Qibla AR Finder

Aplikasi Android Native berbasis Kotlin + Jetpack Compose untuk membantu pengguna menemukan arah Qiblat secara akurat melalui kombinasi lokasi, sensor kompas, cache offline, jadwal salat, dan layar camera overlay sebagai pondasi pengalaman AR.

## Fitur yang sudah disiapkan

- Auto-location dengan Fused Location Provider
- Input manual via kota atau koordinat
- Perhitungan bearing Qiblat berbasis great-circle
- Kompas 2D reaktif dengan accelerometer + magnetometer
- Jadwal salat offline-friendly yang dihitung lokal dan di-cache ke Room
- Camera overlay untuk pengalaman AR beta

## Struktur

- `app/src/main/java/com/qiblaarfinder/` berisi app, domain, data, dan UI
- `app/src/test/java/com/qiblaarfinder/` berisi unit test dasar untuk engine arah kiblat dan jadwal salat

## Catatan

Workspace awal kosong, jadi project ini dibootstrap dari nol dan sekarang sudah bisa diverifikasi lokal memakai Gradle wrapper.

- Unit test terverifikasi dengan `.\gradlew.bat testDebugUnitTest --no-daemon`
- APK debug terverifikasi dengan `.\gradlew.bat assembleDebug --no-daemon`
- Hasil build debug tersedia di `app/build/outputs/apk/debug/app-debug.apk`

## Release Build

- Repo lokal sekarang sudah diinisialisasi dengan git branch `main`
- Build release bisa dijalankan dengan `.\gradlew.bat assembleRelease --no-daemon`
- Jika `keystore.properties` belum dibuat, output release yang dihasilkan akan berupa APK unsigned
- Template signing disiapkan di `keystore.properties.example` untuk dipakai saat sudah punya keystore produksi
- Metadata listing Play Store ada di `fastlane/metadata/android/en-US/`
- Draft privacy policy ada di `docs/PRIVACY_POLICY.md`
