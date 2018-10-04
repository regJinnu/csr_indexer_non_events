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
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 12/09/18
 * @project X-search
 */

@CucumberStepsDefinition
public class CampaignRelatedEventsSteps {

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper;

  MongoHelper mongoHelper = new MongoHelper();


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
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed and check in solr$")
  public void searchServiceCheckIfTheEventIsConsumedAndCheckInSolr() throws Throwable {
    try {
      String campaignFacet = SolrHelper.getSolrProd(searchServiceData.getCampaignFieldInSOLR(),
          SELECT_HANDLER,
          "campaign_CAMP-0001",
          1).get(0).getCampaignName();
      assertThat(campaignFacet, equalTo("campaign.product.list"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set list of campaign codes to go live$")
  public void searchServiceSetListOfCampaignCodesToGoLive() {
    searchServiceData.setCampaignCodeList(searchServiceProperties.get("campaignCodeList"));

  }

  @When("^\\[search-service] publish the campaign live event$")
  public void searchServicePublishTheCampaignLiveEvent() {
    kafkaHelper.campaignLiveEvent(searchServiceData.getCampaignCodeList(), "CAMP-0001",false,"",false);
    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
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
      assertThat(docInStringFormat.contains("CAMP-0001"), equalTo(true));
    }
  }

  @Given("^\\[search-service] set list of campaign codes to stop$")
  public void searchServiceSetListOfCampaignCodesToStop() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("campaignCode"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
  }

  @When("^\\[search-service] publish the campaign stop event$")
  public void searchServicePublishTheCampaignStopEvent() {
    kafkaHelper.campaignStopEvent(searchServiceData.getCampaignCode(), false);
    try {
      Thread.sleep(50000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
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
        assertThat(docInStringFormat.contains("CAMP-0001"), equalTo(false));
      }

      long countWithFq =
          SolrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_CAMP-0001:[* TO *]");

      assertThat("SOLR data not updated",countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Given("^\\[search-service] set list of campaign codes to end$")
  public void searchServiceSetListOfCampaignCodesToEnd() {
    searchServiceData.setCampaignCodeList(searchServiceProperties.get("campaignCodeList"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get("campaignProductSku"));
  }

  @When("^\\[search-service] publish the campaign end event$")
  public void searchServicePublishTheCampaignEndEvent() {
    kafkaHelper.campaignLiveEvent(searchServiceData.getCampaignCodeList(), "CAMP-0001",false,"",false);
    kafkaHelper.campaignEndEvent(searchServiceData.getCampaignCodeList(), false);
    try {
      Thread.sleep(50000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking for the field in solr and mongo db$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingForTheFieldInSolrAndMongoDb() {
    try {
      long countWithFq =
          SolrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_CAMP-0001:[* TO *]");

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
  }

  @When("^\\[search-service] publish the campaign remove event$")
  public void searchServicePublishTheCampaignRemoveEvent() {
    kafkaHelper.campaignRemoveEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getItemSkuForRemove(),
        Double.valueOf(searchServiceData.getCampaignDiscount()));
    try {
      Thread.sleep(50000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the campaign remove event is consumed$")
  public void searchServiceCheckIfTheCampaignRemoveEventIsConsumed() {
    try {

      long countWithFq =
          SolrHelper.getSolrProdCountWithFq("sku:"+searchServiceData.getCampaignProductSku(),
              SELECT_HANDLER,
              "campaign_CAMP-0001:[* TO *]");

      assertThat("SOLR data not updated",countWithFq, equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
