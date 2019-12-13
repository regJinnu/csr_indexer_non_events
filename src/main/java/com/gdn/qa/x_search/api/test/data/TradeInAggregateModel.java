package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shivanimalviya on 2019-12-11
 */

@Data
@Builder
public class TradeInAggregateModel {
  private String id;
  private String productSku;
  private String productName;
  private boolean active;
  long timestamp;
}
