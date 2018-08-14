package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author kumar on 14/08/18
 * @project X-search
 */
@Data
@Builder

public class LogisticOptionChange {

  private long timestamp;
  private List<String> commissionTypeList;
  private List<String> merchantIdList;
  private List<String> logisticProductCodeList;
  private String logisticOptionCode;

}
