@Synonym
  Feature: Synonym related API's

    @CreateSynonyms
    Scenario: User wants to create synonym
      Given [search-service] prepare create synonym using properties using properties data
      When  [search-service] send create synonym request
      Then [search-service] create synonym request response success should be 'true'

    @FindSynonymnByKey @id
    Scenario: User wants to find synonym by key
      Given [search-service] prepare find synonym by key using properties using properties data
      When  [search-service] send find synonym by key request
      Then [search-service] find synonym by key request response success should be 'true'

    @FindSynonymnByID @id
    Scenario: User wants to find synonym by ID
      Given [search-service] prepare find synonym by ID using properties using properties data
      When  [search-service] send find synonym by ID request
      Then [search-service] find synonym by ID request response success should be 'true'

