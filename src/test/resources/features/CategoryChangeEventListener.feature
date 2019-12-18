@CategoryEventChangeFeature @TestSuiteID=11460962
Feature:Verifying item and product change event listeners

  @CategoryEventChange @Positive @Regression
  Scenario Outline: Verify category change reindex for ItemSKU
    Given [search-service] change the categoryName of the sku in Normal and '<other>' collection in case of categoryChange
    When  [search-service] consumes category change event for that itemSku present in Normal and '<other>' collection
    Then  [search-service] category information is properly updated for Sku in Normal and '<other>'collection for categoryChange
    Examples:
      |other|
      | O2O |
#      | CNC |
