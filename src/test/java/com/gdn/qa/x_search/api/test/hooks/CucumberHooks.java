package com.gdn.qa.x_search.api.test.hooks;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class CucumberHooks {

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  ConfigHelper configHelper;

  @Autowired
  SearchServiceProperties searchServiceProperties;

  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  @After
  public void afterRun() {




    //it will executed after scenario run

    //   mongoHelper.deleteFromMongo("synonyms_list","KEY","testingapi");
    //   mongoHelper.deleteFromMongo("keyword_boost_keyword_list","keyword","testingapi");
  }

  @Before("@FindSynonymnByKey")
  public void beforeSynonymRun() {

    try {
      Date date = dateFormat.parse(LocalDateTime.now().toString());
      mongoHelper.deleteFromMongo("synonyms_list","KEY","testingapi");

      Document synDoc = new Document("_class", "com.gdn.x.search.entity.SynonymsEntity").append(
          "KEY",
          "testingapi")
          .append("SYNONYMS", "test1,test2")
          .append("GROUP_NAME", "synonyms_gdn")
          .append("SYNC", "1")
          .append("version", 8)
          .append("CREATED_DATE", date)
          .append("CREATED_BY", "user-dev-src")
          .append("UPDATED_DATE", date)
          .append("UPDATED_BY", "user-dev-src")
          .append("STORE_ID", "10001")
          .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("synonyms_list", synDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }


  @Before("@LogisticOriginChangeEvent")
  public void beforeLogisticOriginChangeEvent() {
    mongoHelper.deleteAllFromMongo("product_atomic_reindex_queue");
    mongoHelper.deleteFromMongo("product_atomic_reindex_data_candidate",
        "solrfieldName",
        "location");
  }


  @Before("@FindBoostedKeyword")
  public void beforeBoostedKeyword() {

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");

      Document keywordBoostDoc =
          new Document("_class", "com.gdn.x.search.entity.KeywordBoostProduct").append("keyword",
              "testingapi")
              .append("products", "MTA-0309256")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("STORE_ID", "10001")
              .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list", keywordBoostDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Before("@ProcessFailedIds")
  public void beforeProcessFailedIds() {

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");
      Document failedIdsDoc =
          new Document("_class", "com.gdn.x.search.entity.SolrFailedIds").append("product_id",
              "MTA-0309046")
              .append("id_type", "PRODUCT_CODE")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("solr_failed_ids", failedIdsDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Before("@Multidelete")
  public void beforeMultiDelete() {

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");
      Document multiDeleteKeywordBoostDoc1 =
          new Document("_class", "com.gdn.x.search.entity.KeywordBoostProduct").append("_id",
              "98765")
              .append("keyword", "automation")
              .append("products", "MTA-0309256,MTA-0306144")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("STORE_ID", "10001")
              .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list", multiDeleteKeywordBoostDoc1);

      Document multiDeleteKeywordBoostDoc2 =
          new Document("_class", "com.gdn.x.search.entity.KeywordBoostProduct").append("_id",
              "43210")
              .append("keyword", "testing")
              .append("products", "MTA-0309256,MTA-0306144")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("STORE_ID", "10001")
              .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list", multiDeleteKeywordBoostDoc2);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Before("@ProcessDeltaStoredEvents")
  public void beforeProcessingStoredEvents() {
/*
    Date date = null;
    try {
      date = dateFormat.parse("2018-07-30T11:45:39.235Z");

      Document storedDeltaDoc1 = new Document("_class" , "com.gdn.x.search.entity.EventIndexingEntity")
          .append("code" , "MTA-0305736")
          .append("type" , "productCode")
          .append("processHost", "1")
          .append("isFailed", "0")
          .append("eventType" , "ALL")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("MARK_FOR_DELETE" , false);

      Document storedDeltaDoc2 = new Document("_class" , "com.gdn.x.search.entity.EventIndexingEntity")
          .append("code" , "TH7-15791-00161-00001")
          .append("type" , "id")
          .append("processHost", "1")
          .append("isFailed", "0")
          .append("eventType" , "LOCATION_AND_INVENTORY_SERVICE")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("indexing_list_new",storedDeltaDoc1);
      mongoHelper.insertInMongo("indexing_list_new",storedDeltaDoc2);

    } catch (ParseException e) {
      e.printStackTrace();
    }*/

  }

  @Before("@ItemChangeDeleteEvent")
  public void beforeItemChangeDeleteEvent() {
    mongoHelper.deleteFromMongo("deleted_products", "_id", "AAA-60015-00008");
  }

  @Before("@ProductChangeDeleteEvent")
  public void beforeProductChangeDeleteEvent() {
    mongoHelper.deleteFromMongo("deleted_products", "_id", "MTA-66666");
  }

  @Before("@StoreClosedWithDelayShippingFalse")
  public void beforeStoreClosedWithDelayShippingFalse() {
    mongoHelper.deleteAllFromMongo("scheduled_events");
  }

  @Before("@StoreClosedDelayShipping")
  public void beforeStoreClosedWithDelayShipping() {
    mongoHelper.deleteAllFromMongo("scheduled_events");
  }

  @Before("@AddPlaceholderRules")
  public void beforeAddingDeleteDuplicatePlaceholder() {
    mongoHelper.deleteFromMongo("placeholder_im_rule", "_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@AddFlight")
  public void beforeAddingDeleteDuplicateFlight() {
    mongoHelper.deleteFromMongo("flight_dictionary", "_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@AddSearchRule")
  public void beforeAddingDeleteDuplicateSearchRule() {
    mongoHelper.deleteFromMongo("search_rule", "_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@AddTrainMapping")
  public void beforeAddingDeleteDuplicateTrainMapping() {
    mongoHelper.deleteFromMongo("train_dictionary", "_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@ItemChangeItemDataChange")
  public void beforeItemDataChange() {
    mongoHelper.deleteAllFromMongo("scheduled_events");
  }

  @Before("@AddMerchantSort")
  public void beforeAddMerchantSort() {
    mongoHelper.deleteFromMongo("merchant_sort", "merchantId", "TH7-15791");
  }

  @After("@DefaultCncJobUpdate")
  public void afterDefaultCncJob() {
    ResponseApi<GdnBaseRestResponse> responseAfterReindexing =
        searchServiceController.defaultCncJob();
    searchServiceData.setSearchServiceResponse(responseAfterReindexing);

    ResponseApi<GdnBaseRestResponse> responseAfterReindexingCNCProd =
        searchServiceData.getSearchServiceResponse();
    assertThat("Request failed",
        responseAfterReindexingCNCProd.getResponse().getStatusCode(),
        equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @After("@DefaultCncJobWhenProdIsDeleted")
  public void afterDeletingDefaultCncProd() {
    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    ResponseApi<GdnBaseRestResponse> responseAfterReindexing =
        searchServiceController.defaultCncJob();
    searchServiceData.setSearchServiceResponse(responseAfterReindexing);

    ResponseApi<GdnBaseRestResponse> responseAfterReindexingCNCProd =
        searchServiceData.getSearchServiceResponse();
    assertThat("Request failed",
        responseAfterReindexingCNCProd.getResponse().getStatusCode(),
        equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @After("@ForceStopSolrCncUpdatesSwitch")
  public void reindexDefCnc() {
    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    ResponseApi<GdnBaseRestResponse> responseAfterReindexing =
        searchServiceController.defaultCncJob();
    searchServiceData.setSearchServiceResponse(responseAfterReindexing);

    ResponseApi<GdnBaseRestResponse> responseAfterReindexingCNCProd =
        searchServiceData.getSearchServiceResponse();
    assertThat("Request failed",
        responseAfterReindexingCNCProd.getResponse().getStatusCode(),
        equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @After("ItemChangeDeleteEvent")
  public void afterItemChangeDeleteEvent() {
    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @After("OfflineEventHandlingFeature")
  public void afterOfflineChangeEvent() {
    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Before("@ProductReviewEventProcessing")
  public void updateRelatedConfigs() {
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
    configHelper.findAndUpdateConfig("force.stop.solr.cnc.updates", "false");
    configHelper.findAndUpdateConfig("delta.event.in.progress", "false");
    configHelper.findAndUpdateConfig("reindex.status", "0");
    configHelper.findAndUpdateConfig("reindex.triggered", "false");
  }

  @Before("@ProductReviewEventProcessingOnCNCCollection")
  public void updateDeltaConfig() {
    configHelper.findAndUpdateConfig("disable.delta.event.list", "''");
  }

  @After("BuyboxFeature")
  public void afterbuyBoxChangeEvent() {
    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getItemSkuForReindex());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Before("@AggregateInventoryChangeNonCNCEvent")
  public void updateAggregateLocationConfig() {
    configHelper.findAndUpdateConfig("enable.aggregate.location.change.service", "true");
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
  }

  @Before("@AggregateInventoryChangeCNCEvent")
  public void updateSolrDefaultDocument() throws Exception {
    String ppCode = searchServiceProperties.get("ppCode1ForInventoryChangeCNC");
    String itemSku = searchServiceProperties.get("itemSkuForInventoryChangeCNC");
    String[] tokens = itemSku.split("-");
    String merchantCode = tokens[0]+"-"+tokens[1];
    String query = "id:"+ppCode+"!"+merchantCode+"!" + itemSku + "-" +ppCode ;
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "inventoryChange",
        SOLR_DEFAULT_COLLECTION);
    assertThat("Updating inventoryStockLocation info in SOLR doc failed", status, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

    SolrResults solrResults = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "allLocation,stockLocation",
        1,
        Collections.emptyList(),
        SOLR_DEFAULT_COLLECTION).get(0);

    ArrayList<String> allLocation = solrResults.getAllLocation();

    ArrayList<String> stockLocation = solrResults.getStockLocation();

    assertThat("Test Product not set in SOLR", allLocation, equalTo(null));
    assertThat("Test Product not set in SOLR", stockLocation, equalTo(null));
  }
}