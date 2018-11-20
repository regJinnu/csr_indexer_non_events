package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PristineEventsModel   {
  private long timestamp;
  private String productId;
  private int itemCount;
  private MergedProductDetailNew productDetail;
  private long eventDateTime;
}
