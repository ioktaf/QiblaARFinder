# Play Store Release Notes

## Identity

- App name: Qibla AR Finder
- Application ID: `com.qiblaarfinder`
- Current version name: `1.0.0`
- Current version code: `1`

## Listing Assets

- Title: `fastlane/metadata/android/en-US/title.txt`
- Short description: `fastlane/metadata/android/en-US/short_description.txt`
- Full description: `fastlane/metadata/android/en-US/full_description.txt`
- Changelog: `fastlane/metadata/android/en-US/changelogs/1.txt`
- Privacy policy draft: `docs/PRIVACY_POLICY.md`
- Privacy policy web page: `docs/index.html`
- Expected Pages URL after enabled: `https://ioktaf.github.io/QiblaARFinder/`

## Release Artifacts

- Signed APK command: `.\gradlew.bat assembleRelease --no-daemon`
- Signed APK output: `app/build/outputs/apk/release/app-release.apk`
- Signed AAB command: `.\gradlew.bat bundleRelease --no-daemon`
- Signed AAB output: `app/build/outputs/bundle/release/app-release.aab`

## Still Needed Before Store Upload

1. Screenshot ponsel minimal 2-4 lembar dari layar Home, compass, dan AR overlay
2. Feature graphic 1024 x 500 jika nanti mau upload ke Play Console
3. Aktifkan GitHub Pages dari branch `main` folder `/docs` agar URL privacy policy publik
4. Review kembali Data safety form di Play Console sebelum submit
