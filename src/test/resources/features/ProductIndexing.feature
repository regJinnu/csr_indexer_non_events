Feature: Product Indexing Api

  Scenario: Verify that failed Ids are processed when the api is run

    Given [search-service] failed Ids exist in the DB
    When [search-service] sends request for processing failed Ids
    Then [search-service] indexes the Ids present in DB
    And [search-service] removes the entries from DB

