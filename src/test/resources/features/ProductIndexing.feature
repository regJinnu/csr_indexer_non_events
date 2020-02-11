@ProductIndexFeature @TestSuiteID=11050177
Feature: Product Indexing Api

  @IndexByProductCode @Regression
  Scenario: Verify that a product can be reindexed using productCode

    Given [search-service] isInStock value is different in SOLR and Xproduct
    When [search-service] sends request for indexing the product using 'productCode'
    Then [search-service] indexes the provided product

  @IndexByProductSku @Regression
  Scenario: Verify that a product can be reindexed using productSku

    Given [search-service] isInStock value is different in SOLR and Xproduct
    When [search-service] sends request for indexing the product using 'sku'
    Then [search-service] indexes the provided product

  @ListServices @Regression
  Scenario: Verify that list of services configured for service index is proper

    Given [search-service] list of services are configured
    When [search-service] sends request for listing services for reindex
    Then [search-service] all services configured are listed

  @CategoryReindex @Regression
  Scenario: Verify category reindex reindexing all products in the category

    Given [search-service] data is different in Solr and Xproduct for products in category
    When [search-service] sends request for category reindex
    Then [search-service] data is corrected for all products in the category

  @FullReindexWithXproduct @Regression
  Scenario: Verify full reindex with Xproduct

    Given [search-service] data is different in Solr and Xproduct for products in category
    When [search-service] sends request for full reindex with xproduct option
    Then [search-service] test data is reindexed

  @ProcessDeltaStoredEvents @Regression
  Scenario: Verify Stored Delta processing

    Given [search-service] events are present in IndexingListNew collection
    When [search-service] sends request for processing stored delta
    Then [search-service] products stored in table are reindexed

  @IndexStatus @Regression
  Scenario: Verify index status api

    Given [search-service] api exist to get indexing status
    When [search-service] sends request to get indexing status
    Then [search-service] indexing status is received
