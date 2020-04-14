package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author kumar on 19/03/20
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class MerchantVoucherSteps {

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

  @Autowired
  private MongoHelper mongoHelper;

  @Given("^\\[search-service] fetch params required to send merchant voucher event$")
  public void fetchDetails() {

    searchServiceData.setItemSkuForOffline(searchServiceProperties.get("itemSkuForOffline"));
    searchServiceData.setVoucherCount(Integer.parseInt(searchServiceProperties.get(
        "merchantVoucherCount")));
    try {
      int statusOfNormalCollectionUpdate = solrHelper.updateSolrDataForAutomation(
          "itemSku:" + searchServiceData.getItemSkuForOffline(),
          SELECT_HANDLER,
          "id",
          2,
          "merchantVoucherCount",
          SOLR_DEFAULT_COLLECTION);

      assertThat("Updating SOLR fields for test failed",
          statusOfNormalCollectionUpdate,
          equalTo(0));

      int statusOfCncCollectionUpdate = solrHelper.updateSolrDataForAutomation(
          "itemSku:" + searchServiceData.getItemSkuForOffline(),
          SELECT_HANDLER,
          "id",
          2,
          "merchantVoucherCount",
          SOLR_DEFAULT_COLLECTION_CNC);

      assertThat("Updating SOLR fields for test failed", statusOfCncCollectionUpdate, equalTo(0));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^\\[search-service] send merchant voucher sku mapping count event through kafka$")
  public void sendMerchantVoucherEvent() {
    kafkaHelper.publishMerchantVoucherEvent(searchServiceData.getItemSkuForOffline(),
        searchServiceData.getVoucherCount());
    commitSOLR();
  }

  @Then("^\\[search-service] verify that merchant voucher count is updated in solr for online item in '(.*)' processing$")
  public void verifyMerchantVoucherCountForOnlineItem(String type) {
    try {
      commitSOLR();
      int voucherCount =
          solrHelper.getSolrProd("itemSku:" + searchServiceData.getItemSkuForOffline(),
              SELECT_HANDLER,
              "merchantVoucherCount",
              1,
              Collections.singletonList("cnc:false"),
              SOLR_DEFAULT_COLLECTION).get(0).getMerchantVoucherCount();

      if (type.equals("direct"))
        assertThat("voucher count not updated",
            voucherCount,
            equalTo(searchServiceData.getVoucherCount()));
      else
        assertThat("voucher count not updated", voucherCount, equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @And("^\\[search-service] verify that merchant voucher count is not updated in solr for offline item$")
  public void verifyMerchantVoucherCountForOfflineItem() {
    try {
      int voucherCount =
          solrHelper.getSolrProd("itemSku:" + searchServiceData.getItemSkuForOffline(),
              SELECT_HANDLER,
              "merchantVoucherCount",
              2,
              Collections.singletonList("cnc:true"),
              SOLR_DEFAULT_COLLECTION_CNC).get(0).getMerchantVoucherCount();

      assertThat("voucher count not updated", voucherCount, equalTo(100));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @And("^\\[search-service] event '(.*)' is stored in delta table$")
  public void searchServiceEventIsStoredInDeltaTable(String eventName) {
    Document searchDoc = new Document("eventName", eventName);
    long indexing_list_new = mongoHelper.countByMongoquery("indexing_list_new", searchDoc);
    assertThat("Count is zero", indexing_list_new, greaterThan(0L));
  }

  void commitSOLR() {
    try {
      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
