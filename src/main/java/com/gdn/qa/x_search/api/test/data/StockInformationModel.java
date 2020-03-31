package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shivanimalviya on 2019-12-15
 */

@Data
@Builder
public class StockInformationModel {
  private String location;
  private String status;
  private String pickupPointCode;
}
