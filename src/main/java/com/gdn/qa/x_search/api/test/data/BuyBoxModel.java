package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BuyBoxModel {
  private List<ItemBuyBoxScoreDetail> buyBoxScores;
  private String eventId;
}
