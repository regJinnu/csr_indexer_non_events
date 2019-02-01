package com.gdn.qa.x_search.api.test.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kumar on 21/11/18
 * @project X-search
 */

@Data
@NoArgsConstructor
public class DataFeed {
  private String id;
  private String price;
  private String deepLink;
  private String name;
  private String brand;
  private String description;
  private String url;
  private String imageUrl;
  private String categoryBreadcrumb;
  private String priceAfterDiscount;
}
