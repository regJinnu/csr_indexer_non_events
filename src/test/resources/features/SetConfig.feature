@SetConfigFeature @TestSuiteID=10352299
Feature: Set Config related API's

  @Regression @FetchSaveConfig
  Scenario:  User wants to fetch saved config
    Given [search-service] prepare request to fetch the saved config using properties using properties data
    When  [search-service] send request to fetch saved config
    Then [search-service] fetch saved config request response success should be 'true'

  @Regression @UpdateFieldCache
  Scenario:  User wants to update field cache
    Given [search-service] prepare request to update the field cache using properties using properties data
    When  [search-service] send request to update field cache
    Then [search-service] update field cache request response success should be 'true'

  @Regression @UpdateNonExistingField
  Scenario:  User wants to update field cache for non existing field
    Given [search-service] prepare request to update the non existing field cache using properties using properties data
    When  [search-service] send request to update non existing field cache
    Then [search-service] update non existing field cache request response success should be false

