@XproductEventHandlingFeature @TestSuiteID=11540257
Feature:Verifying item and product change event listeners

  @ItemChangeEvent @Regression
  Scenario Outline: Verify item change event reindexes the data for the itemSku without itemChangeEventType
    Given [search-service] change the price of the sku in Normal and '<other>' collection
    When [search-service] consumes item change event for that itemSku present in Normal and '<other>' collection
    Then [search-service] price information is properly updated for Sku in Normal and '<other>'collection
    Examples:
      | other |
      | O2O   |
      | CNC   |

  @ItemChangeDeleteEvent @Regression
  Scenario: Verify item change event when isArchived is set to true
    Given [search-service] test product is added in SOLR for 'itemChangeEvent'
    When [search-service] consumes item change event with isArchived is set to true
    Then [search-service] deletes only the item sku of test product from SOLR
    And [search-service] Db entry is created for the Sku in deleted product collection

  @ProductChangeEvent  @Regression
  Scenario Outline: Verify product change event reindexes the data for the sku which is present in both Normal and other collection
    Given [search-service] change the price of the sku in Normal and '<other>' collection
    When [search-service] consumes product change event for that sku wrt normal and '<other>' collection
    Then [search-service] price information is properly updated for Sku in Normal and '<other>'collection

    Examples:
      | other |
      | O2O   |
      | CNC   |


  @ProductChangeDeleteEvent @Regression
  Scenario:  Verify product change event when markForDelete is set to true
    Given [search-service] test product is added in SOLR for 'productChangeEvent'
    When [search-service] consumes item change event with markForDelete set to true
    Then [search-service] deletes the test product from SOLR
    Then [search-service] Db entry is created for the productCode in deleted product collection

  @ItemChangePriceChangeAtomicUpdateNoSchedule @Regression
  Scenario Outline: Verify item change event with price updates without discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with price change and 'no' discount schedule and '<status>'
    Then [search-service] price information is updated with itemChangeEventType and 'no' discount schedule and '<status>'

    Examples:
      | status |
      | false  |
      | true   |

  @ItemChangePriceChangeAtomicUpdateValidSchedule @Regression
  Scenario Outline: Verify item change event with price updates with valid discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with price change and 'valid' discount schedule and '<status>'
    Then [search-service] price information is updated with itemChangeEventType and 'valid' discount schedule and '<status>'
    Examples:
      | status |
      | false  |
      | true   |

  @ItemChangePriceChangeAtomicUpdateInValidSchedule @Regression
  Scenario Outline: Verify item change event with price updates with in-valid discount schedule
    Given [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with price change and 'invalid' discount schedule and '<status>'
    Then [search-service] price information is updated with itemChangeEventType and 'invalid' discount schedule and '<status>'
    Examples:
      | status |
      | false  |
      | true   |

  @ItemChangeOfflineChange @Regression
  Scenario Outline: Verify item change event with OFFLINE_ITEM_FLAG_CHANGE itemChangeEventType
    Given [search-service] update the off2On field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as OFFLINE_ITEM_FLAG_CHANGE and offToOn flag value as '<flag>'
    Then [search-service] o2o flag is updated to '<flag>'
    Examples:
      | flag  |
      | true  |
      | false |

  @ItemChangeSyncUnsync @Regression
  Scenario Outline: Verify item change event with SYNC_UNSYNC_FLAG_CHANGE itemChangeEventType
    Given [search-service] check isSynchronised field in SOLR for the sku
    When [search-service] consumes itemChangeEvent as SYNC_UNSYNC_FLAG_CHANGE 'without' PristineDataItem and offToOn flag as '<flag>'
    Then [search-service] isSynchronised flag is updated to false and offToOn flag as '<flag>'
    And [search-service] level0Id is set to 'productSku' and offToOn flag as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeSyncUnsync @Regression
  Scenario Outline: Verify item change event with SYNC_UNSYNC_FLAG_CHANGE itemChangeEventType in Normal and O2O collections
    Given [search-service] check isSynchronised field in SOLR for the sku
    When [search-service] consumes itemChangeEvent as SYNC_UNSYNC_FLAG_CHANGE 'with' PristineDataItem and offToOn flag as '<flag>'
    Then [search-service] isSynchronised flag is updated to false and offToOn flag as '<flag>'
    And [search-service] level0Id is set to 'PristineId' and offToOn flag as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangePristineMappingChange @Regression
  Scenario Outline: Verify item change event with PRISTINE_MAPPING_CHANGE itemChangeEventType
    Given [search-service] check name and level0Id field in SOLR for the sku
    When [search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE and offToOn flag as '<flag>'
    Then [search-service] pristine name,id and sales catalog is updated accordingly and offToOn flag as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangePristineMappingChange1 @Regression
  Scenario Outline: Verify item change event with PRISTINE_MAPPING_CHANGE and no PristineDataItem
    Given [search-service] update fields in SOLR to test data
    When [search-service] consumes item change event as PRISTINE_MAPPING_CHANGE and no PristineDataItem and offToOn as '<flag>'
    Then [search-service] complete SOLR doc is updated instead of atomic update and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |


  @ItemChangeItemDataChange @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with 'no' schedule and offToOn as '<flag>'
    Then [search-service] buyable,published are added in SOLR with 'no' schedule in DB and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeItemDataChange @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with 'already running' schedule and offToOn as '<flag>'
    Then [search-service] buyable,published are added in SOLR with 'already running' schedule in DB and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeItemDataChangeFutureSchedule @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE itemChangeEventType
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes item change event with 'future' schedule and offToOn as '<flag>'
    Then [search-service] buyable,published are added in SOLR with 'future' schedule in DB and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeShippingArchiveChange @Regression
  Scenario Outline: Verify item change event with SHIPPING_CHANGE itemChangeEventType
    Given [search-service] update fields in SOLR to test data
    When [search-service] consumes item change event  as '<type>' and offToOn as '<flag>'
    Then [search-service] complete SOLR doc is updated instead of atomic update and offToOn as '<flag>'

    Examples:
      | type                 | flag  |
      | SHIPPING_CHANGE      | false |
      | ARCHIVED_FLAG_CHANGE | true  |

  @ItemChangeShippingAndPrice  @Regression
  Scenario Outline: Verify item change event with SHIPPING_CHANGE and ITEM_PRICE_CHANGE together
    Given [search-service] update fields in SOLR to test data
    And [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with both shipping and price change and offToOn as '<flag>'
    Then [search-service] complete SOLR doc is updated instead of atomic update and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangePriceAndData @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE and ITEM_PRICE_CHANGE together
    Given [search-service] check buyable,published field in SOLR for the sku
    And [search-service] change the price of the sku in SOLR
    When [search-service] consumes item change event with with both data and price change and offToOn as '<flag>'
    Then [search-service] consumes item change event with 'no' schedule and offToOn as '<flag>'
    Then [search-service] buyable,published are added in SOLR with 'no' schedule in DB and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeDataWithPublished @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE with published as true
    Given [search-service] remove the SOLR doc from SOLR
    When [search-service] consumes itemChangeEventType as ITEM_DATA_CHANGE and published as 'true' and offToOn as '<flag>'
    Then [search-service] complete SOLR doc is updated instead of atomic update and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |

  @ItemChangeDataWithPublished @Regression
  Scenario Outline: Verify item change event with ITEM_DATA_CHANGE with published as false
    Given [search-service] check buyable,published field in SOLR for the sku
    When [search-service] consumes itemChangeEventType as ITEM_DATA_CHANGE and published as 'false' and offToOn as '<flag>'
    Then [search-service] complete SOLR doc is updated instead of atomic update and offToOn as '<flag>'
    Examples:
      | flag  |
      | false |
      | true  |