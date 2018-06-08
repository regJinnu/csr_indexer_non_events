@ClickThroughData
Feature:Fetch click-through data from BRS and index to solr and Redis


  @FetchDataFromBRS
  Scenario: Fetching click-through data from BRS and store into solr & redis
    Given [search-service] prepare fetching click through data from BRS and index to solr suggestionCollection
    When [search-service] send request to fetch the data and update into redis and solr
    Then [search-service] fetch click through data request response success should be 'true'


  @FetchDataAndStoreitIntoRedis
  Scenario:Fetch click-through data & detectedCategory from suggestionCollection and store into redis
    Given [search-service] prepare fetching click through data from BRS and store it redis
    When [search-service] send request to fetch the data and update into redis
    Then [search-service] fetch click through data and store it into redis request response success should be 'true'

  @GetClickThroughDataAndDetectedCategoryFromRedis
  Scenario: Get click-through data & detectedCategory from redis.
    Given [search-service] prepare fetching click and detected category from redis
    When [search-service] send request to fetch the detected category from redis
    Then [search-service] fetch click through data and detected category from redis request response success should be true