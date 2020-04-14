package com.gdn.qa.x_search.api.test.data;

/**
 * @author kumar on 12/09/18
 * @project X-search
 */
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampaignRemoveEvent {
  private long timestamp;
  private String campaignCode;
  private List<ProductSkuEventModel> skuList;
  private boolean emptyQuota;
}
