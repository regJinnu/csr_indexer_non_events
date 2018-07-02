@BoostedKeywordFeature @Regression
Feature: Boosted keyword related api's

  @FindBoostedKeyword @id1
  Scenario:  User wants to find boosted keyword
    Given [search-service] prepare request to find boosted keyword using properties using properties data
    When  [search-service] send request to find boosted keyword
    Then [search-service] find boosted keyword request response success should be 'true'

  @FindBoostedKeyword @NegativeCase
  Scenario:  User wants to find boosted keyword
    Given [search-service] prepare request to find boosted keyword which is not present using properties using properties data
    When  [search-service] send request to find boosted keyword which is not present
    Then [search-service] find boosted keyword which is not present request response

  @FindBoostedKeywordByID
  Scenario:  User wants to find boosted keyword by ID
    Given [search-service] prepare request to find boosted keyword by ID using properties using properties data
    When  [search-service] send request to find boosted keyword by ID
    Then [search-service] find boosted keyword by ID request response success should be 'true'

  @FindBoostedKeywordByWrongID @NegativeCase
  Scenario:  User wants to find boosted keyword by ID
    Given [search-service] prepare request to find boosted keyword by wrong ID using properties using properties data
    When  [search-service] send request to find boosted keyword by wrong ID
    Then [search-service] find boosted keyword by wrong ID request response

  @ListBoostedKeyword
  Scenario: User wants to list boosted keywords
    Given [search-service] prepare request to list boosted keyword using properties using properties data
    When  [search-service] send request to list boosted keyword
    Then [search-service] list boosted keyword request response success should be 'true'

  @UpdateBoostedKeyword @id1
  Scenario: User wants to update the boosted keyword
    Given [search-service] prepare request to update boosted keyword using properties using properties data
    When  [search-service] send request to update boosted keyword
    Then [search-service] update boosted keyword request response success should be 'true'

  @UpdateBoostedKeywordWithWrongID
  Scenario: User wants to update the boosted keyword with wrong ID
    Given [search-service] prepare request to update boosted keyword with wrong id using properties using properties data
    When  [search-service] send request to update boosted keyword with wrong id
    Then [search-service] check update boosted keyword request with wrong id response


  @GetAllListOfBoostedKeywords
  Scenario:User wants to get the list of boosted keywords
    Given [search-service] prepare request to list all the boosted keywords using properties using properties data
    When  [search-service] send request to list all the boosted keywords
    Then [search-service] list all the boosted keywords request response success should be 'true'

  @UploadBoostedKeyword
  Scenario: User wants to upload the boosted keyword
    Given [search-service] prepare request to upload boosted keyword using properties using properties data
    When  [search-service] send request to upload boosted keyword
    Then [search-service] upload the boosted keywords request response success should be 'true'

  @UploadWrongFormatFile
  Scenario: User wants to upload the file of wrong format
    Given [search-service] prepare request to upload the file of wrong format using properties using properties data
    When  [search-service] send request to upload the file of wrong format
    Then [search-service] upload the file of wrong format request response success should be 'false'

  @DeleteBoostedKeyword @id1
  Scenario:User wants to delete the boosted keyword
    Given [search-service] prepare request to delete boosted keyword using properties using properties data
    When  [search-service] send request to delete boosted keyword
    Then [search-service] delete the boosted keyword request response success should be 'true'

  @ValidateBoostedKeyword @id1
  Scenario:User wants to validate the boosted keyword
    Given [search-service] prepare request to validate boosted keyword using properties using properties data
    When  [search-service] send request to validate boosted keyword
    Then [search-service] validate the boosted keyword request response success should be 'true'

  @Multidelete
  Scenario: User wants to perform multi delete in boosted keyword
    Given [search-service] prepare request to perform multi delete using properties using properties data
    When  [search-service] send request to perform multi delete
    Then [search-service] validate the multi delete request response success should be 'true'




