@CampaignEventsFeature @TestSuiteID=10352299
Feature: Campaign Related Events

  @CampaignPublish @NotExclusive @Regression
  Scenario: Verify the campaign publish events
    Given [search-service] set all the values for publishing the campaign event
    When [search-service] publish the campaign publish event
    Then [search-service] check if the event is consumed and check in solr

  @CampaignLive @NotExclusive @Regression
  Scenario: Verify Campaign live events
    Given [search-service] set list of campaign codes to go live
    When [search-service] publish the campaign live event
    Then [search-service] check if the event is consumed by checking the setconfig db

  @CampaignRemove @NotExclusive @Regression
  Scenario: Verify Campaign remove events
    Given [search-service] set list of campaign codes to remove
    When [search-service] publish the campaign remove event
    Then [search-service] check if the campaign remove event is consumed

  @CampaignStop @NotExclusive @Regression
  Scenario: Verify Campaign Stop events
    Given [search-service] set list of campaign codes to stop
    When [search-service] publish the campaign stop event
    Then [search-service] check if the event is consumed by checking for the field in solr

  @CampaignEnd @NotExclusive @Regression
  Scenario: Verify Campaign end events
    Given [search-service] set list of campaign codes to end
    When [search-service] publish the campaign end event
    Then [search-service] check if the event is consumed by checking for the field in solr and mongo db

  @CampaignPublishForExclusive @Exclusive @Regression
  Scenario: Verify campaign publish for exclusive event
    Given [search-service] set all the values for publishing exclusive campaign
    When [search-service] publish the exclusive campaign publish event
    Then [search-service] check if exclusive campaign publish event is consumed and check in solr

  @CampaignLiveExclusive @Exclusive @Regression
  Scenario: Verify Campaign live events for exclusive event
    Given [search-service] set list of campaign codes to go live for exclusive event
    When [search-service] publish the campaign live exclusive event
    Then [search-service] check if the event is consumed by checking the configs in config db

  @CampaignRemoveExclusive @Exclusive @Regression
  Scenario: Verify Campaign remove events with exclusive flag
    Given [search-service] set list of campaign codes to remove with exclusive flag
    When [search-service] publish the campaign remove event with exclusive flag
    Then [search-service] check if the campaign remove event is consumed with exclusive flag

  @CampaignStopExclusive @Regression @Exclusive
  Scenario: Verify Campaign Stop events with exclusive flag
    Given [search-service] set list of campaign codes to stop with exclusive flag
    When [search-service] publish the campaign stop event with exclusive flag
    Then [search-service] check if the event is consumed by checking for the field in solr in case of exclusive flag

  @CampaignEndExclusive @Regression @Exclusive
  Scenario: Verify Campaign end events with exclusive flag
    Given [search-service] set list of campaign codes to end with exclusive flag
    When [search-service] publish the campaign end event with exclusive flag
    Then [search-service] check if the event is consumed by checking for the field in solr and config db







