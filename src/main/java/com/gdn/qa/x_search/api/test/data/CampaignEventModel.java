package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author kumar on 17/09/18
 * @project X-search
 */

@Data
@Builder
public class CampaignEventModel {

  private String campaignCode;
  private String campaignName;
  private Date promotionStartTime;
  private Date promotionEndTime;
  private String tagLabel;
  private boolean exclusive;
  private int activateSession;
}
