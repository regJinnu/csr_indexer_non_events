@LogisticOptionsEvent @TestSuiteID=10352299

Feature: Verify indexing by logistic option events

  @LogisticOptionChangeEvent
  Scenario: Verify Logistic option change event
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic option event for a merchant containing test product
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated

 @LogisticOriginChangeEvent
  Scenario: Verify storing entries by HighToLow

    Given [search-service] remove all entries from Product Reindex Atomic Queue and Product Atomic Reindex Data Candidate
    When [search-service] receives ORIGIN CHANGE event
    And [search-service] run api to convert High to Low
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated