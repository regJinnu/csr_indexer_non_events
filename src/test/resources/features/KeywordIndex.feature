@KeywordIndexFeature @TestSuiteID=9566460
Feature: Keyword index controller related api's

  @Regression @CleanUp
  Scenario: User wants to delete search keywords having click count below a specified limit
    Given [search-service] prepare request to delete search keywords having click count below a specified limit
    When  [search-service] send request to delete search keywords having click count below a specified limit
    Then [search-service] request to delete search keywords having click count below a specified limit response success should be 'true'

  @Regression @GetDebuginfo
  Scenario: User wants to get debug info for keyword to category Mapping
    Given [search-service] prepare request to get debug info for keyword to category Mapping
    When  [search-service] send request to get debug info for keyword to category Mapping
    Then [search-service] request to get debug info for keyword to category Mapping response success should be 'true'

  @Regression @RunDeltaIndex
  Scenario: User wants to run delta indexing of keywords
    Given [search-service] prepare request to run delta indexing of keywords
    When  [search-service] send request to run delta indexing of keywords
    Then [search-service] request to run delta indexing of keywords response success should be 'true'




