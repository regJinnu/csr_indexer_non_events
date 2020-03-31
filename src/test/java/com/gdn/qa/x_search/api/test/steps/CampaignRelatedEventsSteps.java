package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 12/09/18
 * @project X-search
 */
@Slf4j
@CucumberStepsDefinition
public class CampaignRelatedEventsSteps {

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


  @Given("^\\[search-service] set all the values for publishing the campaign event$")
  public void searchServiceSetAllTheValuesForPublishingTheCampaignEvent() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setCampaignItemSku(searchServiceProperties.get("campaignItemSku"));
    searchServiceData.setCampaignDiscount(searchServiceProperties.get("campaignDiscount"));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignFieldInSOLR"));
  }

  @When("^\\[search-service] publish the campaign publish event$")
  public void searchServicePublishTheCampaignEvent() {
   kafkaHelper.publishCampaignEvent(searchServiceData.getCampaignName(),
        searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getCampaignItemSku(),
        Double.valueOf(searchServiceData.getCampaignDiscount()));

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] check if the event is consumed and check in solr$")
  public void searchServiceCheckIfTheEventIsConsumedAndCheckInSolr() throws Throwable {
    try {
      String campaignFacet = solrHelper.getSolrProd(searchServiceData.getCampaignFieldInSOLR(),
          SELECT_HANDLER,
          "campaign_"+searchServiceData.getCampaignCode(),
          1,SOLR_DEFAULT_COLLECTION).get(0).getCampaignName();
      assertThat(campaignFacet, equalTo(searchServiceData.getCampaignName()));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set list of campaign codes to go live$")
  public void searchServiceSetListOfCampaignCodesToGoLive() {
    searchServiceData.setCampaignCodeList(searchServiceProperties.get("campaignCodeList"));
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));

  }

  @When("^\\[search-service] publish the campaign live event$")
  public void searchServicePublishTheCampaignLiveEvent() {
    kafkaHelper.campaignLiveEvent(searchServiceData.getCampaignCodeList(),
            searchServiceData.getCampaignName(),false,"",false);
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking the setconfig db$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingTheSetconfigDb() {
    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("set_config", "name", "campaign.live.list");
    for (Document doc : mongoDocumentByQuery) {
      String docInStringFormat = doc.toString();
      assertThat(docInStringFormat.contains(searchServiceData.getCampaignCode()), equalTo(true));
    }
  }

  @Given("^\\[search-service] set list of campaign codes to stop$")
  public void searchServiceSetListOfCampaignCodesToStop() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
  }

  @When("^\\[search-service] publish the campaign stop event$")
  public void searchServicePublishTheCampaignStopEvent() {
    log.debug("-----------------------------------CAMPAIGN CODE FOR STOP EVENT-----------------------------------"+searchServiceData.getCampaignCode());
    kafkaHelper.campaignStopEvent(searchServiceData.getCampaignCode(), false);
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking for the field in solr$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingForTheFieldInSolr() {
    try {
      FindIterable<Document> mongoDocumentByQuery =
          mongoHelper.getMongoDocumentByQuery("set_config", "name", "campaign.live.list");
      for (Document doc : mongoDocumentByQuery) {
        String docInStringFormat = doc.toString();
        assertThat(docInStringFormat.contains(searchServiceData.getCampaignCode()+","), equalTo(false));
      }

      long countWithFq =
          solrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

      assertThat("SOLR data not updated",countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Given("^\\[search-service] set list of campaign codes to end$")
  public void searchServiceSetListOfCampaignCodesToEnd() {
    searchServiceData.setCampaignCodeList(searchServiceProperties.get("campaignCodeList"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
  }

  @When("^\\[search-service] publish the campaign end event$")
  public void searchServicePublishTheCampaignEndEvent() {
    log.debug("-----------------------------------CAMPAIGN CODE FOR END EVENT-----------------------------------"+searchServiceData.getCampaignCode());
    kafkaHelper.campaignEndEvent(searchServiceData.getCampaignCodeList(), false);
    try {
     Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking for the field in solr and mongo db$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingForTheFieldInSolrAndMongoDb() {
    try {
      long countWithFq =
          solrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
                  "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

      assertThat("SOLR data not updated",countWithFq, equalTo(0L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set list of campaign codes to remove$")
  public void searchServiceSetListOfCampaignCodesToRemove() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setItemSkuForRemove(searchServiceProperties.get("itemSkuForRemove"));
    searchServiceData.setCampaignDiscount(searchServiceProperties.get("campaignDiscount"));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignFieldInSOLR"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
  }

  @When("^\\[search-service] publish the campaign remove event$")
  public void searchServicePublishTheCampaignRemoveEvent() {
    kafkaHelper.campaignRemoveEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getItemSkuForRemove(),
        Double.valueOf(searchServiceData.getCampaignDiscount()));
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the campaign remove event is consumed$")
  public void searchServiceCheckIfTheCampaignRemoveEventIsConsumed() {
    try {

      long countWithFq =
          solrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

      assertThat("SOLR data not updated",countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set all the values for publishing exclusive campaign$")
  public void searchServiceSetAllTheValuesForPublishingExclusiveCampaign() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setCampaignItemSku(searchServiceProperties.get("campaignItemSku"));
    searchServiceData.setCampaignDiscount(searchServiceProperties.get("campaignDiscount"));
    searchServiceData.setTagLabel(searchServiceProperties.get("tagLabel"));
    searchServiceData.setQuota(Integer.parseInt(searchServiceProperties.get("quota")));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignFieldInSOLR"));
    searchServiceData.setExclusive(Boolean.parseBoolean(searchServiceProperties.get("exclusive")));
  }

  @When("^\\[search-service] publish the exclusive campaign publish event$")
  public void searchServicePublishTheExclusiveCampaignPublishEvent() {
    kafkaHelper.publishCampaignEventExclusive(searchServiceData.getCampaignName(),
        searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getCampaignItemSku(),
        Double.valueOf(searchServiceData.getCampaignDiscount()),
        searchServiceData.getQuota(),
        searchServiceData.getTagLabel(),
        searchServiceData.isExclusive());
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if exclusive campaign publish event is consumed and check in solr$")
  public void searchServiceCheckIfExclusiveCampaignPublishEventIsConsumedAndCheckInSolr() {
    try {
      String campaignFacet = solrHelper.getSolrProd(searchServiceData.getCampaignFieldInSOLR(),
          SELECT_HANDLER,
          "campaign_"+searchServiceData.getCampaignCode(),
          1,SOLR_DEFAULT_COLLECTION).get(0).getCampaignName();
      assertThat(campaignFacet, equalTo(searchServiceData.getCampaignName()));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set list of campaign codes to go live for exclusive event$")
  public void searchServiceSetListOfCampaignCodesToGoLiveForExclusiveEvent() throws Throwable {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setTagLabel(searchServiceProperties.get("tagLabel"));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignFieldInSOLR"));
    searchServiceData.setExclusive(Boolean.parseBoolean(searchServiceProperties.get("exclusive")));

  }

  @When("^\\[search-service] publish the campaign live exclusive event$")
  public void searchServicePublishTheCampaignLiveExclusiveEvent() throws Throwable {
    kafkaHelper.campaignLiveEventExclusive(searchServiceData.getCampaignName(),
        searchServiceData.getCampaignCode(),
        searchServiceData.getTagLabel(),
        searchServiceData.isExclusive());
  }

  @Then("^\\[search-service] check if the event is consumed by checking the configs in config db$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingTheConfigsInConfigDb()
      throws Throwable {
    FindIterable<Document> mongoDocumentByQuery = mongoHelper.getMongoDocumentByQuery("config_list",
        "exclusive.campaign.code",
        searchServiceData.getCampaignCode());
    for (Document doc : mongoDocumentByQuery) {
      String docInStringFormat = doc.toString();
      assertThat(docInStringFormat.contains(searchServiceData.getCampaignCode()), equalTo(null));
    }
  }

  @Given("^\\[search-service] set list of campaign codes to remove with exclusive flag$")
  public void searchServiceSetListOfCampaignCodesToRemoveWithExclusiveFlag() throws Throwable {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setItemSkuForRemove(searchServiceProperties.get("itemSkuForRemove"));
    searchServiceData.setCampaignDiscount(searchServiceProperties.get("campaignDiscount"));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignFieldInSOLR"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
  }

  @When("^\\[search-service] publish the campaign remove event with exclusive flag$")
  public void searchServicePublishTheCampaignRemoveEventWithExclusiveFlag() throws Throwable {
    kafkaHelper.campaignRemoveEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getItemSkuForRemove(),
        Double.valueOf(searchServiceData.getCampaignDiscount()));
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the campaign remove event is consumed with exclusive flag$")
  public void searchServiceCheckIfTheCampaignRemoveEventIsConsumedWithExclusiveFlag()
       {
    try {
      long countWithFq =
          solrHelper.getSolrProdCountWithFq("sku:" + searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

      assertThat("SOLR data not updated", countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set list of campaign codes to end with exclusive flag$")
  public void searchServiceSetListOfCampaignCodesToEndWithExclusiveFlag() throws Throwable {
    searchServiceData.setCampaignCodeList(searchServiceProperties.get("campaignCodeList"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
  }

  @When("^\\[search-service] publish the campaign end event with exclusive flag$")
  public void searchServicePublishTheCampaignEndEventWithExclusiveFlag() throws Throwable {
    kafkaHelper.campaignEndEvent(searchServiceData.getCampaignCodeList(), false);
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking for the field in solr and config db$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingForTheFieldInSolrAndConfigDb()
  {
    long countWithFq = 0;
    try {
      countWithFq = solrHelper.getSolrProdCountWithFq("sku:" + searchServiceData.getCampaignProductSku(),
          SELECT_HANDLER,
              "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertThat("SOLR data not updated", countWithFq, equalTo(0L));
      FindIterable<Document> mongoDocumentByQuery = mongoHelper.getMongoDocumentByQuery(
          "config_list",
          "exclusive.campaign.code",
          searchServiceData.getCampaignCode());
      for (Document doc : mongoDocumentByQuery) {
        String docInStringFormat = doc.toString();
        assertThat(docInStringFormat.contains(searchServiceData.getCampaignCode()+","), equalTo(false));
    }
  }

  @Given("^\\[search-service] set list of campaign codes to stop with exclusive flag$")
  public void searchServiceSetListOfCampaignCodesToStopWithExclusiveFlag() throws Throwable {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
  }

  @When("^\\[search-service] publish the campaign stop event with exclusive flag$")
  public void searchServicePublishTheCampaignStopEventWithExclusiveFlag() throws Throwable {
    kafkaHelper.campaignStopEvent(searchServiceData.getCampaignCode(), false);
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking for the field in solr in case of exclusive flag$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingForTheFieldInSolrInCaseOfExclusiveFlag()
      throws Throwable {
    long countWithFq =
        solrHelper.getSolrProdCountWithFq("sku:" + searchServiceData.getCampaignProductSku(),
            SELECT_HANDLER,
                "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

    assertThat("SOLR data not updated", countWithFq, equalTo(1L));
  }

  @Then("^\\[search-service\\] check if the field is updated for normal product and not updated for cnc product in solr$")
  public void searchServiceCheckIfTheFieldIsUpdatedForNormalProductAndNotUpdatedForCncProductInSolr() {
    try {
      long countWithFq =
              solrHelper.getSolrProdCountWithFq(searchServiceData.getCampaignFieldInSOLR(),
                      SELECT_HANDLER,
                      "campaign_"+searchServiceData.getCampaignCode()+":"+searchServiceData.getCampaignName(),SOLR_DEFAULT_COLLECTION);

      assertThat("cnc product is also updated by campaign event",countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service\\] set all the values for publishing the exclusive campaign event on cnc product$")
  public void searchServiceSetAllTheValuesForPublishingTheExclusiveCampaignEventOnCncProduct() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("campaignName"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignCncProductSku"));
    searchServiceData.setCampaignItemSku(searchServiceProperties.get("campaignCncItemSku"));
    searchServiceData.setCampaignDiscount(searchServiceProperties.get("campaignDiscount"));
    searchServiceData.setTagLabel(searchServiceProperties.get("tagLabel"));
    searchServiceData.setQuota(Integer.parseInt(searchServiceProperties.get("quota")));
    searchServiceData.setCampaignFieldInSOLR(searchServiceProperties.get("campaignCncFieldInSOLR"));
    searchServiceData.setExclusive(Boolean.parseBoolean(searchServiceProperties.get("exclusive")));
  }

  @When("^\\[search-service\\] publish the exclusive campaign publish event for cnc product$")
  public void searchServicePublishTheExclusiveCampaignPublishEventForCncProduct() {
      kafkaHelper.publishCampaignEventExclusiveForCnc(searchServiceData.getCampaignName(),
              searchServiceData.getCampaignCode(),
              searchServiceData.getCampaignProductSku(),
              searchServiceData.getCampaignItemSku(),
              Double.valueOf(searchServiceData.getCampaignDiscount()),
              searchServiceData.getQuota(),
              searchServiceData.getTagLabel(),
              searchServiceData.isExclusive());
      try {
        Thread.sleep(30000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
