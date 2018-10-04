package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AdjustmentProductChangeModel {
  private long timestamp;
  private String adjustmentName;
  private String description;
  private String productSku;
  private Date startDate;
  private Date endDate;
  private long value;
  private boolean activated;
}
