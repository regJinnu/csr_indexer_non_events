@SynonymIntegrationAPIFeature @TestSuiteID=9474896
Feature: Synonym Integration API's

  @Regression @DeleteIntegrationSynonymsSOLR
  Scenario: User wants to integrate delete synonyms to SOLR
    Given [search-service] prepare delete synonyms using properties using properties data
    When  [search-service] send delete synonym request
    Then [search-service] create delete synonym request response success should be 'true'

  @Regression @UpdateAllSynonymsToSolr
  Scenario: User wants to integrate update synonyms to SOLR
    Given [search-service] prepare update synonyms to solr using properties using properties data
    When  [search-service] send update synonyms to solr request
    Then [search-service] create update synonym to solr request response success should be 'true'