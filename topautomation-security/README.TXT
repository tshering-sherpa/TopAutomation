SECURITY TESTS (injection attacks)  -  module topautomation-security
=======================================

Payload library + attack tests for web forms, mobile forms and APIs. Reuses the web/mobile/api modules.

CONTENTS
  src/main/resources/payloads/*.txt   THE ATTACK LIBRARY (one payload per line)
  src/main/java/.../security/payloads/   PayloadCategory, InjectionPayloads
  src/main/java/.../security/checks/     SecurityAssertions (web/mobile), ApiSecurityAssertions
  src/main/java/.../security/data/       InjectionDataProvider
  src/main/resources/config/             security.properties, security-qa.properties
  src/test/java/.../security/tests/      web/  api/  mobile/   (YOUR ATTACK TESTS)
  src/test/resources/suites/             security-web / -api / -mobile / -all .xml

MAVEN PROFILES (run from the ROOT folder, one level up):  -Psecurity-web, -Psecurity-api, -Psecurity-mobile, -Psecurity-all
EXAMPLE
  mvn clean test -Psecurity-api -Dpayload.max.per.category=5

REPORT + LOG
  topautomation-security/target/reports/  and  target/logs/security.log

FULL GUIDE: see README.TXT in the root folder (module guides are Part D, sections 8-12).
