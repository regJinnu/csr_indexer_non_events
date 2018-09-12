package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author kumar on 14/08/18
 * @project X-search
 */

@Data
@Builder
public class CompanyV0 {

  private Set<CompanyChangeFields> changedFields;
  private boolean cncActivated;

}

