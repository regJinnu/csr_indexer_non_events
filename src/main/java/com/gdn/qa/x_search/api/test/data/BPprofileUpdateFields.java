package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 13/08/18
 * @project X-search
 */

@Data
@Builder
public class BPprofileUpdateFields {

  private long timestamp;
  private String businessPartnerCode;
  private CompanyV0 company;
}
