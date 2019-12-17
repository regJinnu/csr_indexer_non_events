@BuyboxFeature @TestSuiteID=11050177
Feature: Automation of buybox events

  @Regression @Positive
  Scenario: Buybox score must be updated for the respective itemsku after receiving the buybox event
    Given [search-service] fetch params required to send buybox event
    When [search-service] send buybox related event through kafka
    Then [search-service] verify that buybox score is updated in solr

    @Regression @Negative
    Scenario: Buybox updates will not happen in cnc collection
    Given [search-service] fetch params required to send buybox event for cnc prod
    When [search-service] send buybox related event through kafka for cnc prod
    Then [search-service] verify that buybox score is not updated in solr



