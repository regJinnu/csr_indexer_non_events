package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 14/04/20
 * @project X-search
 */

@Builder
@Data
public class CampaignSession {
  private String campaignCode;
  private int session;
}
