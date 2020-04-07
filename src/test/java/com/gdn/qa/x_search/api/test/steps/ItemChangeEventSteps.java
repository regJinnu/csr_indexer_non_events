package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
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
import com.gdn.x.product.domain.event.enums.ItemChangeEventType;
import com.gdn.x.product.domain.event.model.*;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.bson.Document;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static com.gdn.x.product.domain.event.enums.ItemChangeEventType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * @author kumar on 20/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class ItemChangeEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  ConfigHelper configHelper;

  private Double offerPrice;
  private Double listPrice;
  private Double salePrice;
  private Double discount;
  private String discountString;
  private Long lastUpdatedTime;

  private Double offerPriceInO2O;
  private Double listPriceInO2O;
  private Double salePriceInO2O;
  private Double discountInO2O;
  private String discountStringInO2O;
  private Long lastUpdatedTimeInO2O;

  private int status = 0;
  private int statusForO2O = 0;


  @Given("^\\[search-service] change the price of the sku in SOLR$")
  public void updateTestDataToSolr() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    try {
      int statusOfNormalCollectionUpdate =
          solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
              SELECT_HANDLER,
              "id",
              1,
              "price",
              SOLR_DEFAULT_COLLECTION);
      assertThat("Updating SOLR fields for test failed",
          statusOfNormalCollectionUpdate,
          equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "offerPrice,listPrice,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      double offerPrice = solrResults.getOfferPrice();

      double listPrice = solrResults.getListPrice();

      Long lastUpdatedTime = solrResults.getLastUpdatedTime();

      log.warn("------Normal coll offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
      assertThat("offer price not set", offerPrice, equalTo(4545455.45));
      assertThat("list price not set", listPrice, equalTo(4545455.50));
      assertThat("Test Product not set in SOLR", lastUpdatedTime, equalTo(1234l));

      int statusOfO2OCollectionUpdate =
          solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
              SELECT_HANDLER,
              "id",
              1,
              "price",
              SOLR_DEFAULT_COLLECTION_O2O);
      assertThat("Updating SOLR fields for test failed", statusOfO2OCollectionUpdate, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "offerPrice,listPrice,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      offerPriceInO2O = solrResultsO2O.getOfferPrice();

      listPriceInO2O = solrResultsO2O.getListPrice();

      long lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

      log.warn("------O2O COLL offerPrice--{}---listPrice--{}---lastUpdatedTimeInO2O-{}",
          offerPriceInO2O,
          listPriceInO2O,
          lastUpdatedTimeInO2O);

      assertThat("offer price not set", offerPriceInO2O, equalTo(4545455.45));
      assertThat("list price not set", listPriceInO2O, equalTo(4545455.50));
      assertThat("Test Product not set in SOLR", lastUpdatedTimeInO2O, equalTo(1234l));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] change the price of the sku in Normal and '(.*)' collection$")
  public void changeThePriceOfTheSkuInNormalAndOtherCollection(String other) {
    resetConfigs();
    if (other.equals("O2O")) {
      searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
      searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
      searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
      searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

      int statusOfNormalCollectionUpdate = 0;
      try {
        statusOfNormalCollectionUpdate =
            solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
                SELECT_HANDLER,
                "id",
                1,
                "price",
                SOLR_DEFAULT_COLLECTION);
        assertThat("Updating SOLR fields for test failed",
            statusOfNormalCollectionUpdate,
            equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

        SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "offerPrice,listPrice",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION).get(0);

        double offerPrice = solrResults.getOfferPrice();

        double listPrice = solrResults.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPrice, equalTo(4545455.45));
        assertThat("list price not set", listPrice, equalTo(4545455.50));

        int statusOfO2OCollectionUpdate =
            solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
                SELECT_HANDLER,
                "id",
                1,
                "price",
                SOLR_DEFAULT_COLLECTION_O2O);
        assertThat("Updating SOLR fields for test failed", statusOfO2OCollectionUpdate, equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

        SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "offerPrice,listPrice",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_O2O).get(0);

        double offerPriceInO2Ocoll = solrResultsO2O.getOfferPrice();

        double listPriceInO2Ocoll = solrResultsO2O.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPriceInO2Ocoll, equalTo(4545455.45));
        assertThat("list price not set", listPriceInO2Ocoll, equalTo(4545455.50));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (other.equals("CNC")) {
      searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
      searchServiceData.setDefCncPP(searchServiceProperties.get("defCncPP"));
      searchServiceData.setDefCncProductSku(searchServiceProperties.get("defCncProductSku"));
      searchServiceData.setDefCncProductCode(searchServiceProperties.get("defCncProductCode"));

      int statusOfNormalCollectionUpdate = 0;
      try {
        statusOfNormalCollectionUpdate =
            solrHelper.updateSolrDataForAutomation("id:" + searchServiceData.getDefCncItemSku1(),
                SELECT_HANDLER,
                "id",
                1,
                "price",
                SOLR_DEFAULT_COLLECTION);
        assertThat("Updating SOLR fields for test failed",
            statusOfNormalCollectionUpdate,
            equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSku1(),
                SELECT_HANDLER,
                "offerPrice,listPrice",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);

        double offerPrice = solrResults.getOfferPrice();

        double listPrice = solrResults.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPrice, equalTo(4545455.45));
        assertThat("list price not set", listPrice, equalTo(4545455.50));

        int statusOfCNCCollectionUpdate = solrHelper.updateSolrDataForAutomation(
            "id:" + searchServiceData.getDefCncItemSku1() + searchServiceData.getPickupPointCode(),
            SELECT_HANDLER,
            "id",
            1,
            "price",
            SOLR_DEFAULT_COLLECTION_CNC);
        assertThat("Updating SOLR fields for test failed", statusOfCNCCollectionUpdate, equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);

        SolrResults solrResultsCNC =
            solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSku1(),
                SELECT_HANDLER,
                "offerPrice,listPrice",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION_CNC).get(0);

        double offerPriceInCNCcoll = solrResultsCNC.getOfferPrice();

        double listPriceInCNCcoll = solrResultsCNC.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPriceInCNCcoll, equalTo(4545455.45));
        assertThat("list price not set", listPriceInCNCcoll, equalTo(4545455.50));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @And("^\\[search-service] price information is updated with itemChangeEventType and '(.*)' discount schedule and '(.*)'$")
  public void priceInformationIsUpdatedForitemChangeEventTypeAndNoDiscountSchedule(String type,
      String status) {
    try {

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "offerPrice,listPrice,salePrice,discount,discountString,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      offerPrice = solrResults.getOfferPrice();

      listPrice = solrResults.getListPrice();

      salePrice = solrResults.getSalePrice();

      discount = solrResults.getDiscount();

      discountString = solrResults.getDiscountString();

      lastUpdatedTime = solrResults.getLastUpdatedTime();


      log.warn("------offerPrice--{}---listPrice--{}---salePrice--{}---lastUpdatedTime---{}---",
          offerPrice,
          listPrice,
          salePrice,
          lastUpdatedTime);
      log.warn("------discount--{}---discountString--{}---}", discount, discountString);

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "offerPrice,listPrice,salePrice,discount,discountString,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      offerPriceInO2O = solrResultsO2O.getOfferPrice();

      listPriceInO2O = solrResultsO2O.getListPrice();

      salePriceInO2O = solrResultsO2O.getSalePrice();

      discountInO2O = solrResultsO2O.getDiscount();

      discountStringInO2O = solrResultsO2O.getDiscountString();

      lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

      log.warn(
          "------offerPriceInO2O--{}---listPriceInO2O--{}---salePriceInO2O--{}-lastUpdatedTimeInO2O--{}--",
          offerPriceInO2O,
          listPriceInO2O,
          salePriceInO2O,
          lastUpdatedTimeInO2O);
      log.warn("------discountInO2O--{}---discountStringInO2O--{}---}",
          discountInO2O,
          discountStringInO2O);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (type.toLowerCase().trim().equals("no") && status.equals("false")) {
      assertThat("offer price not set", offerPrice, equalTo(9000.0));
      assertThat("list price not set", listPrice, equalTo(10000.0));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("offer price not set", offerPriceInO2O, not(equalTo(9000.0)));
      assertThat("list price not set", listPriceInO2O, not(equalTo(10000.0)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, (equalTo(1234L)));
    }

    if (type.toLowerCase().trim().equals("no") && status.equals("true")) {
      assertThat("offer price not set", offerPrice, equalTo(9000.0));
      assertThat("list price not set", listPrice, equalTo(10000.0));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("offer price not set", offerPriceInO2O, equalTo(9000.0));
      assertThat("list price not set", listPriceInO2O, equalTo(10000.0));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, not(equalTo(1234L)));
    }

    if (type.toLowerCase().trim().equals("valid") && status.equals("false")) {
      assertThat("sale price not set", salePrice, equalTo(8900.0));
      assertThat("discount not set", discountString, equalTo("11"));
      assertThat("discount not set", discount, equalTo(11.0));
      assertThat("sale price not set", salePriceInO2O, not(equalTo(8900.0)));
      assertThat("discount not set", discountStringInO2O, not(equalTo("11")));
      assertThat("discount not set", discountInO2O, not(equalTo(11.0)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, (equalTo(1234L)));
    }

    if (type.toLowerCase().trim().equals("valid") && status.equals("true")) {
      assertThat("sale price not set", salePrice, equalTo(8900.0));
      assertThat("discount not set", discountString, equalTo("11"));
      assertThat("discount not set", discount, equalTo(11.0));
      assertThat("sale price not set", salePriceInO2O, equalTo(8900.0));
      assertThat("discount not set", discountStringInO2O, equalTo("11"));
      assertThat("discount not set", discountInO2O, equalTo(11.0));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, not(equalTo(1234L)));
    }

    if (type.toLowerCase().trim().equals("invalid") && status.equals("false")) {
      assertThat("sale price not set", salePrice, equalTo(9000.0));
      assertThat("discount not set", discountString, equalTo("10"));
      assertThat("discount not set", discount, equalTo(10.0));
      assertThat("sale price not set", salePriceInO2O, not(equalTo(9000.0)));
      assertThat("discount not set", discountStringInO2O, not(equalTo("10")));
      assertThat("discount not set", discountInO2O, not(equalTo(10.0)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, (equalTo(1234L)));
    }

    if (type.toLowerCase().trim().equals("invalid") && status.equals("true")) {
      assertThat("sale price not set", salePrice, equalTo(9000.0));
      assertThat("discount not set", discountString, equalTo("10"));
      assertThat("discount not set", discount, equalTo(10.0));
      assertThat("sale price not set", salePriceInO2O, equalTo(9000.0));
      assertThat("discount not set", discountStringInO2O, equalTo("10"));
      assertThat("discount not set", discountInO2O, equalTo(10.0));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, not(equalTo(1234L)));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, not(equalTo(1234L)));
    }
    unInitialize();
  }

  @Given("^\\[search-service] test product is added in SOLR for '(.*)'$")
  public void addTestDataToSolrBeforeItemChangeEvent(String eventType) {

    resetConfigs();

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION);

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU_2,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION);

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION_O2O);

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU_2,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION_O2O);

    //For Deleting CNC Doc creating event

    searchServiceData.setDefCncExternalPickupPointCode(searchServiceProperties.get(
        "defCncExternalPickupPointCode"));
    searchServiceData.setDefCncMerchantCode(searchServiceProperties.get("defCncMerchantCode"));
    searchServiceData.setDefCncProductSku(searchServiceProperties.get("defCncProductSku"));
    searchServiceData.setDefCncOfferPrice(searchServiceProperties.get("defCncOfferPrice"));
    searchServiceData.setDefCncMerchantSku(searchServiceProperties.get("defCncMerchantSku"));
    searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
    searchServiceData.setDefCncPP(searchServiceProperties.get("defCncPP"));
    searchServiceData.setDefCncItemCode(searchServiceProperties.get("defCncItemCode"));

    try {
      assertThat("Test Data Not inserted in SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00001",
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,
              Collections.emptyList()),
          equalTo(1L));
      assertThat("Test Data Not inserted in SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00001",
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,
              Collections.emptyList()),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with isArchived is set to true$")
  public void searchConsumesItemChangeEventWithArchivedTrue() {

    kafkaHelper.publishItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        true,
        true,
        Collections.EMPTY_LIST,
        Collections.EMPTY_SET,
        false,
        new PristineDataItemEventModel(),
        Collections.EMPTY_SET);

    // Adding event to delete from CNC collection

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
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes product change event for that sku wrt normal and '(.*)' collection$")
  public void consumesProductChangeEventForThatSkuWrtNormalAndOtherCollection(String other) {
    if (other.equals("O2O")) {
      kafkaHelper.publishProductChangeEvent(searchServiceData.getProductCodeForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          true);

      try {
        Thread.sleep(90000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (other.equals("CNC")) {
      kafkaHelper.publishProductChangeEvent(searchServiceData.getDefCncProductCode(),
          searchServiceData.getDefCncProductSku(),
          false,
          true);

      try {
        Thread.sleep(90000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @When("^\\[search-service] consumes item change event with markForDelete set to true$")
  public void searchConsumesProductChangeEventWithMarkForDelete() {

    kafkaHelper.publishProductChangeEvent(DANGLING_JOB_PRODUCTCODE,
        DANGLING_JOB_PRODUCTSKU,
        true,
        true);

    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes only the item sku of test product from SOLR$")
  public void checkItemChangeOnlyDeletesItemSku() {
    try {
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU_2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,Collections.emptyList()),
          equalTo(1L));
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU_2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,Collections.emptyList()),
          equalTo(1L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_CNC_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_CNC_ITEMSKU2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,Collections.emptyList()),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes the test product from SOLR$")
  public void checkTestProdIsDeleted() {
    try {
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU_2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,Collections.emptyList()),
          equalTo(0L));

      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU_2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,Collections.emptyList()),
          equalTo(0L));

      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,Collections.emptyList()),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU_2,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,Collections.emptyList()),
          equalTo(0L));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] Db entry is created for the Sku in deleted product collection$")
  public void checkDBEntryCreatedForSkuInDeletedProductCollection() {

    int count = 0;

    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("deleted_products", "_id", "AAA-60015-00008");
    for (Document doc : mongoDocumentByQuery) {
      count++;
    }
    assertThat("DB entries created in Deleted Collection", count, equalTo(1));

  }

  @Then("^\\[search-service] Db entry is created for the productCode in deleted product collection$")
  public void checkDBEntryCreatedForProdCodeInDeletedProductCollection() {

    int count = 0;

    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("deleted_products", "_id", "MTA-66666");
    for (Document doc : mongoDocumentByQuery) {
      count++;
    }
    assertThat("DB entries created in Deleted Collection", count, equalTo(1));

  }

  @When("^\\[search-service] consumes item change event with price change and '(.*)' discount schedule and '(.*)'$")
  public void itemChangeEventWithPriceChangeInItemChangeEventTypeAndNoDiscountSchedule(String type,
      String status) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet<>();
    DiscountPrice discountPrice = new DiscountPrice();
    if (type.toLowerCase().trim().equals("valid")) {
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start = dtOrg.minusDays(1);
      DateTime end = dtOrg.plusDays(1);
      discountPrice.setDiscountPrice(100);
      discountPrice.setStartDateTime(start.toDate());
      discountPrice.setEndDateTime(end.toDate());
      List<DiscountPrice> discountPriceList = new ArrayList<>();
      discountPriceList.add(discountPrice);
      price.setListOfDiscountPrices(discountPriceList);
    } else if (type.toLowerCase().trim().equals("invalid")) {
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start = dtOrg.minusDays(2);
      DateTime end = dtOrg.minusDays(1);
      discountPrice.setDiscountPrice(200);
      discountPrice.setStartDateTime(start.toDate());
      discountPrice.setEndDateTime(end.toDate());
      List<DiscountPrice> discountPriceList = new ArrayList<>();
      discountPriceList.add(discountPrice);
      price.setListOfDiscountPrices(discountPriceList);
    }
    priceSet.add(price);
    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        priceSet,
        Boolean.parseBoolean(status),
        new PristineDataItemEventModel(),
        Collections.EMPTY_SET);
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] price information is properly updated for Sku in Normal and '(.*)'collection$")
  public void priceInformationIsUpdatedForSku(String other) {
    double offerPrice = 0;
    if (other.equals("O2O")) {
      try {

        SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "offerPrice,listPrice",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION).get(0);

        offerPrice = solrResults.getOfferPrice();

        double listPrice = solrResults.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPrice, not(equalTo(4545455.45)));
        assertThat("list price not set", listPrice, not(equalTo(4545455.50)));

        SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "offerPrice,listPrice",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_O2O).get(0);

        double offerPriceInO2Ocoll = solrResultsO2O.getOfferPrice();

        double listPriceInO2Ocoll = solrResultsO2O.getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPriceInO2Ocoll, not(equalTo(4545455.45)));
        assertThat("list price not set", listPriceInO2Ocoll, not(equalTo(4545455.50)));

      } catch (Exception e) {
        e.printStackTrace();
      }

      if (other.equals("CNC")) {
        try {

          SolrResults solrResults =
              solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSku1(),
                  SELECT_HANDLER,
                  "offerPrice,listPrice",
                  1,
                  Collections.emptyList(),
                  SOLR_DEFAULT_COLLECTION).get(0);

          offerPrice = solrResults.getOfferPrice();

          double listPrice = solrResults.getListPrice();

          log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);

          assertThat("offer price not set", offerPrice, not(equalTo(4545455.45)));
          assertThat("list price not set", listPrice, not(equalTo(4545455.50)));

          SolrResults solrResultsCNC = solrHelper.getSolrProd(
              "id:" + searchServiceData.getDefCncItemSku1()
                  + searchServiceData.getPickupPointCode(),
              SELECT_HANDLER,
              "offerPrice,listPrice",
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION_CNC).get(0);

          double offerPriceInCNCcoll = solrResultsCNC.getOfferPrice();

          double listPriceInCNCcoll = solrResultsCNC.getListPrice();

          log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
          assertThat("offer price not set", offerPriceInCNCcoll, not(equalTo(4545455.45)));
          assertThat("list price not set", listPriceInCNCcoll, not(equalTo(4545455.50)));

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Given("^\\[search-service] update the off2On field in SOLR for the sku$")
  public void searchServiceUpdateTheOffOnFieldInSOLRForTheSku() {

    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    searchServiceData.setNormalProductItemsku(searchServiceProperties.get("normalProductItemsku"));
    searchServiceData.setNormalProductSku(searchServiceProperties.get("normalProductSku"));
    resetConfigs();
    try {
      status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "offToOn",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "off2On,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int off2On = solrResults.getOff2On();

      lastUpdatedTime = solrResults.getLastUpdatedTime();

      log.warn("------off2on--{}------lastUpdatedTime------{}-----}", off2On, lastUpdatedTime);
      assertThat("off2on not set", off2On, equalTo(4));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTime, (equalTo(1234l)));


      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "offToOn",
          SOLR_DEFAULT_COLLECTION_O2O);
      assertThat("Updating SOLR fields for test failed In O2O coll", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "off2On,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      int off2OnInO2O = solrResultsO2O.getOff2On();

      lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

      log.warn("------off2on--{}------lastUpdatedTime------{}-----}", off2On, lastUpdatedTimeInO2O);
      assertThat("off2on not set InO2O", off2OnInO2O, equalTo(4));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInO2O, (equalTo(1234l)));

      int statusOfNormalProd =
          solrHelper.updateSolrDataForAutomation(searchServiceData.getNormalProductItemsku(),
              SELECT_HANDLER,
              "id",
              1,
              "offToOn",
              SOLR_DEFAULT_COLLECTION);
      assertThat("Updating SOLR fields for test failed In O2O coll",
          statusOfNormalProd,
          equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults1 = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "off2On,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      int statusOfNormalProdInSolr = solrResults1.getOff2On();

      Long lastUpdatedTimeInNormalProd = solrResults1.getLastUpdatedTime();

      log.warn("------off2on--{}------lastUpdatedTime------{}-----}",
          off2On,
          lastUpdatedTimeInNormalProd);
      assertThat("off2on not set InO2O", off2OnInO2O, equalTo(4));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInNormalProd, (equalTo(1234l)));

      log.warn("------off2on--{}-----lastUpdatedTimeInNormalProd{}---------}",
          statusOfNormalProdInSolr,
          lastUpdatedTimeInNormalProd);
      assertThat("off2on not set InO2O", statusOfNormalProdInSolr, equalTo(4));
      assertThat("lastUpdatedTime not set in SOLR", lastUpdatedTimeInNormalProd, (equalTo(1234l)));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as OFFLINE_ITEM_FLAG_CHANGE and offToOn flag value as '(.*)'$")
  public void eventWithItemChangeEventTypeAsOff2On(boolean flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(OFFLINE_ITEM_FLAG_CHANGE);
    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        Collections.EMPTY_SET,
        flag,
        new PristineDataItemEventModel(),
        Collections.EMPTY_SET);
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] o2o flag is updated to '(.*)'$")
  public void checkO2OFlag(boolean flag) {
    try {
      if (flag) {
        SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "off2On,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION).get(0);

        int off2On = solrResults.getOff2On();
        lastUpdatedTime = solrResults.getLastUpdatedTime();

        log.warn("------off2on--{}----lastUpdatedTime{}--", off2On, lastUpdatedTime);
        assertThat("off2on not set", off2On, equalTo(1));

        SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "off2On,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_O2O).get(0);

        int off2OnInO2O = solrResultsO2O.getOff2On();

        lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

        log.warn("------off2on In O2O--{}---lastUpdatedTimeInO2O---{}------",
            off2OnInO2O,
            lastUpdatedTimeInO2O);
        assertThat("off2on not set In O2O", off2OnInO2O, equalTo(1));
        assertThat("last updated time is not set", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("last updated time is not set", lastUpdatedTimeInO2O, not(equalTo(1234l)));

      }
      if (!flag) {
        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getNormalProductItemsku(),
                SELECT_HANDLER,
                "off2On,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);

        int off2OnInNormalColl =solrResults.getOff2On();

        lastUpdatedTime = solrResults.getLastUpdatedTime();

        log.warn("------off2on--{}------", off2OnInNormalColl);
        assertThat("off2on not set", off2OnInNormalColl, equalTo(0));
        assertThat("last updated time is not set", lastUpdatedTime, not(equalTo(1234l)));
      }
      unInitialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] check isSynchronised field in SOLR for the sku$")
  public void searchServiceUpdateIsSynchronisedFieldInSOLRForTheSku() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      boolean isSynchronised = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isSynchronised",
          1,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).isSynchronised();

      assertThat("isSynchronised not set to true", isSynchronised, equalTo(true));

      boolean isSynchronisedInO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isSynchronised",
          1,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0).isSynchronised();
      assertThat("isSynchronised not set to true In O2O", isSynchronisedInO2O, equalTo(true));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^\\[search-service] consumes item change event with itemChangeEventType as SYNC_UNSYNC_FLAG_CHANGE '(.*)' PristineDataItem$")
  public void itemChangeEventWithTypeAsSYNC_UNSYNC(String type) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(SYNC_UNSYNC_FLAG_CHANGE);
    if (type.toLowerCase().trim().equals("without"))
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          false,
          new PristineDataItemEventModel(),
          Collections.EMPTY_SET);
    else {
      PristineDataItemEventModel pristineDataItemModel = new PristineDataItemEventModel();
      pristineDataItemModel.setPristineId(PRISTINE_ID);
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          false,
          pristineDataItemModel,
          Collections.EMPTY_SET);
    }
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes itemChangeEvent as SYNC_UNSYNC_FLAG_CHANGE '(.*)' PristineDataItem and offToOn flag as '(.*)'$")
  public void searchServiceConsumesItemChangeEventAsSYNC_UNSYNC_FLAG_CHANGE(String type,
      String flag) {

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(SYNC_UNSYNC_FLAG_CHANGE);

    PristineDataItemEventModel pristineDataItemModel = new PristineDataItemEventModel();
    pristineDataItemModel.setPristineId(PRISTINE_ID);

    if (type.toLowerCase().trim().equals("without") && (flag.equals("false"))) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          false,
          new PristineDataItemEventModel(),
          Collections.EMPTY_SET);
    }
    if (type.toLowerCase().trim().equals("without") && (flag.equals("true"))) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          true,
          new PristineDataItemEventModel(),
          Collections.EMPTY_SET);
    }

    if (type.toLowerCase().trim().equals("with") && (flag.equals("false"))) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          false,
          pristineDataItemModel,
          Collections.EMPTY_SET);
    }

    if (type.toLowerCase().trim().equals("with") && (flag.equals("true"))) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          true,
          pristineDataItemModel,
          Collections.EMPTY_SET);
    }
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] isSynchronised flag is updated to false and offToOn flag as '(.*)'$")
  public void isSynchronisedFlagIsUpdatedToFalseAndOffToOnFlagAsFlag(String flag) {
    try {
      boolean isSynchronised = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isSynchronised",
          1,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).isSynchronised();
      assertThat("isSynchronised is set to true", isSynchronised, equalTo(false));

      if (flag.equals("true")) {
        boolean isSynchronisedInO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "isSynchronised",
            1,Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_O2O).get(0).isSynchronised();
        assertThat("isSynchronised is set to true In O2O", isSynchronisedInO2O, equalTo(false));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @And("^\\[search-service] level(\\d+)Id is set to '(.*)' and offToOn flag as '(.*)'$")
  public void searchServiceLevelIdIsSetToPristineIdAndOffToOnFlagAsFlag(int arg0,
      String type,
      String flag) {

    try {
      String level0Id = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "level0Id",
          1,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0).getLevel0Id();

      String level0IdInO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "level0Id",
          1,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0).getLevel0Id();

      if (type.trim().toLowerCase().equals("productsku") && (flag.equals("false"))) {
        assertThat("Level0Id is not set to sku",
            level0Id,
            equalTo(searchServiceData.getSkuForReindex()));
      }
      if (type.trim().toLowerCase().equals("productsku") && (flag.equals("true"))) {
        assertThat("Level0Id is not set to sku",
            level0Id,
            equalTo(searchServiceData.getSkuForReindex()));
        assertThat("Level0Id is not set to sku",
            level0IdInO2O,
            equalTo(searchServiceData.getSkuForReindex()));
      }
      if (type.trim().toLowerCase().equals("PristineId") && (flag.equals("false"))) {
        assertThat("Level0Id is not set to Pristine Id", level0Id, equalTo(PRISTINE_ID));
      }
      if (type.trim().toLowerCase().equals("PristineId") && (flag.equals("true"))) {
        assertThat("Level0Id is not set to Pristine Id", level0Id, equalTo(PRISTINE_ID));
        assertThat("Level0Id is not set to Pristine Id", level0IdInO2O, equalTo(PRISTINE_ID));
      }
      unInitialize();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] check name and level(\\d+)Id field in SOLR for the sku$")
  public void searchServiceCheckNameAndLevelIdFieldInSOLRForTheSku(int arg0) {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {
      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "nameSearch,level0Id",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      String name = solrResults.getNameSearch();

      String level0Id = solrResults.getLevel0Id();

      assertThat("Name is not set", name, not(equalTo("Pristine Product Testing")));
      assertThat("Level0Id is not set",
          level0Id,
          equalTo(searchServiceData.getProductCodeForReindex()));

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "nameSearch,level0Id",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      String nameInO2O = solrResultsO2O.getNameSearch();

      String level0IdInO2O = solrResultsO2O.getLevel0Id();

      assertThat("Name is not set", nameInO2O, not(equalTo("Pristine Product Testing")));
      assertThat("Level0Id is not set",
          level0IdInO2O,
          equalTo(searchServiceData.getProductCodeForReindex()));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE and offToOn flag as '(.*)'$")
  public void consumesItemChangeEventWithItemChangeEventTypeAsPRISTINE_MAPPING_CHANGE(String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(PRISTINE_MAPPING_CHANGE);

    SalesCategorySequence salesCategorySequence = new SalesCategorySequence();
    salesCategorySequence.setSequence(10);
    salesCategorySequence.setCategoryCode("TE-100003");
    List<SalesCategorySequence> salesCategorySequences = new ArrayList<>();
    salesCategorySequences.add(salesCategorySequence);


    SalesCategorySequence salesCategorySequenceOldC1 = new SalesCategorySequence();
    salesCategorySequenceOldC1.setCategoryCode("54912");
    salesCategorySequenceOldC1.setSequence(0);

    SalesCategorySequence salesCategorySequenceOldC2 = new SalesCategorySequence();
    salesCategorySequenceOldC2.setCategoryCode("54913");
    salesCategorySequenceOldC2.setSequence(0);

    SalesCategorySequence salesCategorySequenceOldC3 = new SalesCategorySequence();
    salesCategorySequenceOldC3.setCategoryCode("54914");
    salesCategorySequenceOldC3.setSequence(0);

    List<SalesCategorySequence> oldSalesCategorySequences = new ArrayList<>();
    oldSalesCategorySequences.add(salesCategorySequenceOldC1);
    oldSalesCategorySequences.add(salesCategorySequenceOldC2);
    oldSalesCategorySequences.add(salesCategorySequenceOldC3);

    List<ItemCategoryVO> itemCategories = new ArrayList<>();

    ItemCategoryVO c1 = new ItemCategoryVO();
    c1.setProductCategoryCode("TE-100001");
    c1.setCategory("Test Category C1");
    c1.setLevel(1);

    // itemCategories.add(c1); This is commented to handle bug SEARCH-2143 which will be fixed in future sprint

    ItemCategoryVO c2 = new ItemCategoryVO();
    c2.setProductCategoryCode("TE-100002");
    c2.setCategory("Test Category C2");
    c2.setLevel(2);

    //itemCategories.add(c2); This is commented to handle bug SEARCH-2143 which will be fixed in future sprint

    ItemCategoryVO c3 = new ItemCategoryVO();
    c3.setProductCategoryCode("TE-100003");
    c3.setCategory("Test Category C3");
    c3.setLevel(3);

    itemCategories.add(c1);
    itemCategories.add(c2);
    itemCategories.add(c3);

    ItemCatalogVO itemCatalogVO = new ItemCatalogVO();
    itemCatalogVO.setCatalogId("12051");
    itemCatalogVO.setItemCategories(itemCategories);

    List<ItemCatalogVO> pristineCategoriesHierarchy = new ArrayList<>();
    pristineCategoriesHierarchy.add(itemCatalogVO);

    PristineDataItemEventModel pristineDataItemModel = new PristineDataItemEventModel();
    pristineDataItemModel.setPristineId("PRI-0000-0001");
    pristineDataItemModel.setPristineProductName("Pristine Product Testing");
    pristineDataItemModel.setSalesCategorySequences(salesCategorySequences);
    pristineDataItemModel.setPristineCategoriesHierarchy(pristineCategoriesHierarchy);
    pristineDataItemModel.setOldSalesCategorySequences(oldSalesCategorySequences);

    if (flag.equals("false")) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          false,
          pristineDataItemModel,
          Collections.EMPTY_SET);
    }
    if (flag.equals("true")) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          itemChangeEventStepsList,
          Collections.EMPTY_SET,
          true,
          pristineDataItemModel,
          Collections.EMPTY_SET);
    }
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] pristine name,id and sales catalog is updated accordingly and offToOn flag as '(.*)'$")
  public void pristineNameIdAndSalesCatalogIsUpdatedAccordingly(String flag) {
    try {

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "nameSearch,level0Id,salesCatalogCategoryIdDescHierarchy,categorySequenceTE-100003,description",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      String name = solrResults.getNameSearch();

      String level0Id = solrResults.getLevel0Id();

      String salesCatalogHierarchy = solrResults.getSalesCatalogCategoryIdDescHierarchy().get(0);

      int categorySeq = solrResults.getCategorySequenceTE();

      String description = solrResults.getDescription();

      log.error("----name--{}-description-{}---query-{}",
          name,
          description,
          searchServiceData.getQueryForReindex());

      assertThat("Name is not set", name, equalTo("Pristine Product Testing"));
      assertThat("Level0Id is not set to Pristine Id", level0Id, equalTo(PRISTINE_ID));
      assertThat("Sales Catalog is not set",
          salesCatalogHierarchy,
          equalTo("TE-100001;Test Category C1/TE-100002;Test Category C2/TE-100003;Test Category C3"));
      assertThat("Sales Catalog sequence is not set", categorySeq, equalTo(10));

      if (flag.equals("true")) {

        SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "nameSearch,level0Id,salesCatalogCategoryIdDescHierarchy,categorySequenceTE-100003,description",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION).get(0);

        String nameInO2O = solrResultsO2O.getNameSearch();

        String level0IdInO2O = solrResultsO2O.getLevel0Id();

        String salesCatalogHierarchyInO2O = solrResultsO2O.getSalesCatalogCategoryIdDescHierarchy().get(0);

        int categorySeqInO2O = solrResultsO2O.getCategorySequenceTE();

        String descriptionInO2O = solrResultsO2O.getDescription();

        log.error("----name--{}-description-{}---query-{}",
            nameInO2O,
            descriptionInO2O,
            searchServiceData.getQueryForReindex());

        assertThat("Name is not set", nameInO2O, equalTo("Pristine Product Testing"));
        assertThat("Level0Id is not set to Pristine Id", level0IdInO2O, equalTo(PRISTINE_ID));
        assertThat("Sales Catalog is not set",
            salesCatalogHierarchyInO2O,
            equalTo(
                "TE-100001;Test Category C1/TE-100002;Test Category C2/TE-100003;Test Category C3"));
        assertThat("Sales Catalog sequence is not set", categorySeqInO2O, equalTo(10));
      }

      unInitialize();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] check buyable,published field in SOLR for the sku$")
  public void checkBuyablePublishedFieldInSOLRForTheSku() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "buyableAndPublished",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "buyable,published",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int buyable = solrResults.getBuyable();

      int published = solrResults.getPublished();

      assertThat("Buyable is not set", buyable, equalTo(4));
      assertThat("Published is not set", published, equalTo(4));

      statusForO2O = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "buyableAndPublished",
          SOLR_DEFAULT_COLLECTION_O2O);

      assertThat("Updating SOLR fields for test failed", status, equalTo(0));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "buyable,published",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      int buyableInO2O = solrResultsO2O.getBuyable();

      int publishedInO2O = solrResultsO2O.getPublished();

      assertThat("Buyable is not set in o2o", buyableInO2O, equalTo(4));
      assertThat("Published is not set in o2o", publishedInO2O, equalTo(4));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with '(.*)' schedule and offToOn as '(.*)'$")
  public void searchServiceConsumesItemChangeEventWithNoScheduleAndOffToOnAsFlag(String type,
      String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);

    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();

    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(true);

    if (!type.toLowerCase().equals("no")) {
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start;
      DateTime end;
      if (type.toLowerCase().equals("already running")) {
        start = dtOrg.minusDays(1);
        end = dtOrg.plusDays(2);
      } else {
        start = dtOrg.plusDays(2);
        end = dtOrg.plusDays(3);
      }

      ItemBuyableSchedule itemBuyableSchedule = new ItemBuyableSchedule();
      itemBuyableSchedule.setStartDateTime(start.toDate());
      itemBuyableSchedule.setEndDateTime(end.toDate());
      itemBuyableSchedule.setBuyable(false);

      ItemDiscoverableSchedule itemDiscoverableSchedule = new ItemDiscoverableSchedule();
      itemDiscoverableSchedule.setStartDateTime(start.toDate());
      itemDiscoverableSchedule.setEndDateTime(end.toDate());
      itemDiscoverableSchedule.setDiscoverable(false);

      itemViewConfig.setItemBuyableSchedules(itemBuyableSchedule);
      itemViewConfig.setItemDiscoverableSchedules(itemDiscoverableSchedule);
    }
    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        Collections.EMPTY_SET,
        Boolean.parseBoolean(flag),
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] buyable,published are added in SOLR with '(.*)' schedule in DB and offToOn as '(.*)'$")
  public void buyablePublishedAreAddedInSOLRWithNoScheduleInDBAndOffToOnAsFlag(String type,
      String flag) {
    try {

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "buyable,published,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int buyable = solrResults.getBuyable();

      int published = solrResults.getPublished();

      lastUpdatedTime = solrResults.getLastUpdatedTime();

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "buyable,published,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);

      int buyableInO2O = solrResultsO2O.getBuyable();

      int publishedInO2O = solrResultsO2O.getPublished();

      lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

      FindIterable<Document> mongoDocumentByQuery = mongoHelper.getMongoDocumentByQuery(
          "scheduled_events",
          "documentId",
          searchServiceData.getItemSkuForReindex());

      if (type.equals("no") && flag.equals("false")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Count is not zero", countInDb(mongoDocumentByQuery), equalTo(0));
      }

      if (type.equals("no") && flag.equals("true")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Buyable not set", buyableInO2O, not(equalTo(4)));
        assertThat("Published not set", publishedInO2O, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTimeInO2O, not(equalTo(1234l)));
      }

      if (type.equals("already running") && flag.equals("false")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Count is not zero", countInDb(mongoDocumentByQuery), equalTo(2));
      }

      if (type.equals("already running") && flag.equals("true")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Buyable not set", buyableInO2O, not(equalTo(4)));
        assertThat("Published not set", publishedInO2O, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTimeInO2O, not(equalTo(1234l)));
        assertThat("Count is not zero", countInDb(mongoDocumentByQuery), equalTo(2));
      }

      if (type.equals("future") && flag.equals("false")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Count is not zero", countInDb(mongoDocumentByQuery), equalTo(4));
      }

      if (type.equals("future") && flag.equals("true")) {
        assertThat("Buyable not set", buyable, not(equalTo(4)));
        assertThat("Published not set", published, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTime, not(equalTo(1234l)));
        assertThat("Buyable not set", buyableInO2O, not(equalTo(4)));
        assertThat("Published not set", publishedInO2O, not(equalTo(4)));
        assertThat("lastUpdatedTime is not updated", lastUpdatedTimeInO2O, not(equalTo(1234l)));
        assertThat("Count is not zero", countInDb(mongoDocumentByQuery), equalTo(4));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public int countInDb(FindIterable<Document> mongoDocumentByQuery) {
    int count = 0;
    for (Document doc : mongoDocumentByQuery) {
      count++;
    }
    return count;
  }

  public void resetConfigs() {
    configHelper.findAndUpdateConfig("reindex.status", "0");
    configHelper.findAndUpdateConfig("reindex.triggered", "false");
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
  }

  public String getValue(FindIterable<Document> mongoDocumentByQuery, String field) {
    String value = mongoHelper.getSpecificFieldfromMongoDocument(mongoDocumentByQuery, field);
    log.error("----field-{}---value-{}--", field, value);
    return value;
  }

  @Given("^\\[search-service] update fields in SOLR to test data$")
  public void searchServiceUpdateFieldsInSOLRToTestData() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "categoryReindex",
          SOLR_DEFAULT_COLLECTION);
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "reviewCount,rating,isInStock,merchantCommissionType,merchantRating,location,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      int oosFlag = solrResults.getIsInStock();

      String merchantCommissionType = solrResults.getMerchantCommissionType();

      Double merchantRating = solrResults.getMerchantRating();

      String location = solrResults.getLocation();

      Long lastUpdatedTime = solrResults.getLastUpdatedTime();

      log.warn(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--lastUpdatedTime{}",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location,
          lastUpdatedTime);

      assertThat("Test Product not set in SOLR", reviewCount, equalTo(10));
      assertThat("Test Product not set in SOLR", rating, equalTo("40"));
      assertThat("Test Product not set in SOLR", oosFlag, equalTo(5));
      assertThat("Test Product not set in SOLR", merchantRating, equalTo(30.0));
      assertThat("Test Product not set in SOLR", merchantCommissionType, equalTo("CC"));
      assertThat("Test Product not set in SOLR", location, equalTo("Origin-ABC"));
      assertThat("Test Product not set in SOLR", lastUpdatedTime, equalTo(1234l));


      statusForO2O = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "categoryReindex",
          SOLR_DEFAULT_COLLECTION_O2O);
      assertThat("Updating SOLR fields for test failed", statusForO2O, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

      SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "reviewCount,rating,isInStock,merchantCommissionType,merchantRating,location,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_O2O).get(0);


      int reviewCountInO2O = solrResultsO2O.getReviewCount();

      String ratingInO2O = solrResultsO2O.getRating();

      int oosFlagInO2O = solrResultsO2O.getIsInStock();

      String merchantCommissionTypeInO2O = solrResultsO2O.getMerchantCommissionType();

      Double merchantRatingInO2O = solrResultsO2O.getMerchantRating();

      String locationInO2O = solrResultsO2O.getLocation();

      Long lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();

      log.warn(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--lastUpdatedTime{}",
          reviewCountInO2O,
          ratingInO2O,
          reviewCountInO2O,
          oosFlagInO2O,
          merchantRatingInO2O,
          merchantCommissionTypeInO2O,
          locationInO2O,
          lastUpdatedTimeInO2O);

      assertThat("Test Product not set in SOLR", reviewCountInO2O, equalTo(10));
      assertThat("Test Product not set in SOLR", ratingInO2O, equalTo("40"));
      assertThat("Test Product not set in SOLR", oosFlagInO2O, equalTo(5));
      assertThat("Test Product not set in SOLR", merchantRatingInO2O, equalTo(30.0));
      assertThat("Test Product not set in SOLR", merchantCommissionTypeInO2O, equalTo("CC"));
      assertThat("Test Product not set in SOLR", locationInO2O, equalTo("Origin-ABC"));
      assertThat("Test Product not set in SOLR", lastUpdatedTimeInO2O, equalTo(1234l));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @When("^\\[search-service] consumes item change event  as '(.*)' and offToOn as '(.*)'$")
  public void consumesItemChangeEventAsTypeAndOffToOnAsFlag(String type, String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    if (type.equals("SHIPPING_CHANGE"))
      itemChangeEventStepsList.add(SHIPPING_CHANGE);
    else if (type.equals("ARCHIVED_FLAG_CHANGE"))
      itemChangeEventStepsList.add(ARCHIVED_FLAG_CHANGE);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        Collections.EMPTY_SET,
        Boolean.parseBoolean(flag),
        new PristineDataItemEventModel(),
        Collections.EMPTY_SET);

    try {
      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] complete SOLR doc is updated instead of atomic update and offToOn as '(.*)'$")
  public void completeSOLRDocIsUpdatedInsteadOfAtomicUpdate(String flag) {
    try {

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "reviewCount,rating,isInStock,merchantCommissionType,merchantRating,location,lastUpdatedTime",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      int reviewCount = solrResults.getReviewCount();

      String rating = solrResults.getRating();

      int oosFlag = solrResults.getIsInStock();

      String merchantCommissionType = solrResults.getMerchantCommissionType();

      Double merchantRating = solrResults.getMerchantRating();

      String location = solrResults.getLocation();

      lastUpdatedTime = solrResults.getLastUpdatedTime();

      log.error(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--"
              + "location--{}--lastUpdatedTime---{}---",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location,
          lastUpdatedTime);

      assertThat("ReviewCount not updated after reindex", reviewCount, not(equalTo(10)));
      assertThat("Rating not updated after reindex", rating, not(equalTo("40")));
      assertThat("isInStock not updated after reindex", oosFlag, not(equalTo("5")));
      assertThat("Merchant Rating not updated after reindex", merchantRating, not(equalTo(30.0)));
      assertThat("Merchant Comm Type not updated after reindex",
          merchantCommissionType,
          not(equalTo("CC")));
      assertThat("Location not updated after reindex", location, not(equalTo("Origin-ABC")));
      assertThat("last updated time is not set", lastUpdatedTime, not(equalTo(1234l)));

      if (flag.equals("true")) {

        SolrResults solrResultsO2O = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
            SELECT_HANDLER,
            "reviewCount,rating,isInStock,merchantCommissionType,merchantRating,location,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_O2O).get(0);

        int reviewCountInO2O = solrResultsO2O.getReviewCount();

        String ratingInO2O = solrResultsO2O.getRating();

        int oosFlagInO2O = solrResultsO2O.getIsInStock();

        String merchantCommissionTypeInO2O = solrResultsO2O.getMerchantCommissionType();

        Double merchantRatingInO2O = solrResultsO2O.getMerchantRating();

        String locationInO2O = solrResultsO2O.getLocation();

        Long lastUpdatedTimeInO2O = solrResultsO2O.getLastUpdatedTime();


        log.error(
            "--reviewCountInO2O--{}---ratingInO2O--{}--reviewCountInO2O--{}--oosFlagInO2O--{}--merchantRatingInO2O"
                + "---{}--merchantCommissionTypeInO2O---{}--locationInO2O--{}----lastUpdatedTimeInO2O--{}---",
            reviewCountInO2O,
            ratingInO2O,
            reviewCountInO2O,
            oosFlagInO2O,
            merchantRatingInO2O,
            merchantCommissionTypeInO2O,
            locationInO2O,
            lastUpdatedTimeInO2O);

        assertThat("ReviewCount not updated after reindex", reviewCountInO2O, not(equalTo(10)));
        assertThat("Rating not updated after reindex", ratingInO2O, not(equalTo("40")));
        assertThat("isInStock not updated after reindex", oosFlagInO2O, not(equalTo("5")));
        assertThat("Merchant Rating not updated after reindex",
            merchantRatingInO2O,
            not(equalTo(30.0)));
        assertThat("Merchant Comm Type not updated after reindex",
            merchantCommissionTypeInO2O,
            not(equalTo("CC")));
        assertThat("Location not updated after reindex", locationInO2O, not(equalTo("Origin-ABC")));
        assertThat("last updated time is not set", lastUpdatedTimeInO2O, not(equalTo(1234l)));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with both shipping and price change and offToOn as '(.*)'$")
  public void consumesItemChangeEventWithBothShippingAndPriceChange(String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(SHIPPING_CHANGE);
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet<>();
    priceSet.add(price);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        priceSet,
        Boolean.parseBoolean(flag),
        new PristineDataItemEventModel(),
        Collections.EMPTY_SET);

    try {
      Thread.sleep(40000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with with both data and price change and offToOn as '(.*)'$")
  public void itemChangeEventWithWithBothDataAndPriceChange(String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet<>();
    priceSet.add(price);


    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();

    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(true);
    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        priceSet,
        Boolean.parseBoolean(flag),
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void unInitialize() {
    try {
      ResponseApi<GdnBaseRestResponse> responseApi =
          searchServiceController.prepareRequestForIndexing("skus",
              searchServiceData.getSkuForReindex());

      assertThat("response is not 200", responseApi.getResponse().getStatusCode(), equalTo(200));

      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] remove the SOLR doc from SOLR$")
  public void removeTheSOLRDocFromSOLR() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    solrHelper.deleteSolrDocByQuery(searchServiceData.getQueryForReindex(),
        SOLR_DEFAULT_COLLECTION);
    solrHelper.deleteSolrDocByQuery(searchServiceData.getQueryForReindex(),
        SOLR_DEFAULT_COLLECTION_O2O);
  }

  @When("^\\[search-service] consumes itemChangeEventType as ITEM_DATA_CHANGE and published as '(.*)' and offToOn as '(.*)'$")
  public void itemChangeEventTypeAndPublishedAsTrueAndOffToOnAsFlag(boolean publishedFlag,
      String flag) {

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);

    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();

    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(publishedFlag);

    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        Collections.EMPTY_SET,
        Boolean.parseBoolean(flag),
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event as PRISTINE_MAPPING_CHANGE and no PristineDataItem and offToOn as '(.*)'$")
  public void consumesItemChangeEventAsPRISTINE_MAPPING_CHANGEAndNoPristineDataItem(String flag) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(PRISTINE_MAPPING_CHANGE);
    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),
        false,
        false,
        itemChangeEventStepsList,
        Collections.EMPTY_SET,
        Boolean.parseBoolean(flag),
        null,
        Collections.EMPTY_SET);
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event for that itemSku present in Normal and '(.*)' collection$")
  public void itemChangeEventForThatItemSkuPresentInNormalAndOtherCollection(String other) {
    if (other.equals("O2O")) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),
          false,
          false,
          Collections.EMPTY_LIST,
          Collections.EMPTY_SET,
          false,
          new PristineDataItemEventModel(),
          Collections.EMPTY_SET);
    }

    if (other.equals("CNC")) {
      kafkaHelper.publishItemChangeEvent(searchServiceData.getDefCncItemSku1(),
          searchServiceData.getDefCncProductSku(),
          false,
          false,
          Collections.EMPTY_LIST,
          Collections.EMPTY_SET,
          false,
          new PristineDataItemEventModel(),
          Collections.EMPTY_SET);
    }
    try {
      Thread.sleep(40000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
