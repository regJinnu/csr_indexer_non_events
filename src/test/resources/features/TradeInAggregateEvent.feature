@TradeInAggregateEventFeature @TestSuiteID=11050177
Feature: Trade In Aggregate event feature

  @TradeInAggregateEventProcessing @Regression @Positive
  Scenario Outline: Verify trade in aggregate event processing
    Given [search-service] prepare request for processing trade in aggregate <active> event
    And [search-service] set trade in value as <preEventTradeInValue> of test product based on <eligibility>
    When [search-service] send request for processing trade in aggregate event
    Then [search-service] check if trade in aggregate event is processed and solr is updated with <active> as value
    Examples:
      | active | preEventTradeInValue | eligibility       |
      | true   | false                | tradeInEligible   |
      | false  | true                 | tradeInInEligible |