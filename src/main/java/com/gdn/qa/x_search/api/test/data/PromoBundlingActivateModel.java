package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PromoBundlingActivateModel {
  private String storeId;
  private String promoBundlingId;
  private String mainItemSku;
  private String promoBundlingType;
  private List<String> complementaryProducts;
  private Date startDate;
  private Date endDate;
}
