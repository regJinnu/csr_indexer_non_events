@BusinessPartnerStoreClosedEvent
Feature: Verify indexing by business partner store closed events

  Scenario: Verify store closed events with delay shipping off
    Given [search-service] verify store closed start and end timestamp fields in SOLR for the product
    When [search-service] consumes store closed event with delay shipping as 'false'
    Then [search-service] store closed information is updated in SOLR
    And [search-service] delay shipping is set as '0'

  Scenario: Verify store closed events with delay shipping on
    Given [search-service] verify store closed start and end timestamp fields in SOLR for the product
    When [search-service] consumes store closed event with delay shipping as 'true'
    Then [search-service] store closed information is updated in SOLR
    And [search-service] delay shipping is set as '1'