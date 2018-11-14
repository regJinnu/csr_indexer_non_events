@InventoryEventsTestFeature @TestSuiteID=10257932
Feature: Verify indexing by Inventory OOS and Non OOS events

  @OOSEvent
  Scenario: Verify inventory oos event when force stop is false
    Given [search-service] verify product is in stock in SOLR
    And [search-service] force.stop flag is set to 'false'
    When [search-service] consumes oos event for the product
    Then [search-service] product becomes oos in SOLR

  @NonOOSEvent
  Scenario: Verify inventory non-oos event when force stop is false
    Given [search-service] verify product is out of stock in SOLR
    And [search-service] force.stop flag is set to 'false'
    When [search-service] consumes non oos event for the product
    Then [search-service] product becomes in stock in SOLR

  @OOSEventWithoutWhitelist
  Scenario: Verify inventory oos event are processed when force stop is true but event not added in whitelist config
    Given [search-service] verify product is in stock in SOLR
    And [search-service] force.stop flag is set to 'true'
    And [search-service] inventory 'oos' event is not configured as whitelist
    When [search-service] consumes oos event for the product
    Then [search-service] product does not becomes oos in SOLR
    And [search-service] events are stored in indexing_list_new collection and processed when job is run after turning off the flag

  @NonOOSEventWithoutWhitelist
  Scenario: Verify inventory non-oos event are processed when force stop is true but event not added in whitelist config
    Given [search-service] verify product is out of stock in SOLR
    And [search-service] force.stop flag is set to 'true'
    And [search-service] inventory 'non oos' event is not configured as whitelist
    When [search-service] consumes non oos event for the product
    Then [search-service] product does not becomes non oos in SOLR
    And [search-service] events are stored in indexing_list_new collection and processed when job is run after turning off the flag
    And [search-service] product becomes in stock in SOLR

  @WhitelistOOSEvent
  Scenario: Verify inventory oos event are processed when force stop is true and event is added in whitelist config
    Given [search-service] verify product is in stock in SOLR
    And [search-service] force.stop flag is set to 'true'
    And [search-service] inventory 'oos' event is configured as whitelist
    When [search-service] consumes oos event for the product
    Then [search-service] product becomes oos in SOLR

  @WhitelistNonOOSEvent
  Scenario: Verify inventory non-oos event are processed when force stop is true and event is added in whitelist config
    Given [search-service] verify product is out of stock in SOLR
    And [search-service] force.stop flag is set to 'true'
    And [search-service] inventory 'non oos' event is configured as whitelist
    When [search-service] consumes non oos event for the product
    Then [search-service] product becomes in stock in SOLR