package com.gdn.qa.x_search.api.test.data;

/**
 * @author kumar on 12/09/18
 * @project X-search
 */

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class CampaignPublishEvent {
  long timestamp;
  private String campaignName;
  private Date promotionStartTime;
  private Date promotionEndTime;
  private String campaignCode;
  private List<ProductSkuEventModel> skuList;
  private String tagLabel;
  private boolean exclusive;
}