package com.gdn.qa.x_search.api.test.data;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemBuyBoxScoreDetail {
  private String itemSku;
  private Double buyBoxScore;
}
