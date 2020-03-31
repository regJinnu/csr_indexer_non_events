package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 20/03/20
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class OxfordEventSteps {

  @Autowired
  KafkaHelper kafkaHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  private SearchServiceController searchServiceController;


  @Given("^\\[search-service] fetch params required to send oxford update merchant event$")
  public void fetchDetails() {

    searchServiceData.setMerchantId(searchServiceProperties.get("defCncMerchant"));
    searchServiceData.setItemSkuForOffline(searchServiceProperties.get("itemSkuForOffline"));

    try {
      int statusOfNormalCollectionUpdate = solrHelper.updateSolrDataForAutomation(
          "itemSku:" + searchServiceData.getItemSkuForOffline(),
          SELECT_HANDLER,
          "id",
          2,
          "officialStore",
          SOLR_DEFAULT_COLLECTION);

      assertThat("Updating SOLR fields for test failed",
          statusOfNormalCollectionUpdate,
          equalTo(0));

      int statusOfCncCollectionUpdate = solrHelper.updateSolrDataForAutomation(
          "itemSku:" + searchServiceData.getItemSkuForOffline(),
          SELECT_HANDLER,
          "id",
          2,
          "officialStore",
          SOLR_DEFAULT_COLLECTION_CNC);

      assertThat("Updating SOLR fields for test failed", statusOfCncCollectionUpdate, equalTo(0));

    commitSOLR();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] send oxford update merchant event via kafka$")
  public void sendOxfordUpdateMerchantEvent() {
    kafkaHelper.publishOxfordFlagChange(searchServiceData.getMerchantId(),
        Collections.emptyList(),
        Collections.emptyList(),
        true);
    commitSOLR();
  }

  @Then("^\\[search-service] verify that official store fields are updated in solr for online item in '(.*)' processing$")
  public void verifyOfficialStoreForOnlineItem(String type) {
    commitSOLR();
    try {
      boolean officialFlag =
          solrHelper.getSolrProd("itemSku:" + searchServiceData.getItemSkuForOffline(),
              SELECT_HANDLER,
              "isOfficial",
              1,
              "cnc:false",
              SOLR_DEFAULT_COLLECTION).get(0).isOfficial();

      if(type.equals("direct"))
        assertThat("official flag is not true", officialFlag, equalTo(true));
      else
        assertThat("official flag is not true", officialFlag, equalTo(false));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] verify that official store fields are not updated in solr for offline item$")
  public void verifyOfficialStoreForOfflineItem() {
    commitSOLR();
    try {

      List<SolrResults> solrResultsList =
          solrHelper.getSolrProd("itemSku:" + searchServiceData.getItemSkuForOffline(),
              SELECT_HANDLER,
              "isOfficial",
              10,
              "cnc:true",
              SOLR_DEFAULT_COLLECTION_CNC);

      for (SolrResults solrResults : solrResultsList) {
        assertThat("official flag is not false", solrResults.isOfficial(), equalTo(false));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] fetch params required to send oxford update product event$")
  public void fetchStoreAndBrandDetails() {

    searchServiceData.setDefCncProductSku(searchServiceProperties.get("defCncProductSku"));

    try {
      int statusOfNormalCollectionUpdate =
          solrHelper.updateSolrDataForAutomation("sku:" + searchServiceData.getDefCncProductSku(),
              SELECT_HANDLER,
              "id",
              10,
              "officialStore",
              SOLR_DEFAULT_COLLECTION);

      assertThat("Updating SOLR fields for test failed",
          statusOfNormalCollectionUpdate,
          equalTo(0));

      int statusOfCncCollectionUpdate =
          solrHelper.updateSolrDataForAutomation("sku:" + searchServiceData.getDefCncProductSku(),
              SELECT_HANDLER,
              "id",
              10,
              "officialStore",
              SOLR_DEFAULT_COLLECTION_CNC);

      assertThat("Updating SOLR fields for test failed", statusOfCncCollectionUpdate, equalTo(0));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] send oxford update product event via kafka$")
  public void sendOxfordUpdateProductEvent() {
    kafkaHelper.publishOxfordSkuChange(searchServiceData.getDefCncProductSku(),
        "Test Brand",
        "Test Store");
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] verify brand and store fields are updated in solr for online item$")
  public void getBrandAndStoreContractForOnline() {
    try {

      List<SolrResults> solrResultsList =
          solrHelper.getSolrProd("sku:" + searchServiceData.getDefCncProductSku(),
              SELECT_HANDLER,
              "brandCatalog,storeCatalog",
              10,
              "cnc:false",
              SOLR_DEFAULT_COLLECTION);

      for (SolrResults solrResults : solrResultsList) {
        assertThat("brand catalog got updated",
            String.join(",", solrResults.getBrandCatalog()),
            equalTo("Test Brand"));
        assertThat("store catalog got updated",
            String.join(",", solrResults.getStoreCatalog()),
            equalTo("Test Store"));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @And("^\\[search-service] verify brand and store fields are not updated in solr for offline item$")
  public void getBrandAndStoreContractForOffline() {
    try {

      List<SolrResults> solrResultsList =
          solrHelper.getSolrProd("sku:" + searchServiceData.getDefCncProductSku(),
              SELECT_HANDLER,
              "brandCatalog,storeCatalog",
              10,
              "cnc:true",
              SOLR_DEFAULT_COLLECTION_CNC);

      for (SolrResults solrResults : solrResultsList) {
        assertThat("brand catalog got updated",
            String.join(",", solrResults.getBrandCatalog()),
            equalTo("abc"));
        assertThat("store catalog got updated",
            String.join(",", solrResults.getStoreCatalog()),
            equalTo("abc"));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void commitSOLR(){
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
