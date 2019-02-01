@ProductIndexFeature @TestSuiteID=10541665
Feature: Product Indexing Api

  @ProcessFailedIds
  Scenario: Verify that failed Ids are processed when the api is run

    Given [search-service] failed Ids exist in the DB
    When [search-service] sends request for processing failed Ids
    Then [search-service] indexes the Ids present in DB
    And [search-service] removes the entries from DB

  @IndexByProductCode
  Scenario: Verify that a product can be reindexed using productCode

    Given [search-service] product is OOS in SOLR and isInStock in Xproduct
    When [search-service] sends request for indexing the product using 'productCode'
    Then [search-service] indexes the provided product

  @IndexByProductSku
  Scenario: Verify that a product can be reindexed using productSku

    Given [search-service] product is OOS in SOLR and isInStock in Xproduct
    When [search-service] sends request for indexing the product using 'sku'
    Then [search-service] indexes the provided product

  @ListServices
  Scenario: Verify that list of services configured for service index is proper

    Given [search-service] list of services are configured
    When [search-service] sends request for listing services for reindex
    Then [search-service] all services configured are listed

  @ReviewAndRatingIndex
  Scenario: Verify that review and rating indexing updates proper data in SOLR

    Given [search-service] product is having different rating and review in SOLR and concerned service
    When [search-service] sends request for indexing with review and rating
    Then [search-service] data is corrected in SOLR

  @CategoryReindex
  Scenario: Verify category reindex reindexing all products in the category

    Given [search-service] data is different in Solr and Xproduct for products in category
    When [search-service] sends request for category reindex
    Then [search-service] data is corrected for all products in the category


  @FullReindexWithXproduct
  Scenario: Verify full reindex with Xproduct

    Given [search-service] data is different in Solr and Xproduct for products in category
    When [search-service] sends request for full reindex with xproduct option
    Then [search-service] test data is reindexed

  @ProcessDeltaStoredEvents
  Scenario: Verify Stored Delta processing

    Given [search-service] events are present in IndexingListNew collection
    When [search-service] sends request for processing stored delta
    Then [search-service] products stored in table are reindexed

  @IndexStatus
  Scenario: Verify index status api

    Given [search-service] api exist to get indexing status
    When [search-service] sends request to get indexing status
    Then [search-service] indexing status is received
