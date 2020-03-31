@OxfordEventProcessingFeature @TestSuiteID=11540257
Feature: Automation of oxford events

  @Regression @Positive @OxfordOnlineProcessing
  Scenario: Official store fields should be updated only for online items on receiving the event
    Given [search-service] fetch params required to send oxford update merchant event
    When [search-service] send oxford update merchant event via kafka
    Then [search-service] verify that official store fields are updated in solr for online item in 'direct' processing
    And [search-service] verify that official store fields are not updated in solr for offline item

  @Regression @Positive @OxfordOfflineProcessing
  Scenario: Official store fields should be updated only for online items on receiving the event
    Given [search-service] fetch params required to send oxford update merchant event
    And [search-service] force.stop flag is set to 'true'
    When [search-service] send oxford update merchant event via kafka
    And [search-service] event 'oxfordFlagChangeListener' is stored in delta table
    Then [search-service] sends request for processing stored delta
    Then [search-service] verify that official store fields are updated in solr for online item in 'delta' processing
    And [search-service] verify that official store fields are not updated in solr for offline item

  @Regression @Positive @OxfordSkuChangeOnlineProcessing
  Scenario: Brand and Store contract to be updated only for the online item on receiving update product event
    Given [search-service] fetch params required to send oxford update product event
    When [search-service] send oxford update product event via kafka
    Then [search-service] verify brand and store fields are updated in solr for online item
    And [search-service] verify brand and store fields are not updated in solr for offline item

  @Regression @Positive @OxfordSkuChangeOfflineProcessing
  Scenario: Brand and Store contract to be updated only for the online item on receiving update product event
    Given [search-service] fetch params required to send oxford update product event
    And [search-service] force.stop flag is set to 'true'
    When [search-service] send oxford update product event via kafka
    And [search-service] event 'oxfordSkuChangeEventListener' is stored in delta table
    Then [search-service] sends request for processing stored delta
    Then [search-service] verify brand and store fields are updated in solr for online item
    And [search-service] verify brand and store fields are not updated in solr for offline item