package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import com.mongodb.client.FindIterable;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

/**
 * @author kumar on 02/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class BusinessPartnerEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;

  MongoHelper mongoHelper = new MongoHelper();

  @Given("^\\[search-service] verify store closed start and end timestamp fields in SOLR for the product$")
  public void verifyStoreClosedInfoInSolr() {

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setBusinessPartnerCode(searchServiceProperties.get("businessPartnerCode"));

    try {

      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "closedStore");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);
      

      int isDelayShipping =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isDelayShipping", 1)
              .get(0)
              .getIsDelayShipping();

      long startDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "startDateStoreClosed", 1)
              .get(0)
              .getStartDateStoreClosed();

      long endDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "endDateStoreClosed", 1)
              .get(0)
              .getEndDateStoreClosed();

      log.warn("------isDelayShipping--{}---startDateStoreClosed--{}---endDateStoreClosed--{}", isDelayShipping,startDateStoreClosed,endDateStoreClosed);
      assertThat("isDelayShipping not set", isDelayShipping, equalTo(3));
      assertThat("startDateStoreClosed not set",startDateStoreClosed,equalTo(1111111111L));
      assertThat("endDateStoreClosed not set",endDateStoreClosed,equalTo(22222222L));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes store closed event with delay shipping as '(.*)'$")
  public void checkStoreEventIsConsumed(boolean isDelayShipping){

      kafkaHelper.publishStoreClosedEvent(searchServiceData.getBusinessPartnerCode(),isDelayShipping);

    try {
      Thread.sleep(60000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] store closed information is updated in SOLR$")
  public void checkStoreClosedInfoIsUpdated(){

    try {
      long startDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "startDateStoreClosed", 1)
              .get(0)
              .getStartDateStoreClosed();

      long endDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "endDateStoreClosed", 1)
              .get(0)
              .getEndDateStoreClosed();

      log.warn("---startDateStoreClosed--{}---endDateStoreClosed--{}",startDateStoreClosed,endDateStoreClosed);
      assertThat("startDateStoreClosed not set",startDateStoreClosed,not(equalTo(1111111111L)));
      assertThat("endDateStoreClosed not set",endDateStoreClosed,not(equalTo(22222222L)));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] delay shipping is set as '(.*)'$")
  public void checkDelayShipping(int isDelayShippingExpected){

    try {

      int isDelayShippingActual = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isDelayShipping", 1)
          .get(0)
          .getIsDelayShipping();

      log.warn("------isDelayShipping--{}---", isDelayShippingActual);
      assertThat("isDelayShipping not set", isDelayShippingActual, equalTo(isDelayShippingExpected));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] cnc is set as true in products for merchant$")
  public void setCncTrueForTestProduct(){

    SolrHelper.addSolrDocument();
    try {
      assertThat(SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001-PP-3001012",SELECT_HANDLER),equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  @When("^\\[search-service] consumes com.gdn.x.businesspartner.profile.update.fields event$")
  public void searchConsumesBPprofileUpdateEvent(){
    kafkaHelper.publishBPprofileFieldUpdateEvent("AAA-60015");
    try {
      Thread.sleep(60000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] cnc true is removed for all products under that merchant$")
  public void checkCncIsRemoved(){
    try {
      assertThat(SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001-PP-3001012",SELECT_HANDLER),equalTo(0L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] storeClose field is set to true$")
  public void searchServiceStoreCloseFieldIsSetToTrue(){

    try {
      boolean storeClose = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "storeClose",
          1).get(0).isStoreClose();

      assertThat("Store Close is not set",storeClose,equalTo(true));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] documents are created in mongo collection$")
  public void searchServiceDocumentsAreCreatedInMongoCollection(){

    int size=0;

    FindIterable<Document> mongoDocument = mongoHelper.getMongoDocumentByQuery(
        "scheduled_events",
        "field",
        "storeClose");

    for (Document d:mongoDocument
         ) {
      size++;
    }

    log.error("Count of entries in scheduled_events--{}",size);

    assertThat("No Mongo doc created",size,greaterThan(0));
  }

  @Then("^\\[search-service] solr documents are updated on running scheduled events job$")
  public void searchServiceSolrDocumentsAreUpdatedOnRunningScheduledEventsJob(){

    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    log.error("simpleDateFormat.format(date)=={}",simpleDateFormat.format(date));
    mongoHelper.updateAllMongo("scheduled_events", "date",date);


    ResponseApi<GdnBaseRestResponse> responseApi =
        searchServiceController.fetchTheListOfUnpublishedProducts();

    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(40000);

      long count =
          SolrHelper.getSolrProdCountWithFq(searchServiceData.getQueryForReindex(),
              SELECT_HANDLER,
              "published:[0 TO *] AND storeClose:*");

      assertThat("storeClose field not removed after reindex",count,equalTo(0L));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] documents are not created in mongo collection$")
  public void searchServiceDocumentsAreNotCreatedInMongoCollection(){

    int size = 0;

    FindIterable<Document> mongoDocument = mongoHelper.getMongoDocumentByQuery(
        "scheduled_events",
        "documentId",
        searchServiceData.getQueryForReindex());

    for (Document d:mongoDocument
    ) {
      size++;
    }

    assertThat("No Mongo doc created",size,equalTo(0));

  }

  @Given("^\\[search-service] business partner is set as whitelist$")
  public void searchServiceBusinessPartnerIsSetAsWhitelist(){
    ConfigHelper configHelper = new ConfigHelper();
    configHelper.addToWhitelist("STORE_CLOSE_SERVICE");
  }

  @Then("^\\[search-service] storeClose field is not set for delayShipping true$")
  public void searchServiceStoreCloseFieldIsNotSetForDelayShippingTrue(){

    try {

    long count = SolrHelper.getSolrProdCountWithFq("id:"+searchServiceProperties.get("businessPartnerCode")+"*",
          SELECT_HANDLER,
          "published:[0 TO *] AND storeClose:*");

    assertThat("storeClose field not removed after reindex",count,equalTo(0L));

    } catch (Exception e) {
      e.printStackTrace();
    }


  }
}
