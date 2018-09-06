package com.gdn.qa.x_search.api.test.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.json.JsonApi;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.automation.core.restassured.ServiceApi;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kumar on 23/08/18
 * @project X-search
 */

@Component
public class FeedController extends ServiceApi {

  @Autowired
  JsonApi jsonApi;

  public ResponseApi<GdnBaseRestResponse> prepareFacebookPopulateAllIdsRequest(){
    Response response = service("searchservice")
        .get("/api/search/facebook/all-ids");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,new TypeReference<GdnBaseRestResponse>() {});
  }

  public ResponseApi<GdnBaseRestResponse> prepareFacebookFullFeedRequest(){
    Response response = service("searchservice")
        .get("/api/search/facebook/full");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,new TypeReference<GdnBaseRestResponse>() {});
  }

  public ResponseApi<GdnBaseRestResponse> prepareFacebookDeltaFeedRequest(){
    Response response = service("searchservice")
        .get("/api/search/facebook/delta");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,new TypeReference<GdnBaseRestResponse>() {});
  }

  public ResponseApi<GdnBaseRestResponse> prepareFacebookExclusionCheckRequest(){
    Response response = service("searchservice")
        .get("/api/search/facebook/exclusion-check");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,new TypeReference<GdnBaseRestResponse>() {});
  }

}
