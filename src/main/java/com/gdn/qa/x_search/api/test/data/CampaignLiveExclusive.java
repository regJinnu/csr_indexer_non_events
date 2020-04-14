package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data

public class CampaignLiveExclusive {
  private long timestamp;
  List<CampaignEventModel> campaigns;
}
