package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kumar on 20/08/18
 * @project X-search
 */

@Data
@Builder

public class ItemChangeEvent {

  private long timestamp;
  private String itemSku;
  private String productSku;
  private boolean isSynchronized;
  private boolean isArchived;
  private String uniqueId;

}
