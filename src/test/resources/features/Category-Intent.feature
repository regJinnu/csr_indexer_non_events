@category-intentFeature @TestSuiteID=11050177
Feature:Category Intent Controller

  @Regression @DeleteSearchTerm
  Scenario:Delete searchTerm which has intent mining
    Given [search-service] prepare request for deleting the searchTerm which has intent mining
    When [search-service] send request to delete the search term which has intent mining
    Then [search-service] Delete search term request response success should be 'true'

  @Regression @NegativeCase @DeleteSearchTermWhichIsNotPresent
  Scenario:Delete searchTerm which doesnot intent mining
    Given [search-service] prepare request for deleting the searchTerm which doesnot intent mining
    When [search-service] send request to delete the search term which doesnot intent mining
    Then [search-service] check the Delete search term request response

  @Regression @SaveSearchTerm
  Scenario: User wants to add new searchTerm which has intent mining
    Given [search-service] prepare request for saving searchTerm which has intent mining
    When [search-service] send request to save the search term which has intent mining
    Then [search-service] find that the save searchTerm having IM request response success should be 'true'

  @Regression @NegativeCase @SaveExistingSearchTerm
  Scenario: User wants to save existing searchTerm
    Given [search-service] prepare request for saving existing searchTerm
    When [search-service] send request to save the existing search term which has intent mining
    Then [search-service] check the response for saving existing searchTerm again

  @Regression @GetTheCategoryOfSearchTerm
  Scenario: get category by search term which has intent mining
    Given [search-service] prepare request for finding category ID to which search term is mapped
    When [search-service] send request to find the category id of the search term which has intent mining
    Then [search-service] check that the response of the request conatins categoryId

  @Regression @NegativeCase @GetTheCategoryOfSearchTermWhichDoesnotExist
  Scenario: get category by search term which does not have intent mining
    Given [search-service] prepare request for finding category by search term which does not have intent mining
    When [search-service] send request to find the category id of the search term which doesnot have intent mining
    Then [search-service] check that the response of the request to find categoryId of nonexisting searchTerm


  @Regression @findCategoryIntentEntity
  Scenario:Find searchTerm which has intent mining
    Given [search-service] prepare request for finding the searchTerm which has intent mining
    When [search-service] send request to find the search term which has intent mining
    Then [search-service] find search term request response success should be 'true'

  @NegativeCase @FindSearchTermWhichDoesnothaveIM
  Scenario: Find searchTerm which doesnot have intent mining
    Given [search-service] prepare request for finding the searchTerm which doesnot have intent mining
    When [search-service] send request to find the search term which doesnot intent mining
    Then [search-service] find search term which doesnt have IM request response success should be 'true'


  @Regression  @listCategoryIntentEntity
  Scenario: Get List of searchterms having intent mining
    Given [search-service] prepare request for finding list of searchTerms which have intent mining
    When [search-service] send request to find the list of search terms which have intent mining
    Then [search-service] find list of searchTerms having IM request response success should be 'true'


  @Regression @UpdateSearchTermsIntoRedis
  Scenario: User wants to update CategoryIntent in redis
    Given [search-service] prepare request for updating searchTerms which has intent mining into redis
    When [search-service] send request to update the search terms which have intent mining
    Then [search-service] find that the update searchTerms having IM request response success should be 'true'

  @Regression @ValidateCategory
  Scenario: User wants to validate the category
    Given [search-service] prepare request for validating the category
    When [search-service] send request to validate category
    Then [search-service] find that the validate category request response success should be 'true'

  @Regression  @NegativeCase @ValidInValidCategory
  Scenario: User wants to validate the invalid category
    Given [search-service] prepare request for validating the invalid category
    When [search-service] send request to validate invalidcategory
    Then [search-service] check response for trying to validate invalid category

