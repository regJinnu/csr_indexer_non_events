package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author kumar on 20/03/20
 * @project X-search
 */

@Data
@Builder
public class OxfordSkuChangeModel {
  public enum ContractType{
    BRAND,
    STORE;
  }
  long timestamp;
  private Map<ContractType, List<String>> catalogNamesByContractType;
  private String sku;
}
