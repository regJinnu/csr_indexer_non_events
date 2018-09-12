package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 02/08/18
 * @project X-search
 */

@Data
@Builder
public class BPStoreClosedEvent {

  private long timestamp;
  private String businessPartnerCode;
  private long startDate;
  private long endDate;
  private String publishType;
  private long startDateWithBufferClosingStoreDay;
  private boolean delayShipping;

}
