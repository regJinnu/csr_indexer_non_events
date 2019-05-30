@OfflineEventHandlingFeature @TestSuiteID=11050177
Feature:Verifying offline item change event listeners

  @Regression @Positive
  Scenario: Verify offline item change event with pickup point code
    Given [search-service] update the SOLR doc for offline prod
    When [search-service] consumes offline item change event for that itemSku with pickup point code
    Then [search-service] SOLR doc is updated only for offline item associated to pickup point code

  @Regression @Negative
  Scenario: Verify offline item change event without pickup point code
    Given [search-service] update the SOLR doc for offline prod
    When [search-service] consumes offline item change event for that itemSku without pickup point code
    Then [search-service] all solr doc associated to the itemSku are updated