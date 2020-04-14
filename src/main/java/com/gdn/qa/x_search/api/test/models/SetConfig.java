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
@JsonPropertyOrder({"_id","name","value","UPDATED_DATE","version"})
public class SetConfig {

  @JsonProperty("_id")
  private Object id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("value")
  private String[] value;
  @JsonProperty("UPDATED_DATE")
  private Object updatedDate;
  @JsonProperty("version")
  private Object version;

}
