package com.gdn.qa.x_search.api.test.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoData {

  private String _id;
  private String _class;
  private String NAME;
  private String LABEL;
  private String VALUE;
  private String version;
  private String CREATED_DATE;
  private String CREATED_BY;
  private String UPDATED_DATE;
  private String UPDATED_BY;
  private String STORE_ID;
  private String MARK_FOR_DELETE;

}
