package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder

public class MergedProductDetailNew {
  private String id;
  private String productId;
  private String category;
  private List<String> blibliCategoryHierarchy;
  private List<MergedProductItemDetail> productItemDetails;
}
