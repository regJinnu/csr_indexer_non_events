package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shivanimalviya on 2019-12-09
 */
@Data
@Builder
public class RatingProductIdModel {
  private int storeId;
  private String productId;
  private String metaDataType;
  private int averageRating;
  private int[] ratings;
  private double[] ratingPercentages;
  private int reviewCount;
}