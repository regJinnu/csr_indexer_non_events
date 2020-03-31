package com.gdn.qa.x_search.api.test.steps;

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
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION_O2O;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author kumar on 01/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class InventoryEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;
  
  @Autowired
  SolrHelper solrHelper;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  ConfigHelper configHelper;

  private int oosFlagInO2OColl = 0;
  private int oosFlag = 0;

  @Given("^\\[search-service] verify product is in stock in SOLR$")
  public void checkProductIsInStock() {

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          "/select",
          "id",
          1,
          "nonOOS",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating isInStock field in SOLR failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int statusInO2O =
          solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
              "/select",
              "id",
              1,
              "nonOOS",
              SOLR_DEFAULT_COLLECTION_O2O);
      assertThat("Updating isInStock field in SOLR failed", statusInO2O, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      int oosFlagInO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION_O2O).get(0).getIsInStock();

      log.warn("-----Product {} Set non OOS before test---{}",
          searchServiceData.getQueryForReindex(),
          oosFlag);

      assertThat("Product non OOS", oosFlag, equalTo(1));
      assertThat("Product non OOS", oosFlagInO2O, equalTo(1));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes oos event for the product$")
  public void searchConsumesOOSEvent(){

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
   kafkaHelper.publishOOSEvent(itemSku,"TH7-15641","oos");

    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes oos in SOLR$")
  public void checkProductIsOOSAfterEvent(){
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      log.warn("-----IsInStock After reindex by event---{}", oosFlag);
      assertThat("Product not OOS", oosFlag, equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      oosFlagInO2OColl = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION_O2O).get(0).getIsInStock();
      log.warn("-----IsInStock After reindex by event in o2o coll----{}", oosFlagInO2OColl);
      assertThat("Product not OOS", oosFlagInO2OColl, equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] verify product is out of stock in SOLR$")
  public void checkProductIsOutOfStock(){
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          "/select",
          "id",
          1,
          "oos",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating isInStock field in SOLR failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();

      log.warn("-----Product {} Set non OOS before test---{}",searchServiceData.getQueryForReindex(),oosFlag);

      assertThat("Product non OOS",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes non oos event for the product$")
  public void searchConsumesNonOOSEvent(){

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
    kafkaHelper.publishOOSEvent(itemSku,"TH7-15791","nonOOS");

    try {
      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes in stock in SOLR$")
  public void checkProductIsNotOOSAfterEvent(){
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      assertThat("Product not OOS", oosFlag, equalTo(1));
      log.warn("-----IsInStock After reindex by event---{}", oosFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] force.stop flag is set to '(.*)'$")
  public void searchServiceForceStopFlagIsSetAccordingly(String flag) {
    configHelper.findAndUpdateConfig("force.stop.solr.updates",flag);
    configHelper.findAndUpdateConfig("process.delta.during.reindex","false");
  }


  @Given("^\\[search-service] inventory '(.*)' event is configured as whitelist$")
  public void searchServiceInventoryOosEventIsConfiguredAsWhitelist(String eventType){
    if(eventType.equals("oos"))
      configHelper.findAndUpdateConfig("whitelist.events","ProductOutOfStockEvent");
    else
      configHelper.findAndUpdateConfig("whitelist.events","ProductNonOutOfStockEvent");

  }

  @And("^\\[search-service] inventory '(.*)' event is not configured as whitelist$")
  public void searchServiceInventoryOosEventIsNotConfiguredAsWhitelist(String event){
    configHelper.findAndUpdateConfig("whitelist.events","abc");
  }

  @Then("^\\[search-service] product does not becomes oos in SOLR$")
  public void searchServiceProductDoesNotBecomesOosInSOLR(){
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      assertThat("Product OOS", oosFlag, equalTo(1));
      log.warn("-----Product does not become oos in SOLR---{}", oosFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      oosFlagInO2OColl = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION_O2O).get(0).getIsInStock();
      log.warn("-----IsInStock After reindex by event---{}", oosFlagInO2OColl);
      assertThat("Product not OOS", oosFlagInO2OColl, equalTo(1));
      log.warn("-----IsInStock After reindex by event---{}",oosFlagInO2OColl);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] events are stored in indexing_list_new collection and processed when job is run after turning off the flag$")
  public void searchServiceEventsAreStoredInIndexing_list_newCollectionAndProcessedWhenJobIsRun() {
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    FindIterable<Document> indexing_list_new = mongoHelper.getMongoDocumentByQuery(
        "indexing_list_new",
        "code",
        searchServiceData.getItemSkuForReindex());

    int size = 0;
    for (Document doc : indexing_list_new) {
      size++;
    }

    log.error("Size of indexing_list_new--{}", size);

    assertThat("Entry does not exists in collection", size, greaterThan(0));

    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> processingStoredDelta =
        searchServiceController.prepareRequestForProcessingStoredDelta();

    assertThat("Status Code Not 200",
        processingStoredDelta.getResponse().getStatusCode(),
        equalTo(200));

    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product does not becomes non oos in SOLR$")
  public void searchServiceProductDoesNotBecomesNonOosInSOLR() {
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          "/select",
          "isInStock",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getIsInStock();
      log.warn("-----Product does not become non oos in SOLR---{}", oosFlag);
      assertThat("Product Non OOS", oosFlag, equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
