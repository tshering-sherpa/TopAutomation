# Mobile testing setup (Appium)

## How the pieces fit

```
TestNG test (Java)  --HTTP-->  Appium SERVER (Node.js, port 4723)  -->  driver (UiAutomator2 / XCUITest)  -->  emulator / simulator / real device  -->  your APP
```

The Java framework is only the *client*. Something must run the Appium server. Two ways:

| Mode | How | When |
|---|---|---|
| **Manual** (default) | open a terminal and run `appium` | simplest, best for debugging with Appium Inspector |
| **Auto-start from Java** | set `appium.server.autostart=true` (or `-Dappium.server.autostart=true`); `AppiumServerManager` starts a *local* server before mobile tests, reuses one that is already running, and stops it at the end | convenient locally / on a CI box that has Appium installed |
| **Remote / cloud** | set `appium.server.url` to the grid or vendor URL (BrowserStack, Sauce Labs, LambdaTest...) and `android.app` / `ios.app` to the vendor's uploaded-app id/URL | device farms |

## One-time machine setup

1. **JDK 17+** and **Node.js 20+**
2. `npm install -g appium`
3. Drivers: `appium driver install uiautomator2` (Android), `appium driver install xcuitest` (iOS - macOS only)
4. Verify: `appium driver doctor uiautomator2` (recent Appium versions) or `appium driver list --installed`

**Android:** install Android Studio -> Device Manager -> create an emulator (AVD) and start it. Set `ANDROID_HOME`
(SDK path) and add `platform-tools` to PATH. `adb devices` must list your emulator/device (real device: enable
Developer options + USB debugging).

**iOS:** macOS + Xcode (+ command line tools). Simulators are created in Xcode. Real devices additionally need an
Apple developer signing setup (team id, `xcodeOrgId`/`xcodeSigningId`, device `udid`). iOS cannot be tested on Windows/Linux.

## What app file do I need?

| Platform | File to give Appium | Notes |
|---|---|---|
| Android | **`.apk`** | A debug or QA build is fine (no need for it to be debuggable). `.aab` (app bundle) is a *store upload format* and cannot be installed directly - ask for an `.apk` (or generate one from the bundle with `bundletool build-apks --mode=universal`) |
| iOS simulator | **`.app`** folder (or a zip of it) built for the *simulator* | Build with Xcode/`xcodebuild -sdk iphonesimulator` |
| iOS real device | **`.ipa`**, signed for your device | Needs provisioning/signing |
| Already installed | no file - use `android.app.package` + `android.app.activity` or `ios.bundle.id` | App must already be on the device |

Ask your mobile developers for a **QA/test build** that has stable `content-desc` (Android) / `accessibilityIdentifier`
(iOS) values on every control - those become your locators (`AppiumBy.accessibilityId("...")`), one page object for both
platforms. Find them with **Appium Inspector** (https://github.com/appium/appium-inspector).

## Where do I put the app?

Drop it in **`topautomation-mobile/apps/`** (git-ignored) and reference it in `topautomation-mobile/src/main/resources/config/mobile.properties`
(or per run with `-D`):

```properties
android.app=apps/myapp.apk
android.device.name=emulator-5554      # name from `adb devices`
android.platform.version=14            # optional
ios.app=apps/MyApp.app                 # simulator build
ios.device.name=iPhone 15
ios.platform.version=17.5
```

Relative paths are resolved to absolute automatically. `http(s)://` URLs are passed through untouched.

## Run

```bash
# terminal 1 (skip if autostart=true)
appium
# terminal 2
mvn clean test -Pmobile-functional -Dplatform=android -Dandroid.app=apps/myapp.apk
mvn clean test -Pmobile-functional -Dplatform=ios     -Dios.app=apps/MyApp.app
mvn clean test -Pmobile-functional -Dappium.server.autostart=true     # let Java start Appium
```

Appium server log (autostart mode): `target/logs/appium.log`. Failure screenshots appear in the Extent report.

## Replace the sample locators

`mobile/pages/MobileLoginPage` uses the ids of the open-source WebdriverIO demo app so you can try the flow first
(`https://github.com/webdriverio/native-demo-app` -> `android.wdio.native.app.v1.0.8.apk`). Replace them with your
app's ids, then adjust `MobileLoginTest` (mobile module) and `MobileLoginInjectionTest` (security module).

> Full beginner guide: README.TXT in the project root (Section 12). Paths above are relative to `topautomation-mobile/`.
