@ContextualSearchFeature @TestSuiteID=11050177
Feature: contextual search related api's

  @Regression @AddFlight @Flight
  Scenario: User want to add flight mapping
    Given [search-service] prepare request to add a flight
    When [search-service] send add flight mapping request
    Then [search-service] add flight request response success should be 'true'

  @Regression @AddFlightWithoutMandatoryFields @Negative
  Scenario: User adding flight without giving mandatory fields
    Given [search-service] prepare request to add flight without giving mandatory fields
    When [search-service] send add flight request without mandatory
    Then [search-service] add flight without mandatory request response should be true

  @Regression @GetAllFlights @Flight
  Scenario: User want to search all flights
    Given [search-service] produce request to get all flights
    When [search-service] send get all flights request
    Then [search-service] get all flights request response success should be 'true'

  @Regression @DeleteFlight @Flight
  Scenario: User want to delete flight mapping
    Given [search-service] prepare request delete flight mapping
    When [search-service] send delete flight mapping request
    Then [search-service] delete flight mapping request response success should be 'true'

  @Regression @DeleteFlightWithWrongId @Negative
  Scenario: User want to delete flight with wrong id
    Given [search-service] prepare request to delete flight with wrong id
    When [search-service] send delete flight with wrong id request
    Then [search-service] delete flight with wrong id request response should be true

  @Regression @AddPlaceholderRules @Placeholder
  Scenario: User want to add placeholder rules
    Given [search-service] prepare request to add placeholder rules
    When [search-service] send add placeholder rules request
    Then [search-service] add placeholder rules request response success should be 'true'

  @Regression @AddPlaceholderWithoutMandatoryFields @Negative
  Scenario: User adding placeholder without giving mandatory fields
    Given [search-service] prepare request to add placeholder without giving mandatory fields
    When [search-service] send add placeholder request without mandatory
    Then [search-service] add placeholder without mandatory request response should be true

  @Regression @GetAllPlaceholder @Placeholder
  Scenario: User want to get all placeholder
    Given [search-service] produce request to get all placeholder
    When [search-service] send get all placeholder request
    Then [search-service] get all placeholder request response success should be 'true'

  @Regression @UpdatePlaceholder @Placeholder
  Scenario: User want to update placeholder rule
    Given [search-service] prepare request to update placeholder rule
    When [search-service] send update placeholder rule request
    Then [search-service] update placeholder rule request response success should be 'true'

  @Regression @UpdatePlaceholderWithNonExistingId @Negative
  Scenario: User want to update placeholder with non existing id
    Given [search-service] prepare request to update placeholder with non existing id
    When [search-service] send update placeholder with non existing id request
    Then [search-service] update placeholder with non existing id request response success should be true

  @Regression @DeletePlaceholder @Placeholder
  Scenario: User want to delete placeholder
    Given [search-service] prepare request to delete placeholder
    When [search-service] send delete placeholder request
    Then [search-service] delete placeholder request response success should be 'true'

  @Regression @DeletePlaceholderWithWrongId @Negative
  Scenario: User want to delete placeholder with wrong id
    Given [search-service] prepare request to delete placeholder with wrong id
    When [search-service] send delete placeholder with wrong id request
    Then [search-service] delete placeholder with wrong id request response should be true

  @Regression @AddSearchRule @Search
  Scenario: user want to add search rule
    Given [search-service] prepare request to add search rule
    When [search-service] send add search rule request
    Then [search-service] add search rule request response should be 'true'

  @Regression @GetAllSearch @Search
  Scenario: User want to get all search rule
    Given [search-service] prepare request to get all search rule
    When [search-service] send get all search rule request
    Then [search-service] get all search rule request response should be 'true'

  @Regression @ReRankSearchRule @Search
  Scenario: user want to rerank search rule
    Given [search-service] prepare request to rerank search rule
    When [search-service] send rerank search rule request
    Then [search-service] rerank search rule request response should be 'true'

  @Regression @UpdateSearchRule @Search
  Scenario: user want to update search rule
    Given [search-service] prepare request to update search rule
    When [search-service] send update search rule request
    Then [search-service] update search rule request response should be 'true'

  @Regression @DeleteSearchRule @Search
  Scenario: User want to delete search rule
    Given [search-service] prepare delete search rule request
    When [search-service] send delete search rule request
    Then [search-service] delete search rule request response success should be 'true'

  @Regression @DeleteSearchRuleWithWrongId @Negative
  Scenario: User want to delete Search rule with wrong id
    Given [search-service] prepare request to delete search rule with wrong id
    When [search-service] send delete search rule with wrong id request
    Then [search-service] delete search rule with wrong id request response should be true

  @Regression @AddTrainMapping @Train
  Scenario: User want to add train mapping
    Given [search-service] prepare add train mapping request
    When [search-service] send add train mapping request
    Then [search-service] add train mapping request response should be 'true'

  @Regression @GetAllTrain @Train
  Scenario: User want to get all train mapping
    Given [search-service] prepare get all train mapping request
    When [search-service] send get all train mapping request
    Then [search-service] get all train mapping request response should be 'true'

  @Regression @DeleteTrainMapping @Train
  Scenario: User want to delete train mapping
    Given [search-service] prepare request to delete train mapping
    When [search-service] send delete train mapping request
    Then [search-service] delete train mapping request response should be 'true'

  @Regression @DeleteTrainMappingWithWrongId @Negative
  Scenario: User want to delete train mapping with wrong id
    Given [search-service] prepare request to delete train mapping with wrong id
    When [search-service] send delete train mapping with wrong id request
    Then [search-service] delete train mapping with wrong id request response should be true