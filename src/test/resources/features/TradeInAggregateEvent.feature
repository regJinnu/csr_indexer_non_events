@TradeInAggregateEvent @TestSuiteID=11050177
Feature: Trade In Aggregate event feature

  @TradeInAggregateEventProcessing @Regression
  Scenario: Verify trade in aggregate event processing
    Given [search-service] prepare request for processing trade in aggregate event
    When [search-service] send request for processing trade in aggregate event
    Then [search-service] check if trade in aggregate event is processed and solr is updated

  @TradeInAggregateInEligibleEventProcessing @Regression
  Scenario: Verify tradeIn aggregate inEligible event processing
    Given [search-service] prepare request for processing tradeIn aggregate inEligible event
    When [search-service] send request for processing tradeIn aggregate inEligible event
    Then [search-service] check if tradeIn aggregate inEligible event is processed and solr is updated