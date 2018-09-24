@CampaignRelatedEvents
Feature: Campaign Related Events

  @CampaignPublish  @Regression
  Scenario: Verify the campaign publish events
    Given [search-service] set all the values for publishing the campaign event
    When [search-service] publish the campaign publish event
    Then [search-service] check if the event is consumed and check in solr

  @CampaignLive
  Scenario: Verify Campaign live events
    Given [search-service] set list of campaign codes to go live
    When [search-service] publish the campaign live event
    Then [search-service] check if the event is consumed by checking the setconfig db

  @CampaignStop
  Scenario: Verify Campaign Stop events
    Given [search-service] set list of campaign codes to stop
    When [search-service] publish the campaign stop event
    Then [search-service] check if the event is consumed by checking for the field in solr

    @CampaignRemove
    Scenario: Verify Campaign remove events
      Given [search-service] set list of campaign codes to remove
      When [search-service] publish the campaign remove event
      Then [search-service] check if the campaign remove event is consumed

  @CampaignEnd
  Scenario: Verify Campaign end events
    Given [search-service] set list of campaign codes to end
    When [search-service] publish the campaign end event
    Then [search-service] check if the event is consumed by checking for the field in solr and mongo db

