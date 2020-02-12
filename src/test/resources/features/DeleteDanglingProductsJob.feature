@DeleteDanglingProducts @TestSuiteID=11540257
Feature: Automation of Delete dangling products job

  @DeleteDanglingProductsFromAllCollections @Regression
  Scenario: Verify that dangling products is deleted from all collection
    Given [search-service] add test data into related collections
    When [search-service] delete dangling products job has run
    Then [search-service] verify that dangling docs have removed from all the collections
