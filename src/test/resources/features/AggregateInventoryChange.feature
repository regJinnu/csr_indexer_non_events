@AggregateInventoryChangeFeature @TestSuiteID=11050177
Feature: Verify Aggregate Inventory Change Event

  @AggregateInventoryChangeNonCNCEvent @Regression
  Scenario: Verify aggregate inventory change event processing
    Given [search-service] prepare request for processing aggregate inventory change event
    And [search-service] set location info of test product with random values
    When [search-service] send request for processing aggregate inventory change event
    Then [search-service] aggregate inventory change event is processed and solr is updated

  @AggregateInventoryChangeCNCEvent @Regression
  Scenario: Verify aggregate inventory change event processing for cnc product and default cnc product updation in normal collection
    Given [search-service] prepare request for processing aggregate inventory change event for cnc
    And [search-service] set location info of test product with random values in cnc collection
    When [search-service] send request for processing aggregate inventory change event for cnc
    Then [search-service] aggregate inventory change event is processed and solr cnc collection is updated
    And the default cnc job has ran and collections are committed
    Then default product in normal collection should be updated with aggregate inventory change data