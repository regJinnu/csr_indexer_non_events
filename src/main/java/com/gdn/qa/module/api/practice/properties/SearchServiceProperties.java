package com.gdn.qa.module.api.practice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Data
@ConfigurationProperties(prefix = "search")
public class SearchServiceProperties {
  private HashMap<String, String> data;

  public String get(String name) {
    return data.get(name);
  }
}
