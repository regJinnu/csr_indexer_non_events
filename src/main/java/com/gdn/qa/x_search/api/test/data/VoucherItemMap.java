package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 19/03/20
 * @project X-search
 */
@Data
@Builder
public class VoucherItemMap {
  private String uniqueId;
  private int voucherCount;
  private boolean cncRuleApplied;
}
