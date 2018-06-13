@ProductReportController  @TestSuiteID=9040152
Feature: Product report controller api's

  @QueryToGetCatergoryList
  Scenario: User wants to query to get Catergory list
    Given [search-service] prepare query to fetch category list using properties using properties data
    When  [search-service] send query to fetch category list request
    Then [search-service] query to fetch category list request response success should be 'true'

  @QueryToGetMerchantList
  Scenario:User wants to query to get merchant list
    Given [search-service] prepare query to fetch merchant list using properties using properties data
    When  [search-service] send query to fetch merchant list request
    Then [search-service] query to fetch merchant list request response success should be 'true'

  @QueryToGetProductResponse
  Scenario:User wants to query to get product list
    Given [search-service] prepare query to get product list using properties using properties data
    When  [search-service] send query to fetch product list request
    Then [search-service] query to fetch product list request response success should be 'true'


