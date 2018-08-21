package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 21/08/18
 * @project X-search
 */

@Data
@Builder

public class ProductChangeEvent {

  private long timestamp;
  private String productSku;
  private String productCode;
  private boolean isSynchronized;
  private boolean markForDelete;

}
