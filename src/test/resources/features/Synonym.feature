@SynonymFeature @TestSuiteID=11540257
Feature: Synonyms related api's

  @CreateSynonyms @Regression
  Scenario: User wants to create synonym
    Given [search-service] prepare create synonym using properties using properties data
    When  [search-service] send create synonym request
    Then [search-service] create synonym request response success should be 'true'

  @Regression @FindSynonymnByKey
  Scenario: User wants to find synonym by key
    Given [search-service] prepare find synonym by key using properties using properties data
    When  [search-service] send find synonym by key request
    Then [search-service] find synonym by key request response success should be 'true'

  @Regression @FindSynonymnByWrongKey
  Scenario: User wants to find synonym by key
    Given [search-service] prepare find synonym by wrong key using properties using properties data
    When  [search-service] send find synonym by wrong  key request
    Then [search-service] find synonym by wrong key request response success should be false

  @Regression @FindSynonymnByID
  Scenario: User wants to find synonym by ID
    Given [search-service] prepare find synonym by ID using properties using properties data
    When  [search-service] send find synonym by ID request
    Then [search-service] find synonym by ID request response success should be 'true'

  @Regression @FindSynonymnByWrongID
  Scenario: User wants to find synonym by wrong ID
    Given [search-service] prepare find synonym by wrong ID using properties using properties data
    When  [search-service] send find synonym by wrong ID request
    Then [search-service] find synonym by wrong ID request response success should be 'false'

  @Regression @FindSynonymnByWord
  Scenario: User wants to find Synonym by word
    Given [search-service] prepare find synonym using properties using properties data
    When  [search-service] send find synonym  request
    Then [search-service] find synonym request response success should be 'true'

  @Regression @FindSynonymnByWrongWord
  Scenario: User wants to find Synonym by wrong word
    Given [search-service] prepare find synonym by wrong word using properties using properties data
    When  [search-service] send find synonym by wrong word request
    Then [search-service] find synonym by wrong word request response success should be 'true'


  @Regression @SynonymnList
  Scenario: User wants to list Synonyms
    Given [search-service] prepare list synonym using properties using properties data
    When  [search-service] send list synonym  request
    Then [search-service] find synonym list request response success should be 'true'

  @Regression @DeleteSynonym
  Scenario: User wants to delete Synonyms
    Given [search-service] prepare delete synonym request using properties using properties data
    When  [search-service] send delete synonym  request
    Then [search-service] find synonym delete request response success should be 'true'

