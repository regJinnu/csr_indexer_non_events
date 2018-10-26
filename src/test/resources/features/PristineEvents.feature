@PristineEventsAutomationFeature
Feature: Automation of pristine related events

  @ComputerCategoryPristineEvents
  Scenario: Verify pristine events for Computer category
    Given [search-service] set all the values for publishing the pristine event for Computer category
    When [search-service] publish the pristine event for Computer category
    Then [search-service] check if the event is consumed by checking the solr field

  @CameraCategoryPristineEvents
  Scenario: Verify pristine events for camera category
    Given [search-service] set all the values for publishing the pristine event for camera category
    When [search-service] publish the pristine event for camera category
    Then [search-service] check if the event is consumed by checking the solr field for camera category

  @HandphonePristineEvents
  Scenario: Verify pristine events for handphone category
    Given [search-service] set all the values for publishing the pristine event for handphone category
    When [search-service] publish the pristine event for handphone category
    Then [search-service] check if the event is consumed by checking the solr field for handphone category