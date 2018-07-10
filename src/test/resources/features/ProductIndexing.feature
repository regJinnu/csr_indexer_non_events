@FailedProductIndexFeature
Feature: Product Indexing Api

  @ProcessFailedIds
  Scenario: Verify that failed Ids are processed when the api is run

    Given [search-service] failed Ids exist in the DB
    When [search-service] sends request for processing failed Ids
    Then [search-service] indexes the Ids present in DB
    And [search-service] removes the entries from DB

  @TestIndex
  Scenario: Verify that a product can be reindexed using productCode

    Given [search-service] product is OOS in SOLR and isInStock in Xproduct
    When [search-service] sends request for indexing the product using 'productCode'
    Then [search-service] indexes the provided product

  @TestIndex
  Scenario: Verify that a product can be reindexed using productSku

    Given [search-service] product is OOS in SOLR and isInStock in Xproduct
    When [search-service] sends request for indexing the product using 'sku'
    Then [search-service] indexes the provided product
