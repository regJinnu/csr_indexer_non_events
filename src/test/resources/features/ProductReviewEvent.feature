@ProductReviewEventFeature @TestSuiteID=11540257
Feature: Product review event processing

  @ProductReviewEventProcessing @Regression
  Scenario: Verify product review event should process only on normal and o2o collection
    Given [search-service] prepare request for processing product review event
    And [search-service] set review and rating of test product with random value
    When [search-service] send request for processing product review event
    Then [search-service] check if product review event is processed and solr is updated
    And [search-service] id get stored in delta table for processing in cnc collection

  @DeltaProcessingExcludingProductReview @Regression
  Scenario: Verify delta processing excluding product review ids
    Given [search-service] update config to exclude product review id from processing
    And [search-service] documents are present in IndexingListNew collection
    When [search-service] sends request for processing stored delta
    Then [search-service] product review id should be present in delta collection

  @ProductReviewEventProcessingOnCNCCollection @Regression
  Scenario: Verify delta processing update product review event on cnc collection
    Given [search-service] product review event on cnc collection is present in delta table
    When [search-service] sends request for processing stored delta
    Then [search-service] product review event should be processed and cncCollection must be updated