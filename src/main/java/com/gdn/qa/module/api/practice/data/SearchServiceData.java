package com.gdn.qa.module.api.practice.data;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.x.search.rest.web.model.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class SearchServiceData {
  public String updateFeedValue;
  private String username;
  private String id;
  private String name;
  private String value;
  private String label;
  private String word;
  private String page;
  private String size;
  private String updatedValue;
  private String wrongid;
  private String emptyname;
  private String emptyid;
  private String wrongname;
  private String autoid;
  private String wrongword;
  private String searchTerm;
  private String categoryId;
  private Boolean turnedOn;
  private String searchTermNotPresent;
  private String wrongcategoryId;
  private String pagenumberForIMlist;
  private String searchTermToFind;
  private String categoryIdToFind;
  private String feedKey;
  private String feedValue;
  private String feedType;
  private String positiveFilter;
  private String autoFeedId;
  private String keyword;
  private String negativeKeyword;
  private String categoryProductId;
  private String categoryProductName;
  private String type;
  private String autoKeywordId;
  private Date AutoUpdatedDate;
  private String updatedNegativeKeyword;
  private String index;
  private String wrongdate;
  private String email;
  private String path;
  private String merchant;
  private String keywordForsearchingWithDateStamp;
  private String AutoSynonymnId;
  private String mongoURL;
  private String mongoDB;
  private String synonyms;
  private String stopwordgroup;
  private String sync;
  private String autoStopwordID;
  private String setConfig;
  private String fieldName;
  private String autoBoostedKeywordID;
  private String productID;
  private String pathForBoostedKeyword;
  private String validate;
  private String wrongFile;
  private String queryType;
  private String className;
  private String mongoValue;
  private String key;
  private ResponseApi<GdnRestSingleResponse> validateID;
  private ResponseApi<GdnBaseRestResponse> searchServiceResponse;
  private ResponseApi<GdnRestSingleResponse<ConfigResponse>> findByRequest;
  private ResponseApi<GdnRestListResponse<ConfigResponse>> findByListRequest;
  private ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> findByCategoryIdResponse;
  private ResponseApi<GdnRestListResponse<CategoryIntentResponse>> findCategoryIntentList;
  private ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>>
      categoryClickThroughResponse;
  private ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> findFeedRequest;
  private ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> findFeedByIdRequest;
  private ResponseApi<GdnRestListResponse<KeywordResponse>> listOfKeywordsRequest;
  private ResponseApi<GdnRestSingleResponse<KeywordResponse>> findKeywordRequest;
  private ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> validateIdAndGetName;
  private ResponseApi<GdnRestSingleResponse<SynonymsResponse>> findSynonym;
  private ResponseApi<GdnRestListResponse<SynonymsResponse>> findSynonymnByWord;
  private ResponseApi<GdnRestListResponse<StopWordResponse>> findStopword;
  private ResponseApi<GdnRestSingleResponse<StopWordResponse>> findStopWordByID;
  private ResponseApi<GdnRestSingleResponse<SetConfigResponse>> setConfigResponse;
  private ResponseApi<GdnRestListResponse<KeywordBoostProductResponse>> findBoostedKeyword;
  private ResponseApi<GdnRestSingleResponse<KeywordBoostProductResponse>> findBoostedKeywordByID;
  private ResponseApi<GdnRestSingleResponse<ProductResponse>> findDataByProductID;

}

