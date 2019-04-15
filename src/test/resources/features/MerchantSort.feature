@MerchantSortFeature @TestSuiteID=10541665
Feature:Merchant Sort controller related API's

  @Regression @AddMerchantSort
  Scenario: User want to add merchant sort mapping
    Given [search-service] prepare request to add new merchant sort mapping
    When [search-service] send add new merchant sort mapping request
    Then [search-service] add new merchant sort mapping request response success should be 'true'

  @Negative @AddMerchantSortWithIncorrectMerchantInfo
  Scenario: User want to add merchant sort mapping with incorrect merchant info
    Given [search-service] prepare request to add new merchant sort mapping with incorrect merchant info
    When [search-service] send add new merchant sort mapping with incorrect merchant info request
    Then [search-service] check the response for adding merchant sort mapping with incorrect merchant info

  @Regression @FindMerchantSortMapping
  Scenario: User want to search for merchant sort mapping
    Given [search-service] prepare search merchant sort mapping request
    When [search-service] send search merchant sort mapping request
    Then [search-service] search merchant sort mapping request response success should be 'true'

  @Regression @FindByMerchantId
  Scenario: User want to search for merchant sort mapping by merchant id
    Given [search-service] prepare search merchant sort mapping by merchant id request
    When [search-service] send search merchant sort mapping by merchant id request
    Then [search-service] search merchant sort mapping by merchant id request response success should be 'true'

  @Negative @FindByIncorrectMerchantId
  Scenario: User want to search for merchant sort mapping by incorrect merchant id
    Given [search-service] prepare search merchant sort mapping by incorrect merchant id request
    When [search-service] send search merchant sort mapping by incorrect merchant id request
    Then [search-service] check the response for searching merchant sort mapping by incorrect merchant id

  @Regression @MerchantSortMappingList
  Scenario: User want existing merchant sort mapping
    Given [search-service] prepare list all merchant sort mapping request
    When [search-service] send list all merchant sort mapping request
    Then [search-service] list all merchant sort mapping request response success should be 'true'

  @Regression @UpdateMerchantSort
  Scenario: User want to update merchant sort mapping
    Given [search-service] prepare update merchant sort mapping request
    When [search-service] send update merchant sort mapping request
    Then [search-service] update merchant sort mapping request response success should be 'true'

  @Regression @FetchMerchantList
  Scenario: User want to fetch list of all the merchants
    Given [search-service] prepare fetch list of all the merchants request
    When [search-service] send fetch list of all the merchants request
    Then [search-service] fetch list of all the merchants request response success should be 'true'

  @Regression @DeleteMerchantSortMapping
  Scenario: User want to delete merchant sort mapping
    Given [search-service] prepare delete merchant sort mapping request
    When [search-service] send delete merchant sort mapping request
    Then [search-service] delete merchant sort mapping request response success should be 'true'

  @Negative @DeleteWithIncorrectId
  Scenario: User want to delete merchant sort mapping with incorrect id
    Given [search-service] prepare delete merchant sort mapping with incorrect id request
    When [search-service] send delete merchant sort mapping with incorrect id request
    Then [search-service] check the response for delete merchant sort mapping with incorrect id