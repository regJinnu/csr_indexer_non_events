@FacebookFeed @TestSuiteID=10257932
Feature: Verification of Facebook Feed

  @PopulateFacebookIdsInRedis
  Scenario: Verify population of feed ids in redis by populate job
    Given [search-service] exists api for storing all ids in redis
    When [search-service] runs facebook all-ids api
    Then [search-service] all documents are removed from product feed map collection
    And [search-service] data is populated in redis

  @FacebookFullFeed
  Scenario: Verify facebook full feed
    Given [search-service] exists api to run facebook full feed
    When [search-service] runs api to generate full feed
    Then [search-service] new files are created in specified location
    And [search-service] count of records written is equal to ids stored in redis
    And [search-service] written sync records are proper
    And [search-service] written unsync records are proper
    And [search-service] all items for same product are not written only default is written
    And [search-service] records not satisfying the solr query are not written in file
    And [search-service] records satisfying exclusion are not written in file
    And [search-service] facebook feed last updated time is updated in config_list
    And [search-service] all fields are populated in the feed

  @FacebookDeltaFeed
  Scenario: Verify facebook delta feed
    Given [search-service] exists api to run facebook delta feed
    And [search-service] data is updated in SOLR
    When [search-service] runs api to generate facebook delta feed
    Then [search-service] new delta directory with files are created in specified location
    And [search-service] products which are updated are written in files
    And [search-service] facebook feed last updated time is updated in config_list