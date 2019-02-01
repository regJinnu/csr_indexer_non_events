package com.gdn.qa.x_search.api.test.models;

import com.gdn.x.search.model.IdName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kumar on 21/11/18
 * @project X-search
 */

@Data
@NoArgsConstructor
public class ProductDataFeed extends DataFeed {
  private List<IdName> categoryData = new ArrayList<>();
  private String thumbnailUrl;
  private String discountPercentage;
  private String merchantId;
  private String affiliateUrl;
}
