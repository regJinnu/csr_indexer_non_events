@XproductEventHandlingFeature @TestSuiteID=10541665
Feature:Verifying item and product change event listeners

  @ItemChangeEvent
  Scenario: Verify item change event reindexes the data for the itemSku without itemChangeEventType
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event for that itemSku
    Then [search-service] price information is properly updated for the Sku

  @ItemChangeDeleteEvent
  Scenario: Verify item change event when isArchived is set to true
    Given [search-service] test product is added in SOLR for 'itemChangeEvent'
    When [search-service] consumes item change event with isArchived is set to true
    Then [search-service] deletes only the item sku of test product from SOLR
    And [search-service] Db entry is created for the Sku in deleted product collection

  @ProductChangeEvent
  Scenario: Verify product change event reindexes the data for the sku
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes product change event for that sku
    Then [search-service] price information is properly updated for the Sku

  @ProductChangeDeleteEvent
  Scenario:  Verify product change event when markForDelete is set to true
    Given [search-service] test product is added in SOLR for 'productChangeEvent'
    When [search-service] consumes item change event with markForDelete set to true
    Then [search-service] deletes the test product from SOLR
    Then [search-service] Db entry is created for the productCode in deleted product collection

  @ItemChangePriceChangeAtomicUpdateNoSchedule
  Scenario: Verify item change event with price updates without discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event for that itemSku with price change in itemChangeEventType and 'no' discount schedule
    Then [search-service] price information is properly updated for the Sku with itemChangeEventType and 'no' discount schedule

  @ItemChangePriceChangeAtomicUpdateValidSchedule
  Scenario: Verify item change event with price updates with valid discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event for that itemSku with price change in itemChangeEventType and 'valid' discount schedule
    Then [search-service] price information is properly updated for the Sku with itemChangeEventType and 'valid' discount schedule

  @ItemChangePriceChangeAtomicUpdateInValidSchedule
  Scenario: Verify item change event with price updates with in-valid discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event for that itemSku with price change in itemChangeEventType and 'invalid' discount schedule
    Then [search-service] price information is properly updated for the Sku with itemChangeEventType and 'invalid' discount schedule

  @ItemChangeOfflineChange
  Scenario Outline: Verify item change event with OFFLINE_ITEM_FLAG_CHANGE itemChangeEventType
    Given [search-service] update the off2On field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as OFFLINE_ITEM_FLAG_CHANGE and offToOn flag value as '<flag>'
    Then [search-service] o2o flag is updated to '<flag>'

    Examples:
    |flag   |
    |true   |
    |false  |

  @ItemChangeSyncUnsync
  Scenario: Verify item change event with SYNC_UNSYNC_FLAG_CHANGE itemChangeEventType
    Given [search-service] check isSynchronised field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as SYNC_UNSYNC_FLAG_CHANGE 'without' PristineDataItem
    Then [search-service] isSynchronised flag is updated to false
    And [search-service] level0Id is set to 'productSku'

  @ItemChangeSyncUnsync
  Scenario: Verify item change event with SYNC_UNSYNC_FLAG_CHANGE itemChangeEventType
    Given [search-service] check isSynchronised field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as SYNC_UNSYNC_FLAG_CHANGE 'with' PristineDataItem
    Then [search-service] isSynchronised flag is updated to false
    And [search-service] level0Id is set to 'PristineId'

  @ItemChangePristineMappingChange
  Scenario: Verify item change event with PRISTINE_MAPPING_CHANGE itemChangeEventType
    Given [search-service] check name and level0Id field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE
    Then [search-service] pristine name,id and sales catalog is updated accordingly

  @ItemChangePristineMappingChange1
  Scenario: Verify item change event with PRISTINE_MAPPING_CHANGE and no PristineDataItem
    Given [search-service] update fields in SOLR to test data
    When [search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE and no PristineDataItem
    Then [search-service] complete SOLR doc is updated instead of atomic update

  @ItemChangeItemDataChange
  Scenario: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE with 'no' schedule
    Then [search-service] buyable,published field are added in SOLR with 'no' schedule in DB

  @ItemChangeItemDataChange
  Scenario: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE with 'already running' schedule
    Then [search-service] buyable,published field are added in SOLR with 'already running' schedule in DB

  @ItemChangeItemDataChange
  Scenario: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE with 'future' schedule
    Then [search-service] buyable,published field are added in SOLR with 'future' schedule in DB

  @ItemChangeShippingArchiveChange
  Scenario Outline: Verify item change event with SHIPPING_CHANGE itemChangeEventType
    Given [search-service] update fields in SOLR to test data
    When [search-service] consumes item change event with itemChangeEventType as '<type>'
    Then [search-service] complete SOLR doc is updated instead of atomic update

  Examples:
    |type|
    |SHIPPING_CHANGE|
    |ARCHIVED_FLAG_CHANGE|

  @ItemChangeShippingAndPrice
  Scenario: Verify item change event with SHIPPING_CHANGE and ITEM_PRICE_CHANGE together
    Given [search-service] update fields in SOLR to test data
    And [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with itemChangeEventType with both shipping and price change
    Then [search-service] complete SOLR doc is updated instead of atomic update for price

  @ItemChangePriceAndData
  Scenario: Verify item change event with ITEM_DATA_CHANGE and ITEM_PRICE_CHANGE together
    Given [search-service] check buyable,published field in SOLR for the sku
    And [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with itemChangeEventType with both data and price change
    Then [search-service] buyable,published field are added in SOLR with 'no' schedule in DB
    And [search-service] price information is properly updated for the Sku with itemChangeEventType and 'no' discount schedule

  @ItemChangeDataWithPublished
  Scenario: Verify item change event with ITEM_DATA_CHANGE with published as true
    Given [search-service] remove the SOLR doc from SOLR
    When [search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE and published as 'true'
    Then [search-service] complete SOLR doc is updated instead of atomic update

  @ItemChangeDataWithPublished
  Scenario: Verify item change event with ITEM_DATA_CHANGE with published as false
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE and published as 'false'
    Then [search-service] complete SOLR doc is updated instead of atomic update