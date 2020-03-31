package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shivanimalviya on 2019-12-09
 */

@Data
@Builder
public class ProductReviewEventModel {
  long timestamp;
  private RatingProductIdModel[] productReviewMetaDataList;
}