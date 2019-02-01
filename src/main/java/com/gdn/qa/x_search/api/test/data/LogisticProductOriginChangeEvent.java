package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author kumar on 16/08/18
 * @project X-search
 */

@Data
@Builder

public class LogisticProductOriginChangeEvent {

  private long timestamp;
  private List<String> originList;
  private String logisticProductCode;

}
