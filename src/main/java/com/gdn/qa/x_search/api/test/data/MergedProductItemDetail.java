package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
@Data
@Builder
public class MergedProductItemDetail {
  private String productItemId;
  private Set<MergedProductAttributeDetail> attributes;
  private ActionType actionType;
}

