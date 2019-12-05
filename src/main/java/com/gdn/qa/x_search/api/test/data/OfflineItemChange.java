package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * @author kumar on 2019-05-03
 * @project X-search
 */
@Data
@Builder
public class OfflineItemChange {

  private long timestamp;
  private String uniqueId;
  private String merchantCode;
  private String itemSku;
  private String merchantSku;
  private String pickupPointCode;
  private String externalPickupPointCode;
  private String productSku;
  private Double offerPrice;
  private String itemCode;
  private Boolean markForDelete;


}
