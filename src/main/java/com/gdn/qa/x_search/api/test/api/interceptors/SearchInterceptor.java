package com.gdn.qa.x_search.api.test.api.interceptors;

import com.gdn.qa.automation.core.restassured.ServiceInterceptor;
import com.gdn.qa.x_search.api.test.properties.DefaultProperties;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchInterceptor implements ServiceInterceptor {

  @Autowired
  private DefaultProperties properties;

  @Override
  public boolean isSupport(String serviceName) {
      if(serviceName.equalsIgnoreCase("searchservice")){
        return true;
      }
      return false;
    }


  @Override
  public void prepare(RequestSpecification requestSpecification) {
    requestSpecification.header("Content-Type", "application/json;charset=UTF-8")
        .queryParam("storeId", properties.getStoreId())
        .queryParam("requestId", properties.getRequestId())
        .queryParam("channelId", properties.getChannelId())
        .queryParam("username", properties.getUsername())
        .queryParam("clientId", properties.getClientId())
        .log()
        .all();
  }

}
