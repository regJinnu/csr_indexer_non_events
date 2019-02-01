@FeedExclusionFeature @TestSuiteID=10541665
Feature: Feed exclusion entity related API's

  @Regression @SaveFeed
  Scenario: User wants to save feed exclusion entity
    Given [search-service] prepare save feed exclusion using properties using properties data
    When  [search-service] send save feed exclusion request
    Then [search-service] save feed exclusion request response success should be 'true'

  @Regression @FindFeedByGivingValue
  Scenario: User wants to find the added feed exclusion
    Given [search-service] prepare find feed exclusion using properties using properties data
    When  [search-service] send find feed exclusion request
    Then [search-service] find feed exclusion request response success should be 'true'

  @Regression @FindFeedByGivingWrongValue
  Scenario: User wants to find the added feed exclusion which is not in the db
    Given [search-service] prepare find feed exclusion which is not present in the db
    When  [search-service] send find feed exclusion request for feed which is absent in db
    Then [search-service] check find feed exclusion request response when feed is not present

  @Regression @FindFeedByID
  Scenario: User wants to find feed by ID
    Given [search-service] prepare find feed exclusion By ID using properties using properties data
    When  [search-service] send find feed exclusion by ID request
    Then [search-service] find feed exclusion request by ID response success should be true

  @Regression  @FindFeedByWrongID
  Scenario: User wants to find feed by wrong ID
    Given [search-service] prepare find feed exclusion By wrong ID request
    When  [search-service] send find feed exclusion by wrong ID request
    Then [search-service] find feed exclusion request by wrong ID response

  @Regression @UpdateTheFeed
  Scenario: User wants to update the feed Exclusion
    Given [search-service] prepare update feed exclusion using properties using properties data
    When  [search-service] send update feed exclusion by ID request
    Then [search-service] update feed exclusion request response success should be 'true'

  @Regression @UpdateByProvidingWrongID
  Scenario:User wants to  update feed exclusion by providing wrong ID
    Given [search-service] prepare update feed exclusion by providing wrong ID
    When  [search-service] send update feed exclusion by wrong ID request
    Then [search-service] update feed exclusion request response by providing wrong ID

  @Regression @ListOfFeedExclusionList
  Scenario: User wants to list feed exclusion list
    Given [search-service] prepare find feed exclusion list using properties using properties data
    When  [search-service] send find feed exclusion list
    Then [search-service] find feed exclusion list request response success should be true

  @Regression @DeleteFeedExclusionList
  Scenario: User wants to delete the feed Exclusion
    Given [search-service] prepare delete feed exclusion using properties using properties data
    When  [search-service] send delete feed exclusion by ID request
    Then [search-service] delete feed exclusion request response success should be 'true'


