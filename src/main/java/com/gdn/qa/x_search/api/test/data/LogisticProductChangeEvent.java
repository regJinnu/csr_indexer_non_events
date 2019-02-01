package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author kumar on 2018-12-06
 * @project X-search
 */

@Data
@Builder
public class LogisticProductChangeEvent {

  private long timestamp;
  private List<String> commissionTypeList;
  private List<String> merchantIdList;
  private String logisticProductCode;
  private List<String> logisticOptionCodeList;
  private boolean markForDelete;
  private boolean activeStatus;
}
