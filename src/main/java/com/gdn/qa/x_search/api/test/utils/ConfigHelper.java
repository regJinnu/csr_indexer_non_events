package com.gdn.qa.x_search.api.test.utils;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.api.services.ConfigController;
import com.gdn.x.search.rest.web.model.ConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 17/09/18
 * @project X-search
 */

@Service
public class ConfigHelper {

  @Autowired
  ConfigController configController;

  public void findAndUpdateConfig(String configName,String configValue){

    ResponseApi<GdnRestSingleResponse<ConfigResponse>> configByName =
        configController.findConfigByName(configName);

    String id = configByName.getResponseBody().getValue().getId();

    ResponseApi<GdnBaseRestResponse> updateConfigList =
        configController.updateConfigList(id, configName, configName, configValue);

    assertThat("Config is not updated",updateConfigList.getResponse().getStatusCode(),equalTo(200));

  }

}
