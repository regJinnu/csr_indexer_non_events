package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoBundlingDeactivateModel {
  private String storeId;
  private String sku;
  private String promoBundlingType;
}
