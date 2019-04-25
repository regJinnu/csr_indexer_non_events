@LogisticOptionsEvent @TestSuiteID=11050177

Feature: Verify indexing by logistic option events

  @LogisticOptionChangeEvent
  Scenario: Verify Logistic option change event is ignored when logisticOption present in event is not configured in eligible logisticOption
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'option' event with data not present in config
    Then [search-service] merchant commission type,location and logistic option for test product is not updated

  @LogisticOptionChangeEvent1
  Scenario: Verify Logistic option change event when merchant count is less than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'option' event for a merchant containing test product when merchant count is 'less' than 10
    Then [search-service] merchant commission type and logistic option for test product is updated

  @LogisticOptionChangeEvent
  Scenario: Verify Logistic option change event when merchant count is greater than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'option' event for a merchant containing test product when merchant count is 'more' than 10
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated

  @LogisticProductChangeEvent
  Scenario: Verify Logistic product change event is ignored when logisticOption present in event is not configured in eligible logisticOption
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'product' event with data not present in config
    Then [search-service] merchant commission type,location and logistic option for test product is not updated

  @LogisticProductChangeEvent2
  Scenario: Verify Logistic product change event when merchant count is less than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'product' event for a merchant containing test product when merchant count is 'less' than 10
    Then [search-service] merchant commission type and logistic option for test product is updated

  @LogisticProductChangeEvent3
  Scenario: Verify Logistic product change event when merchant count is greater than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'product' event for a merchant containing test product when merchant count is 'more' than 10
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated

  @LogisticOriginChangeEvent
  Scenario: Verify Logistic origin change event is ignored when logistic code present in event is not configured as eligible
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'origin' event with data not present in config
    Then [search-service] merchant commission type,location and logistic option for test product is not updated

  @LogisticOriginChangeEvent
  Scenario: Verify Logistic origin change event when merchant count is less than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'origin' event for a merchant containing test product when merchant count is 'less' than 10
    Then [search-service] merchant commission type and logistic option for test product is updated

  @LogisticOriginChangeEvent
  Scenario: Verify Logistic origin change event when merchant count is greater than 10
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic 'origin' event for a merchant containing test product when merchant count is 'more' than 10
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated