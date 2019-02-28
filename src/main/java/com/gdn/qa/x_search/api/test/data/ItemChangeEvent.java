package com.gdn.qa.x_search.api.test.data;

import com.gdn.x.product.domain.event.enums.ItemChangeEventType;
import com.gdn.x.product.domain.event.model.ItemViewConfig;
import com.gdn.x.product.domain.event.model.Price;

import com.gdn.x.product.domain.event.model.PristineDataItemEventModel;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
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
  private List<ItemChangeEventType> itemChangeEventTypes;
  private Set<Price> price = new HashSet<Price>();
  private Set<ItemViewConfig> itemViewConfigs = new HashSet<ItemViewConfig>();
  private boolean off2OnChannelActive;
  private PristineDataItemEventModel pristineDataItem;
}
