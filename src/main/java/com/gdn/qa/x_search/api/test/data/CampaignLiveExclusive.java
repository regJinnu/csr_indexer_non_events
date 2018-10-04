package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data

public class CampaignLiveExclusive {
  List<CampaignEventModel> campaigns;
}
