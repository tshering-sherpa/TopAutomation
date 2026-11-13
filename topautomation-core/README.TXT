SHARED CORE (no tests)  -  module topautomation-core
===========================

Shared plumbing used by every other module. Change it rarely.

CONTENTS
  config/ConfigManager      layered settings (-D > env var > <module>.properties files)
  reporting/               ExtentManager, TestListener (HTML report + screenshots), RetryAnalyzer
  driver/DriverManager     one driver per thread
  utils/ + data/           ExcelReader, ExcelData, HtmlUtils, ScreenshotUtils
  resources/               config/core.properties, log4j2.xml (log file name = <module>.log)

REPORT + LOG
  (none - core has no tests)

FULL GUIDE: see README.TXT in the root folder (module guides are Part D, sections 8-12).
