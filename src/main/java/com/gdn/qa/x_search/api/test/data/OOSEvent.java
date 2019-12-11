package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 01/08/18
 * @project X-search
 */

@Data
@Builder

public class OOSEvent {

  private long timestamp;
  private String storeId;
  private String level2Id;
  private String level2MerchantCode;
  private String uniqueId;
  private Boolean cncActivated;
}
