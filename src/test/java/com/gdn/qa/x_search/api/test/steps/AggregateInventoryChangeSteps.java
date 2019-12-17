package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberStepsDefinition
public class AggregateInventoryChangeSteps {

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  SearchServiceProperties searchServiceProperties;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  KafkaHelper kafkaHelper;

  long lastUpdatedTimeBeforeEvent;
  long lastUpdatedTimeAfterEvent;
  long lastUpdatedTimeBeforeEventO2O;
  long lastUpdatedTimeAfterEventO2O;
  long lastUpdatedTimeBeforeEventCNC;
  long lastUpdatedTimeAfterEventCNC;

  @Given("^\\[search-service] prepare request for processing aggregate inventory change event$")
  public void searchServicePrepareRequestForProcessingAggregateInventoryChangeEvent() {
    searchServiceData.setItemSku(searchServiceProperties.get("itemSkuForInventoryChange"));
    searchServiceData.setCnc(false);
    searchServiceData.setType("OFFLINE");
    searchServiceData.setLocation1("Jakarta");
    searchServiceData.setLocation2("Bogor");
    searchServiceData.setLocation3("Tangerang");
    searchServiceData.setStatus1("IN_STOCK");
    searchServiceData.setStatus2("OUT_OF_STOCK");
  }


  @And("^\\[search-service] set location info of test product with random values$")
  public void searchServiceSetLocationInfoOfTestProductWithRandomValues() throws Exception {
    String query = "id:" + searchServiceProperties.get("itemSkuForInventoryChange");
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "inventoryChange",
        SOLR_DEFAULT_COLLECTION);
    assertThat("Updating inventoryStockLocation info in SOLR doc failed", status, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    ArrayList<String> allLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "stockLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getStockLocation();
    lastUpdatedTimeBeforeEvent = System.currentTimeMillis();

    assertThat("Test Product not set in SOLR", allLocation, equalTo(null));
    assertThat("Test Product not set in SOLR", stockLocation, equalTo(null));

    int status1 = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "inventoryChange",
        SOLR_DEFAULT_COLLECTION_O2O);
    assertThat("Updating inventoryStockLocation info in SOLR doc failed", status1, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    ArrayList<String> allLocation1 =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation1 = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_O2O).get(0).getStockLocation();
    lastUpdatedTimeBeforeEventO2O = System.currentTimeMillis();

    assertThat("Test Product not set in SOLR", allLocation1, equalTo(null));
    assertThat("Test Product not set in SOLR", stockLocation1, equalTo(null));
  }

  @When("^\\[search-service] send request for processing aggregate inventory change event$")
  public void searchServiceSendRequestForProcessingAggregateInventoryChangeEvent()
      throws Exception {

    kafkaHelper.publishAggregateInventoryChangeEvent(searchServiceData.getItemSku(),
        searchServiceData.isCnc(),
        searchServiceData.getType(),
        searchServiceData.getLocation1(),
        searchServiceData.getLocation2(),
        searchServiceData.getLocation3(),
        searchServiceData.getStatus1(),
        searchServiceData.getStatus2());
    Thread.sleep(30000);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
  }

  @Then("^\\[search-service] aggregate inventory change event is processed and solr is updated$")
  public void searchServiceAggregateInventoryChangeEventIsProcessedAndSolrIsUpdated()
      throws Exception {

    String query = "id:" + searchServiceProperties.get("itemSkuForInventoryChange");
    ArrayList<String> allLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "stockLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getStockLocation();
    lastUpdatedTimeAfterEvent =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "lastUpdatedTime", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getLastUpdatedTime();
    assertThat("Test Product not updated after event processing",
        allLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta", "Bogor", "Tangerang"))));
    assertThat("Test Product not updated after event processing",
        stockLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta", "Tangerang"))));
    assertThat("LastUpdatedTime is not Updated",
        lastUpdatedTimeAfterEvent,
        greaterThan(lastUpdatedTimeBeforeEvent));

    ArrayList<String> allLocation1 =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation1 = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_O2O).get(0).getStockLocation();
    lastUpdatedTimeAfterEventO2O = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "lastUpdatedTime",
        1,
        SOLR_DEFAULT_COLLECTION_O2O).get(0).getLastUpdatedTime();
    assertThat("Test Product not updated after event processing",
        allLocation1,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta", "Bogor", "Tangerang"))));
    assertThat("Test Product not updated after event processing",
        stockLocation1,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta", "Tangerang"))));
    assertThat("LastUpdatedTime is not Updated",
        lastUpdatedTimeAfterEventO2O,
        greaterThan(lastUpdatedTimeBeforeEventO2O));
  }

  @Given("^\\[search-service] prepare request for processing aggregate inventory change event for cnc$")
  public void searchServicePrepareRequestForProcessingAggregateInventoryChangeEventForCnc() {
    searchServiceData.setItemSku(searchServiceProperties.get("itemSkuForInventoryChangeCNC"));
    searchServiceData.setCnc(true);
    searchServiceData.setType("OFFLINE");
    searchServiceData.setLocation1("Jakarta");
    searchServiceData.setPickupPointCode(searchServiceProperties.get("ppCode1ForInventoryChangeCNC"));
    searchServiceData.setLocation2("Bogor");
    searchServiceData.setPickupPointCode2(searchServiceProperties.get("ppCode2ForInventoryChangeCNC"));
    searchServiceData.setStatus1("IN_STOCK");
    searchServiceData.setStatus2("OUT_OF_STOCK");
  }

  @And("^\\[search-service] set location info of test product with random values in cnc collection$")
  public void searchServiceSetLocationInfoOfTestProductWithRandomValuesInCncCollection()
      throws Exception {

    String query = "id:" + searchServiceProperties.get("itemSkuForInventoryChangeCNC") + "-"
        + searchServiceProperties.get("ppCode1ForInventoryChangeCNC");
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "inventoryChange",
        SOLR_DEFAULT_COLLECTION_CNC);
    assertThat("Updating inventoryStockLocation info in SOLR doc failed", status, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    ArrayList<String> allLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getStockLocation();

    assertThat("Test Product not set in SOLR", allLocation, equalTo(null));
    assertThat("Test Product not set in SOLR", stockLocation, equalTo(null));

    String query1 = "id:" + searchServiceProperties.get("itemSkuForInventoryChangeCNC") + "-"
        + searchServiceProperties.get("ppCode2ForInventoryChangeCNC");
    int status1 = solrHelper.updateSolrDataForAutomation(query1,
        SELECT_HANDLER,
        "id",
        1,
        "inventoryChange",
        SOLR_DEFAULT_COLLECTION_CNC);
    assertThat("Updating inventoryStockLocation info in SOLR doc failed", status1, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    ArrayList<String> allLocation1 = solrHelper.getSolrProd(query1,
        SELECT_HANDLER,
        "allLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getAllLocation();
    ArrayList<String> stockLocation1 = solrHelper.getSolrProd(query1,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getStockLocation();

    assertThat("Test Product not set in SOLR", allLocation1, equalTo(null));
    assertThat("Test Product not set in SOLR", stockLocation1, equalTo(null));
    lastUpdatedTimeBeforeEventCNC = System.currentTimeMillis();
  }

  @When("^\\[search-service] send request for processing aggregate inventory change event for cnc$")
  public void searchServiceSendRequestForProcessingAggregateInventoryChangeEventForCnc()
      throws Exception {

    kafkaHelper.publishAggregateInventoryChangeEvent(searchServiceData.getItemSku(),
        searchServiceData.isCnc(),
        searchServiceData.getType(),
        searchServiceData.getLocation1(),
        searchServiceData.getStatus1(),
        searchServiceData.getPickupPointCode(),
        searchServiceData.getLocation2(),
        searchServiceData.getStatus2(),
        searchServiceData.getPickupPointCode2());
    Thread.sleep(20000);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
  }

  @Then("^\\[search-service] aggregate inventory change event is processed and solr cnc collection is updated$")
  public void searchServiceAggregateInventoryChangeEventIsProcessedAndSolrCncCollectionIsUpdated()
      throws Exception {

    String query = "id:" + searchServiceProperties.get("itemSkuForInventoryChangeCNC") + "-"
        + searchServiceProperties.get("ppCode1ForInventoryChangeCNC");
    ArrayList<String> allLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getStockLocation();
    lastUpdatedTimeAfterEventCNC = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "lastUpdatedTime",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getLastUpdatedTime();
    assertThat("Test Product not updated after event processing",
        allLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta"))));
    assertThat("Test Product not updated after event processing",
        stockLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta"))));
    assertThat("LastUpdatedTime is not Updated",
        lastUpdatedTimeAfterEventCNC,
        greaterThan(lastUpdatedTimeBeforeEventCNC));

    String query1 = "id:" + searchServiceProperties.get("itemSkuForInventoryChangeCNC") + "-"
        + searchServiceProperties.get("ppCode2ForInventoryChangeCNC");
    ArrayList<String> allLocation1 = solrHelper.getSolrProd(query1,
        SELECT_HANDLER,
        "allLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getAllLocation();
    ArrayList<String> stockLocation1 = solrHelper.getSolrProd(query1,
        SELECT_HANDLER,
        "stockLocation",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getStockLocation();
    lastUpdatedTimeAfterEventCNC = solrHelper.getSolrProd(query1,
        SELECT_HANDLER,
        "lastUpdatedTime",
        1,
        SOLR_DEFAULT_COLLECTION_CNC).get(0).getLastUpdatedTime();
    assertThat("Test Product not updated after event processing",
        allLocation1,
        equalTo(new ArrayList<>(Arrays.asList("Bogor"))));
    assertThat("Test Product not updated after event processing", stockLocation1, equalTo(null));
    assertThat("LastUpdatedTime is not Updated",
        lastUpdatedTimeAfterEventCNC,
        greaterThan(lastUpdatedTimeBeforeEventCNC));
  }

  @Then("^default product in normal collection should be updated with aggregate inventory change data$")
  public void defaultProductInNormalCollectionShouldBeUpdatedWithAggregateInventoryChangeData()
      throws Exception {

    String query = "id:" + searchServiceProperties.get("itemSkuForInventoryChangeCNC") + "-"
        + searchServiceProperties.get("ppCode1ForInventoryChangeCNC");
    ArrayList<String> allLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "allLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getAllLocation();
    ArrayList<String> stockLocation =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "stockLocation", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getStockLocation();
    lastUpdatedTimeAfterEventCNC =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "lastUpdatedTime", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getLastUpdatedTime();
    assertThat("Test Product not updated after event processing",
        allLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta", "Bogor"))));
    assertThat("Test Product not updated after event processing",
        stockLocation,
        equalTo(new ArrayList<>(Arrays.asList("Jakarta"))));
    assertThat("LastUpdatedTime is not Updated",
        lastUpdatedTimeAfterEventCNC,
        greaterThan(lastUpdatedTimeBeforeEventCNC));
  }
}