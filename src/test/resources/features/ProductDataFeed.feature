@PDFFeature @TestSuiteID=10541665
Feature: Verify product data feed

  @Regression
  Scenario: Verify product data feed is being generated when api is run
    Given [search-service] prepare request to run product data feed
    When  [search-service] send request to run pdf feed
    Then  [search-service] file is written to ftp server
    And [search-service] product count matches with solr query
    And [search-service] products mentioned as exclusion are not written in the feed
    And [search-service] only default products are written in the file
    And [search-service] product details matches with data in solr