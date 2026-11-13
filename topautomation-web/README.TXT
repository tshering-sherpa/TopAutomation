WEB TESTS (Selenium)  -  module topautomation-web
=========================

Browser tests with the Page Object Model.

CONTENTS
  src/main/java/.../web/pages/       page objects (BasePage, LoginPage, DropdownPage, GenericFormPage ...)
  src/main/java/.../web/base/        BaseTest (opens/closes a browser per test)
  src/main/java/.../web/driver/      WebDriverFactory (chrome | firefox | edge | grid)
  src/main/java/.../web/data/        WebExcelDataProvider
  src/main/resources/config/         web.properties, web-qa.properties, web-staging.properties
  src/test/java/.../web/tests/       YOUR TESTS (+ tests/templates/ copy-me files)
  src/test/resources/suites/         web-all / web-sanity / web-functional / web-regression .xml
  src/test/resources/testdata/       web-testdata.xlsx (Login, Registration, Dropdown)

MAVEN PROFILES (run from the ROOT folder, one level up):  -Pweb-sanity, -Pweb-functional, -Pweb-regression, -Pweb-all
EXAMPLE
  mvn clean test -Pweb-sanity -Dheadless=true

REPORT + LOG
  topautomation-web/target/reports/  and  target/logs/web.log

FULL GUIDE: see README.TXT in the root folder (module guides are Part D, sections 8-12).
