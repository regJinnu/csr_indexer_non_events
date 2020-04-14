package com.gdn.qa.x_search.api.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * @author kumar on 14/04/20
 * @project X-search
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"_id","NAME","VALUE","LABEL","STORE_ID","UPDATED_BY","UPDATED_DATE"})
public class Config {

  @JsonProperty("_id")
  private Object id;
  @JsonProperty("NAME")
  private String name;
  @JsonProperty("VALUE")
  private String value;
  @JsonProperty("LABEL")
  private String label;
  @JsonProperty("STORE_ID")
  private String storeId;
  @JsonProperty("UPDATED_BY")
  private String updatedBy;
  @JsonProperty("UPDATED_DATE")
  private Object updatedDate;

}
