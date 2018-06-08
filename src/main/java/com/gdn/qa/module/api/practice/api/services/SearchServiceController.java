package com.gdn.qa.module.api.practice.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.json.JsonApi;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.automation.core.restassured.ServiceApi;
import com.gdn.qa.automation.core.template.TemplateApi;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.x.search.rest.web.model.*;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component
public class SearchServiceController extends ServiceApi {
  @Autowired
  JsonApi jsonApi;

  @Autowired
  TemplateApi templateAPI;

  @Autowired
  SearchServiceData searchserviceData;

  public ResponseApi<GdnBaseRestResponse> BodyofDeleteConfigRequest() {
    String bodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoid());
    data.put("name", searchserviceData.getName());

    String bodyRequest = templateAPI.createFromString(bodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").body(bodyRequest).post("/config/delete");

    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyofRequestWithWrongID() {
    String bodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getWrongid());
    data.put("name", searchserviceData.getName());

    String bodyRequest = templateAPI.createFromString(bodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice")
        //.queryParam("username","testuser")
        .body(bodyRequest).post("/config/delete");

    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }


  public ResponseApi<GdnBaseRestResponse> BodyOfRequest() {
    String Bodytemplate = "{\n" + "  \"name\": \"{{name}}\",\n" + "  \"label\": \"{{label}}\",\n"
        + "  \"value\": \"{{value}}\"\n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("name", searchserviceData.getName());
    data.put("label", searchserviceData.getLabel());
    data.put("value", searchserviceData.getValue());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").body(bodyRequest).post("/config/save");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> BodyOfRequestwithEmptyBody() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getEmptyid());
    data.put("name", searchserviceData.getEmptyname());


    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").body(bodyRequest).post("/config/save");
    response.getBody().prettyPrint();

    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> BodyOfRequestToUpdateTheConfig() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"name\": \"{{name}}\",\n"
        + "  \"label\": \"{{label}}\",\n" + "  \"value\": \"{{value}}\"\n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoid());
    data.put("name", searchserviceData.getName());
    data.put("label", searchserviceData.getLabel());
    data.put("value", searchserviceData.getUpdatedValue());

    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").body(bodyRequest).post("/config/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnBaseRestResponse> BodyOfRequestToUpdateTheConfigWithEmptyBody() {
    String Bodytemplate =
        "{\n" + " \n" + "\"id\":\"{{id}}\",\n" + "  \"name\": \"{{name}}\"\n" + " \n" + "}";

    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getEmptyid());
    data.put("name", searchserviceData.getEmptyname());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").body(bodyRequest).post("/config/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> FindByNameResponse() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("name", searchserviceData.getName())
        .get("/config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> FindByWrongNameResponse() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("name", searchserviceData.getWrongname())
        .get("/config/find-by-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> SetFindByIDResponse() {
    System.out.println("--------------------------ID PASSED IS----------------------------------"
        + searchserviceData.getAutoid());
    Response response = service("searchservice").queryParam("id", searchserviceData.getAutoid())
        .get("/config/find-by-id");

    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> SetFindByWrongIDResponse() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getWrongid())
        .get("/config/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> FindByWordResponse() {
    Response response = service("searchservice").queryParam("word", searchserviceData.getWord())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .log()
        .all()
        .get("/config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ConfigResponse>> FindByWrongWordResponse() {
    Response response =
        service("searchservice").queryParam("word", searchserviceData.getWrongname())
            .queryParam("page", searchserviceData.getPage())
            .queryParam("size", searchserviceData.getSize())
            .log()
            .all()
            .get("/config/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<ConfigResponse>> ConfigListResponse() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/config/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<ConfigResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataFromBRS() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("username", searchserviceData.getUsername())
        .post("/clickthrough/fetch-data");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> fetchClickthroughDataAndStoreIntoRedis() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("username", searchserviceData.getUsername())
        .post("/clickthrough/store-click-through-and-category-redis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichHasIntentMining() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("searchTerm", searchserviceData.getSearchTerm())
        .post("/categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> deleteSearchTermWhichDoesNotIntentMining() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("searchTerm", searchserviceData.getSearchTermNotPresent())
        .post("/categoryIntent/deleteCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichHasIntentMining() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("word", searchserviceData.getSearchTerm())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findSearchTermWhichDoesNotIntentMining() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("word", searchserviceData.getSearchTermNotPresent())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/categoryIntent/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTerm() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("searchTerm", searchserviceData.getSearchTerm())
        .get("/categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findCategoryIdForSearchTermNotPresent() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("searchTerm", searchserviceData.getSearchTermNotPresent())
        .get("/categoryIntent/getCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<CategoryIntentResponse>> findlistOfSearchTerms() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/categoryIntent/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<CategoryIntentResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> saveSearchTermWhichHasIntentMining() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("searchTerm", searchserviceData.getSearchTerm())
        .queryParam("categoryId", searchserviceData.getCategoryId())
        .queryParam("turnedOn", searchserviceData.getTurnedOn())
        .post("/categoryIntent/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> UpdateSearchTermWhichHasIntentMiningIntoRedis() {
    Response response = service("searchservice").log().all().post("/categoryIntent/updateInRedis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateCategoryID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("categoryId", searchserviceData.getCategoryId())
        .post("/categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> validateInValidCategoryID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("categoryId", searchserviceData.getWrongcategoryId())
        .post("/categoryIntent/validateCategory");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>> FetchListOfSearchTermsWhichHasImFromRedis() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("page", searchserviceData.getPagenumberForIMlist())
        .get("/clickthrough/get-click-through-and-category-redis");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfFeedExclusionSaveRequest() {
    String Bodytemplate = "{\n" + "  \"key\": \"{{key}}\",\n" + "  \"value\": \"{{value}}\",\n"
        + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("key", searchserviceData.getFeedKey());
    data.put("value", searchserviceData.getFeedValue());
    data.put("feedType", searchserviceData.getFeedType());
    //  data.put("positiveType",searchserviceData.getPositiveFilter());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").body(bodyRequest).post("/feed-exclusion-entity/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });

  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWord() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("word", searchserviceData.getFeedValue())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedByWrongWord() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("word", searchserviceData.getWrongword())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/feed-exclusion-entity/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getAutoFeedId())
        .get("/feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByWrongID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getWrongid())
        .get("/feed-exclusion-entity/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> ListAllFeedExclusions() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/feed-exclusion-entity/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestListResponse<FeedExclusionEntityResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfFeedExclusionUpdateRequest() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoFeedId());
    data.put("key", searchserviceData.getFeedKey());
    data.put("value", searchserviceData.getUpdateFeedValue());
    data.put("feedType", searchserviceData.getFeedType());
    //   data.put("positiveType",searchserviceData.getPositiveFilter());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").log()
        .all()
        .body(bodyRequest)
        .post("/feed-exclusion-entity/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfFeedExclusionUpdateRequestWithWrongID() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getWrongid());
    data.put("key", searchserviceData.getFeedKey());
    data.put("value", searchserviceData.getFeedValue());
    data.put("feedType", searchserviceData.getFeedType());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").log()
        .all()
        .body(bodyRequest)
        .post("/feed-exclusion-entity/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfFeedExclusionDeleteRequest() {
    String Bodytemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"key\": \"{{key}}\",\n"
        + "  \"value\": \"{{value}}\",\n" + "  \"feedType\": \"{{feedType}}\"\n" + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoFeedId());
    data.put("key", searchserviceData.getFeedKey());
    data.put("value", searchserviceData.getUpdateFeedValue());
    data.put("feedType", searchserviceData.getFeedType());
    String bodyRequest = templateAPI.createFromString(Bodytemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response = service("searchservice").log()
        .all()
        .body(bodyRequest)
        .post("/feed-exclusion-entity/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfSaveKeyword() {
    String BodyTemplate = "{\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("keyword", searchserviceData.getKeyword());
    data.put("negativeKeyword", searchserviceData.getNegativeKeyword());
    data.put("categoryProductId", searchserviceData.getCategoryProductId());
    data.put("categoryProductName", searchserviceData.getCategoryProductName());
    data.put("type", searchserviceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").log().all().body(bodyRequest).post("/keyword/save");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfUpdateExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoKeywordId());
    data.put("keyword", searchserviceData.getKeyword());
    data.put("negativeKeyword", searchserviceData.getUpdatedNegativeKeyword());
    data.put("categoryProductId", searchserviceData.getCategoryProductId());
    data.put("categoryProductName", searchserviceData.getCategoryProductName());
    data.put("type", searchserviceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").log().all().body(bodyRequest).post("/keyword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfUpdateNonExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getWrongid());
    data.put("keyword", searchserviceData.getKeyword());
    data.put("negativeKeyword", searchserviceData.getUpdatedNegativeKeyword());
    data.put("categoryProductId", searchserviceData.getCategoryProductId());
    data.put("categoryProductName", searchserviceData.getCategoryProductName());
    data.put("type", searchserviceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").log().all().body(bodyRequest).post("/keyword/update");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> FindListOfKeywords() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/keyword/list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> FindByKeywordRequest() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("keyword", searchserviceData.getKeyword())
        .get("/keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> FindByNonExistingKeywordRequest() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("keyword", searchserviceData.getWrongword())
        .get("/keyword/find-by-keyword");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> FindKeywordRequestByID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getAutoKeywordId())
        .get("/keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> FindSynonymRequestByID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getAutoSynonymnId())
        .get("/synonyms/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }


  public ResponseApi<GdnRestSingleResponse<KeywordResponse>> FindKeywordRequestByWrongID() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("id", searchserviceData.getWrongid())
        .get("/keyword/find-by-id");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> FindKeywordsByDate() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("currentDate", searchserviceData.getAutoUpdatedDate())
        .queryParam("word", searchserviceData.getCategoryProductName())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> FindKeywordsByWrongDate() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("currentDate", searchserviceData.getWrongdate())
        .queryParam("word", searchserviceData.getKeywordForsearchingWithDateStamp())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/keyword/page");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> FindIfProductExists() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("productId", searchserviceData.getCategoryProductId())
        .get("/keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> findForNonExistingProduct() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("productId", searchserviceData.getWrongcategoryId())
        .get("/keyword/product-exist");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> FindListOfKeywordsWithKeyValuePair() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("index", searchserviceData.getIndex())
        .queryParam("word", searchserviceData.getCategoryProductId())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestListResponse<KeywordResponse>> FindListOfKeywordsWithWrongInput() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("index", searchserviceData.getCategoryProductName())
        .queryParam("word", searchserviceData.getCategoryProductId())
        .queryParam("page", searchserviceData.getPage())
        .queryParam("size", searchserviceData.getSize())
        .get("/keyword/find");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestListResponse<KeywordResponse>>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> ValidateIdAndGetName() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("categoryProductId", searchserviceData.getCategoryProductId())
        .queryParam("type", searchserviceData.getType())
        .get("/keyword/validate-id-and-get-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<ValidateIdAndGetNameResponse>>() {
        });
  }

  public ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> ValidateNonExistingIdAndGetName() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("categoryProductId", searchserviceData.getWrongid())
        .queryParam("type", searchserviceData.getType())
        .get("/keyword/validate-id-and-get-name");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response,
        new TypeReference<GdnRestSingleResponse<ValidateIdAndGetNameResponse>>() {
        });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfDeleteKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getAutoKeywordId());
    data.put("keyword", searchserviceData.getKeyword());
    data.put("negativeKeyword", searchserviceData.getNegativeKeyword());
    data.put("categoryProductId", searchserviceData.getCategoryProductId());
    data.put("categoryProductName", searchserviceData.getCategoryProductName());
    data.put("type", searchserviceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").log().all().body(bodyRequest).post("/keyword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> BodyOfDeleteNonExistingKeyword() {
    String BodyTemplate = "{\n" + "  \"id\": \"{{id}}\",\n" + "  \"keyword\": \"{{keyword}}\",\n"
        + "  \"negativeKeyword\": \"{{negativeKeyword}}\",\n"
        + "  \"categoryProductId\": \"{{categoryProductId}}\",\n"
        + "  \"categoryProductName\": \"{{categoryProductName}}\",\n" + "  \"type\": \"{{type}}\"\n"
        + "}";
    HashMap<String, String> data = new HashMap<>();
    data.put("id", searchserviceData.getWrongid());
    data.put("keyword", searchserviceData.getKeyword());
    data.put("negativeKeyword", searchserviceData.getNegativeKeyword());
    data.put("categoryProductId", searchserviceData.getCategoryProductId());
    data.put("categoryProductName", searchserviceData.getCategoryProductName());
    data.put("type", searchserviceData.getType());
    String bodyRequest = templateAPI.createFromString(BodyTemplate, data);
    System.out.println(
        "___________________________BODYREQUEST___________________________" + bodyRequest);
    Response response =
        service("searchservice").log().all().body(bodyRequest).post("/keyword/delete");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getCategoryList() {
    Response response = service("searchservice").log()
        .all()
        .post("/fetch-category-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getMerchantList() {
    Response response = service("searchservice").log()
        .all()
        .post("/fetch-merchant-list");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> getProductList() {
    Response response = service("searchservice").log()
        .all()
        .queryParam("category",searchserviceData.getCategoryId())
        .queryParam("merchant",searchserviceData.getMerchant())
        .post("/product-data-report");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> uploadKeyword() {
    Response response = service("searchservice").removeHeader("content-type").log()
        .all()
        .queryParam("email",searchserviceData.getEmail())
        .header("content-type","multipart/data")
        .log().all()
        .queryParam("email",searchserviceData.getEmail())
        .multiPart(new File(searchserviceData.getPath())).post("/keyword/upload");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnBaseRestResponse> generateSynonyms() {
    Response response = service("searchservice").log()
        .all()
        .get("/synonyms/create-synonyms");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnBaseRestResponse>() {
    });
  }

  public ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findByKey(){
    Response response = service("searchservice").log()
        .all()
        .queryParam("key",searchserviceData.getSearchTerm())
        .get("/synonyms/find-by-key");
    response.getBody().prettyPrint();
    return jsonApi.fromJson(response, new TypeReference<GdnRestSingleResponse<SynonymsResponse>>() {
    });
  }

}
