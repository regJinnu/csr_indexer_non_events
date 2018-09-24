@ScheduledEventsApi @TestSuiteID=9677376
Feature: Scheduled events related api's

  @Regression @DeleteUnpublishedProducts
  Scenario:  User wants to delete the unpublished/unbuyable/non off2ON products
    Given [search-service] prepare request to delete the unpublished products
    When  [search-service] send request to delete the unpublished products
    Then [search-service] fetch delete the unpublished products response success should be 'true'


  @Regression @FetchScheduledEvents
  Scenario: User wants to fetch scheduled events from mongo and do atomic update to solr
    Given [search-service] prepare request to fetch scheduled events from mongo and do atomic update to solr
    When  [search-service] send request to fetch scheduled events from mongo and do atomic update to solr
    Then [search-service] fetch scheduled events from mongo and do atomic update to solr response success should be 'true'