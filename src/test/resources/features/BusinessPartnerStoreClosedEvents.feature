@BusinessPartnerStoreClosedEvent @TestSuiteID=11050177
Feature: Verify indexing by business partner store closed events

  @StoreClosedWithDelayShippingFalse
  Scenario: Verify store closed events with delay shipping off
    Given [search-service] verify store closed start and end timestamp fields in SOLR for the product
    When [search-service] consumes store closed event with delay shipping as 'false'
    When [search-service] runs the scheduled events job
    Then [search-service] store closed information is updated in SOLR
    And [search-service] delay shipping is set as '0'
    And [search-service] storeClose field is set to true
    And [search-service] documents are created in mongo collection
    And [search-service] solr documents are updated on running scheduled events job

  @StoreClosedWithDelayShippingTrue
  Scenario: Verify store closed events with delay shipping on
    Given [search-service] verify store closed start and end timestamp fields in SOLR for the product
    When [search-service] consumes store closed event with delay shipping as 'true'
    Then [search-service] store closed information is updated in SOLR
    And [search-service] delay shipping is set as '1'
    And [search-service] storeClose field is not set for delayShipping true
    And [search-service] documents are not created in mongo collection

  @StoreClosedDelayShippingWithForceStopTrue
  Scenario: Verify store closed events with delay shipping on
    Given [search-service] verify store closed start and end timestamp fields in SOLR for the product
    And [search-service] force.stop flag is set to 'true'
    And [search-service] business partner is set as whitelist
    When [search-service] consumes store closed event with delay shipping as 'true'
    Then [search-service] store closed information is updated in SOLR
    And [search-service] delay shipping is set as '1'
    And [search-service] storeClose field is not set for delayShipping true
    And [search-service] documents are not created in mongo collection

 @BPProfileUpdateFields
  Scenario: Verify Business Partner Profile update field event
    Given [search-service] cnc is set as true in products for merchant
    And [search-service] force.stop flag is set to 'false'
    When [search-service] consumes com.gdn.x.businesspartner.profile.update.fields event
    Then [search-service] cnc true is removed for all products under that merchant