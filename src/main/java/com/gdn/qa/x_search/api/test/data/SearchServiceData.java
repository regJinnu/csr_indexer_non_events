package com.gdn.qa.x_search.api.test.data;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
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
  private String productCodeForReindex;
  private String skuForReindex;
  private String itemSkuForReindex;
  private String queryForReindex;
  private String categoryForReindex;
  private String queryForCategoryReindex;
  private String queryForReindexOfDeletedProd;
  private String queryForReviewAndRatingIndex;
  private String queryForProductCode;
  private String businessPartnerCode;
  private int errorMessage;
  private String unSyncProduct;
  private String queryForUnsyncProduct;
  private String campaignName;
  private String campaignCode;
  private String campaignProductSku;
  private String campaignItemSku;
  private String campaignDiscount;
  private String campaignFieldInSOLR;
  private String campaignCodeList;
  private String itemSkuForRemove;
  private String facebookFeedLastUpdatedTime;
  private String defaultProd;
  private String fbOOSProd;
  private String fbExcludedProd;
  private String filterQuery;
  private String sortType;
  private String url;
  private String spel;
  private Integer rank;
  private String authenticator;
  private String effectiveValue;
  private String autoFlightId;
  private String autoPlaceholderId;
  private String autoSearchId;
  private String autoTrainId;
  private String effectiveSearchPattern;
  private String trainSearchTerm;
  private String trainMapping;
  private String searchRulSearchTerm;
  private String tagLabel;
  private int quota;
  private boolean exclusive;
  private String adjustmentName;
  private String description;
  private String promoValue;
  private String promoItemSKU;
  private String promoItemSKUinSOLR;
  private boolean promoActivated;
  private String promoBundlingId;
  private String promoBundlingType;
  private String complementaryProducts;
  private ResponseApi<GdnRestListResponse<TrainResponse>> getAllTrain;
  private ResponseApi<GdnRestListResponse<SearchRuleResponse>> getAllSearchRule;
  private ResponseApi<GdnRestListResponse<PlaceholderRuleResponse>> getAllPlaceholder;
  private ResponseApi<GdnRestListResponse<FlightResponse>> allFlights;
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
  private ResponseApi<GdnRestSingleResponse> processFailedIds;
  private ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> listReindexServices;
  private ResponseApi<GdnRestSingleResponse<StatusReIndexResponse>> reindexStatus;
}

