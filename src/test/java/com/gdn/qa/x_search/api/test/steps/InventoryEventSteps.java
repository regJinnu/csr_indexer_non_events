package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.RedisHelper;
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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.REDIS_PORT;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

  MongoHelper mongoHelper = new MongoHelper();

  @Given("^\\[search-service] verify product is in stock in SOLR$")
  public void checkProductIsInStock(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));

    try {
      
      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"nonOOS");
      assertThat("Updating isInStock field in SOLR failed",status,equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----Product Set non OOS before test---{}",oosFlag);
      assertThat("Product non OOS",oosFlag,equalTo(1));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes oos event for the product$")
  public void searchConsumesOOSEvent(){

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
   kafkaHelper.publishOOSEvent(itemSku,"TH7-15791","oos");

    try {
      Thread.sleep(10000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes oos in SOLR$")
  public void checkProductIsOOSAfterEvent(){

    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----IsInStock After reindex by event---{}",oosFlag);
      assertThat("Product not OOS",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] verify product is out of stock in SOLR$")
  public void checkProductIsOutOfStock(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));

    try {

      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"oos");
      assertThat("Updating isInStock field in SOLR failed",status,equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product OOS",oosFlag,equalTo(0));

      log.warn("-----Product Set OOS before test---{}",oosFlag);
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
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes in stock in SOLR$")
  public void checkProductIsNotOOSAfterEvent(){

    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS",oosFlag,equalTo(1));
      log.warn("-----IsInStock After reindex by event---{}",oosFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] force.stop flag is set to '(.*)'$")
  public void searchServiceForceStopFlagIsSetAccordingly(String flag) {
    mongoHelper.updateMongo("config_list","NAME","force.stop.solr.updates","VALUE",flag);
    mongoHelper.updateMongo("config_list","NAME","process.delta.during.reindex","VALUE","false");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RedisHelper.deleteAll(searchServiceProperties.get("REDIS_HOST"),REDIS_PORT);

  }


  @Given("^\\[search-service] inventory (.*) event is configured as whitelist$")
  public void searchServiceInventoryOosEventIsConfiguredAsWhitelist(String eventType){
    if(eventType.equals("oos"))
      mongoHelper.updateMongo("config_list","NAME","whitelist.events","VALUE","ProductOutOfStockEvent");
    else
      mongoHelper.updateMongo("config_list","NAME","whitelist.events","VALUE","ProductNonOutOfStockEvent");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RedisHelper.deleteAll(searchServiceProperties.get("REDIS_HOST"),REDIS_PORT);
  }

  @And("^\\[search-service] inventory '(.*)' event is not configured as whitelist$")
  public void searchServiceInventoryOosEventIsNotConfiguredAsWhitelist(String event){
    mongoHelper.updateMongo("config_list","NAME","whitelist.events","VALUE","abc");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RedisHelper.deleteAll(searchServiceProperties.get("REDIS_HOST"),REDIS_PORT);
  }

  @Then("^\\[search-service] product does not becomes oos in SOLR$")
  public void searchServiceProductDoesNotBecomesOosInSOLR(){
    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----Product does not become oos in SOLR---{}",oosFlag);
      assertThat("Product OOS",oosFlag,equalTo(1));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] events are stored in indexing_list_new collection and processed when job is run after turning off the flag$")
  public void searchServiceEventsAreStoredInIndexing_list_newCollectionAndProcessedWhenJobIsRun() {
    FindIterable<Document> indexing_list_new =
        mongoHelper.getMongoDocumentByQuery("indexing_list_new", "code", "TH7-15791-00075-00001");

    int size = 0;
    for (Document doc:indexing_list_new
         ) {
      size++;
    }

    log.error("Size of indexing_list_new--{}",size);

    assertThat("Entry does not exists in collection",size,equalTo(1));

    mongoHelper.updateMongo("config_list","NAME","force.stop.solr.updates","VALUE","false");

    RedisHelper.deleteAll(searchServiceProperties.get("REDIS_HOST"),REDIS_PORT);

    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> processingStoredDelta =
        searchServiceController.prepareRequestForProcessingStoredDelta();

    assertThat("Status Code Not 200", processingStoredDelta.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(40000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product does not becomes non oos in SOLR$")
  public void searchServiceProductDoesNotBecomesNonOosInSOLR(){
    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----Product does not become non oos in SOLR---{}",oosFlag);
      assertThat("Product Non OOS",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
