package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author kumar on 19/03/20
 * @project X-search
 */

@Data
@Builder
public class MerchantVoucherEventModel {
  private List<VoucherItemMap> voucherItemMap;
  private String storeId;
}
