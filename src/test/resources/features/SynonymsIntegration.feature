@SynonymIntegrationAPIFeature @TestSuiteID=10352299
Feature: Synonym Integration API's

  @Regression @UpdateAllSynonymsToSolr
  Scenario: User wants to integrate update synonyms to SOLR

    Given [search-service] prepare update synonyms to solr using properties using properties data
    When  [search-service] send update synonyms to solr request
    Then [search-service] create update synonym to solr request response success should be 'true'