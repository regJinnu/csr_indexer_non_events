@FlashSaleFeature @TestSuiteID=11540257
Feature: Verify flash sale related campaign events

@FlashSalePublish @Regression @Positive
Scenario: Verify campaign publish event for flash sale
  Given [search-service] set all the values for publishing flash sale campaign
  When [search-service] trigger the flash sale publish event
  Then [search-service] check if flash sale publish event is consumed and SOLR is updated
  And [search-service] check that set config is not updated with campaign code

@FlashSaleLive @Regression @Positive
Scenario: Verify campaign live event for flash sale
  Given [search-service] set all the values for making flash sale campaign live
  When [search-service] publish the flash sale live event
  Then [search-service] check that set config is updated with campaign code
  And [search-service] check config are updated in config table

@FlashSaleRemove @Regression @Positive
Scenario: Verify campaign remove event for flash sale
  Given [search-service] set data to remove items from live flash sale
  When [search-service] publish the flash sale item remove event
  Then [search-service] check if flash sale remove event is consumed and SOLR is updated
  And [search-service] check that set config is updated with campaign code
  And [search-service] check config are updated in config table

@FlashSaleStop @Regression @Positive
Scenario: Verify campaign stop event for flash sale
  Given [search-service] set data to stop the flash sale
  When [search-service] publish the flash sale stop event
  Then [search-service] check flash sale info is removed from set config and config table

@FlashSaleEnd @Regression @Positive
Scenario: Verify Campaign end events with exclusive flag
  Given [search-service] set data to end the flash sale
  When [search-service] publish the flash sale end event
  Then [search-service] check dynamic fields are removed from SOLR
  And [search-service] check flash sale info is removed from set config and config table
