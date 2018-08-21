@XproductEventHandlingFeature @TestSuiteID=9429123
Feature:Verifying item and product change event listeners

  @ItemChangeEvent
  Scenario: Verify item change event reindexes the data for the itemSku
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event for that itemSku
    Then [search-service] price information is properly updated for the Sku

  @ItemChangeDeleteEvent
  Scenario: Verify item change event when isArchived is set to true
    Given [search-service] test product is added in SOLR
    When [search-service] consumes item change event with isArchived is set to true
    Then [search-service] deletes only the item sku of test product from SOLR
    And [search-service] Db entry is created for the Sku in deleted product collection

  @ProductChangeEvent
  Scenario: Verify product change event reindexes the data for the sku
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes product change event for that sku
    Then [search-service] price information is properly updated for the Sku

  @ProductChangeDeleteEvent
  Scenario:  Verify item change event when markForDelete is set to true
    Given [search-service] test product is added in SOLR
    When [search-service] consumes item change event with markForDelete set to true
    Then [search-service] deletes the test product from SOLR
    Then [search-service] Db entry is created for the productCode in deleted product collection