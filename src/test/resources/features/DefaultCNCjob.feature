@DefaultCncJobFeature @TestSuiteID=11540257
Feature: Automation of Default CNC job

  @DefaultCncJobUpdate @Regression
  Scenario: Verify CNC default job when product is updated
    Given add event for an offline item such there is default prod change
    When the default cnc job has ran and collections are committed
    Then verify that the default cnc product is updated

  @DefaultCncJobWhenProdIsDeleted @Regression
  Scenario: Verify CNC default job when product is deleted
    Given add event for an offline item such there is default prod is deleted
    When the default cnc job has ran and collections are committed
    Then verify that product is deleted from respective collections as well

  @ForceStopSolrCncUpdatesSwitch @Regression
  Scenario: Verify that cnc updates are not updated in solr when the force.stop.solr.cnc.updates is true
    Given switch the force stop solr cnc updates config to true
    When cnc related event has come to search
    Then verify that event is not processed and stored in mongo
    And after triggering delta job event is updated to solr
    Then verify that product is deleted from cnc collection as well


