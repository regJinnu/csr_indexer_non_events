@MongoQueryFeature @TestSuiteID=11540257
Feature: Mongo query tab related api's

  @Regression @RunDeltaIndex
  Scenario: User wants to query to mongo and fetch classes
    Given [search-service] prepare request to query to mongo and fetch classes
    When  [search-service] send request to query to mongo and fetch classes
    Then [search-service] request to query to mongo and fetch classes response success should be 'true'

  @Regression @QueryToMongo
  Scenario: User wants to query to mongo
    Given [search-service] prepare request to query to mongo
    When  [search-service] send request to query to mongo
    Then [search-service] request to query to mongo response success should be 'true'