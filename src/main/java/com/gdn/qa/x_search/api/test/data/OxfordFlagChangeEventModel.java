package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author kumar on 20/03/20
 * @project X-search
 */
@Data
@Builder
public class OxfordFlagChangeEventModel {
  long timestamp;
  private String code;
  private String mainCategory;
  private String name;
  private List<String> officialBrands;
  private boolean officialStore;
  private List<String> removedOfficialBrands;
}
