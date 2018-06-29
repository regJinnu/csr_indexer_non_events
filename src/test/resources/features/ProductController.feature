@ProductControllerFeature
  Feature: Product Controller feature related api's

    @GetProductRawDataByProductID
    Scenario: User wants to get product raw data by product id or by sku
      Given [search-service] prepare request to get product raw data by product id or by sku
      When  [search-service] send request to get product raw data by product id or by sku
      Then [search-service] request to get product raw data by product id or by sku response success should be 'true'