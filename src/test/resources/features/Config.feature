@ConfigFeature @Regression
Feature:Config controller related API's

  @FindByName
  Scenario: User wants to search for Config by giving name
    Given [search-service] prepare find config by name request using properties using properties data
    When  [search-service] send find config by name request
    Then [search-service] find config name by request response success should be 'true'

  @NegativeCase @FindByNameWhichIsNotPresent
  Scenario: User wants to search for Config by giving name which is not present
    Given [search-service] prepare find config by name which is not present in the config list
    When  [search-service] send find config by name which is not present in the config list
    Then [search-service] check the response for finding config with name which is not present

  @FindById
  Scenario: User wants to search for config by giving ID
    Given [search-service] prepare find config by id request using properties using properties data
    When [search-service] send find config by id request
    Then [search-service] find config id by request response success should be 'true'

  @NegativeCase @FindByWrongId
  Scenario: User wants to search for config by giving wrong ID
    Given [search-service] prepare find config by id request by providing wrong ID
    When [search-service] send find config by id request with wrong id
    Then [search-service] check the response for finding config with wrong id


  @FindByWord
  Scenario: User wants to search for config by giving word
    Given [search-service] prepare find config by word request using properties using properties data
    When [search-service] send find config by word request
    Then [search-service] find config word by request response success should be 'true'

  @NegativeCase @FindByNotExistingWord
  Scenario: User wants to search for config by giving word which doesnot exists
    Given [search-service] prepare find config by word request which is not present in the list
    When [search-service] send find request config by non existing word in the config list
    Then [search-service] check the response for finding config with wrong word

  @Update
  Scenario: User wants to update the existing config
    Given [search-service] prepare update existing config request using properties using properties data
    When [search-service] send update config request
    Then [search-service] set update config request response success should be 'true'

  @NegativeCase  @UpdateConfigWithEmptyBody
  Scenario: User wants to update the existing config with empty body
    Given [search-service] prepare update existing config request with empty body
    When [search-service] send update config request with empty body
    Then [search-service] check out the response of update config with empty body

  @Delete
  Scenario: User wants to delete the existing config
    Given [search-service] prepare delete existing config request using properties using properties data
    When [search-service] send delete config request
    Then [search-service] set delete config request response success should be 'true'

  @Delete @NegativeCase
  Scenario: User enters wrong id to delete the existing config
    Given [search-service] prepare delete existing config request by providing wrong id
    When [search-service] send delete config request with wrong id
    Then [search-service] check the response for deleting config with wrong id

  @Save
  Scenario: User wants to save the config
    Given [search-service] prepare save config request using properties using properties data
    When  [search-service] send save config request
    Then  [search-service] set save config request response success should be 'true'

  @Save @NegativeCase
  Scenario: User wants to save config with the empty body
    Given [search-service] prepare save config request with empty body
    When [search-service] send save config request with empty body
    Then [search-service] check response for saving config with empty body

  @ConfigList
  Scenario: User wants to the existing configs
    Given [search-service] prepare get config list request using properties using properties data
    When [search-service] send get config list request
    Then [search-service] get config list by request response success should be 'true'








