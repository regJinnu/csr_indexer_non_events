package com.gdn.qa.x_search.api.test.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.json.JsonApi;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.automation.core.restassured.ServiceApi;
import com.gdn.qa.automation.core.template.TemplateApi;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import com.gdn.x.search.rest.web.model.*;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class SearchServiceController extends ServiceApi {
  @Autowired
  private JsonApi jsonApi;

  @Autowired
  private TemplateApi templateAPI;

  @Autowired
  private SearchServiceData searchServiceData;

  public ResponseApi<GdnBaseRestResponse> bodyofDeleteConfigRequest() {
    String bodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

   Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoid());
    data.put("name", searchServiceData.getName());

    String bodyRequest = templateAPI.createFromString(bodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/config/delete");

    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyofRequestWithWrongID() {
    String bodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("name", searchServiceData.getName());

    String bodyRequest = templateAPI.createFromString(bodyTemplate, data);
    Response response = service("searchservice")
        //.queryParam("username","testuser")
        .body(bodyRequest).post("/config/delete");

    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }


  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfSaveConfig() {
    String Bodytemplate = "{\n" + "  \"name\": \"{{name}}\",\n" + "  \"label\": \"{{label}}\",\n"
        + "  \"value\": \"{{value}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("name", searchServiceData.getName());
    data.put("label", searchServiceData.getLabel());
    data.put("value", searchServiceData.getValue());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/config/save");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestwithEmptyBody() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getEmptyid());
    data.put("name", searchServiceData.getEmptyname());


    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/config/save");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestToUpdateTheConfig() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\",\n"
        + "  \"label\": \"{{label}}\",\n" + "  \"value\": \"{{value}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoid());
    data.put("name", searchServiceData.getName());
    data.put("label", searchServiceData.getLabel());
    data.put("value", searchServiceData.getUpdatedValue());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/config/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestToUpdateTheConfigWithEmptyBody() {
    String Bodytemplate =
        "{\n" + " \n" + "\"id\":\"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + " \n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getEmptyid());
    data.put("name", searchServiceData.getEmptyname());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/config/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByNameResponse() {
    Response response = service("searchservice")
        .queryParam("name", searchServiceData.getName())
        .get("/config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWrongNameResponse() {
    Response response = service("searchservice")
        .queryParam("name", searchServiceData.getWrongname())
        .get("/config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> setFindByIDResponse() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getAutoid())
        .get("/config/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> setFindByWrongIDResponse() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/config/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWordResponse() {
    Response response = service("searchservice").queryParam("word", searchServiceData.getWord())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWrongWordResponse() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongname())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get("/config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<ConfigResponse>> configListResponse() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/config/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataFromBRS() {
    Response response = service("searchservice")
        .queryParam("username", searchServiceData.getUsername())
        .post("/clickthrough/fetch-data");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataAndStoreIntoRedis() {
    Response response = service("searchservice")
        .queryParam("username", searchServiceData.getUsername())
        .post("/clickthrough/store-click-through-and-category-redis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichHasIntentMining() {
    Response response = service("searchservice")
        .queryParam("searchTerm", searchServiceData.getSearchTerm())
        .post("/categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichDoesNotIntentMining() {
    Response response = service("searchservice")
        .queryParam("searchTerm", searchServiceData.getSearchTermNotPresent())
        .post("/categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichHasIntentMining() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getSearchTerm())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichDoesNotIntentMining() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getSearchTermNotPresent())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTerm() {
    Response response = service("searchservice")
        .queryParam("searchTerm", searchServiceData.getSearchTerm())
        .get("/categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTermNotPresent() {
    Response response = service("searchservice")
        .queryParam("searchTerm", searchServiceData.getSearchTermNotPresent())
        .get("/categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<CategoryIntentResponse>> findlistOfSearchTerms() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/categoryIntent/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> saveSearchTermWhichHasIntentMining() {
    Response response = service("searchservice")
        .queryParam("searchTerm", searchServiceData.getSearchTerm())
        .queryParam("categoryId", searchServiceData.getCategoryId())
        .queryParam("turnedOn", searchServiceData.getTurnedOn())
        .post("/categoryIntent/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateSearchTermWhichHasIntentMiningIntoRedis() {
    Response response = service("searchservice").post("/categoryIntent/updateInRedis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateCategoryID() {
    Response response = service("searchservice")
        .queryParam("categoryId", searchServiceData.getCategoryId())
        .post("/categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateInValidCategoryID() {
    Response response = service("searchservice")
        .queryParam("categoryId", searchServiceData.getWrongcategoryId())
        .post("/categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>> fetchListOfSearchTermsWhichHasImFromRedis() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPagenumberForIMlist())
        .get("/clickthrough/get-click-through-and-category-redis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfFeedExclusionSaveRequest() {
    String Bodytemplate = "{\n" + "  \"key\": \"{{key}}\",\n" + "  \"value\": \"{{value}}\",\n"
        + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("key", searchServiceData.getFeedKey());
    data.put("value", searchServiceData.getFeedValue());
    data.put("feedType", searchServiceData.getFeedType());
    //  data.put("positiveType",searchserviceData.getPositiveFilter());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/feed-exclusion-entity/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWord() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getFeedValue())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWrongWord() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getWrongword())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getAutoFeedId())
        .get("/feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByWrongID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> listAllFeedExclusions() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/feed-exclusion-entity/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfFeedExclusionUpdateRequest() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoFeedId());
    data.put("key", searchServiceData.getFeedKey());
    data.put("value", searchServiceData.getUpdateFeedValue());
    data.put("feedType", searchServiceData.getFeedType());
    //   data.put("positiveType",searchserviceData.getPositiveFilter());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice")
        .body(bodyRequest)
        .post("/feed-exclusion-entity/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfFeedExclusionUpdateRequestWithWrongID() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("key", searchServiceData.getFeedKey());
    data.put("value", searchServiceData.getFeedValue());
    data.put("feedType", searchServiceData.getFeedType());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice")
        .body(bodyRequest)
        .post("/feed-exclusion-entity/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfFeedExclusionDeleteRequest() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoFeedId());
    data.put("key", searchServiceData.getFeedKey());
    data.put("value", searchServiceData.getUpdateFeedValue());
    data.put("feedType", searchServiceData.getFeedType());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice")
        .body(bodyRequest)
        .post("/feed-exclusion-entity/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfSaveKeyword() {
    String BodyTemplate = "{\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    Map<String, String> data = new HashMap<>();
    data.put("keyword", searchServiceData.getKeyword());
    data.put("negativeKeyword", searchServiceData.getNegativeKeyword());
    data.put("categoryProductId", searchServiceData.getCategoryProductId());
    data.put("categoryProductName", searchServiceData.getCategoryProductName());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/keyword/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoKeywordId());
    data.put("keyword", searchServiceData.getKeyword());
    data.put("negativeKeyword", searchServiceData.getUpdatedNegativeKeyword());
    data.put("categoryProductId", searchServiceData.getCategoryProductId());
    data.put("categoryProductName", searchServiceData.getCategoryProductName());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/keyword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateNonExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("keyword", searchServiceData.getKeyword());
    data.put("negativeKeyword", searchServiceData.getUpdatedNegativeKeyword());
    data.put("categoryProductId", searchServiceData.getCategoryProductId());
    data.put("categoryProductName", searchServiceData.getCategoryProductName());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/keyword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywords() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/keyword/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findByKeywordRequest() {
    Response response = service("searchservice")
        .queryParam("keyword", searchServiceData.getKeyword())
        .get("/keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findByNonExistingKeywordRequest() {
    Response response = service("searchservice")
        .queryParam("keyword", searchServiceData.getWrongword())
        .get("/keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findKeywordRequestByID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getAutoKeywordId())
        .get("/keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findSynonymRequestByID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getAutoSynonymnId())
        .get("/synonyms/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findSynonymRequestByWrongID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/synonyms/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }


  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findKeywordRequestByWrongID() {
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findKeywordsByDate() {
    Response response = service("searchservice")
        .queryParam("currentDate", searchServiceData.getAutoUpdatedDate())
        .queryParam("word", searchServiceData.getCategoryProductName())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findKeywordsByWrongDate() {
    Response response = service("searchservice")
        .queryParam("currentDate", searchServiceData.getWrongdate())
        .queryParam("word", searchServiceData.getKeywordForsearchingWithDateStamp())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findIfProductExists() {
    Response response = service("searchservice")
        .queryParam("productId", searchServiceData.getCategoryProductId())
        .get("/keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findForNonExistingProduct() {
    Response response = service("searchservice")
        .queryParam("productId", searchServiceData.getWrongcategoryId())
        .get("/keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywordsWithKeyValuePair() {
    Response response = service("searchservice")
        .queryParam("index", searchServiceData.getIndex())
        .queryParam("word", searchServiceData.getCategoryProductId())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywordsWithWrongInput() {
    Response response = service("searchservice")
        .queryParam("index", searchServiceData.getCategoryProductName())
        .queryParam("word", searchServiceData.getCategoryProductId())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get("/keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> validateIdAndGetName() {
    Response response = service("searchservice")
        .queryParam("categoryProductId", searchServiceData.getValidate())
        .queryParam("type", searchServiceData.getType())
        .get("/keyword/validate-id-and-get-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<ValidateIdAndGetNameResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> validateNonExistingIdAndGetName() {
    Response response = service("searchservice")
        .queryParam("categoryProductId", searchServiceData.getWrongid())
        .queryParam("type", searchServiceData.getType())
        .get("/keyword/validate-id-and-get-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<ValidateIdAndGetNameResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfDeleteKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoKeywordId());
    data.put("keyword", searchServiceData.getKeyword());
    data.put("negativeKeyword", searchServiceData.getNegativeKeyword());
    data.put("categoryProductId", searchServiceData.getCategoryProductId());
    data.put("categoryProductName", searchServiceData.getCategoryProductName());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/keyword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfDeleteNonExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("keyword", searchServiceData.getKeyword());
    data.put("negativeKeyword", searchServiceData.getNegativeKeyword());
    data.put("categoryProductId", searchServiceData.getCategoryProductId());
    data.put("categoryProductName", searchServiceData.getCategoryProductName());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/keyword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getCategoryList() {
    Response response = service("searchservice").post("/fetch-category-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getMerchantList() {
    Response response = service("searchservice").post("/fetch-merchant-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getProductList() {
    Response response = service("searchservice")
        .queryParam("category", searchServiceData.getCategoryId())
        .queryParam("merchant", searchServiceData.getMerchant())
        .post("/product-data-report");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> uploadKeyword() {
    Response response = service("searchservice").removeHeader("content-type")
        .queryParam("email", searchServiceData.getEmail())
        .header("content-type", "multipart/data")
        .queryParam("email", searchServiceData.getEmail())
        .multiPart(new File(searchServiceData.getPath()))
        .post("/keyword/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> generateSynonyms() {
    Response response = service("searchservice").get("/synonyms/create-synonyms");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findByKey() {
    Response response = service("searchservice")
        .queryParam("key", searchServiceData.getSearchTerm())
        .get("/synonyms/find-by-key");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findByWrongKey() {
    Response response = service("searchservice")
        .queryParam("key", searchServiceData.getWrongname())
        .get("/synonyms/find-by-key");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> findSynonymRequestByWord() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getSearchTerm())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .queryParam("status", searchServiceData.getPagenumberForIMlist())
        .get("/synonyms/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> findSynonymRequestByWrongWord() {
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getWrongword())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .queryParam("status", searchServiceData.getPagenumberForIMlist())
        .get("/synonyms/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> listSynonyms() {
    Response response = service("searchservice")
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .queryParam("status", searchServiceData.getPagenumberForIMlist())
        .get("/synonyms/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfDeleteSynonym() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"synonyms\": \"{{synonyms}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoSynonymnId());
    data.put("key", searchServiceData.getSearchTerm());
    data.put("synonyms",searchServiceData.getSynonyms());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/synonyms/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSynonymFromSolr() {
    Response response = service("searchservice")
        .queryParam("key", searchServiceData.getSearchTerm())
        .delete("/integration/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateSynonymFromSolr() {
    Response response = service("searchservice")
        .queryParam("updateAll", searchServiceData.getWrongword())
        .post("/integration/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfSaveStopword() {
    String BodyTemplate =
        "{\n" + "  \"stopWord\": \"{{stopWord}}\",\n" + "  \"groupName\": \"{{groupName}}\",\n" + "  \"sync\": \"{{sync}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getSearchTerm());
    data.put("groupName", searchServiceData.getStopwordgroup());
    data.put("sync", searchServiceData.getSync());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/stopword/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> findStopwordByword(){
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getSearchTerm())
        .queryParam("page",searchServiceData.getPage())
        .queryParam("size",searchServiceData.getSize())
        .get("/stopword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> findStopwordByWrongword(){
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getWrongword())
        .queryParam("page",searchServiceData.getPage())
        .queryParam("size",searchServiceData.getSize())
        .get("/stopword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<StopWordResponse>> findStopwordByID(){
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getAutoStopwordID())
        .get("/stopword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<StopWordResponse>> findStopwordByWrongID(){
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/stopword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> listStopword(){
    Response response = service("searchservice")
        .queryParam("page",searchServiceData.getPage())
        .queryParam("size",searchServiceData.getSize())
        .get("/stopword/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateStopword() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id",searchServiceData.getAutoStopwordID());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateStopwordByWrongID() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id",searchServiceData.getWrongid());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfDeleteStopword() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id",searchServiceData.getAutoStopwordID());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post("/stopword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteStopwordIntegration() {
    Response response = service("searchservice")
        .queryParam("key", searchServiceData.getSearchTerm())
        .delete("/integration/stopword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateStopwordFromSolr() {
    Response response = service("searchservice")
        .queryParam("updateAll", searchServiceData.getWrongword())
        .post("/integration/stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SetConfigResponse>> setConfigRequest(){
    Response response = service("searchservice")
        .queryParam("name", searchServiceData.getSetConfig())
        .get("/fetchSaveConfig");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SetConfigResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateFieldCache() {
    Response response = service("searchservice")
        .queryParam("fieldName", searchServiceData.getFieldName())
        .get("/updateFieldCache");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }
  public ResponseApi<GdnBaseRestResponse> updateNonExistingFieldCache() {
    Response response = service("searchservice")
        .queryParam("fieldName", searchServiceData.getWrongname())
        .get("/updateFieldCache");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public  ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> findBoostedKeyword(){
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getSearchTerm())
        .queryParam("size",searchServiceData.getSize())
        .get("/keywordBoost/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordBoostProductResponse>> findBoostedKeywordByID(){
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getAutoBoostedKeywordID())
        .get("/keywordBoost/findById");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordBoostProductResponse>>() {
    });
  }

  public  ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> listBoostedKeyword(){
    Response response = service("searchservice")
        .queryParam("size",searchServiceData.getSize())
        .get("/keywordBoost/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfUpdateBoostedKeyword() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"products\": \"{{products}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoBoostedKeywordID());
    data.put("keyword", searchServiceData.getSearchTerm());
    data.put("products", searchServiceData.getProductID());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/keywordBoost/update");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public  ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> listAllBoostedKeyword(){
    Response response = service("searchservice")
        .get("/keywordBoost/getAll");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
    });
  }
  public ResponseApi<GdnBaseRestResponse> uploadBoostedKeyword() {
    Response response = service("searchservice").removeHeader("content-type")
        .header("content-type", "multipart/data")
        .queryParam("email", searchServiceData.getEmail())
        .multiPart(new File(searchServiceData.getPathForBoostedKeyword()))
        .post("/keywordBoost/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfDeleteBoostedKeyword() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"products\": \"{{products}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoBoostedKeywordID());
    data.put("keyword", searchServiceData.getSearchTerm());
    data.put("products", searchServiceData.getProductID());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/keywordBoost/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse> bodyOfRequestOfValidateID(String incorrectID,String correctID) {
    String Bodytemplate="[\""+correctID+"\",\""+incorrectID+"\"]";
   Response response=service("searchservice").body(Bodytemplate).post("/keywordBoost/validate");
   response.getBody().prettyPrint();
   return jsonApi.fromJson(response,new TypeReference<GdnRestSingleResponse>(){
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfMultiDelete() throws Exception {
    String Bodytemplate = "[\n" + "  {\n" + "    \"id\": \"98765\"\n"
        + "   }, {\n" + "\"id\": \"43210\"\n" + "}\n" + "]";
    Response response = service("searchservice").body(Bodytemplate).post("/keywordBoost/multidelete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public  ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> findWrongBoostedKeyword(){
    Response response = service("searchservice")
        .queryParam("word", searchServiceData.getWrongname())
        .queryParam("size",searchServiceData.getSize())
        .get("/keywordBoost/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordBoostProductResponse>> findBoostedKeywordByWrongID(){
    Response response = service("searchservice")
        .queryParam("id", searchServiceData.getWrongid())
        .get("/keywordBoost/findById");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordBoostProductResponse>>() {
    });
  }
  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfUpdateBoostedKeywordWithWrongID() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"products\": \"{{products}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("keyword", searchServiceData.getSearchTerm());
    data.put("products", searchServiceData.getProductID());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    Response response = service("searchservice").body(bodyRequest).post("/keywordBoost/update");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> uploadWrongFile() {
    Response response = service("searchservice").removeHeader("content-type")
        .header("content-type", "multipart/data")
        .queryParam("email", searchServiceData.getEmail())
        .multiPart(new File(searchServiceData.getWrongFile()))
        .post("/keywordBoost/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> cleanUp() {
    Response response = service("searchservice")
        .post("/searchkeyword/cleanup");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }
  public ResponseApi<GdnBaseRestResponse> debug() {
    Response response = service("searchservice")
        .queryParam("searchKeyword",searchServiceData.getSearchTerm())
        .get("/searchkeyword/debugDetected");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deltaIndex() {
    Response response = service("searchservice")
        .post("/searchkeyword/delta-index");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> mongoClasses() {
    Response response = service("searchservice")
        .post("/mongo/classes");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> mongoQuery() {
    Response response = service("searchservice")
        .queryParam("key",searchServiceData.getKey())
        .queryParam("value",searchServiceData.getMongoValue())
        .queryParam("className",searchServiceData.getClassName())
        .queryParam("queryType",searchServiceData.getQueryType())
        .post("/query/mongo");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }
  public ResponseApi<GdnRestSingleResponse<ProductResponse>> bodyOfRequestToQueryWithProductID(String correctID) {
    String Bodytemplate="{\n" + "  \"value\": [\n" + "    \""+correctID+"\"\n" + "  ]\n" + "}";
    Response response=service("searchservice").body(Bodytemplate).post("/product");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,new TypeReference<GdnRestSingleResponse<ProductResponse>>(){
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteUnpublished() {
    Response response = service("searchservice")
        .get("/scheduled-events/delete-unpublished");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchTheListOfUnpublishedProducts() {
    Response response = service("searchservice")
        .post("/scheduled-events/process-events");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse> prepareRequestForProcessingFailedIds(){
    Response response = service("searchservice")
        .get("/index/failed-ids");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse>() {});
  }

  public ResponseApi<GdnBaseRestResponse> prepareRequestForIndexing(String type,String value){

    String postReq="{\""+type+"\": [\""+value+"\" ]}";
    Response response = service("searchservice")
        .body(postReq)
        .post("/index/product");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {});
  }

  public ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> prepareRequestForListingServicesForReindexing(){
    Response response = service("searchservice")
        .get("/index/services");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SimpleStringResponse>>() {});
  }


  public ResponseApi<GdnBaseRestResponse> prepareRequestForReviewAndRatingIndex(){
    Response response = service("searchservice")
        .post("/update/review-and-rating");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {});

  }
}
