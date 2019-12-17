package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shivanimalviya on 2019-12-15
 */

@Data
@Builder
public class AggregateInventoryChangeModel {
  private String itemSku;
  private boolean cnc;
  private String type;
  private StockInformationModel[] stockInformations;
}