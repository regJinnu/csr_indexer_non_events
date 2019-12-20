@BwaSearchEventsFeature @TestSuiteID=11050177
Feature: Bwa Search Events

  @SearchBwaEvent  @Regression
  Scenario: Verify the bwa search event and see that the search count is increasing in db
    Given [search-service] getting search count from db before event
    And [search-service] set all the values for publishing the bwa search event
    When [search-service] publish the bwa search event
    Then [search-service] check if the search count increased in db







