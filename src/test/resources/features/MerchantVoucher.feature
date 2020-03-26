@MerchantVoucherEventProcessingFeature @TestSuiteID=11540257
Feature: Automation of merchant voucher events

  @Regression @Positive @MVOnlineProcessing
  Scenario: Merchant Voucher count should be updated only for online items on receiving the event
    Given [search-service] fetch params required to send merchant voucher event
    When [search-service] send merchant voucher sku mapping count event through kafka
    Then [search-service] verify that merchant voucher count is updated in solr for online item in 'direct' processing
    And [search-service] verify that merchant voucher count is not updated in solr for offline item

  @Regression @Positive @MVOfflineProcessing
  Scenario: Merchant Voucher count should be updated only for online items on processing stored delta event
    Given [search-service] fetch params required to send merchant voucher event
    And [search-service] force.stop flag is set to 'true'
    When [search-service] send merchant voucher sku mapping count event through kafka
    And [search-service] event 'merchantVoucherChangeListener' is stored in delta table
    Then [search-service] sends request for processing stored delta
    And [search-service] verify that merchant voucher count is updated in solr for online item in 'delta' processing
    And [search-service] verify that merchant voucher count is not updated in solr for offline item