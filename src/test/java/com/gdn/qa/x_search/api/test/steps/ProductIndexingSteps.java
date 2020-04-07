package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.*;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import com.gdn.x.search.rest.web.model.StatusReIndexResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@CucumberStepsDefinition
public class ProductIndexingSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  private RedisHelperNew redisHelper;

  @Autowired
  private ConfigHelper configHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  MongoHelper mongoHelper;

  Date lastModifiedActual;
  Date lastModifiedUpdated;
  long reviewAndRatingTimestampActual;
  long reviewAndRatingTimestampUpdated;


  private static final String expectedListOfServices =
      "MERCHANT_SERVICE,ANALYTICS_SERVICE,STORE_CLOSE_SERVICE,LOCATION_AND_INVENTORY_SERVICE,PRODUCT_REVIEW_AND_RATING_SERVICE,PRISTINE_SERVICE,KEYWORD_SERVICE,CAMPAIGN_SERVICE,BOOSTED_KEYWORD,MODEL_NUMBER_EXTRACTION,BUYBOX_SERVICE,OXFORD,MERCHANT_ANALYTICS,NONE,CAMPAIGN_INDEX,AGGREGATE_SERVICE,MERCHANT_VOUCHER_SERVICE";

  @Given("^\\[search-service] failed Ids exist in the DB$")
  public void checkFailedIdsExist() {
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    searchServiceData.setQueryForProductCode(searchServiceProperties.get("queryForProductCode"));
    assertThat("No failed Ids", countOfFailedIds, not(equalTo(0)));
    try {

      log.warn("---searchServiceData.getProductCodeForReindex()-{}",
          searchServiceData.getProductCodeForReindex());
      log.warn("---searchServiceData.getQueryForCategoryReindex()-{}",
          searchServiceData.getQueryForProductCode());

      lastModifiedActual = solrHelper.getSolrProd(searchServiceData.getQueryForProductCode(),
          SELECT_HANDLER,
          "lastModifiedDate",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getLastModifiedDate();
      log.warn("------Earlier Date---- lastModifiedActual ----:{}" + lastModifiedActual);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for processing failed Ids$")
  public void searchSendsReqForPrcessingFailedIds() {
    ResponseApi<GdnRestSingleResponse> response =
        searchServiceController.prepareRequestForProcessingFailedIds();
    searchServiceData.setProcessFailedIds(response);
  }

  @Then("^\\[search-service] removes the entries from DB$")
  public void checkEntriesAreRemovedFromDB() {
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    assertThat("No failed Ids", countOfFailedIds, equalTo(0L));
  }

  @Then("^\\[search-service] indexes the Ids present in DB$")
  public void checkDbProductsAreReindexed() {

    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      lastModifiedUpdated = solrHelper.getSolrProd(searchServiceData.getQueryForProductCode(),
          SELECT_HANDLER,
          "lastModifiedDate",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getLastModifiedDate();
      log.warn("------Earlier Date---- lastModifiedActual ----:{}", lastModifiedActual);
      log.warn("------Update Date---- lastModifiedUpdated ----:{}", lastModifiedUpdated);

/*      assertThat("Last Modified Data is not Updated",
          lastModifiedUpdated,
          greaterThan(lastModifiedActual));*/
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service\\] isInStock value is different in SOLR and Xproduct$")
  public void searchServiceIsInStockValueIsDifferentInSOLRAndXproduct() {
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    try {
      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "randomInStockValue",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating OOS in SOLR doc failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isInStock",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      assertThat("Product not updated in SOLR", oosFlag, equalTo(5));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for indexing the product using '(.*)'$")
  public void sendProductReindexRequest(String reqType) {

    ResponseApi responseApi;

    if (reqType.equals("productCode"))
      responseApi = searchServiceController.prepareRequestForIndexing("productCodes",
          searchServiceData.getProductCodeForReindex());
    else if (reqType.equals("sku"))
      responseApi = searchServiceController.prepareRequestForIndexing("skus",
          searchServiceData.getSkuForReindex());
    else
      responseApi = searchServiceController.prepareRequestForIndexing("", "");

    searchServiceData.setSearchServiceResponse(responseApi);
  }

  @Then("^\\[search-service] indexes the provided product$")
  public void checkIfProdWasReindexed() {

    ResponseApi responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      System.out.println("--------ABHINAV----searchServiceData.getQueryForReindex()-"
          + searchServiceData.getQueryForReindex());
      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isInStock",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      assertThat("Product not updated in SOLR", oosFlag, not(equalTo(5)));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] list of services are configured$")
  public void checkServicesAreConfigured() {

  }

  @When("^\\[search-service] sends request for listing services for reindex$")
  public void sendRequestToFetchListReindexServices() {

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> responseApi =
        searchServiceController.prepareRequestForListingServicesForReindexing();

    searchServiceData.setListReindexServices(responseApi);

  }

  @Then("^\\[search-service] all services configured are listed$")
  public void checkListIsConfiguredAsExpected() {

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> responseApi =
        searchServiceData.getListReindexServices();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    String actual = responseApi.getResponseBody().getValue().getValue();

    for (String s : actual.split(",")) {
      assertThat("Services do not match", expectedListOfServices, containsString(s));
    }

  }

  @Given("^\\[search-service] product is having different rating and review in SOLR and concerned service$")
  public void checkReviewAndRatingDataInSolrAndService() {

    searchServiceData.setQueryForReviewAndRatingIndex(searchServiceProperties.get(
        "queryForReviewAndRatingIndex"));
    try {
      String query = searchServiceData.getQueryForReviewAndRatingIndex();
      int status = solrHelper.updateSolrDataForAutomation(query,
          SELECT_HANDLER,
          "id",
          1,
          "reviewAndRating",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating review and rating in SOLR doc failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "reviewCount,rating,reviewAndRatingServiceLastUpdatedTimestamp",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();
      String rating = solrResults.getRating();
      reviewAndRatingTimestampActual = solrResults.getReviewAndRatingServiceLastUpdatedTimestamp();

      log.warn("-----Review Count ---{}-----Rating--{}---reviewAndRatingTimestampActual--{}",
          reviewCount,
          rating,
          reviewAndRatingTimestampActual);
      assertThat("Test Product not set in SOLR", reviewCount, equalTo(100));
      assertThat("Test Product not set in SOLR", rating, equalTo("23"));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] sends request for indexing with review and rating$")
  public void sendReindexRequestForReviewAndRating() {

    ResponseApi<GdnBaseRestResponse> responseApi =
        searchServiceController.prepareRequestForReviewAndRatingIndex();
    searchServiceData.setSearchServiceResponse(responseApi);

  }

  @Then("^\\[search-service] data is corrected in SOLR$")
  public void checkReviewAndRatingDataIsReindexedProperly() {

    ResponseApi<GdnBaseRestResponse> responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(5000);
      String query = searchServiceData.getQueryForReviewAndRatingIndex();
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      Thread.sleep(10000);

      SolrResults solrResults = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "reviewCount,rating,reviewAndRatingServiceLastUpdatedTimestamp",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();
      String rating = solrResults.getRating();
      reviewAndRatingTimestampUpdated = solrResults.getReviewAndRatingServiceLastUpdatedTimestamp();

      log.warn("-----Review Count ---{}-----Rating--{}---reviewAndRatingTimestampUpdated--{}",
          reviewCount,
          rating,
          reviewAndRatingTimestampUpdated);
      assertThat("Review and rating not indexed", reviewCount, not(equalTo(100)));
      assertThat("Product not OOS in SOLR", rating, not(equalTo("23")));
      assertThat("reviewAndRatingTimestamp is not Updated",
          reviewAndRatingTimestampUpdated,
          greaterThan(reviewAndRatingTimestampActual));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] data is different in Solr and Xproduct for products in category$")
  public void xcheckCategoryDataIsDifferentInXprodAndSolr() {
    searchServiceData.setQueryForCategoryReindex(searchServiceProperties.get(
        "queryForCategoryReindex"));
    searchServiceData.setCategoryForReindex(searchServiceProperties.get("categoryForReindex"));

    configHelper.findAndUpdateConfig("reindex.status", "0");
    configHelper.findAndUpdateConfig("reindex.status.of.node.1", "0");
    configHelper.findAndUpdateConfig("reindex.triggered", "false");
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");

    try {
      int status =
          solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForCategoryReindex(),
              SELECT_HANDLER,
              "id",
              1,
              "categoryReindex",
              SOLR_DEFAULT_COLLECTION);
      assertThat("Updating solr fields to different values failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults =
          solrHelper.getSolrProd(searchServiceData.getQueryForCategoryReindex(),
              SELECT_HANDLER,
              "reviewCount,rating,merchantCommissionType,merchantRating,location",
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      int oosFlag = solrResults.getIsInStock();

      String merchantCommissionType = solrResults.getMerchantCommissionType();

      Double merchantRating = solrResults.getMerchantRating();

      String location = solrResults.getLocation();

      log.warn(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location);

      assertThat("Test Product not set in SOLR", reviewCount, equalTo(10));
      assertThat("Test Product not set in SOLR", rating, equalTo("40"));
      assertThat("Test Product not set in SOLR", oosFlag, equalTo(5));
      assertThat("Test Product not set in SOLR", merchantRating, equalTo(30.0));
      assertThat("Test Product not set in SOLR", merchantCommissionType, equalTo("TC"));
      assertThat("Test Product not set in SOLR", location, equalTo("Origin-ABC"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for category reindex$")
  public void sendCategoryReindexRequest() {

    ResponseApi<GdnBaseRestResponse> responseApi =
        searchServiceController.prepareRequestForCategoryReindex(searchServiceData.getCategoryForReindex());
    searchServiceData.setSearchServiceResponse(responseApi);

  }

  @Then("^\\[search-service] data is corrected for all products in the category$")
  public void checkDataInSolrIsCorrectedAfterCategoryReindex() {

    ResponseApi<GdnBaseRestResponse> responseApi = searchServiceData.getSearchServiceResponse();

    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(60000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults =
          solrHelper.getSolrProd(searchServiceData.getQueryForCategoryReindex(),
              SELECT_HANDLER,
              "reviewCount,rating,merchantCommissionType,merchantRating,location",
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      int oosFlag = solrResults.getIsInStock();

      String merchantCommissionType = solrResults.getMerchantCommissionType();

      Double merchantRating = solrResults.getMerchantRating();

      String location = solrResults.getLocation();

      log.error(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location);

      assertThat("ReviewCount not updated after reindex", reviewCount, not(equalTo(10)));
      assertThat("Rating not updated after reindex", rating, not(equalTo("40")));
      assertThat("isInStock not updated after reindex", oosFlag, not(equalTo(5)));
      assertThat("Merchant Rating not updated after reindex", merchantRating, not(equalTo(30.0)));
      assertThat("Merchant Comm Type not updated after reindex",
          merchantCommissionType,
          not(equalTo("TC")));
      assertThat("Location not updated after reindex", location, not(equalTo("Origin-ABC")));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for full reindex with xproduct option$")
  public void sendRequestForFullReindexWithXprodOption() {

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setQueryForReindexOfDeletedProd(searchServiceProperties.get(
        "queryForReindexOfDeletedProd"));

    ResponseApi<GdnBaseRestResponse> responseApi =
        searchServiceController.prepareRequestForFullReindexing("false", "ALL");
    searchServiceData.setSearchServiceResponse(responseApi);

    try {

      configHelper.findAndUpdateConfig("force.stop.solr.updates", "true");
      Thread.sleep(60000);
      mongoHelper.deleteAllFromMongo("reindex_entity");
      Thread.sleep(20000);
      assertThat("Deleted all document from Mongo",
          mongoHelper.countOfRecordsInCollection("reindex_entity"),
          equalTo(0L));

      Document indexDoc1 = new Document("_class", "com.gdn.x.search.entity.ReIndexEntity").append(
          "productId",
          searchServiceProperties.get("productCodeForDeletedProd"))
          .append("status", 0)
          .append("isFailed", 0)
          .append("idType", "productCode")
          .append("version", 1)
          .append("MARK_FOR_DELETE", false);

      Document indexDoc2 = new Document("_class", "com.gdn.x.search.entity.ReIndexEntity").append(
          "productId",
          searchServiceProperties.get("skuForCategoryReindex"))
          .append("status", 0)
          .append("isFailed", 0)
          .append("idType", "productSku")
          .append("version", 1)
          .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("reindex_entity", indexDoc1);
      mongoHelper.insertInMongo("reindex_entity", indexDoc2);

      assertThat("Test data insertion in Mongo failed",
          mongoHelper.countOfRecordsInCollection("reindex_entity"),
          equalTo(2L));

      solrHelper.deleteSolrDocByQuery(searchServiceData.getQueryForReindexOfDeletedProd(),
          SOLR_DEFAULT_COLLECTION);

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      assertThat("Failed to delete data in SOLR",
          solrHelper.getSolrProdCount(searchServiceData.getQueryForReindexOfDeletedProd(),
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,
              Collections.emptyList()),
          equalTo(0L));

      configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
/*      configHelper.findAndUpdateConfig("reindex.status.of.node.1", "1");
      configHelper.findAndUpdateConfig("reindex.status", "1");*/

      ResponseApi<GdnBaseRestResponse> responsePubEvent =
          searchServiceController.publishReindexEvents();
      assertThat("Status Code Not 200",
          responsePubEvent.getResponse().getStatusCode(),
          equalTo(200));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] test data is reindexed$")
  public void dataIndexedAfterFullReindex() {

    ResponseApi<GdnBaseRestResponse> responseApi = searchServiceData.getSearchServiceResponse();

    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {

      Thread.sleep(100000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      long count = solrHelper.getSolrProdCount(searchServiceData.getQueryForReindexOfDeletedProd(),
          SELECT_HANDLER,
          SOLR_DEFAULT_COLLECTION,
          Collections.emptyList());

      log.warn("---searchServiceData.getQueryForReindexOfDeletedProd()--{}--count--{}",
          searchServiceData.getQueryForReindexOfDeletedProd(),
          count);

      assertThat("Deleted data in SOLR recovered", count, equalTo(1L));

      String queryForCatReindex = searchServiceData.getQueryForCategoryReindex();

      log.warn("--searchServiceData.getQueryForCategoryReindex()--{}-", queryForCatReindex);

      SolrResults solrResults = solrHelper.getSolrProd(queryForCatReindex,
          SELECT_HANDLER,
          "reviewCount,rating,merchantCommissionType,merchantRating,location",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      int oosFlag = solrResults.getIsInStock();

      String merchantCommissionType = solrResults.getMerchantCommissionType();

      Double merchantRating = solrResults.getMerchantRating();

      String location = solrResults.getLocation();

      log.error(
          "---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--",
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location);

      assertThat("Review Count not indexed in SOLR", reviewCount, not(equalTo(10)));
      assertThat("Rating not indexed in SOLR", rating, not(equalTo("40")));
      assertThat("isInStock not indexed in SOLR", oosFlag, not(equalTo(5)));
      assertThat("Merchant Rating not indexed in SOLR", merchantRating, not(equalTo(30.0)));
      assertThat("Merchant commission type not indexed in SOLR",
          merchantCommissionType,
          not(equalTo("TC")));
      assertThat("Location not indexed in SOLR", location, not(equalTo("Origin-ABC")));

    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  @Given("^\\[search-service] events are present in IndexingListNew collection$")
  public void checkEntriesAreExistingInIndexingListNewCollection() {

    searchServiceData.setQueryForProductCode(searchServiceProperties.get("queryForProductCode"));
    searchServiceData.setItemSkuForStoredDelta(searchServiceProperties.get("itemSkuForStoredDelta"));
    searchServiceData.setQueryForReindexOfDeletedProd(searchServiceProperties.get(
        "queryForReindexOfDeletedProd"));
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "true");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    Date date = null;
    try {
      date = dateFormat.parse("2018-07-30T11:45:39.235Z");

      log.error("----PC---{}", searchServiceData.getQueryForProductCode().split(":")[1]);
      log.error("----iSku---{}", searchServiceData.getItemSkuForStoredDelta().split(":")[1]);

      Document storedDeltaDoc1 =
          new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
              searchServiceData.getQueryForProductCode().split(":")[1])
              .append("type", "productCode")
              .append("isFailed", "0")
              .append("eventType", "ALL")
              .append("eventName", "productChangeEventListener")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("MARK_FOR_DELETE", false);

      Document storedDeltaDoc2 =
          new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
              searchServiceData.getItemSkuForStoredDelta().split(":")[1])
              .append("type", "id")
              .append("isFailed", "0")
              .append("eventType", "LOCATION_AND_INVENTORY_SERVICE")
              .append("eventName", "productNonOutOfStockEventListener")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("MARK_FOR_DELETE", false);

      Document storedDeltaDoc3 =
          new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
              searchServiceData.getQueryForReindexOfDeletedProd().split(":")[1])
              .append("type", "id")
              .append("isFailed", "0")
              .append("eventType", "LOCATION_AND_INVENTORY_SERVICE")
              .append("eventName", "productNonOutOfStockEventListener")
              .append("version", 0)
              .append("CREATED_DATE", date)
              .append("CREATED_BY", "user-dev-src")
              .append("UPDATED_DATE", date)
              .append("UPDATED_BY", "user-dev-src")
              .append("MARK_FOR_DELETE", false);

      mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc1);
      mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc2);
      mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc3);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    long countOfStoredEvents = mongoHelper.countOfRecordsInCollection("indexing_list_new");
    assertThat("No Stored events exists in Mongo", countOfStoredEvents, greaterThan(0L));

    try {

      String query = searchServiceData.getItemSkuForStoredDelta();

      int status = solrHelper.updateSolrDataForAutomation(query,
          SELECT_HANDLER,
          "id",
          1,
          "reviewAndRating",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating review and rating in SOLR doc failed", status, equalTo(0));
      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "reviewCount,rating,lastModifiedDate",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();
      String rating = solrResults.getRating();
      lastModifiedActual = solrResults.getLastModifiedDate();

      log.error("----reviewCount---{}---rating---{}", reviewCount, rating);

      assertThat("Product review count not set", reviewCount, equalTo(100));
      assertThat("Product rating not set", rating, equalTo("23"));

      String queryInv = searchServiceData.getQueryForReindexOfDeletedProd();

      solrHelper.updateSolrDataForAutomation(queryInv,
          SELECT_HANDLER,
          "id",
          1,
          "randomInStockValue",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating isInStock field in SOLR failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = solrHelper.getSolrProd(queryInv,
          SELECT_HANDLER,
          "isInStock",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      assertThat("Product not updated", oosFlag, equalTo(5));

      log.warn("--oosFlag--{}---reviewCount---{}---rating--{}", oosFlag, reviewCount, rating);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for processing stored delta$")
  public void sendRequestToProcessStoredDelta() {

    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
    configHelper.findAndUpdateConfig("force.stop.solr.cnc.updates", "false");

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> processingStoredDelta =
        searchServiceController.prepareRequestForProcessingStoredDelta();
    searchServiceData.setListReindexServices(processingStoredDelta);

  }

  @Then("^\\[search-service] products stored in table are reindexed$")
  public void checkStoredIdsAreReindexedInSolr() {

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> responseApi =
        searchServiceData.getListReindexServices();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindexOfDeletedProd(),
              SELECT_HANDLER,
              "isInStock,reviewCount,rating,lastModifiedDate",
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION).get(0);

      int oosFlag = solrResults.getIsInStock();

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      lastModifiedUpdated = solrResults.getLastModifiedDate();

      log.warn(
          "--oosFlag--{}---reviewCount---{}---rating--{}---lastModifiedUpdated---{}---lastModifiedActual--{}",
          oosFlag,
          reviewCount,
          rating,
          lastModifiedUpdated,
          lastModifiedActual);

      assertThat("Product review count not updated even after reindex",
          reviewCount,
          not(equalTo(100)));
      assertThat("Product rating not updated even after reindex", rating, not(equalTo("23")));
      assertThat("StockValue not changed after reindex", oosFlag, not(equalTo(5)));
      assertThat("Last Modified Data is not Updated",
          lastModifiedUpdated,
          greaterThan(lastModifiedActual));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] api exist to get indexing status$")
  public void checkApiExist() {
  }

  @When("^\\[search-service] sends request to get indexing status$")
  public void sendReqToGetIndexingStatus() {

    ResponseApi<GdnRestSingleResponse<StatusReIndexResponse>> reindexStatus =
        searchServiceController.prepareRequestForGettingIndexingStatus();

    searchServiceData.setReindexStatus(reindexStatus);
  }

  @Then("^\\[search-service] indexing status is received$")
  public void checkReindexStatus() {

    ResponseApi responseApi = searchServiceData.getReindexStatus();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

  }

}
