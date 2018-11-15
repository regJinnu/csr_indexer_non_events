@PristineEventsAutomationFeature @TestSuiteID=10257932
Feature: Automation of pristine related events

  @PristineEvents @Regression
  Scenario Outline: Verify pristine events for pristine categories
    Given [search-service] set all the values for publishing the pristine event for '<pristine>' category
    When [search-service] publish the pristine event for '<pristine>' by providing '<category>' '<PristineAttributesName>' '<PristineAttributesValue>' values for each category
    Then [search-service] verify if the '<PristineAttributesName>' is updated for that particular ID with '<PristineAttributesValue>' for '<pristine>' category in SOLR

    Examples:
    | pristine  | category        |   PristineAttributesName   | PristineAttributesValue |
    | handphone | HANDPHONE       | HANDPHONE_OPERATING_SYSTEM | ANDROID                 |
    | camera    | KAMERA          |  CAMERA_MODEL              |  x5                     |
    | computer  | Komputer&Laptop | COMPUTER_BRAND             |  hp                     |
