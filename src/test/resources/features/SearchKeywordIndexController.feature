@SuggestionIndexFeature @TestSuiteID=11540257
Feature: Suggestion Indexing Api

  @Regression
  Scenario: Verify suggestion delta reindex api
    Given [search-service] entries with isPushedToSolr as 0 exists in searchKeyword collection
    When [search-service] sends request for suggestion collection delta reindex
    Then [search-service] indexes the Ids present in DB to SOLR suggestion collection