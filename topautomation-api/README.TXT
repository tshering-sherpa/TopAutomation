API TESTS (REST Assured)  -  module topautomation-api
=============================

HTTP API tests with API objects, JSON models and schema checks.

CONTENTS
  src/main/java/.../api/endpoints/  API objects (BookingApi)   models/  JSON classes   base/ApiBaseTest
  src/main/java/.../api/            ApiConfig, AuthManager, ApiLogFilter, BookingFactory   data/ApiExcelDataProvider
  src/main/resources/config/        api.properties (base URL + auth), api-qa.properties, api-staging.properties
  src/test/java/.../api/tests/      YOUR TESTS (+ tests/templates/)
  src/test/resources/               suites/api-*.xml   testdata/api-testdata.xlsx   schemas/*.json

MAVEN PROFILES (run from the ROOT folder, one level up):  -Papi-sanity, -Papi-functional, -Papi-regression, -Papi-all
EXAMPLE
  mvn clean test -Papi-sanity

REPORT + LOG
  topautomation-api/target/reports/  and  target/logs/api.log

FULL GUIDE: see README.TXT in the root folder (module guides are Part D, sections 8-12).
