package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberStepsDefinition
public class ProductIndexingSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  MongoHelper mongoHelper = new MongoHelper();
  Date lastModifiedActual ;
  Date lastModifiedUpdated ;
  long reviewAndRatingTimestampActual;
  long reviewAndRatingTimestampUpdated;


  static Logger LOGGER = LoggerFactory.getLogger(ProductIndexingSteps.class);

  private static final String expectedListOfervices = "MERCHANT_SERVICE,ANALYTICS_SERVICE,STORE_CLOSE_SERVICE,LOCATION_AND_INVENTORY_SERVICE,PRODUCT_REVIEW_AND_RATING_SERVICE,PRISTINE_SERVICE,KEYWORD_SERVICE,CAMPAIGN_SERVICE,BOOSTED_KEYWORD,MODEL_NUMBER_EXTRACTION,NONE";

  @Given("^\\[search-service] failed Ids exist in the DB$")
  public void checkFailedIdsExist(){
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    assertThat("No failed Ids", countOfFailedIds, not(equalTo(0)));
    try {
       lastModifiedActual = SolrHelper.getSolrProd("level0Id:MTA-0305736","/select","lastModifiedDate",1).get(0).getLastModifiedDate();
      System.out.println("------Earlier Date---- lastModifiedActual ----:{}"+lastModifiedActual);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for processing failed Ids$")
  public void searchSendsReqForPrcessingFailedIds(){
    ResponseApi<GdnRestSingleResponse> response = searchServiceController.prepareRequestForProcessingFailedIds();
    searchServiceData.setProcessFailedIds(response);
  }

  @Then("^\\[search-service] removes the entries from DB$")
  public void checkEntriesAreRemovedFromDB(){
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    assertThat("No failed Ids", countOfFailedIds, equalTo(0L));
  }

  @Then("^\\[search-service] indexes the Ids present in DB$")
  public void checkDbProductsAreReindexed(){

    try {
      solrCommit("productCollectionNew");
      lastModifiedUpdated = SolrHelper.getSolrProd("level0Id:MTA-0305736","/select","lastModifiedDate",1).get(0).getLastModifiedDate();
      LOGGER.debug("------Earlier Date---- lastModifiedActual ----:{}",lastModifiedActual);
      LOGGER.debug("------Update Date---- lastModifiedUpdated ----:{}",lastModifiedUpdated);

      System.out.println("------Earlier Date---- lastModifiedActual ----:{}"+lastModifiedActual);
      System.out.println("------Update Date---- lastModifiedUpdated ----:{}"+lastModifiedUpdated);

      assertThat("Last Modified Data is not Updated",lastModifiedUpdated,greaterThan(lastModifiedActual));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] product is OOS in SOLR and isInStock in Xproduct$")
  public void checkProductStatusInSOLR(){
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    try {
      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"oos");
      assertThat("Updating OOS in SOLR doc failed",status,equalTo(0));
      solrCommit("productCollectionNew");
      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS in SOLR",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for indexing the product using '(.*)'$")
  public void sendProductReindexRequest(String reqType){

    ResponseApi responseApi;

    if(reqType.equals("productCode"))
      responseApi=searchServiceController.prepareRequestForIndexing("productCodes",searchServiceData.getProductCodeForReindex());
    else if(reqType.equals("sku"))
      responseApi=searchServiceController.prepareRequestForIndexing("skus",searchServiceData.getSkuForReindex());
    else
      responseApi=searchServiceController.prepareRequestForIndexing("","");

    searchServiceData.setSearchServiceResponse(responseApi);
  }

  @Then("^\\[search-service] indexes the provided product$")
  public void checkIfProdWasReindexed(){

    ResponseApi responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      solrCommit("productCollectionNew");
      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS in SOLR",oosFlag,equalTo(1));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] list of services are configured$")
  public void checkServicesAreConfigured(){

  }

  @When("^\\[search-service] sends request for listing services for reindex$")
  public void sendRequestToFetchListReindexServices(){

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> responseApi =
        searchServiceController.prepareRequestForListingServicesForReindexing();

    searchServiceData.setListReindexServices(responseApi);

  }

  @Then("^\\[search-service] all services configured are listed$")
   public void checkListIsConfiguredAsExpected(){

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> responseApi = searchServiceData.getListReindexServices();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    String actual = responseApi.getResponseBody().getValue().getValue();

    for (String s: actual.split(",")
         ) {
      assertThat("Services do not match",expectedListOfervices,containsString(s));
    }

  }

  @Given("^\\[search-service] product is having different rating and review in SOLR and concerned service$")
  public void checkReviewAndRatingDataInSolrAndService(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    try {
      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"reviewAndRating");
      assertThat("Updating review and rating in SOLR doc failed",status,equalTo(0));
      solrCommit("productCollectionNew");
      int reviewCount = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","reviewCount",1).get(0).getReviewCount();
      String rating = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","rating",1).get(0).getRating();
      System.out.println("-----Review Count ---"+reviewCount+"-----Rating--{}--"+rating);
      LOGGER.debug("-----Review Count ---{}-----Rating--{}--",reviewCount,rating);
      assertThat("Test Product not set in SOLR",reviewCount,equalTo(0));
      assertThat("Test Product not set in SOLR",rating,equalTo("0"));
      reviewAndRatingTimestampActual = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","reviewAndRatingServiceLastUpdatedTimestamp",1).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] sends request for indexing with review and rating$")
  public void sendReindexRequestForReviewAndRating(){

    ResponseApi<GdnBaseRestResponse> responseApi = searchServiceController.prepareRequestForReviewAndRatingIndex();
    searchServiceData.setSearchServiceResponse(responseApi);

  }

  @Then("^\\[search-service] data is corrected in SOLR$")
  public void checkReviewAndRatingDataIsReindexedProperly(){

    ResponseApi<GdnBaseRestResponse> responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(2000);
      solrCommit("productCollectionNew");
      int reviewCount = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","reviewCount",1).get(0).getReviewCount();
      String rating = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","rating",1).get(0).getRating();

      System.out.println("-----Review Count ---"+reviewCount+"-----Rating----"+rating);
      LOGGER.debug("-----Review Count ---{}-----Rating--{}--",reviewCount,rating);
      assertThat("Review and rating not indexed",reviewCount,not(equalTo(0)));
      assertThat("Product not OOS in SOLR",rating,not(equalTo("0")));

      reviewAndRatingTimestampUpdated = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","reviewAndRatingServiceLastUpdatedTimestamp",1).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();

      LOGGER.debug("------Earlier Date---- lastModifiedActual ----:{}",reviewAndRatingTimestampActual);
      LOGGER.debug("------Update Date---- lastModifiedUpdated ----:{}",reviewAndRatingTimestampUpdated);

      System.out.println("--reviewAndRatingTimestampUpdated---"+reviewAndRatingTimestampUpdated+" reviewAndRatingTimestampActual"+reviewAndRatingTimestampActual);
      assertThat("reviewAndRatingTimestamp is not Updated",reviewAndRatingTimestampUpdated,greaterThan(reviewAndRatingTimestampActual));
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

}
