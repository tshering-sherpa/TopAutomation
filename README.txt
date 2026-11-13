================================================================================================
 TopAutomation  -  Java Test Automation Framework
 Separate modules for WEB, MOBILE, API and SECURITY testing
 BEGINNER'S GUIDE
================================================================================================

HOW THIS FILE IS ORGANISED
  PART A  START HERE ............ 0  How to learn and use this project     1  What is this project
  PART B  SETUP ................. 2  What to install                       3  Libraries and imports
                                  4  Getting the project from Git and the first run
  PART C  HOW IT IS ORGANISED ... 5  Module map (what lives where)          6  Configuration
                                  7  Running tests: Maven profiles
  PART D  MODULE GUIDES ......... 8  WEB   9  EXCEL DATA PROVIDERS   10  API   11  SECURITY   12  MOBILE
  PART E  REFERENCE ............. 13 Reports and logs     14 Group-name cheat sheet     15 CI
                                  16 Troubleshooting      17 Command cheat sheet

NOTE ABOUT THIS ZIP: it was assembled on a machine with no internet and no Java compiler, so it has
never been compiled. It is written and cross-checked carefully (package names, imports, module
dependencies, data providers, Excel sheets, suites and profiles were verified by script), but the very
first "mvn clean test-compile" on YOUR computer is the real check. Section 16 lists likely fixes.


================================================================================================
 PART A  -  START HERE
================================================================================================

------------------------------------------------------------------------------------------------
0. HOW TO LEARN AND USE THIS PROJECT  (BRIEF GUIDE - READ THIS FIRST)
------------------------------------------------------------------------------------------------
WHO THIS IS FOR
  Testers and developers who know a little Java (classes, methods, String, List, Map, annotations).
  No test-automation experience is needed. Never written Java? Spend a few hours on a beginner tutorial
  first (https://dev.java/learn/ is free), then come back.

THE MENTAL MODEL (6 ideas - everything else is detail)
  1. The project is split into MODULES, one per kind of testing. Each module owns its code, its config
     files, its test data, its suites, its logs and its reports:
         topautomation-web       browser tests (Selenium)
         topautomation-mobile    mobile app tests (Appium)
         topautomation-api       REST API tests (REST Assured)
         topautomation-security  injection-attack library + attack tests for web, mobile and API
         topautomation-core      small shared base: config loader, HTML report, Excel reader, logging
  2. A TEST says WHAT to check. It reads like a short story: open page -> do something -> assert result.
  3. A PAGE OBJECT / API OBJECT knows HOW to talk to one page, screen or API resource. Tests never
     contain locators or URLs - only calls to these classes.
  4. DATA lives in Excel (one workbook per module) or in the payload files - not inside Java code.
  5. SETTINGS live in the module's config files and can be overridden with -Dname=value.
  6. GROUPS -> SUITES -> PROFILES: you tag a test with groups, a suite file picks groups, a Maven
     profile picks a suite. You change nothing to run "just the web sanity tests":

        you type:  mvn clean test -Pweb-sanity
                      |
        pom.xml profile "web-sanity"  ->  topautomation-web/.../suites/web-sanity.xml
                      |
        suite includes group "web-sanity"  ->  every @Test tagged groups = {"web-sanity"} runs
                      |
        test extends BaseTest (browser opens) -> calls page objects -> asserts
                      |
        topautomation-web/target/reports/*.html  (+ screenshot on failure)  and  target/logs/web.log

THE LEARNING PATH  (about 2-3 days at a relaxed pace; each stage ends with a "done when")
  STAGE 1  Get it running (1 hour)
      Do:   Section 2 (installs) and Section 4 (clone, open in IDE, run  mvn clean test -Pweb-sanity).
      Done when: you have opened the HTML report in topautomation-web/target/reports/ and seen a green test.

  STAGE 2  Read one web test end to end (1 hour)
      Read in this order (all inside topautomation-web/):
         src/test/java/.../web/tests/LoginTest.java
         src/main/java/.../web/pages/LoginPage.java   then   BasePage.java
         src/main/java/.../web/base/BaseTest.java
      Done when: you can explain who opens the browser, who finds the username field, and where the
                 assertion is.

  STAGE 3  Change things and break things on purpose (1 hour)   -> Exercises 1, 2
      Done when: you have seen a red test with a screenshot in the report, and fixed it again.

  STAGE 4  Excel-driven testing (1 hour)   -> Section 9, Exercises 3, 4
      Done when: you have added your own sheet and provider and seen one report entry per row.

  STAGE 5  API testing (2 hours)   -> Section 10, Exercise 5
      Read in topautomation-api/: BookingApiTest -> BookingApi -> Booking (model).
      Done when: you have written a new API test that calls an endpoint and asserts on the response.

  STAGE 6  Security (injection) testing (1 hour)   -> Section 11, Exercise 6
      Read in topautomation-security/: LoginInjectionTest, ApiInjectionTest, SecurityAssertions.
      Done when: you can say what a "finding" is and have added your own payload line.

  STAGE 7  Mobile (half a day, mostly environment setup)   -> Section 12
      Done when: the sample-app test passes on your emulator. Skip this stage if you do not test mobile.

  STAGE 8  Make it yours (ongoing)   -> Section 6 checklist
      Point the modules at your application, replace the sample page objects / API objects with your
      own, write real tests, tag them with groups.

HANDS-ON EXERCISES  (all use the public sample sites)
  EXERCISE 1 - add a data row (no Java).
      Open topautomation-web/src/test/resources/testdata/web-testdata.xlsx, sheet "Login", add a row:
        LOGIN_07 | Empty password | tomsmith | (leave blank) | FAILURE | Your password is invalid! | Y
      Save, close Excel, run:  mvn clean test -Pweb-functional      The report must show [LOGIN_07].
  EXERCISE 2 - make a test fail on purpose.
      Change ExpectedMessage in row LOGIN_02 to  Banana  and run the same command. Open the report: find
      the failed test, read the message, look at the screenshot. Change it back.
  EXERCISE 3 - add a row to the "Dropdown" sheet:  DD_03 | Select option 2 again | Option 2 | Option 2 | Y
  EXERCISE 4 - your own Excel sheet: follow the Section 9 worked example with a new sheet "Checkboxes".
  EXERCISE 5 - a new API test.
      Copy topautomation-api/src/test/java/com/topautomation/api/tests/templates/ApiTestTemplate.java to
      .../api/tests/MyFirstApiTest.java (note: one folder up - suites only scan the "tests" package),
      remove "enabled = false", create a booking, read it back with bookingApi.get(id), assert the first
      names are equal, tag it "api-functional". Run:  mvn clean test -Papi-functional
  EXERCISE 6 - extend the attack library.
      Add the line   ' OR 'x'='x   to topautomation-security/src/main/resources/payloads/sqli.txt and run
      mvn clean test -Psecurity-web -Dpayload.categories=SQLI    - the report must list your new payload.
  EXERCISE 7 - a new page object.
      Page: https://the-internet.herokuapp.com/checkboxes . Create web/pages/CheckboxesPage.java (copy
      DropdownPage; locator By.cssSelector("#checkboxes input")), then a test that ticks the first box
      and asserts it is selected. Follow "HOW TO ADD YOUR OWN WEB TEST" in Section 8.

HOW TO WRITE A NEW TEST  (the loop you will repeat every day)
  1. Pick the module: web / api / mobile (functional tests) or security (attack tests).
  2. Does a page object / API object exist for it? If not, create one first (copy a sample).
  3. Copy a template from  <module>/src/test/java/.../tests/templates/ , move it one folder up into
     the "tests" package (security: tests/web | tests/api | tests/mobile), rename class + file and
     delete "enabled = false".
     !! Suite files only scan that "tests" package of their module, so a test placed elsewhere never runs.
  4. Tag it with groups (Section 14 lists every group name).
  5. Run your module only:  mvn clean test -P<profile>   and read its report.
  6. Commit to Git with a clear message.

GOOD HABITS (the rules this framework is built on)
  * One test checks one behaviour and has a clear name:  loginWithWrongPasswordShowsError().
  * Every test is independent - it must pass alone and in any order (a fresh browser opens for each).
  * No Thread.sleep(). Use the wait-aware helpers in BasePage (visible/click/type).
  * Locators only in page objects; URLs/endpoints only in page/API objects; test data in Excel.
  * Assertion messages say what was expected:  Assert.assertTrue(x, "Expected to be logged in").
  * Keep modules separate: web code never imports api or mobile code, and so on (see Section 5).
  * Never put real passwords in Git (Section 6 - secrets).
  * Security tests: only against systems you may test, never production (Section 11).
  * Keep sanity tiny (2-5 minutes) and regression complete; tag accordingly.

"I WANT TO ..." QUICK FINDER
  I want to ...                                       Go to
  --------------------------------------------------  ---------------------------------------------------
  run something                                       Section 7 (profiles) / Section 17 (cheat sheet)
  point the tests at my site, API or app              Section 6 + the module's config/*.properties files
  test a new web page                                 Section 8
  run the same test with many data rows               Section 9 (Excel + data provider)
  test a new API endpoint                             Section 10
  check my form/API against injection attacks         Section 11
  test a mobile app                                   Section 12
  find a report or a log                              Section 13
  understand a failure                                Section 13 + Section 16
  change which tests run in "sanity"/"regression"     change the groups on the @Test (Section 14)
  run in CI                                           Section 15

WORDS YOU WILL SEE
  Module                   a Maven sub-project (web, mobile, api, security, core) with its own pom.xml.
  Page Object Model (POM)  one class per page/screen/API resource, holding locators and actions.
  Locator                  how to find an element on a page: By.id("username"), By.cssSelector("..."), ...
  Assertion                a check that fails the test if wrong:  Assert.assertEquals(actual, expected).
  TestNG                   the test runner. @Test marks a test; groups tag it; @DataProvider feeds it rows.
  Data provider            a method that returns rows of test data; the test runs once per row.
  Group                    a label on a test ("web-sanity", "api-regression", "security-api" ...).
  Suite file               an XML file listing which groups/packages to run.
  Maven profile            -P<name>: selects a module + suite from the parent pom.xml.
  Listener                 code reacting to test events (builds the HTML report, takes screenshots).
  Payload / finding        an attack string / a suspicious reaction to it.

------------------------------------------------------------------------------------------------
1. WHAT IS THIS PROJECT?
------------------------------------------------------------------------------------------------
TopAutomation is a Java/Maven MULTI-MODULE project. Every kind of testing lives in its own module:

  MODULE                   WHAT IT TESTS / HOLDS                          TECHNOLOGY
  -----------------------  ---------------------------------------------  ---------------------------
  topautomation-web        web sites in a real browser                    Selenium + TestNG
  topautomation-mobile     Android / iOS apps                             Appium + TestNG
  topautomation-api        REST APIs (requests sent straight to backend)  REST Assured + TestNG
  topautomation-security   injection attacks on web forms, mobile forms   TestNG + the payload library
                           and APIs
  topautomation-core       shared plumbing only (no tests)                config, HTML report, Excel,
                                                                          logging

Each module has its OWN:  config files - Excel data workbook - data providers - TestNG suite files -
Maven profiles - log file - HTML report.  Nothing is mixed: a web run never writes into the API
report, an API run never needs a browser, and so on.

Shared features: Page Object Model, Excel data-driven tests, Extent HTML reports with screenshots,
automatic retry of flaky failures (never for security tests), and ~160 injection-attack payloads.

WHICH LANGUAGE? The whole project is JAVA (plus XML/properties/Excel files for configuration and data).
There is no JavaScript or other scripting language to learn or install.

The samples run against public practice sites so everything works immediately:
  Web : https://the-internet.herokuapp.com        API : https://restful-booker.herokuapp.com
  Mobile: the open-source WebdriverIO demo app (Section 12)
Later you point the modules at YOUR application (Section 6).


================================================================================================
 PART B  -  SETUP
================================================================================================

------------------------------------------------------------------------------------------------
2. WHAT MUST BE INSTALLED
------------------------------------------------------------------------------------------------
Needed for WEB, API and SECURITY(web/API) work:
  [A] JDK 17 or newer (Java Development Kit - not just the JRE). Recommended: Eclipse Temurin 17 or 21.
        https://adoptium.net
  [B] Apache Maven 3.9.0 or newer.   https://maven.apache.org/download.cgi
  [C] Git.                           https://git-scm.com
  [D] A browser: Google Chrome (default), or Firefox / Microsoft Edge.
      You do NOT install chromedriver/geckodriver: Selenium Manager downloads the right one itself.
  [E] An IDE (recommended): IntelliJ IDEA Community (free), Eclipse, or VS Code with the
      "Extension Pack for Java".
  [F] Internet access (Maven downloads libraries on the first build; the samples call public sites).

Only for MOBILE (and mobile security) tests:
  [G] Node.js 20 or newer            https://nodejs.org
  [H] Appium server:                 npm install -g appium
  [I] Appium drivers:                appium driver install uiautomator2      (Android)
                                     appium driver install xcuitest          (iOS, Mac only)
  [J] Android: Android Studio (SDK + emulator).   iOS: a Mac with Xcode.

HOW TO CHECK EACH INSTALL (open a NEW terminal / command prompt after installing):
    java -version          -> must print 17 or higher
    javac -version         -> must also print 17+ (proves it is a JDK, not just a JRE)
    mvn -version           -> must print Apache Maven 3.9.x and the same Java version
    git --version
    node -v                -> (mobile only)  v20 or higher
    appium --version       -> (mobile only)

IF "mvn -version" SAYS JAVA_HOME IS NOT SET / WRONG JAVA:
  Windows: Start menu -> "Edit the system environment variables" -> Environment Variables ->
           New system variable  JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17...   (your JDK folder)
           Edit Path -> add  %JAVA_HOME%\bin   and the Maven  bin  folder.  Open a new terminal.
  macOS/Linux (add to ~/.zshrc or ~/.bashrc):
           export JAVA_HOME=$(/usr/libexec/java_home -v 17)      # macOS
           export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # Linux (example path)
           export PATH="$JAVA_HOME/bin:$PATH"

Behind a company proxy? Maven needs proxy settings in ~/.m2/settings.xml (ask IT for host/port) -
otherwise the first build cannot download libraries.

------------------------------------------------------------------------------------------------
3. LIBRARIES (DOWNLOADED BY MAVEN) AND THE JAVA IMPORTS YOU WILL USE
------------------------------------------------------------------------------------------------
YOU DO NOT INSTALL LIBRARIES BY HAND. They are declared in the pom.xml files; Maven downloads them from
Maven Central on the first build into ~/.m2/repository (a few hundred MB, once). Versions are defined
ONCE, in the parent pom.xml <properties>.

  Library (groupId : artifactId)                    Version  Used by module     Purpose
  ------------------------------------------------  -------  -----------------  -----------------------------
  org.testng : testng                               7.12.0   core (all)         test runner
  com.aventstack : extentreports                    5.1.2    core (all)         HTML report
  org.apache.poi : poi-ooxml                        5.4.1    core (all)         read Excel (.xlsx)
  org.apache.logging.log4j : log4j-api/-core        2.25.2   core (all)         logging
  org.apache.logging.log4j : log4j-slf4j2-impl      2.25.2   core (all)         other libs' logs -> log4j
  org.seleniumhq.selenium : selenium-api            4.51.0   core (all)         driver abstraction
  org.seleniumhq.selenium : selenium-java           4.51.0   web                browser automation
  io.appium : java-client                           10.1.1   mobile             mobile automation
  io.rest-assured : rest-assured                    6.3.0    api                API testing
  io.rest-assured : json-schema-validator           6.3.0    api                JSON schema checks
  com.fasterxml.jackson.core : jackson-databind     2.19.2   api                Java object <-> JSON

  Maven plugins (also automatic): maven-compiler-plugin 3.14.0, maven-surefire-plugin 3.5.3 (runs the
  tests), maven-enforcer-plugin 3.5.0 (stops the build if Java/Maven is too old),
  versions-maven-plugin 2.18.0 (checks for updates).

  The security module has no extra libraries: it uses core + web + mobile + api.

To check for newer versions later:   mvn versions:display-dependency-updates
If a version cannot be found, or REST Assured 6.x gives compile errors: edit the matching
<...version> line in the PARENT pom.xml <properties> (for REST Assured use 5.5.6), then
mvn clean test-compile.

JAVA IMPORTS YOU WILL NEED (the IDE adds them for you: IntelliJ = Alt+Enter on red code, Eclipse =
Ctrl+Shift+O). Note the package of each class tells you the module it lives in.

  --- every test ---
  import org.testng.Assert;                       import org.testng.annotations.Test;
  import org.testng.annotations.DataProvider;     // only if you write your own data provider
  import java.util.Map;  import java.util.List;   // Excel rows arrive as Map<String,String>

  --- WEB test / page object ---
  import com.topautomation.web.base.BaseTest;               // web test classes extend this
  import com.topautomation.web.pages.BasePage;              // page objects extend this
  import com.topautomation.web.data.WebExcelDataProvider;  // dataProviderClass for web Excel data
  import org.openqa.selenium.By;  import org.openqa.selenium.WebDriver;  import org.openqa.selenium.WebElement;
  import org.openqa.selenium.support.ui.Select;            // <select> dropdowns

  --- API test / API object ---
  import com.topautomation.api.base.ApiBaseTest;
  import com.topautomation.api.ApiConfig;                  // request defaults (base URL, JSON, auth)
  import com.topautomation.api.data.ApiExcelDataProvider;
  import com.topautomation.api.models.Booking;             // your Java request/response classes
  import io.restassured.RestAssured;  import io.restassured.response.Response;
  import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
  import static org.hamcrest.Matchers.equalTo;

  --- MOBILE test / page object ---
  import com.topautomation.mobile.base.MobileBaseTest;
  import com.topautomation.mobile.pages.MobileBasePage;
  import com.topautomation.mobile.data.MobileExcelDataProvider;
  import io.appium.java_client.AppiumBy;                    // AppiumBy.accessibilityId("login-button")

  --- SECURITY test ---
  import com.topautomation.security.data.InjectionDataProvider;
  import com.topautomation.security.payloads.PayloadCategory;
  import com.topautomation.security.payloads.InjectionPayloads;
  import com.topautomation.security.checks.SecurityAssertions;      // web & mobile checks
  import com.topautomation.security.checks.ApiSecurityAssertions;   // API checks

  --- shared (core), usable everywhere ---
  import com.topautomation.core.config.ConfigManager;           // ConfigManager.get("base.url")
  import com.topautomation.core.reporting.ExtentTestManager;    // ExtentTestManager.info("step text")
  import com.topautomation.core.data.ExcelData;                 // used inside Excel data providers

------------------------------------------------------------------------------------------------
4. START HERE: GET THE PROJECT FROM GIT AND RUN IT (FIRST TIME)
------------------------------------------------------------------------------------------------
STEP 1  Install the tools from Section 2 ([A] to [F]) and verify with java -version / mvn -version.

STEP 2  Get the code.
        a) Brand-new repository (you have the TopAutomation.zip):
              unzip it, then inside the TopAutomation folder (the one containing pom.xml):
              git init
              git add .
              git commit -m "Initial TopAutomation framework"
              git remote add origin <your-repo-url>
              git push -u origin main
        b) Existing repository (a teammate already pushed it):
              git clone <your-repo-url>
              cd TopAutomation

STEP 3  Open it in your IDE.
        IntelliJ IDEA : File -> Open -> select the ROOT  pom.xml  -> "Open as Project" -> Trust Project.
                        Wait for "Resolving dependencies" to finish; you will see the 5 modules in the
                        Project view. Check File -> Project Structure -> SDK = JDK 17+, language level 17.
        Eclipse       : File -> Import -> Maven -> Existing Maven Projects -> pick the root folder.
        VS Code       : File -> Open Folder (root), install "Extension Pack for Java", let it import.

STEP 4  Download libraries and compile everything (terminal, in the ROOT folder - the one with the
        parent pom.xml and the five topautomation-* folders):
              mvn clean test-compile
        First run takes a few minutes. SUCCESS looks like a table of 6 modules, all SUCCESS, then
        BUILD SUCCESS. (Errors? Section 16.)

STEP 5  Run your first tests:
              mvn clean test -Pweb-sanity              (opens Chrome, under a minute)
              mvn clean test -Pweb-sanity -Dheadless=true     (no browser window)
              mvn clean test -Papi-sanity              (API - no browser at all)
        !! You MUST give a profile (-P...). Plain "mvn clean test" deliberately runs nothing: every
           module only runs when one of ITS profiles is chosen (Section 7).

STEP 6  Open the report of the module you ran:
              topautomation-web/target/reports/TopAutomation-web_<date_time>.html   (double-click)
              topautomation-api/target/reports/TopAutomation-api_<date_time>.html
        Logs:  topautomation-web/target/logs/web.log      topautomation-api/target/logs/api.log

STEP 7  You are set up. Next: Section 8 (web), 9 (Excel), 10 (API), 11 (security), 12 (mobile),
        and Section 6 to point everything at YOUR application.

What to commit to Git: everything except what .gitignore excludes (target/ folders, IDE files, the app
binaries in topautomation-mobile/apps/). Never commit real passwords - see "secrets" in Section 6.


================================================================================================
 PART C  -  HOW THE PROJECT IS ORGANISED
================================================================================================

------------------------------------------------------------------------------------------------
5. MODULE MAP: WHAT LIVES WHERE
------------------------------------------------------------------------------------------------
TopAutomation/                                  <- ROOT: run all Maven commands here
  pom.xml                                       PARENT pom: versions, module list, ALL profiles
  README.TXT                                    this file
  .mvn/  .gitignore  .github/workflows/ci.yml   Maven JVM settings, Git ignore rules, CI pipeline
  |
  +-- topautomation-core/            SHARED (no tests)
  |     src/main/java/com/topautomation/core/
  |        config/ConfigManager        reads settings (see Section 6)
  |        reporting/                  ExtentManager, ExtentTestManager, TestListener, RetryAnalyzer,
  |                                    AnnotationTransformer  (HTML report, screenshots, retry)
  |        driver/DriverManager        one driver per thread (shared by web + mobile)
  |        utils/                      ExcelReader, HtmlUtils, ScreenshotUtils
  |        data/ExcelData              helper behind every module's Excel data providers
  |     src/main/resources/            config/core.properties, log4j2.xml
  |
  +-- topautomation-web/             WEB TESTS (Selenium)
  |     src/main/java/.../web/         base/BaseTest  driver/WebDriverFactory  pages/ (PAGE OBJECTS)
  |                                    data/WebExcelDataProvider
  |     src/main/resources/config/     web.properties  web-qa.properties  web-staging.properties
  |     src/test/java/.../web/tests/   YOUR WEB TESTS  (LoginTest, DropdownTest)  + tests/templates/
  |     src/test/resources/suites/     web-all / web-sanity / web-functional / web-regression .xml
  |     src/test/resources/testdata/   web-testdata.xlsx  (sheets: Login, Registration, Dropdown)
  |     target/reports/  target/logs/  created when you run  (web.log, TopAutomation-web_<date>.html)
  |
  +-- topautomation-mobile/          MOBILE TESTS (Appium)
  |     apps/                          PUT YOUR .apk / .app / .ipa HERE (git-ignored)
  |     docs/MOBILE_SETUP.md           short setup notes
  |     src/main/java/.../mobile/      base/MobileBaseTest  driver/ (MobileDriverFactory,
  |                                    AppiumServerManager)  pages/  data/MobileExcelDataProvider
  |     src/main/resources/config/     mobile.properties  mobile-qa.properties
  |     src/test/java/.../mobile/tests/  YOUR MOBILE TESTS (MobileLoginTest) + tests/templates/
  |     src/test/resources/suites/     mobile-all / -sanity / -functional / -regression .xml
  |     src/test/resources/testdata/   mobile-testdata.xlsx  (sheet: MobileLogin)
  |     target/reports/  target/logs/  (mobile.log, appium.log, TopAutomation-mobile_<date>.html)
  |
  +-- topautomation-api/             API TESTS (REST Assured)
  |     src/main/java/.../api/         ApiConfig, AuthManager, ApiLogFilter, BookingFactory,
  |                                    base/ApiBaseTest, endpoints/ (API OBJECTS), models/ (JSON classes),
  |                                    data/ApiExcelDataProvider
  |     src/main/resources/config/     api.properties  api-qa.properties  api-staging.properties
  |     src/test/java/.../api/tests/   YOUR API TESTS (BookingApiTest)  + tests/templates/
  |     src/test/resources/            suites/ (api-*.xml)  testdata/api-testdata.xlsx  schemas/*.json
  |     target/reports/  target/logs/  (api.log, TopAutomation-api_<date>.html)
  |
  +-- topautomation-security/        SECURITY (injection attacks on web, mobile and API)
        src/main/java/.../security/    payloads/ (PayloadCategory, InjectionPayloads)
                                       checks/ (SecurityAssertions, ApiSecurityAssertions)
                                       data/InjectionDataProvider
        src/main/resources/            payloads/*.txt  (THE ATTACK LIBRARY, one payload per line)
                                       config/security.properties  security-qa.properties
        src/test/java/.../security/tests/   web/ (LoginInjectionTest)  api/ (ApiInjectionTest)
                                            mobile/ (MobileLoginInjectionTest)
        src/test/resources/suites/     security-web / -api / -mobile / -all .xml
        target/reports/  target/logs/  (security.log, TopAutomation-security_<date>.html)

DEPENDENCY RULES (keeps the modules separated - do not break them):
      core  <-  web      core  <-  mobile      core  <-  api
      core, web, mobile, api  <-  security       (security reuses their page objects/API objects)
  * web, mobile and api depend ONLY on core. They never import each other.
  * core depends on nobody.   * security may use all of them, and nobody depends on security.
  * Data, config, suites, logs and reports are never shared between web/mobile/api/security.

WHERE DO TEST FOLDERS GET SCANNED?  Each suite file scans exactly one package:
      web      -> com.topautomation.web.tests         mobile -> com.topautomation.mobile.tests
      api      -> com.topautomation.api.tests         security -> com.topautomation.security.tests.web | .api | .mobile
  (sub-packages such as .../tests/templates are NOT scanned - the templates are disabled copy-me files.)

------------------------------------------------------------------------------------------------
6. CONFIGURATION
------------------------------------------------------------------------------------------------
Each module ships its own config files in  <module>/src/main/resources/config/  (unique names, so they
never collide). A setting is found in this order (first hit wins):
   1. Command line      -Dsetting=value              e.g.  mvn test -Pweb-sanity -Dbrowser=firefox
   2. Environment var   SETTING_NAME                 (dots -> underscores, upper case):  API_AUTH_PASSWORD
   3. Config files, later overrides earlier:
         core.properties -> core-<env>.properties -> <module>.properties -> <module>-<env>.properties
   <env> defaults to "qa"; choose another with -Denv=staging (loads web-staging.properties, ...).

WHICH FILE HOLDS WHAT
  core/config/core.properties           env=qa, explicit.wait.seconds, retry.count, screenshot.on.failure,
                                        report.dir
  web/config/web.properties             browser, headless, base.url, grid.url, ci, implicit/page-load waits,
                                        web.testdata.file
  web/config/web-qa|staging.properties  per-environment overrides (base.url ...)
  api/config/api.properties             api.base.url, api.timeout.ms, api.auth.* (login), api.testdata.file
  api/config/api-qa|staging.properties  per-environment overrides
  mobile/config/mobile.properties       platform, appium.server.url, appium.server.autostart, android.*,
                                        ios.*, mobile.no.reset, mobile.testdata.file
  security/config/security.properties   payload.categories, payload.max.per.category,
                                        api.payload.max.per.category, security.time.threshold.ms
  security/config/security-qa.properties  qa overrides (e.g. cap API payloads at 5 per category)

COMMON COMMAND-LINE SWITCHES
  -Dbrowser=firefox|edge|chrome   -Dheadless=true   -Denv=staging   -Dretry.count=0
  -Dplatform=android|ios          -Dgrid.url=http://localhost:4444   -Dci=true
  -Dpayload.categories=SQLI,XSS   -Dpayload.max.per.category=5       -Dapi.payload.max.per.category=3

POINT THE FRAMEWORK AT YOUR OWN APPLICATION (checklist):
  1. Web:  topautomation-web/src/main/resources/config/web-qa.properties        -> base.url=https://qa.my-company.com
  2. API:  topautomation-api/src/main/resources/config/api-qa.properties        -> api.base.url=https://api.qa.my-company.com
           and the api.auth.* block in api.properties (Section 10).
  3. Mobile: topautomation-mobile/.../mobile.properties and put your app in topautomation-mobile/apps/ (Section 12).
  4. Create web-staging / api-staging ... the same way, run with -Denv=staging.
  5. Replace the sample page objects / API objects with yours; edit the Excel workbooks (Section 9).

SECRETS (passwords/tokens): never write real ones into files that go to Git. Use environment variables:
     Windows (cmd):   set API_AUTH_PASSWORD=mySecret
     macOS/Linux:     export API_AUTH_PASSWORD=mySecret
  or pass on the command line:  mvn test -Papi-sanity -Dapi.auth.password=mySecret

------------------------------------------------------------------------------------------------
7. RUNNING TESTS: MAVEN PROFILES
------------------------------------------------------------------------------------------------
All profiles live in the PARENT pom.xml. Syntax (run in the ROOT folder):   mvn clean test -P<profile>
A profile switches ON the tests of ONE module and selects ONE of that module's suite files.
Modules without a chosen profile are compiled but their tests are skipped.
Plain "mvn clean test" with no profile therefore runs NOTHING - this is on purpose.

  MODULE     PROFILE              RUNS (group)                           NEEDS
  ---------  -------------------  -------------------------------------  -------------------------------
  WEB        web-sanity           tiny smoke set (web-sanity)            browser
             web-functional       feature tests (web-functional)         browser
             web-regression       full regression (web-regression)       browser
             web-all              every web test (web)                   browser
  API        api-sanity           API smoke (api-sanity)                 nothing extra
             api-functional       API feature tests (api-functional)     nothing extra
             api-regression       API regression (api-regression)        nothing extra
             api-all              every API test (api)                   nothing extra
  MOBILE     mobile-sanity        mobile smoke (mobile-sanity)           Appium + device + app
             mobile-functional    mobile feature tests                   Appium + device + app
             mobile-regression    mobile regression                      Appium + device + app
             mobile-all           every mobile test (mobile)             Appium + device + app
  SECURITY   security-web         web form injection attacks             browser
             security-api         API injection attacks                  nothing extra
             security-mobile      mobile form injection attacks          Appium + device + app
             security-all         all three of the above                 browser + Appium + device
  CONVENIENCE full                web-regression + api-regression       browser

COMBINING PROFILES - profiles of DIFFERENT modules combine with a comma:
      mvn clean test -Pweb-sanity,api-sanity          (two modules, two reports)
  (two profiles of the SAME module conflict - the one declared last in the parent pom wins.)

EXAMPLES
  mvn clean test -Pweb-sanity -Dheadless=true
  mvn clean test -Pweb-regression -Dbrowser=firefox -Denv=staging
  mvn clean test -Papi-regression
  mvn clean test -Psecurity-api -Dpayload.categories=SQLI,XSS -Dapi.payload.max.per.category=3
  mvn clean test -Pmobile-functional -Dplatform=android
  mvn clean test -Pfull

FASTER: BUILD ONLY ONE MODULE (plus what it needs)
  mvn clean test -pl topautomation-api -am -Papi-sanity
  (-pl = project list, -am = also make the modules it depends on; their tests stay skipped.)

RUNNING ONE TEST CLASS OR A CUSTOM LIST (a suite file of your own - keeps reports and settings working)
  1. Create  topautomation-web/src/test/resources/suites/my.xml :
        <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
        <suite name="My run" verbose="1">
            <listeners>
                <listener class-name="com.topautomation.core.reporting.TestListener"/>
            </listeners>
            <test name="One class">
                <classes>
                    <class name="com.topautomation.web.tests.LoginTest"/>
                </classes>
            </test>
        </suite>
  2. Run:   mvn clean test -Pweb-all -Dweb.suite=my.xml
     (the property is <module>.suite: web.suite, api.suite, mobile.suite or security.suite)
  From the IDE you can also right-click a test class -> Run. That skips the HTML report listener and
  needs the working directory to be the MODULE folder (so the Excel path resolves); use Maven when you
  need the report.


================================================================================================
 PART D  -  MODULE GUIDES
================================================================================================

------------------------------------------------------------------------------------------------
8. WEB MODULE (topautomation-web)  -  SELENIUM
------------------------------------------------------------------------------------------------
HOW IT WORKS
  * A test class extends BaseTest. Before EVERY @Test a fresh browser opens; after it, it closes.
  * Tests never touch the browser directly: they call PAGE OBJECTS (Page Object Model).
  * @Test(groups = {...}) decides which profile runs the test (Section 14).

SAMPLES ALREADY IN THE MODULE
  src/test/java/.../web/tests/LoginTest.java       valid login/logout (sanity) + Excel-driven login rows
  src/test/java/.../web/tests/DropdownTest.java    Excel-driven dropdown test (worked example, Section 9)
  src/main/java/.../web/pages/                      LoginPage, SecureAreaPage, DropdownPage, GenericFormPage
  src/test/java/.../web/tests/templates/            WebTestTemplate, ExcelDrivenTestTemplate (disabled)
  Run them:  mvn clean test -Pweb-sanity    |    mvn clean test -Pweb-functional

A PAGE OBJECT  (web/pages/DropdownPage.java is a real example):
    public class DropdownPage extends BasePage {
        private final By dropdown = By.id("dropdown");            // 1. locators are private
        public DropdownPage(WebDriver driver) { super(driver); }
        public DropdownPage open() { open("/dropdown"); visible(dropdown); return this; }   // 2. actions public
        public DropdownPage choose(String text) { new Select(visible(dropdown)).selectByVisibleText(text); return this; }
        public String selectedOption() { return new Select(visible(dropdown)).getFirstSelectedOption().getText(); }
    }
  BasePage gives you wait-aware helpers: visible(by), click(by), type(by, text), textOf(by),
  isDisplayed(by). You never need Thread.sleep().

A TEST:
    public class DropdownTest extends BaseTest {
        @Test(groups = {"web", "web-functional", "web-regression"})
        public void pickOption() {
            DropdownPage page = new DropdownPage(driver()).open().choose("Option 1");
            Assert.assertEquals(page.selectedOption(), "Option 1");
        }
    }

HOW TO ADD YOUR OWN WEB TEST - STEP BY STEP
  1. Find locators: open your page in Chrome -> right-click the element -> Inspect. Prefer, in order:
        By.id("x")  >  By.name("x")  >  By.cssSelector("[data-test='x']")  >  By.xpath(...) (last resort).
  2. Create  topautomation-web/src/main/java/com/topautomation/web/pages/MyPage.java
     extending BasePage (copy LoginPage).
  3. Copy  .../web/tests/templates/WebTestTemplate.java  to  .../web/tests/MyPageTest.java
     (one folder UP, into the scanned "tests" package). Rename class AND file, delete "enabled = false",
     set groups, write the steps.
  4. Run it:  mvn clean test -Pweb-all        (or tag it "web-sanity" and run -Pweb-sanity)
  5. Open topautomation-web/target/reports/*.html. Failures include a screenshot.

TESTING ANY OTHER FORM QUICKLY: GenericFormPage needs no new page object:
    new GenericFormPage(driver()).openPath("/contact")
        .fill(By.name("email"), "a@b.com").fill(By.name("message"), "Hello")
        .submit(By.cssSelector("button[type=submit]"));

CROSS-BROWSER:  mvn clean test -Pweb-sanity -Dbrowser=firefox     (chrome | firefox | edge)
GRID / CLOUD:   mvn clean test -Pweb-sanity -Dgrid.url=http://localhost:4444

------------------------------------------------------------------------------------------------
9. EXCEL DATA PROVIDERS  (DATA-DRIVEN TESTING, ALL MODULES)
------------------------------------------------------------------------------------------------
IDEA: the same test runs once per row of an Excel sheet. Testers add rows - no Java changes needed.

EACH MODULE HAS ITS OWN WORKBOOK, ITS OWN PROVIDER CLASS AND ITS OWN CONFIG KEY:
  MODULE   WORKBOOK (in <module>/src/test/resources/testdata/)   PROVIDER CLASS (in <module>/src/main/java)
  -------  ------------------------------------------------------  ----------------------------------------
  web      web-testdata.xlsx    sheets: Login, Registration,       web/data/WebExcelDataProvider
                                Dropdown, ReadMe                    providers: loginData, registrationData,
                                                                    dropdownData
  api      api-testdata.xlsx    sheets: ApiBooking, ReadMe          api/data/ApiExcelDataProvider
                                                                    provider: apiBookingData
  mobile   mobile-testdata.xlsx sheets: MobileLogin, ReadMe         mobile/data/MobileExcelDataProvider
                                                                    provider: mobileLoginData
  security (no Excel - its data is the payload files, Section 11)  security/data/InjectionDataProvider
  Config keys: web.testdata.file, api.testdata.file, mobile.testdata.file (in each module's .properties).

SHEET LAYOUTS
   Login          TestCaseId | Description | Username | Password | ExpectedResult | ExpectedMessage | Run
   Dropdown       TestCaseId | Description | OptionToSelect | ExpectedSelected | Run
   Registration   template to fill in (all rows Run=N)
   ApiBooking     TestCaseId | Description | Firstname | Lastname | TotalPrice | DepositPaid | CheckIn |
                  CheckOut | Needs | ExpectedStatus | Run
   MobileLogin    TestCaseId | Description | Email | Password | ExpectedResult | Run

RULES FOR EVERY SHEET
   * Row 1 = column headers (these names are the keys you use in Java: data.get("Username")).
   * Every following row = one test run. Completely blank rows are ignored.
   * Column  Run  = N  skips the row (blank or Y runs it).  Keep it, it is very handy.
   * Keep a TestCaseId column - it appears in the report name, e.g.  loginDrivenByExcel [LOGIN_03].
   * All values arrive in Java as TEXT (String). Convert yourself:  Integer.parseInt(data.get("TotalPrice")).
   * Format cells as TEXT (Format Cells -> Text) so values like 007 or text starting with = are kept.
   * Close the Excel file before running tests (an open file can be locked on Windows).
   * The sheet NAME must match what the provider passes - including capital letters.
   * Empty cell = empty string "" (useful for testing empty fields).

HOW TO ADD A NEW EXCEL SHEET - WORKED EXAMPLE (this is how the web "Dropdown" sheet was made)
  STEP 1  Open topautomation-web/src/test/resources/testdata/web-testdata.xlsx.
          Add a new tab (sheet) named exactly:   Dropdown
  STEP 2  Type headers in row 1:  TestCaseId | Description | OptionToSelect | ExpectedSelected | Run
  STEP 3  Add data rows:          DD_01 | Select first option | Option 1 | Option 1 | Y
                                  DD_02 | Select second option | Option 2 | Option 2 | Y
          Save and close Excel.
  STEP 4  Register a data provider: open
          topautomation-web/src/main/java/com/topautomation/web/data/WebExcelDataProvider.java and add:
              @DataProvider(name = "dropdownData", parallel = true)
              public static Object[][] dropdownData() {
                  return ExcelData.rows(FILE, "Dropdown");     // FILE = web.testdata.file; "Dropdown" = tab name
              }
          name = "dropdownData" is what tests use.  parallel = true lets rows run at the same time
          (use false if rows depend on each other).
  STEP 5  Use it in a test (see web/tests/DropdownTest.java):
              @Test(groups = {"web", "web-functional", "web-regression"},
                    dataProvider = "dropdownData",             // <- provider name from step 4
                    dataProviderClass = WebExcelDataProvider.class)
              public void selectOptionFromExcel(Map<String, String> data) {      // one Excel row
                  DropdownPage page = new DropdownPage(driver()).open().choose(data.get("OptionToSelect"));
                  Assert.assertEquals(page.selectedOption(), data.get("ExpectedSelected"));
              }
  STEP 6  Run:  mvn clean test -Pweb-functional   -> the report shows  selectOptionFromExcel [DD_01]  and  [DD_02].
  FOR THE API OR MOBILE MODULE do the same inside THAT module: its workbook, its *ExcelDataProvider
  class (with its own FILE key), its tests.

USING ANOTHER EXCEL FILE: change the module's  *.testdata.file  setting, or call directly
    ExcelReader.read("path/to/file.xlsx", "SheetName")    (returns List<Map<String,String>>)
    (paths are relative to the MODULE folder, because Maven runs each module's tests from there)

------------------------------------------------------------------------------------------------
10. API MODULE (topautomation-api)  -  REST ASSURED
------------------------------------------------------------------------------------------------
HOW IT WORKS
  * An API test class extends ApiBaseTest. NO browser is started - requests go straight to the server.
  * API OBJECTS (api/endpoints/BookingApi.java) = one method per endpoint, returning a Response.
  * MODELS (api/models/Booking.java) = Java "record" classes that mirror the JSON body.
  * Settings (base URL, timeouts, login) come from api.properties / api-qa.properties.
  * Every request/response is logged to api.log and into the test's entry in the HTML report
    (passwords masked). Login is automatic: AuthManager logs in ONCE per run and re-uses the token.

SAMPLES ALREADY IN THE MODULE  (src/test/java/.../api/tests/)
  BookingApiTest: pingReturnsCreated, createBookingReturnsIdAndEchoesData, createBookingResponseMatchesSchema,
  bookingCrudLifecycle (create/update/delete with token), searchByFirstName, unknownBookingReturns404,
  updateWithoutTokenIsForbidden, badCredentialsReturnNoToken, responseTimeIsAcceptable,
  createBookingDrivenByExcel (sheet "ApiBooking").   templates/ApiTestTemplate.java = copy-me template.
  Run:  mvn clean test -Papi-sanity    |    mvn clean test -Papi-all

CONFIGURE AUTHENTICATION FOR YOUR API  (topautomation-api/src/main/resources/config/api.properties):
   api.auth.login.path=/auth                 URL path of your login endpoint
   api.auth.username=...   api.auth.password=...        (use env vars / -D for real secrets)
   api.auth.username.field=username          JSON field names your login expects
   api.auth.password.field=password
   api.auth.token.jsonpath=token             where the token is in the login response ("data.accessToken" works too)
   api.auth.header=Authorization             header the token is sent in
   api.auth.prefix=Bearer                    text put before the token (a space is added automatically)
  The sample restful-booker API uses  Cookie  and prefix  token=  - already set (prefixes ending in "="
  are used exactly as written, so the header becomes  Cookie: token=<value>).
  For a public API with no login, ignore api.auth.* and never call ApiConfig.authSpec().

HOW TO ADD YOUR OWN API TEST - STEP BY STEP  (example: an "orders" API)
  1. MODEL  topautomation-api/src/main/java/com/topautomation/api/models/Order.java
         @JsonIgnoreProperties(ignoreUnknown = true)
         public record Order(String id, String item, int quantity) {}
     (field names must equal the JSON names; import com.fasterxml.jackson.annotation.JsonIgnoreProperties)
  2. API OBJECT  api/endpoints/OrderApi.java   (copy BookingApi and edit)
         public class OrderApi {
             public Response create(Order o) { return RestAssured.given().spec(ApiConfig.authSpec()).body(o).post("/orders"); }
             public Response get(String id)  { return RestAssured.given().spec(ApiConfig.authSpec()).get("/orders/{id}", id); }
         }
     ApiConfig.baseSpec() = no login needed;  ApiConfig.authSpec() = sends the token.
  3. TEST  topautomation-api/src/test/java/com/topautomation/api/tests/OrderApiTest.java
     (copy tests/templates/ApiTestTemplate.java one folder up)
         public class OrderApiTest extends ApiBaseTest {
             private final OrderApi orderApi = new OrderApi();
             @Test(groups = {"api", "api-sanity", "api-regression"})
             public void createOrder() {
                 Response r = orderApi.create(new Order(null, "Book", 2));
                 Assert.assertEquals(r.statusCode(), 201);
                 Assert.assertEquals(r.as(Order.class).item(), "Book");
             }
         }
  4. RUN  mvn clean test -Papi-sanity      -> report: topautomation-api/target/reports/*.html

OTHER USEFUL ASSERTIONS
   One JSON field:     response.then().body("booking.firstname", equalTo("Jane"));
   Read a field:       int id = response.jsonPath().getInt("bookingid");
   A header:           Assert.assertEquals(response.header("Content-Type"), "application/json; charset=utf-8");
   Speed:              Assert.assertTrue(response.time() < 2000);
   JSON SCHEMA:        save the schema as topautomation-api/src/test/resources/schemas/order.json, then
                       response.then().body(matchesJsonSchemaInClasspath("schemas/order.json"));
   Query parameters:   RestAssured.given().spec(ApiConfig.baseSpec()).queryParam("page", 2).get("/orders")
   Excel-driven:       Section 9 - see createBookingDrivenByExcel and sheet "ApiBooking".

------------------------------------------------------------------------------------------------
11. SECURITY MODULE (topautomation-security)  -  INJECTION ATTACKS ON WEB, MOBILE AND API
------------------------------------------------------------------------------------------------
!! ONLY TEST SYSTEMS YOU OWN OR HAVE WRITTEN PERMISSION TO TEST. Never run these against production
   or against a system holding real customer data. The tests create/modify data on the target. !!

WHY A SEPARATE MODULE: the attack library, the checks and the attack tests are not mixed into the
functional modules. The security module reuses the web, mobile and API page/API objects, but has its own
config (security.properties), suites, log file and report - so an attack run is always its own, clearly
labelled run (topautomation-security/target/reports/TopAutomation-security_<date>.html).

THE LIBRARY: topautomation-security/src/main/resources/payloads/  (one payload per line; ## = comment)
   sqli.txt                SQL injection                    nosqli.txt         NoSQL (MongoDB-style)
   xss.txt                 cross-site scripting             html-injection.txt HTML injection
   command-injection.txt   OS command injection (harmless id/whoami/sleep)
   path-traversal.txt      ../../etc/passwd style           ldap.txt           LDAP injection
   ssti.txt                template injection ({{1337*1337}} -> 1787569 if evaluated)
   xxe.txt                 XML external entities            crlf.txt           header/CRLF injection
   malformed.txt           odd/boundary input; 256/1024/10000-character strings are generated in code
   mobile.txt              deep links, intents, WebView bridge, SQLite probes, format strings
   They are non-destructive DETECTION probes. Keep your own additions that way.

WHAT THE TESTS CHECK (a "finding" = suspicious; a finding fails the test):
   * a JavaScript alert opened (XSS) / payload reflected unescaped in an HTML response
   * database, stack-trace, LDAP, NoSQL error text in the response
   * contents of /etc/passwd or win.ini, or output of "id" (file/command injection)
   * template expression evaluated (1787569 appears)
   * delay payload (sleep 5) answered much slower than normal (time-based injection)
   * API only: HTTP 5xx, injected response headers/cookies, a login that returns a token for an attack string
   These are signals, not proof - check each failure by hand. Real scanners (OWASP ZAP, Burp) go deeper.

THE THREE READY-MADE ATTACK TESTS  (src/test/java/.../security/tests/)
   WEB     web/LoginInjectionTest                run:  mvn clean test -Psecurity-web
   API     api/ApiInjectionTest                  run:  mvn clean test -Psecurity-api
   MOBILE  mobile/MobileLoginInjectionTest       run:  mvn clean test -Psecurity-mobile
   Everything: mvn clean test -Psecurity-all   (needs browser AND Appium + device)
   Narrow a run:  -Dpayload.categories=SQLI,XSS   -Dpayload.max.per.category=5
                  -Dapi.payload.max.per.category=3   (API run only; security-qa.properties caps it at 5)

ATTACK YOUR OWN WEB FORM - create  .../security/tests/web/ContactFormInjectionTest.java :
    public class ContactFormInjectionTest extends BaseTest {          // BaseTest comes from the web module
        @Test(groups = {"security", "security-web"},
              dataProvider = "webInjection", dataProviderClass = InjectionDataProvider.class)
        public void contactFormResistsInjection(PayloadCategory category, String payload) {
            long start = System.nanoTime();
            new GenericFormPage(driver()).openPath("/contact")
                .fill(By.name("email"), payload).fill(By.name("message"), payload)
                .submit(By.cssSelector("button[type=submit]"));
            long ms = (System.nanoTime() - start) / 1_000_000;
            Assert.assertTrue(SecurityAssertions.analyse(driver(), payload, ms).isEmpty(),
                    category + " payload produced findings");
        }
    }
  (imports: InjectionDataProvider, PayloadCategory, SecurityAssertions, GenericFormPage, BaseTest, By,
   Assert, Test - see Section 3)

ATTACK YOUR OWN API ENDPOINT: open security/tests/api/ApiInjectionTest.java and add one line in the
static { } block (your API object must exist in the api module - Section 10):
    register(new Target("POST /orders [json: item]",                     // a name for the report
            p -> orderApi.create(new Order(null, p, 1)),                 // how to send ONE payload
            r -> false));                                                // when is it a compromise? (false = never)
  Examples for the last line:  r -> r.statusCode() == 200   (a nonsense id must never be found)
                               r -> r.asString().contains("\"token\"")   (login must never hand out a token)
  Query-parameter, path-parameter and header examples are already in that file.

ATTACK YOUR OWN MOBILE FORM: copy security/tests/mobile/MobileLoginInjectionTest and swap in your page
  object from the mobile module (Section 12). Group tags: {"security", "security-mobile"}.

ADD YOUR OWN PAYLOADS: add a line to a file in payloads/ . Done - every attack test uses it on the next run.
ADD A NEW CATEGORY: (1) create payloads/mycategory.txt  (2) add  MYCATEGORY("mycategory.txt")  to the enum
   security/payloads/PayloadCategory.java  (3) run with -Dpayload.categories=MYCATEGORY.
USE PAYLOADS IN YOUR OWN CODE:   List<String> list = InjectionPayloads.get(PayloadCategory.SQLI);
                                 String big = InjectionPayloads.longString(10_000);
SAFETY NOTE: payload text is HTML-escaped in the Extent report. TestNG's own report in
   target/surefire-reports does NOT escape - do not open that HTML in a browser after attack runs.
Security tests are never auto-retried (a finding is a finding).

------------------------------------------------------------------------------------------------
12. MOBILE MODULE (topautomation-mobile)  -  APPIUM
------------------------------------------------------------------------------------------------
HOW THE PIECES FIT
   Your Java test  --HTTP-->  APPIUM SERVER (Node.js program, port 4723)  -->  driver  -->  emulator /
   simulator / real phone  -->  YOUR APP
   The Java framework is only the CLIENT. A server must be running. The framework does not install it.

A. ONE-TIME INSTALL
   1. Node.js 20+   (https://nodejs.org)
   2. npm install -g appium
   3. appium driver install uiautomator2          (Android)
      appium driver install xcuitest              (iOS - Mac only)
      appium driver list --installed              (check)
   4. ANDROID: install Android Studio -> More Actions -> Virtual Device Manager -> create an emulator -> start it.
      Set environment variable ANDROID_HOME = your SDK folder (Android Studio: Settings -> Android SDK shows it)
      and add  %ANDROID_HOME%\platform-tools  (Windows)  /  $ANDROID_HOME/platform-tools  (Mac/Linux) to PATH.
      Check:   adb devices       -> your emulator must be listed as "device".
      REAL PHONE: Settings -> About -> tap Build number 7 times -> Developer options -> enable USB debugging.
   5. iOS (Mac only): install Xcode, open it once, accept the license; simulators are in
      Xcode -> Window -> Devices and Simulators. A REAL iPhone additionally needs Apple signing setup
      (team id, signing id, device UDID) - ask your iOS developer.

B. WHICH APP FILE DO I NEED?   (ask your mobile developers for a QA / test build)
   +--------------------+--------------------------------------------------------------------------------+
   | ANDROID            | an  .apk  file. (An .aab "app bundle" is a Play Store upload format: Appium    |
   |                    | cannot install it directly. Ask for an .apk, or convert with Google's          |
   |                    | "bundletool build-apks --mode=universal".)                                     |
   | iOS SIMULATOR      | a  .app  folder (or a .zip of it) built FOR THE SIMULATOR (Xcode build with    |
   |                    | the iphonesimulator SDK)                                                       |
   | iOS REAL DEVICE    | a  .ipa  file signed for your device                                           |
   | APP ALREADY ON THE | no file at all: set the package+activity (Android) or bundle id (iOS) instead  |
   | DEVICE             |                                                                                |
   +--------------------+--------------------------------------------------------------------------------+
   Also ask developers to put stable "content-desc" (Android) / "accessibilityIdentifier" (iOS) on every
   button and field. Those names are the best locators and work on both platforms.

C. WHERE TO PUT THE APP AND HOW TO CONFIGURE IT
   1. Copy the file into  topautomation-mobile/apps/   (e.g. topautomation-mobile/apps/myapp.apk).
      The folder is git-ignored, so binaries never get committed.
   2. Edit topautomation-mobile/src/main/resources/config/mobile.properties (or pass -D on the command line):
        # Android
        android.app=apps/myapp.apk                 # path relative to topautomation-mobile/
        android.device.name=emulator-5554          # name shown by "adb devices"
        android.platform.version=14                # optional (the Android version)
        # alternatively, for an already-installed app (leave android.app blank):
        android.app.package=com.mycompany.myapp
        android.app.activity=.MainActivity
        # iOS
        ios.app=apps/MyApp.app                     # simulator build   (or apps/MyApp.ipa for a real device)
        ios.device.name=iPhone 15
        ios.platform.version=17.5
        ios.bundle.id=com.mycompany.myapp          # only if the app is already installed
      Relative paths become absolute automatically. A web address (http/https) is passed through
      unchanged (used by cloud device farms).
   3. Choose the platform:  -Dplatform=android   or   -Dplatform=ios      (default android)

D. START THE APPIUM SERVER  (choose ONE)
   OPTION 1 - manually (recommended while learning): open a separate terminal and run
        appium
      and leave it running (http://127.0.0.1:4723 = the default appium.server.url).
   OPTION 2 - let the framework start it:  mvn clean test -Pmobile-functional -Dappium.server.autostart=true
      (starts a LOCAL server before the mobile tests, re-uses one already running, stops it at the end;
       log: topautomation-mobile/target/logs/appium.log)
   OPTION 3 - cloud device farm (BrowserStack, Sauce Labs, LambdaTest ...): set appium.server.url to the
      vendor URL and android.app / ios.app to the vendor's uploaded-app id/URL. Vendor-specific
      capabilities (user name/key ...) must be added in mobile/driver/MobileDriverFactory.java.

E. RUN
        mvn clean test -Pmobile-sanity -Dplatform=android
        mvn clean test -Pmobile-functional -Dplatform=ios -Dios.app=apps/MyApp.app
        mvn clean test -Psecurity-mobile            (injection attacks on the mobile login form)
   Mobile tests run ONLY with mobile-* / security-mobile profiles - never with web or api profiles.

F. TRY IT FIRST WITH A SAMPLE APP
   Download the open-source "WebdriverIO native demo app" .apk from
   https://github.com/webdriverio/native-demo-app/releases , save it as
   topautomation-mobile/apps/sample.apk (the default android.app value), start an emulator and run the
   commands above. The sample page object (mobile/pages/MobileLoginPage.java) uses that app's ids.

G. WRITE YOUR OWN MOBILE TEST
   1. Find locators with APPIUM INSPECTOR (https://github.com/appium/appium-inspector): connect it to your
      Appium server, start a session with your app, click an element, read its "accessibility id".
   2. Copy mobile/pages/MobileLoginPage.java -> MyScreenPage.java and replace the ids:
          private final By saveButton = AppiumBy.accessibilityId("save-button");
      (other locator types: AppiumBy.id("com.app:id/save"), AppiumBy.androidUIAutomator("..."),
       AppiumBy.iOSNsPredicateString("..."), By.xpath(...) as last resort)
   3. Copy .../mobile/tests/templates/MobileTestTemplate.java up one folder to .../mobile/tests/
      MyScreenTest.java, remove "enabled = false", write the steps. Groups: {"mobile", "mobile-functional"}.
   4. Excel-driven mobile tests: sheet "MobileLogin" in mobile-testdata.xlsx + MobileExcelDataProvider
      (see MobileLoginTest.loginDrivenByExcel).
   5. Run:  mvn clean test -Pmobile-functional

H. NOTES
   * A new app session starts for each test (clean but slower). -Dmobile.no.reset=true keeps app data.
   * Short notes also in topautomation-mobile/docs/MOBILE_SETUP.md


================================================================================================
 PART E  -  REFERENCE
================================================================================================

------------------------------------------------------------------------------------------------
13. REPORTS AND LOGS  (ONE SET PER MODULE - NEVER MIXED)
------------------------------------------------------------------------------------------------
  MODULE     HTML REPORT (Extent)                                           LOG FILE
  ---------  -------------------------------------------------------------  ---------------------------------
  web        topautomation-web/target/reports/TopAutomation-web_<date>.html            .../target/logs/web.log
  api        topautomation-api/target/reports/TopAutomation-api_<date>.html            .../target/logs/api.log
  mobile     topautomation-mobile/target/reports/TopAutomation-mobile_<date>.html      .../target/logs/mobile.log
                                                                                       .../target/logs/appium.log
  security   topautomation-security/target/reports/TopAutomation-security_<date>.html  .../target/logs/security.log
  Also per module: target/surefire-reports/ (TestNG's own XML/HTML).
  The HTML report shows: pass/fail, categories (= groups), steps, API calls, screenshots on failure, and the
  module/environment/browser at the top.
  "mvn clean" deletes every target/ folder - copy reports out first if you need to keep them.
  Add your own step text to the report from any test:   ExtentTestManager.info("Opened checkout page");

------------------------------------------------------------------------------------------------
14. GROUP NAMES  (CHEAT SHEET)
------------------------------------------------------------------------------------------------
Put the group names in @Test(groups = {...}). A test can have several. Group names are prefixed with the
module so they cannot be confused.

   MODULE    GROUP              RUN BY PROFILE            MEANING
   --------  -----------------  ------------------------  ---------------------------------------------
   web       web                web-all                   every browser test (always add it)
             web-sanity         web-sanity                tiny must-pass smoke tests
             web-functional     web-functional            feature tests
             web-regression     web-regression, full      everything that should pass before a release
   api       api                api-all                   every API test (always add it)
             api-sanity         api-sanity                API smoke
             api-functional     api-functional            API feature tests
             api-regression     api-regression, full      API release checks
   mobile    mobile             mobile-all                every Appium test (always add it)
             mobile-sanity      mobile-sanity             mobile smoke
             mobile-functional  mobile-functional         mobile feature tests
             mobile-regression  mobile-regression         mobile release checks
   security  security           (report category label)   add to every attack test
             security-web       security-web              web injection tests
             security-api       security-api              API injection tests
             security-mobile    security-mobile           mobile injection tests
   (security-all runs the three security-* suites.)

Example:  @Test(groups = {"web", "web-sanity", "web-regression"})  -> runs in web-all, web-sanity,
web-regression and full.

------------------------------------------------------------------------------------------------
15. CONTINUOUS INTEGRATION (GitHub Actions)
------------------------------------------------------------------------------------------------
  File: .github/workflows/ci.yml - on every push/pull request it runs two independent jobs:
     web-sanity :  mvn -B test -Pweb-sanity -Dheadless=true -Dci=true    (uploads the WEB report)
     api-sanity :  mvn -B test -Papi-sanity                              (uploads the API report)
  Add jobs the same way for regression or security (keep attack runs against test systems only).
  Linux servers / Docker: always add  -Dci=true -Dheadless=true  for web runs.
  Secrets: add them in GitHub -> Settings -> Secrets and pass them as environment variables in the
  workflow (e.g. API_AUTH_PASSWORD).
  Optional Maven Wrapper (so teammates do not need Maven installed):  mvn -N wrapper:wrapper
  then use  ./mvnw  (Mac/Linux) or  mvnw.cmd  (Windows) instead of  mvn.

------------------------------------------------------------------------------------------------
16. TROUBLESHOOTING
------------------------------------------------------------------------------------------------
 "Tests are skipped." / nothing ran    -> you did not give a profile. Use e.g.  -Pweb-sanity (Section 7).
 "The requested profile ... could not be activated" -> typo. Profiles are listed in Section 7.
 "mvn is not recognized" / "JAVA_HOME is not defined correctly"
      -> Section 2: install JDK + Maven, set JAVA_HOME and PATH, open a NEW terminal.
 "Rule 0: RequireJavaVersion failed" / "TopAutomation needs JDK 17 or newer"
      -> your java is too old. Install JDK 17+, check with  java -version  and  mvn -version.
 "release version 17 not supported"  -> Maven is using an older Java. Fix JAVA_HOME.
 "Could not resolve dependencies" / "Could not transfer artifact"
      -> no internet or a proxy/firewall. Configure the proxy in ~/.m2/settings.xml, or try again.
 "Could not find artifact ...:<version>" or compile errors in the api module (RestAssured)
      -> edit that version in the PARENT pom.xml <properties>. For REST Assured set
         <restassured.version>5.5.6</restassured.version>, then  mvn clean test-compile.
 "Could not resolve dependencies for project ...topautomation-security"  when building one module with -pl
      -> add -am (also make) so the modules it needs are built:  -pl topautomation-security -am
 Red underlined imports in the IDE -> right-click the ROOT pom.xml -> Maven -> Reload project
      (IntelliJ: the circular "Load Maven Changes" icon). Then File -> Invalidate Caches if it persists.
 "Missing config file on classpath: config/core.properties" -> you ran a class from an IDE with the wrong
      module classpath; run through Maven or set the module in the run configuration.
 "Cannot find suite file" / "No tests were executed" -> check that the suite file exists in
      <module>/src/test/resources/suites/ and that your test class is in the scanned "tests" package.
 Browser opens then closes at once / "session not created: This version of ChromeDriver only supports..."
      -> update Chrome; Selenium Manager fetches the matching driver automatically (needs internet).
 Linux/Docker: "DevToolsActivePort file doesn't exist"  -> add  -Dci=true -Dheadless=true
 "NoSuchElementException/TimeoutException" -> locator wrong or page slower than explicit.wait.seconds.
      Inspect the element again; raise the wait for slow pages (-Dexplicit.wait.seconds=20).
 Excel "Excel file not found" -> paths are relative to the MODULE folder; run through Maven, or (IDE) set the
      run configuration's working directory to the module folder, or fix *.testdata.file.
 Excel "Sheet 'X' not found" -> the tab name differs (capitals/spaces) from what the provider passes.
 Excel: no rows run -> every row has Run = N, or the sheet has only a header.
 Mobile: "Connection refused 127.0.0.1:4723" -> Appium is not running (start "appium" or use autostart).
 Mobile: "Could not find a connected Android device" -> start the emulator; check  adb devices.
 Mobile: "app ... does not exist" -> wrong path; it is relative to topautomation-mobile/ (apps/yourfile).
 Mobile: "...uiautomator2 driver not installed" ->  appium driver install uiautomator2
 API/security run is slow or hammers a demo server -> -Dapi.payload.max.per.category=3

------------------------------------------------------------------------------------------------
17. COMMAND CHEAT SHEET   (run in the ROOT folder)
------------------------------------------------------------------------------------------------
   mvn clean test-compile                           check everything compiles / download libraries
   mvn clean test -Pweb-sanity                      web smoke tests        (+ -Dheadless=true)
   mvn clean test -Pweb-functional | -Pweb-regression | -Pweb-all
   mvn clean test -Papi-sanity                      API smoke tests
   mvn clean test -Papi-functional | -Papi-regression | -Papi-all
   mvn clean test -Pmobile-sanity -Dplatform=android   mobile (Appium running)
   mvn clean test -Psecurity-web | -Psecurity-api | -Psecurity-mobile | -Psecurity-all
   mvn clean test -Pweb-sanity,api-sanity           two modules in one run
   mvn clean test -Pfull                            web regression + API regression
   mvn clean test -pl topautomation-api -am -Papi-sanity     build only the API module (+ core)
   mvn clean test -Pweb-all -Dweb.suite=my.xml      your own suite file
   mvn versions:display-dependency-updates          are there newer library versions?

Where to start learning the code (in this order):
   1. web/tests/LoginTest.java   2. web/pages/LoginPage.java   3. web/tests/DropdownTest.java + the
   "Dropdown" Excel sheet   4. api/tests/BookingApiTest.java + api/endpoints/BookingApi.java
   5. security/tests/web/LoginInjectionTest.java   6. each module's tests/templates/
================================================================================================
