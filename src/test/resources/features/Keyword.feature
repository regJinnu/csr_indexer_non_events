@KeywordFeature @TestSuiteID=9317806
Feature: Keyword related API's

  @Regression @SaveKeyword
  Scenario: User wants to upload new keyword
    Given [search-service] prepare save keyword using properties using properties data
    When  [search-service] send save keyword request
    Then [search-service] save keyword request response success should be 'true'

  @Regression  @ListOfKeywords
  Scenario: User wants list of keywords
    Given [search-service] prepare request to get list of keywords
    When  [search-service] send listing keyword request
    Then [search-service] list keyword request response success should be 'true'

  @Regression @FindByKeyword
  Scenario: User wants to search by using keyword
    Given [search-service] prepare request to get find by keyword
    When  [search-service] send find by keyword request
    Then [search-service]find by keyword request response success should be true

  @Regression @FindByNonExistingKeyword
  Scenario: User wants to search by using non existing keyword
    Given [search-service] prepare request to get find by non existing keyword
    When  [search-service] send find by non existing keyword request
    Then [search-service]find by non existing keyword request response success should be true

  @Regression @FindByID
  Scenario: User wants to search keyword  by using ID
    Given [search-service] prepare request to get keyword find by ID
    When  [search-service] send find keyword by ID request
    Then [search-service]find keyword by id request response success should be true

  @Regression @FindByWrongID
  Scenario: User wants to search keyword  by using wrong ID
    Given [search-service] prepare request to get keyword find by wrong ID
    When  [search-service] send find keyword by wrong ID request
    Then [search-service]find keyword by wrong id request response success should be true

  @Regression @FindKeywordByDateStamp
  Scenario: User wants to search keyword  by date stamp
    Given [search-service] prepare request to get keyword find by date
    When  [search-service] send find keyword by date request
    Then [search-service]find keyword by date request response success should be true

  @Regression @FindKeywordByProvidingWrongDate
  Scenario:User wants to search keyword  by wrong date stamp
    Given [search-service] prepare request to get keyword find by wrong date
    When  [search-service] send find keyword by wrong  date request
    Then [search-service]find keyword by wrong date request response success should be true

  @Regression @FindIfProductExists
  Scenario: User wants to search if the product exists
    Given [search-service] prepare request to find if the product exists
    When  [search-service] send request to find if the product exists
    Then [search-service]find if the product exists request response success should be 'true'

  @Regression @TryToFindNonExistingProduct
  Scenario: User wants to search non existing product
    Given [search-service] prepare request to find non existing product
    When  [search-service] send request to find if the non existing product
    Then [search-service]check response for request to find keyword which is not existing

  @Regression  @KeywordUpdate
  Scenario: User wants to update existing keyword
    Given [search-service] prepare request to update the existing product
    When  [search-service] send request to update the existing product
    Then [search-service]find if the update the existing product request response success should be 'true'

  @UpdateNonExistingKeyword
  Scenario: User wants to update non existing keyword
    Given [search-service] prepare request to update the non existing product
    When [search-service] send request to update the non existing product
    Then [search-service]find if the update the non existing product request response success should be false

  @Regression  @ValidateIdAndGetName
  Scenario: User wants to validate ID and Get Name
    Given [search-service] prepare request to validate id and get name
    When [search-service] send request to validate id and get name
    Then [search-service]find if the validate ID and Get Name request response success should be 'true'

  @ProvideInValidIDAndGetName
  Scenario: User enters to invalid ID to Get Name
    Given [search-service] prepare request by providing invalidate id
    When [search-service] send request by providing invalidate id
    Then [search-service]check the response by paasing invalid ID as input

  @Regression @FindKeywordByListing
  Scenario: User wants to find Keyword
    Given [search-service] prepare request to find keyword
    When  [search-service] send request to find keyword
    Then [search-service]find if the find keyword request response success should be 'true'

  @Regression @FindKeywordByQueryingInWrongWay
  Scenario: User wants to find Keyword by doing wrong request
    Given [search-service] prepare request to find keyword by querying in wrong way
    When  [search-service] send request to find keyword by querying in wrong way
    Then [search-service]find if the find keyword by querying in wrong way request response success should be true

  @Regression @DeleteKeyword
  Scenario: User wants to delete the Keyword
    Given [search-service] prepare request to delete keyword
    When  [search-service] send request to delete keyword
    Then [search-service]find if the delete keyword request response success should be 'true'

  @Regression @DeleteNonExistingKeyword
  Scenario: User wants to delete non existing keyword
    Given [search-service] prepare request to delete non existing keyword
    When  [search-service] send request to delete non existing keyword
    Then [search-service]find if the non existing delete keyword request response

  @Regression @UploadKeyword
  Scenario: User wants to upload the keyword
    Given [search-service] prepare request to upload keyword
    When  [search-service] send request to upload keyword
    Then [search-service]find the upload keyword request response is 'true'



