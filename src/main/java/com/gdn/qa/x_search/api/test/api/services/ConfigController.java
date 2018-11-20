package com.gdn.qa.x_search.api.test.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.x.search.rest.web.model.ConfigResponse;
import io.restassured.response.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.json.JsonApi;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.automation.core.restassured.ServiceApi;
import com.gdn.x.search.rest.web.model.ConfigResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.FILE_PATH;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.X_SEARCH_SERVICE;


/**
 * @author kumar on 16/10/18
 * @project X-search
 */

@Component
public class ConfigController  extends ServiceApi {


  @Autowired
  JsonApi jsonApi;

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findConfigByName(String configName) {
    Response response = service(X_SEARCH_SERVICE)
        .queryParam("name", configName)
        .get(FILE_PATH + "config/find-by-name");

    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> updateConfigList(String id,String label,String name,String value) {
    String bodyJson = "{\n" + "  \"id\": \""+id+"\",\n"
        + "  \"label\": \""+label+"\",\n" + "  \"name\": \""+name+"\",\n"
        + "  \"value\": \""+value+"\"\n" + "}";

    Response response = service(X_SEARCH_SERVICE)
        .body(bodyJson)
        .post(FILE_PATH + "config/update");

    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }



}
