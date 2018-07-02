package com.gdn.qa.x_search.api.test.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "param")
public class DefaultProperties {
  private String storeId;
  private String requestId;
  private String channelId;
  private String clientId;
  private String requestParams;
  private boolean test;
  private String username;
}
