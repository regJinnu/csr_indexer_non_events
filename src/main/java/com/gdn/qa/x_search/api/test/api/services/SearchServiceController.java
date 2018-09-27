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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.FILE_PATH;

@Component
public class SearchServiceController extends ServiceApi {
  @Autowired
  private JsonApi jsonApi;

  @Autowired
  private TemplateApi templateAPI;

  @Autowired
  private SearchServiceData searchServiceData;

  private static final String BASEPATH = FILE_PATH;

  public ResponseApi<GdnBaseRestResponse> bodyofDeleteConfigRequest() {
    String bodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoid());
    data.put("name", searchServiceData.getName());

    String bodyRequest = templateAPI.createFromString(bodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "config/delete");

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
        .body(bodyRequest).post(BASEPATH + "config/delete");

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
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "config/save");
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
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "config/save");
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
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "config/update");
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
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "config/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByNameResponse() {
    Response response = service("searchservice").queryParam("name", searchServiceData.getName())
        .get(BASEPATH + "config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWrongNameResponse() {
    Response response =
        service("searchservice").queryParam("name", searchServiceData.getWrongname())
            .get(BASEPATH + "config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> setFindByIDResponse() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getAutoid())
        .get(BASEPATH + "config/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> setFindByWrongIDResponse() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "config/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWordResponse() {
    Response response = service("searchservice").queryParam("word", searchServiceData.getWord())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByWrongWordResponse() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongname())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<ConfigResponse>> configListResponse() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "config/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataFromBRS() {
    Response response =
        service("searchservice").queryParam("username", searchServiceData.getUsername())
            .post(BASEPATH + "clickthrough/fetch-data");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataAndStoreIntoRedis() {
    Response response =
        service("searchservice").queryParam("username", searchServiceData.getUsername())
            .post(BASEPATH + "clickthrough/store-click-through-and-category-redis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichHasIntentMining() {
    Response response =
        service("searchservice").queryParam("searchTerm", searchServiceData.getSearchTerm())
            .post(BASEPATH + "categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichDoesNotIntentMining() {
    Response response = service("searchservice").queryParam("searchTerm",
        searchServiceData.getSearchTermNotPresent())
        .post(BASEPATH + "categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichHasIntentMining() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getSearchTerm())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichDoesNotIntentMining() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getSearchTermNotPresent())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTerm() {
    Response response =
        service("searchservice").queryParam("searchTerm", searchServiceData.getSearchTerm())
            .get(BASEPATH + "categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTermNotPresent() {
    Response response = service("searchservice").queryParam("searchTerm",
        searchServiceData.getSearchTermNotPresent()).get(BASEPATH + "categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<CategoryIntentResponse>> findlistOfSearchTerms() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "categoryIntent/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> saveSearchTermWhichHasIntentMining() {
    Response response =
        service("searchservice").queryParam("searchTerm", searchServiceData.getSearchTerm())
            .queryParam("categoryId", searchServiceData.getCategoryId())
            .queryParam("turnedOn", searchServiceData.getTurnedOn())
            .post(BASEPATH + "categoryIntent/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateSearchTermWhichHasIntentMiningIntoRedis() {
    Response response = service("searchservice").post(BASEPATH + "categoryIntent/updateInRedis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateCategoryID() {
    Response response =
        service("searchservice").queryParam("categoryId", searchServiceData.getCategoryId())
            .post(BASEPATH + "categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateInValidCategoryID() {
    Response response =
        service("searchservice").queryParam("categoryId", searchServiceData.getWrongcategoryId())
            .post(BASEPATH + "categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>> fetchListOfSearchTermsWhichHasImFromRedis() {
    Response response =
        service("searchservice").queryParam("page", searchServiceData.getPagenumberForIMlist())
            .get(BASEPATH + "clickthrough/get-click-through-and-category-redis");
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
        service("searchservice").body(bodyRequest).post(BASEPATH + "feed-exclusion-entity/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWord() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getFeedValue())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWrongWord() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongword())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getAutoFeedId())
        .get(BASEPATH + "feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByWrongID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> listAllFeedExclusions() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "feed-exclusion-entity/list");
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "feed-exclusion-entity/update");
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "feed-exclusion-entity/update");
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "feed-exclusion-entity/delete");
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
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "keyword/save");
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
        service("searchservice").body(bodyRequest).post(BASEPATH + "keyword/update");
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
        service("searchservice").body(bodyRequest).post(BASEPATH + "keyword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywords() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "keyword/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findByKeywordRequest() {
    Response response =
        service("searchservice").queryParam("keyword", searchServiceData.getKeyword())
            .get(BASEPATH + "keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findByNonExistingKeywordRequest() {
    Response response =
        service("searchservice").queryParam("keyword", searchServiceData.getWrongword())
            .get(BASEPATH + "keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findKeywordRequestByID() {
    Response response =
        service("searchservice").queryParam("id", searchServiceData.getAutoKeywordId())
            .get(BASEPATH + "keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findSynonymRequestByID() {
    Response response =
        service("searchservice").queryParam("id", searchServiceData.getAutoSynonymnId())
            .get(BASEPATH + "synonyms/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findSynonymRequestByWrongID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "synonyms/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }


  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> findKeywordRequestByWrongID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findKeywordsByDate() {
    Response response =
        service("searchservice").queryParam("currentDate", searchServiceData.getAutoUpdatedDate())
            .queryParam("word", searchServiceData.getCategoryProductName())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findKeywordsByWrongDate() {
    Response response =
        service("searchservice").queryParam("currentDate", searchServiceData.getWrongdate())
            .queryParam("word", searchServiceData.getKeywordForsearchingWithDateStamp())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findIfProductExists() {
    Response response =
        service("searchservice").queryParam("productId", searchServiceData.getCategoryProductId())
            .get(BASEPATH + "keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findForNonExistingProduct() {
    Response response =
        service("searchservice").queryParam("productId", searchServiceData.getWrongcategoryId())
            .get(BASEPATH + "keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywordsWithKeyValuePair() {
    Response response = service("searchservice").queryParam("index", searchServiceData.getIndex())
        .queryParam("word", searchServiceData.getCategoryProductId())
        .queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> findListOfKeywordsWithWrongInput() {
    Response response =
        service("searchservice").queryParam("index", searchServiceData.getCategoryProductName())
            .queryParam("word", searchServiceData.getCategoryProductId())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> validateIdAndGetName() {
    Response response =
        service("searchservice").queryParam("categoryProductId", searchServiceData.getValidate())
            .queryParam("type", searchServiceData.getType())
            .get(BASEPATH + "keyword/validate-id-and-get-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<ValidateIdAndGetNameResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> validateNonExistingIdAndGetName() {
    Response response =
        service("searchservice").queryParam("categoryProductId", searchServiceData.getWrongid())
            .queryParam("type", searchServiceData.getType())
            .get(BASEPATH + "keyword/validate-id-and-get-name");
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
        service("searchservice").body(bodyRequest).post(BASEPATH + "keyword/delete");
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
        service("searchservice").body(bodyRequest).post(BASEPATH + "keyword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getCategoryList() {
    Response response = service("searchservice").post(BASEPATH + "fetch-category-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getMerchantList() {
    Response response = service("searchservice").post(BASEPATH + "fetch-merchant-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getProductList() {
    Response response =
        service("searchservice").queryParam("category", searchServiceData.getCategoryId())
            .queryParam("merchant", searchServiceData.getMerchant())
            .post(BASEPATH + "product-data-report");
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
        .post(BASEPATH + "keyword/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> generateSynonyms() {
    Response response = service("searchservice").get(BASEPATH + "synonyms/create-synonyms");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findByKey() {
    Response response =
        service("searchservice").queryParam("key", searchServiceData.getSearchTerm())
            .get(BASEPATH + "synonyms/find-by-key");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findByWrongKey() {
    Response response = service("searchservice").queryParam("key", searchServiceData.getWrongname())
        .get(BASEPATH + "synonyms/find-by-key");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> findSynonymRequestByWord() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getSearchTerm())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .queryParam("status", searchServiceData.getPagenumberForIMlist())
            .get(BASEPATH + "synonyms/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> findSynonymRequestByWrongWord() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongword())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .queryParam("status", searchServiceData.getPagenumberForIMlist())
            .get(BASEPATH + "synonyms/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SynonymsResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SynonymsResponse>> listSynonyms() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .queryParam("status", searchServiceData.getPagenumberForIMlist())
        .get(BASEPATH + "synonyms/list");
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
    data.put("synonyms", searchServiceData.getSynonyms());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "synonyms/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSynonymFromSolr() {
    Response response =
        service("searchservice").queryParam("key", searchServiceData.getSearchTerm())
            .delete(BASEPATH + "integration/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateSynonymFromSolr() {
    Response response =
        service("searchservice").queryParam("updateAll", searchServiceData.getWrongword())
            .post(BASEPATH + "integration/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfSaveStopword() {
    String BodyTemplate =
        "{\n" + "  \"stopWord\": \"{{stopWord}}\",\n" + "  \"groupName\": \"{{groupName}}\",\n"
            + "  \"sync\": \"{{sync}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getSearchTerm());
    data.put("groupName", searchServiceData.getStopwordgroup());
    data.put("sync", searchServiceData.getSync());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest).post(BASEPATH + "stopword/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> findStopwordByword() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getSearchTerm())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "stopword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> findStopwordByWrongword() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongword())
            .queryParam("page", searchServiceData.getPage())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "stopword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<StopWordResponse>> findStopwordByID() {
    Response response =
        service("searchservice").queryParam("id", searchServiceData.getAutoStopwordID())
            .get(BASEPATH + "stopword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<StopWordResponse>> findStopwordByWrongID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "stopword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<StopWordResponse>> listStopword() {
    Response response = service("searchservice").queryParam("page", searchServiceData.getPage())
        .queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "stopword/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<StopWordResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateStopword() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id", searchServiceData.getAutoStopwordID());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfUpdateStopwordByWrongID() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id", searchServiceData.getWrongid());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfDeleteStopword() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"stopWord\": \"{{stopWord}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("stopWord", searchServiceData.getUpdateFeedValue());
    data.put("id", searchServiceData.getAutoStopwordID());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "stopword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteStopwordIntegration() {
    Response response =
        service("searchservice").queryParam("key", searchServiceData.getSearchTerm())
            .delete(BASEPATH + "integration/stopword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateStopwordFromSolr() {
    Response response =
        service("searchservice").queryParam("updateAll", searchServiceData.getWrongword())
            .post(BASEPATH + "integration/stopword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SetConfigResponse>> setConfigRequest() {
    Response response =
        service("searchservice").queryParam("name", searchServiceData.getSetConfig())
            .get(BASEPATH + "fetchSaveConfig");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<SetConfigResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> updateFieldCache() {
    Response response =
        service("searchservice").queryParam("fieldName", searchServiceData.getFieldName())
            .get(BASEPATH + "updateFieldCache");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateNonExistingFieldCache() {
    Response response =
        service("searchservice").queryParam("fieldName", searchServiceData.getWrongname())
            .get(BASEPATH + "updateFieldCache");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> findBoostedKeyword() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getSearchTerm())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "keywordBoost/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordBoostProductResponse>> findBoostedKeywordByID() {
    Response response =
        service("searchservice").queryParam("id", searchServiceData.getAutoBoostedKeywordID())
            .get(BASEPATH + "keywordBoost/findById");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<KeywordBoostProductResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> listBoostedKeyword() {
    Response response = service("searchservice").queryParam("size", searchServiceData.getSize())
        .get(BASEPATH + "keywordBoost/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "keywordBoost/update");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> listAllBoostedKeyword() {
    Response response = service("searchservice").get(BASEPATH + "keywordBoost/getAll");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> uploadBoostedKeyword() {
    Response response = service("searchservice").removeHeader("content-type")
        .header("content-type", "multipart/data")
        .queryParam("email", searchServiceData.getEmail())
        .multiPart(new File(searchServiceData.getPathForBoostedKeyword()))
        .post(BASEPATH + "keywordBoost/upload");
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "keywordBoost/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse> bodyOfRequestOfValidateID(String incorrectID,
      String correctID) {
    String Bodytemplate = "[\"" + correctID + "\",\"" + incorrectID + "\"]";
    Response response =
        service("searchservice").body(Bodytemplate).post(BASEPATH + "keywordBoost/validate");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> bodyOfRequestOfMultiDelete() throws Exception {
    String Bodytemplate =
        "[\n" + "  {\n" + "    \"id\": \"98765\"\n" + "   }, {\n" + "\"id\": \"43210\"\n" + "}\n"
            + "]";
    Response response =
        service("searchservice").body(Bodytemplate).post(BASEPATH + "keywordBoost/multidelete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> findWrongBoostedKeyword() {
    Response response =
        service("searchservice").queryParam("word", searchServiceData.getWrongname())
            .queryParam("size", searchServiceData.getSize())
            .get(BASEPATH + "keywordBoost/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<KeywordBoostProductResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordBoostProductResponse>> findBoostedKeywordByWrongID() {
    Response response = service("searchservice").queryParam("id", searchServiceData.getWrongid())
        .get(BASEPATH + "keywordBoost/findById");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<KeywordBoostProductResponse>>() {
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
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "keywordBoost/update");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> uploadWrongFile() {
    Response response = service("searchservice").removeHeader("content-type")
        .header("content-type", "multipart/data")
        .queryParam("email", searchServiceData.getEmail())
        .multiPart(new File(searchServiceData.getWrongFile()))
        .post(BASEPATH + "keywordBoost/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> cleanUp() {
    Response response = service("searchservice").post(BASEPATH + "searchkeyword/cleanup");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> debug() {
    Response response =
        service("searchservice").queryParam("searchKeyword", searchServiceData.getSearchTerm())
            .get(BASEPATH + "searchkeyword/debugDetected");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deltaIndex() {
    Response response = service("searchservice").post(BASEPATH + "searchkeyword/delta-index");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> mongoClasses() {
    Response response = service("searchservice").post(BASEPATH + "mongo/classes");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }


  public ResponseApi<GdnBaseRestResponse> mongoQuery() {
    Response response = service("searchservice").queryParam("key", searchServiceData.getKey())
        .queryParam("value", searchServiceData.getMongoValue())
        .queryParam("className", searchServiceData.getClassName())
        .queryParam("queryType", searchServiceData.getQueryType())
        .post(BASEPATH + "query/mongo");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ProductResponse>> bodyOfRequestToQueryWithProductID(
      String correctID) {
    String Bodytemplate =
        "{\n" + "  \"value\": [\n" + "    \"" + correctID + "\"\n" + "  ]\n" + "}";
    Response response = service("searchservice").body(Bodytemplate).post(BASEPATH + "product");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ProductResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteUnpublished() {
    Response response =
        service("searchservice").get(BASEPATH + "scheduled-events/delete-unpublished");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchTheListOfUnpublishedProducts() {
    Response response = service("searchservice").post(BASEPATH + "scheduled-events/process-events");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse> prepareRequestForProcessingFailedIds() {
    Response response = service("searchservice").get(BASEPATH + "index/failed-ids");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> prepareRequestForIndexing(String type, String value) {

    String postReq = "{\"" + type + "\": [\"" + value + "\" ]}";
    Response response = service("searchservice").body(postReq).post(BASEPATH + "index/product");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> prepareRequestForListingServicesForReindexing() {
    Response response = service("searchservice").get(BASEPATH + "index/services");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<SimpleStringResponse>>() {
        });
  }


  public ResponseApi<GdnBaseRestResponse> prepareRequestForReviewAndRatingIndex() {
    Response response = service("searchservice").post(BASEPATH + "update/review-and-rating");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> prepareRequestForCategoryReindex(String categoryCode) {

    String requestJson = "{\n" + "\"categoryCodes\": [ \"" + categoryCode + "\"]\n" + "}";

    Response response =
        service("searchservice").body(requestJson).post(BASEPATH + "index/category");

    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> prepareRequestForFullReindexing(String solrToSolr,
      String serviceName) {
    Response response = service("searchservice").queryParam("solr", solrToSolr)
        .queryParam("services", serviceName)
        .post(BASEPATH + "index/product-revamp");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> prepareRequestForProcessingStoredDelta() {
    Response response =
        service("searchservice").get(BASEPATH + "index/process-delta-stored-events");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<SimpleStringResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<StatusReIndexResponse>> prepareRequestForGettingIndexingStatus() {
    Response response = service("searchservice").get(BASEPATH + "index/status");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<StatusReIndexResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> prepareRequestForAtomicReindexQueue() {
    Response response = service("searchservice").get("/api/atomic/reindex");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<FlightResponse>> allFlights() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .get(BASEPATH + "intent-rules/flight/get-all");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<FlightResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<PlaceholderRuleResponse>> getAllPlaceholder() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .get(BASEPATH + "intent-rules/placeholder-rule/get-all");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<PlaceholderRuleResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> addTrainMapping(
      String trainSearchTerm,
      String trainMapping) {
    String BodyTemplate =
        "{\n" +"  \"searchTerm\": \"" + trainSearchTerm + "\",\n"
            + "  \"effectiveValue\": \"" + trainMapping + "\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("searchTerm", trainSearchTerm);
    data.put("effectiveValue", trainMapping);
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "intent-rules/train/add");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<TrainResponse>> getAllTrain() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .get(BASEPATH + "intent-rules/train/get-all");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<TrainResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteTrainMapping() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getAutoTrainId())
            .delete(BASEPATH + "intent-rules/train/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteTrainMappingWithWrongId() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getWrongid())
            .delete(BASEPATH + "intent-rules/train/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteFlightMapping() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getAutoFlightId())
            .delete(BASEPATH + "intent-rules/flight/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteFlightWithWrongId() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getWrongid())
            .delete(BASEPATH + "intent-rules/flight/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deletePlaceholderWithWrongId() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getWrongid())
            .delete(BASEPATH + "intent-rules/placeholder-rule/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deletePlaceholder() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getAutoPlaceholderId())
            .delete(BASEPATH + "intent-rules/placeholder-rule/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> saveFlight(
      String trainSearchTerm,
      String trainMapping) {
    String BodyTemplate =
        "{\n"+ " \"searchTerm\": \"" + trainSearchTerm + "\",\n"
            + "  \"effectiveValue\": \"" + trainMapping + "\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("searchTerm", trainSearchTerm);
    data.put("effectiveValue", trainMapping);
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "intent-rules/flight/add");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> addPlaceholderRules(
      String name,
      String searchTerm,
      String effectiveSearchPattern,
      String type) {
    String BodyTemplate = "{\n" + "  \"name\": \"" + name + "\",\n"
        + "  \"searchTerm\": \"" + searchTerm + "\",\n" + "  \"effectiveSearchPattern\": \""
        + effectiveSearchPattern + "\",\n" + "  \"type\": \"" + type + "\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("name", name);
    data.put("searchTerm", searchTerm);
    data.put("effectiveSearchPattern", effectiveSearchPattern);
    data.put("type", type);
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest)
        .post(BASEPATH + "intent-rules/placeholder-rule/add");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updatePlaceholder() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\",\n"
        + "  \"searchTerm\": \"{{searchTerm}}\",\n"
        + "  \"effectiveSearchPattern\": \"{{effectiveSearchPattern}}\",\n"
        + "  \"type\": \"{{type}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoPlaceholderId());
    data.put("name", searchServiceData.getName());
    data.put("searchTerm", searchServiceData.getSearchTerm());
    data.put("effectiveSearchPattern", searchServiceData.getEffectiveSearchPattern());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest)
        .put(BASEPATH + "intent-rules/placeholder-rule/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updatePlaceholderWithNonExistingId() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\",\n"
        + "  \"searchTerm\": \"{{searchTerm}}\",\n"
        + "  \"effectiveSearchPattern\": \"{{effectiveSearchPattern}}\",\n"
        + "  \"type\": \"{{type}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getWrongid());
    data.put("name", searchServiceData.getName());
    data.put("searchTerm", searchServiceData.getSearchTerm());
    data.put("effectiveSearchPattern", searchServiceData.getEffectiveSearchPattern());
    data.put("type", searchServiceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest)
        .put(BASEPATH + "intent-rules/placeholder-rule/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> addSearchRule() {
    String BodyTemplate =
        "{\n" + "  \"searchTerm\": \"{{searchTerm}}\",\n"
            + "  \"filterQuery\": \"{{filterQuery}}\",\n" + "  \"sortType\": \"{{sortType}}\",\n"
            + "  \"effectiveSearchPattern\": \"{{effectiveSearchPattern}}\",\n"
            + "  \"defaultLogic\": {\n" + "    \"filterQuery\": \"{{filterQuery}}\",\n"
            + "    \"sortType\": \"{{sortType}}\"\n" + "  },\n" + "  \"url\": \"{{url}}\",\n"
            + "  \"type\": \"{{type}}\",\n" + "  \"rank\": {{rank}},\n"
            + "  \"spel\": \"{{spel}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("searchTerm", searchServiceData.getSearchRulSearchTerm());
    data.put("filterQuery", searchServiceData.getFilterQuery());
    data.put("sortType", searchServiceData.getSortType());
    data.put("effectiveSearchPattern", searchServiceData.getEffectiveSearchPattern());
    data.put("defaultLogic.filterQuery", searchServiceData.getFilterQuery());
    data.put("defaultLogic.sortType", searchServiceData.getSortType());
    data.put("url", searchServiceData.getUrl());
    data.put("type", searchServiceData.getType());
    data.put("rank", String.valueOf(searchServiceData.getRank()));
    data.put("spel", searchServiceData.getSpel());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response =
        service("searchservice").body(bodyRequest).post(BASEPATH + "intent-rules/search-rule/add");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> rerankSearchRule() {
    String BodyTemplate =
        "[\n" + "  {\n" + "    \"id\": \"{{id}}\",\n" + "    \"searchTerm\": \"{{searchTerm}}\",\n"
            + "    \"filterQuery\": \"{{filterQuery}}\",\n"
            + "    \"sortType\": \"{{sortType}}\",\n"
            + "    \"effectiveSearchPattern\": \"{{effectiveSearchPattern}}\",\n"
            + "    \"defaultLogic\": {\n" + "      \"filterQuery\": \"{{filterQuery}}\",\n"
            + "      \"sortType\": \"{{sortType}}\"\n" + "    },\n" + "    \"url\": \"{{url}}\",\n"
            + "    \"type\": \"{{type}}\",\n" + "    \"rank\": {{rank}},\n"
            + "    \"spel\": \"{{spel}}\"\n" + "  }\n" + "]";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoSearchId());
    data.put("searchTerm", searchServiceData.getSearchRulSearchTerm());
    data.put("filterQuery", searchServiceData.getFilterQuery());
    data.put("sortType", searchServiceData.getSortType());
    data.put("effectiveSearchPattern", searchServiceData.getEffectiveSearchPattern());
    data.put("defaultLogic.filterQuery", searchServiceData.getFilterQuery());
    data.put("defaultLogic.sortType", searchServiceData.getSortType());
    data.put("url", searchServiceData.getUrl());
    data.put("type", searchServiceData.getType());
    data.put("rank", String.valueOf(searchServiceData.getRank()));
    data.put("spel", searchServiceData.getSpel());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest)
        .post(BASEPATH + "intent-rules/search-rule/rerank");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> updateSearchRule() {
    String BodyTemplate =
        "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"searchTerm\": \"{{searchTerm}}\",\n"
            + "  \"filterQuery\": \"{{filterQuery}}\",\n" + "  \"sortType\": \"{{sortType}}\",\n"
            + "  \"effectiveSearchPattern\": \"{{effectiveSearchPattern}}\",\n"
            + "  \"defaultLogic\": {\n" + "    \"filterQuery\": \"{{filterQuery}}\",\n"
            + "    \"sortType\": \"{{sortType}}\"\n" + "  },\n" + "  \"url\": \"{{url}}\",\n"
            + "  \"type\": \"{{type}}\",\n" + "  \"rank\": {{rank}},\n"
            + "  \"spel\": \"{{spel}}\"\n" + "}";
    Map<String, String> data = new HashMap<>();
    data.put("id", searchServiceData.getAutoSearchId());
    data.put("searchTerm", searchServiceData.getName());
    data.put("filterQuery", searchServiceData.getFilterQuery());
    data.put("sortType", searchServiceData.getSortType());
    data.put("effectiveSearchPattern", searchServiceData.getEffectiveSearchPattern());
    data.put("defaultLogic.filterQuery", searchServiceData.getFilterQuery());
    data.put("defaultLogic.sortType", searchServiceData.getSortType());
    data.put("url", searchServiceData.getUrl());
    data.put("type", searchServiceData.getType());
    data.put("rank", String.valueOf(searchServiceData.getRank()));
    data.put("spel", searchServiceData.getSpel());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    Response response = service("searchservice").body(bodyRequest)
        .put(BASEPATH + "intent-rules/search-rule/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<SearchRuleResponse>> getAllSearch() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .get(BASEPATH + "intent-rules/search-rule/get-all");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<SearchRuleResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchRule() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getAutoSearchId())
            .delete(BASEPATH + "intent-rules/search-rule/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchRuleWithWrongId() {
    Response response =
        service("searchservice").queryParam("authenticator", searchServiceData.getAuthenticator())
            .queryParam("id", searchServiceData.getWrongid())
            .delete(BASEPATH + "intent-rules/search-rule/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

}