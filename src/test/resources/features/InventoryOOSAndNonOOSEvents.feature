@InventoryEventsTestFeature
Feature: Verify indexing by Inventory OOS and Non OOS events

  @OOSEvent
  Scenario: Verify inventory oos event
    Given [search-service] verify product is in stock in SOLR
    When [search-service] consumes oos event for the product
    Then [search-service] product becomes oos in SOLR

  @NonOOSEvent
  Scenario: Verify inventory non-oos event
    Given [search-service] verify product is out of stock in SOLR
    When [search-service] consumes non oos event for the product
    Then [search-service] product becomes in stock in SOLR