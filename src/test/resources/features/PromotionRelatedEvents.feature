@PromotionRelatedEventsFeature @TestSuiteID=10257932

Feature:Promotion related events

  @ProductAdjustmentChangeEvent
  Scenario: Verify the product adjustment change events
    Given [search-service] set all the values for publishing the product adjustment change events
    When [search-service] publish the product adjustment change events
    Then [search-service] check if the product adjustment change event is consumed and check in solr

  @PromoBundlingActivatedEvent
  Scenario: Verify the PromoBundling Activated event
    Given [search-service] set all the values for publishing promoBundling Activated event
    When [search-service] publish the promo Bundling Activated event
    Then [search-service] check if the promo Bundling Activated event is consumed and check in solr

  @PromoBundlingDeactivatedEvent
  Scenario: Verify the PromoBundling Deactivated event
    Given [search-service] set all the values for publishing promoBundling Deactivated event
    When [search-service] publish the promo Bundling Deactivated event
    Then [search-service] check if the promo Bundling Deactivated event is consumed and check in solr
