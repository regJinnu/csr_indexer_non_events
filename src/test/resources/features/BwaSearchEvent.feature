@BwaSearchEventsFeature @TestSuiteID=11540257
Feature: Bwa Search Events

  @SearchBwaEvent  @Regression @Positive
  Scenario: Verify the bwa search event and see that the search count is increasing in db
    Given [search-service] getting search count from db before event
    And [search-service] set all the values for publishing the bwa search event
    And [search-service] prepare payload for publishing the bwa search event
    When [search-service] publish the bwa search event
    Then [search-service] check if the search count increased in db