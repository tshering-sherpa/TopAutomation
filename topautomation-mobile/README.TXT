MOBILE TESTS (Appium)  -  module topautomation-mobile
==========================

Android/iOS app tests. Needs an Appium server, a device/emulator and an app file in apps/.

CONTENTS
  apps/                             PUT YOUR .apk / .app / .ipa HERE (git-ignored)
  src/main/java/.../mobile/pages/   page objects (MobileBasePage, MobileLoginPage)
  src/main/java/.../mobile/driver/  MobileDriverFactory, AppiumServerManager (optional auto-start)
  src/main/java/.../mobile/base/    MobileBaseTest      data/MobileExcelDataProvider
  src/main/resources/config/        mobile.properties (platform, Appium URL, android.*, ios.*)
  src/test/java/.../mobile/tests/   YOUR TESTS (+ tests/templates/)
  src/test/resources/               suites/mobile-*.xml   testdata/mobile-testdata.xlsx

MAVEN PROFILES (run from the ROOT folder, one level up):  -Pmobile-sanity, -Pmobile-functional, -Pmobile-regression, -Pmobile-all
EXAMPLE
  mvn clean test -Pmobile-functional -Dplatform=android   (start "appium" first, or add -Dappium.server.autostart=true)

REPORT + LOG
  topautomation-mobile/target/reports/  and  target/logs/mobile.log, appium.log

FULL GUIDE: see README.TXT in the root folder (module guides are Part D, sections 8-12).
