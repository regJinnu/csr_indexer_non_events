package com.gdn.qa.x_search.api.test.data;

/**
 * @author kumar on 12/09/18
 * @project X-search
 */
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSkuEventModel {
  private String productSku;
  private String itemSku;
  private Double discount;
  private int quota;
  private int sessionId;
  private Double blibliDiscount;
  private int blibliQuota;
}
