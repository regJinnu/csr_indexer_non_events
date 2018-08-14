@LogisticOptionsEvent

Feature: Verify indexing by logistic option events

  Scenario: Verify Logistic option change event
    Given [search-service] update merchant commission type and logistic option for test product
    When [search-service] consumes logistic option event for a merchant containing test product
    And [search-service] run api to reindex products in product atomic reindex queue
    Then [search-service] merchant commission type and logistic option for test product is updated

