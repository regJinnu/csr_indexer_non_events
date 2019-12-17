package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

@Slf4j
@CucumberStepsDefinition
public class DefaultCNCjobSteps {
  @Autowired
  KafkaHelper kafkaHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  ConfigHelper configHelper;

  @Autowired
  private SearchServiceController searchServiceController;


  @Given("^add event for an offline item such there is default prod change$")
  public void addEventForAnOfflineItemSuchThereIsDefaultProdChange() {

    searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
    searchServiceData.setDefCncPP(searchServiceProperties.get("defCncPP"));

    String query =
        "id:" + searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP();
    int status = 0;
    try {
      status = solrHelper.updateSolrDataForAutomation(query,
          SELECT_HANDLER,
          "id",
          1,
          "defCncOfferPrice",
          SOLR_DEFAULT_COLLECTION_CNC);

      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      List<SolrResults> solrProd = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,salePrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION_CNC);

      for (int i = 0; i < solrProd.size(); i++) {
        assertThat("Offer Price is not set", solrProd.get(i).getOfferPrice(), equalTo(3000.0));
        assertThat("List Price is not set", solrProd.get(i).getListPrice(), equalTo(3000.0));
        assertThat("Last updated Time is not set",
            solrProd.get(i).getLastUpdatedTime(),
            equalTo(Long.valueOf(1234)));
      }
      List<SolrResults> solrProdInNormalCollection = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION);
      assertThat(solrProdInNormalCollection.isEmpty(), equalTo(true));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^the default cnc job has ran and collections are committed$")
  public void theDefaultCncJobHasRan() throws Exception {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.defaultCncJob();
    searchServiceData.setSearchServiceResponse(response);
    Thread.sleep(20000);
    while(!Boolean.parseBoolean(configHelper.findConfigValue("enable.default.cnc.index"))) {
      Thread.sleep(60000);
    }
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
  }

  @Then("^verify that the default cnc product is updated$")
  public void verifyThatTheDefaultCncProductIsUpdated() {

    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    String Response = response.getResponseBody().getErrorMessage();
    assertThat("Response failed", response.getResponseBody().isSuccess(), equalTo(true));

    List<SolrResults> solrProdInNormalCollection = null;
    try {
      String query =
          "id:" + searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP();
      solrProdInNormalCollection = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (int i = 0; i < solrProdInNormalCollection.size(); i++) {

      assertThat(solrProdInNormalCollection.isEmpty(), equalTo(false));
      assertThat("Last updated Time is not set",
          solrProdInNormalCollection.get(i).getLastUpdatedTime(),
          not(equalTo(Long.valueOf(1234))));
    }

    ResponseApi responseApi;
    responseApi = searchServiceController.prepareRequestForIndexing("itemSkus",
        searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP());
    searchServiceData.setSearchServiceResponse(responseApi);

    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^add event for an offline item such there is default prod is deleted$")
  public void defaultProdIsDeleted() {
    searchServiceData.setDefCncExternalPickupPointCode(searchServiceProperties.get(
        "defCncExternalPickupPointCode"));
    searchServiceData.setDefCncMerchantCode(searchServiceProperties.get("defCncMerchantCode"));
    searchServiceData.setDefCncProductSku(searchServiceProperties.get("defCncProductSku"));
    searchServiceData.setDefCncOfferPrice(searchServiceProperties.get("defCncOfferPrice"));
    searchServiceData.setDefCncMerchantSku(searchServiceProperties.get("defCncMerchantSku"));
    searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
    searchServiceData.setDefCncPP(searchServiceProperties.get("defCncPP"));
    searchServiceData.setDefCncItemCode(searchServiceProperties.get("defCncItemCode"));

    Map<String, String> payload = new HashMap<>();
    payload.put("uniqueId",
        searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP());
    payload.put("merchantCode", searchServiceData.getDefCncMerchantCode());
    payload.put("itemSku", searchServiceData.getDefCncItemSku1());
    payload.put("itemCode", searchServiceData.getDefCncItemCode());
    payload.put("merchantSku", searchServiceData.getDefCncMerchantSku());
    payload.put("pickupPointCode", searchServiceData.getDefCncPP());
    payload.put("externalPickupPointCode", searchServiceData.getDefCncExternalPickupPointCode());
    payload.put("productSku", searchServiceData.getDefCncProductSku());
    payload.put("offerPrice", searchServiceData.getDefCncOfferPrice());


    kafkaHelper.publishOfflineItemChangeEventforDefCncJob(payload);
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^verify that product is deleted from respective collections as well$")
  public void productIsDeletedFromRespectiveCollections() {
    String query =
        "id:" + searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP();
    List<SolrResults> solrProdDeletionInNormalColl = null;
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrProdDeletionInNormalColl = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION);
      assertThat("Found product in Normal collection",
          solrProdDeletionInNormalColl.isEmpty(),
          equalTo(true));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^switch the force stop solr cnc updates config to (.*)")
  public void switchTheForceStopSolrCncUpdates(String flag) {
    searchServiceData.setDefCncUpdatesConfig(searchServiceProperties.get("defCncUpdatesConfig"));
    configHelper.findAndUpdateConfig(searchServiceData.getDefCncUpdatesConfig(), flag);
  }

  @When("^cnc related event has come to search$")
  public void cncRelatedEventHasComeToSearch() {

    searchServiceData.setDefCncExternalPickupPointCode(searchServiceProperties.get(
        "defCncExternalPickupPointCode"));
    searchServiceData.setDefCncMerchantCode(searchServiceProperties.get("defCncMerchantCode"));
    searchServiceData.setDefCncProductSku(searchServiceProperties.get("defCncProductSku"));
    searchServiceData.setDefCncOfferPrice(searchServiceProperties.get("defCncOfferPrice"));
    searchServiceData.setDefCncMerchantSku(searchServiceProperties.get("defCncMerchantSku"));
    searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
    searchServiceData.setDefCncPP(searchServiceProperties.get("defCncPP"));
    searchServiceData.setDefCncItemCode(searchServiceProperties.get("defCncItemCode"));

    Map<String, String> payload = new HashMap<>();
    payload.put("uniqueId",
        searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP());
    payload.put("merchantCode", searchServiceData.getDefCncMerchantCode());
    payload.put("itemSku", searchServiceData.getDefCncItemSku1());
    payload.put("itemCode", searchServiceData.getDefCncItemCode());
    payload.put("merchantSku", searchServiceData.getDefCncMerchantSku());
    payload.put("pickupPointCode", searchServiceData.getDefCncPP());
    payload.put("externalPickupPointCode", searchServiceData.getDefCncExternalPickupPointCode());
    payload.put("productSku", searchServiceData.getDefCncProductSku());
    payload.put("offerPrice", searchServiceData.getDefCncOfferPrice());

    kafkaHelper.publishOfflineItemChangeEventforDefCncJob(payload);
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^verify that event is not processed and stored in mongo$")
  public void verifyThatEventIsNotProcessedAndStoredInMongo() {
    Long count= mongoHelper.countOfRecordsInCollection("indexing_list_new");
    assertThat("Entry does not exists in collection",count, greaterThan(0L));
    System.out.println("TEST MONGO \n"+mongoHelper.countOfRecordsInCollection("indexing_list_new"));
    configHelper.findAndUpdateConfig("force.stop.solr.cnc.updates", "false");
  }

  @And("^after triggering delta job event is updated to solr$")
  public void afterTriggeringDeltaJobEventIsUpdatedToSolr() {
    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> processingStoredDelta =
        searchServiceController.prepareRequestForProcessingStoredDelta();
    assertThat("Status Code Not 200",
        processingStoredDelta.getResponse().getStatusCode(),
        equalTo(200));
    try {
      Thread.sleep(60000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^verify that product is deleted from cnc collection as well$")
  public void verifyThatProductIsDeletedFromCncCollection() {
    List<SolrResults> solrProdDeletionInCNCColl = null;
    List<SolrResults> solrProdDeletionInNormalColl = null;
    List<SolrResults> solrProdDeletionInO2OColl = null;
    String query =
        "id:" + searchServiceData.getDefCncItemSku1() + "-" + searchServiceData.getDefCncPP();
    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrProdDeletionInCNCColl = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION_CNC);
      assertThat("Found product in cnc collection",
          solrProdDeletionInCNCColl.isEmpty(),
          equalTo(true));

      solrProdDeletionInNormalColl = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION);
      assertThat("Found product in Normal collection",
          solrProdDeletionInNormalColl.isEmpty(),
          equalTo(true));

      solrProdDeletionInO2OColl = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice,lastUpdatedTime",
          10,
          SOLR_DEFAULT_COLLECTION_O2O);
      assertThat("Found product in O2O collection",
          solrProdDeletionInO2OColl.isEmpty(),
          equalTo(true));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

