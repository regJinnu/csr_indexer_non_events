@FacebookFeed @TestSuiteID=9510901
Feature: Verification of Facebook Feed

  Scenario: Verify population of feed ids in redis by populate job
    Given [search-service] exists api for storing all ids in redis
    When [search-service] runs facebook all-ids api
    Then [search-service] all documents are removed from product feed map collection
    And [search-service] data is populated in redis

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