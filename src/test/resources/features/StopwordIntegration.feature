@StopwordIntegrationFeature @TestSuiteID=10257932
Feature: Stopword integration API's

  @Regression  @DeleteStopwordIntegration
  Scenario: User wants to integarte deleted stopwords to SOLR
    Given [search-service] prepare delete stopword integration using properties using properties data
    When  [search-service] send delete stopword integration request
    Then [search-service] delete stopword integration request response success should be 'true'

  @Regression @UpdateStopwordToSolr
  Scenario: User wants to integarte updated stopwords to SOLR
    Given [search-service] prepare update stopword integration using properties using properties data
    When  [search-service] send update stopword integration request
    Then [search-service] update stopword integration request response success should be 'true'
