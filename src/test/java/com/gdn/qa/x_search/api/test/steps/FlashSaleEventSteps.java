package com.gdn.qa.x_search.api.test.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.Config;
import com.gdn.qa.x_search.api.test.models.SetConfig;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author kumar on 14/04/20
 * @project X-search
 */
@Slf4j
@CucumberStepsDefinition
public class FlashSaleEventSteps {

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

  private ObjectMapper objectMapper = new ObjectMapper();

  @Given("^\\[search-service] set all the values for publishing flash sale campaign$")
  public void setDataForPublishEvent() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("flashSaleCampaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("flashSaleCampaignName"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get(
        "flashSaleCampaignProductSku"));
    searchServiceData.setCampaignItemSku(searchServiceProperties.get("flashSaleCampaignItemSku"));
    searchServiceData.setSessionIds(searchServiceProperties.get("flashSaleSessionIds"));
  }

  @When("^\\[search-service] trigger the flash sale publish event$")
  public void triggerFlashSalePublishEvent() {

    Map<String, String> params = new HashMap<>();
    params.put("campaignCode", searchServiceData.getCampaignCode());
    params.put("campaignName", searchServiceData.getCampaignName());
    params.put("itemSkus", searchServiceData.getCampaignItemSku());
    params.put("productSku", searchServiceData.getCampaignProductSku());
    params.put("sessionIds", searchServiceData.getSessionIds());

    kafkaHelper.publishFlashSaleCampaignEvent(params);

    try {
      Thread.sleep(15000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if flash sale publish event is consumed and SOLR is updated$")
  public void checkSOLRisUpdatedWithFlashSaleData() {

    try {

      String[] sessionIds = searchServiceData.getSessionIds().split(",");
      Map<String, Integer> map = new HashMap<>();
      for (String s : sessionIds) {
        int count = 0;
        if (map.containsKey(s)) {
          count = map.get(s);
        }
        map.put(s, count + 1);
      }

      for (String s : sessionIds) {
        String campField = "campaign_" + searchServiceData.getCampaignCode() + "_" + s;

        List<SolrResults> solrProdList = solrHelper.getSolrProd(campField + ":*",
            SELECT_HANDLER,
            campField,
            10,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION);

        assertThat("Count does not match", solrProdList.size(), equalTo(map.get(s)));
        for (SolrResults solrDoc : solrProdList) {
          String campName;

          if (solrDoc.getCampaignNameFS1() != null)
            campName = solrDoc.getCampaignNameFS1();
          else
            campName = solrDoc.getCampaignNameFS2();

          assertThat("Campaign Name does not match",
              campName,
              equalTo(searchServiceData.getCampaignName()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @And("^\\[search-service] check that set config is not updated with campaign code$")
  public void checkSetConfigIsNotUpdated() throws IOException {

    FindIterable<Document> mongoDoc =
        mongoHelper.getMongoDocumentByQuery("set_config", "name", "campaign.live.list");

    for (Document doc : mongoDoc) {
      String json = doc.toJson();
      SetConfig setConfig = objectMapper.readValue(json, SetConfig.class);

      String[] value = setConfig.getValue();

      String[] sessionIds = searchServiceData.getSessionIds().split(",");

      for (String s : sessionIds) {

        assertThat("Un-expected campaign is present",
            value,
            not(hasItemInArray(searchServiceData.getCampaignCode() + "_" + s)));
      }
    }
  }

  @Given("^\\[search-service] set all the values for making flash sale campaign live$")
  public void setValuesForMakingFlashSaleCampaignLive() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("flashSaleCampaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("flashSaleCampaignName"));
    searchServiceData.setActiveSession(Integer.parseInt(searchServiceProperties.get(
        "flashSaleActiveSession")));
  }

  @When("^\\[search-service] publish the flash sale live event$")
  public void publishFlashSaleLiveEvent() {

    kafkaHelper.publishFlashSaleLiveEvent(searchServiceData.getCampaignName(),
        searchServiceData.getCampaignCode(),
        searchServiceData.getActiveSession());

    try {
      Thread.sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check that set config is updated with campaign code$")
  public void checkSetConfigIsUpdated() throws IOException {
    FindIterable<Document> mongoDoc =
        mongoHelper.getMongoDocumentByQuery("set_config", "name", "campaign.live.list");

    for (Document doc : mongoDoc) {
      String json = doc.toJson();
      SetConfig setConfig = objectMapper.readValue(json, SetConfig.class);

      String[] value = setConfig.getValue();

      assertThat("Expected campaign is not present",
          value,
          hasItemInArray(
              searchServiceData.getCampaignCode() + "_" + searchServiceData.getActiveSession()));

    }
  }

  @Then("^\\[search-service] check config are updated in config table$")
  public void checkFlashSaleEntriesInConfigTable() throws IOException {

    FindIterable<Document> mongoDoc =
        mongoHelper.getMongoDocumentByQuery("config_list", "NAME", "flash.sale.");

    Map<String, String> mapDBValues = new HashMap<>();

    int count = 0;
    for (Document doc : mongoDoc) {
      count++;
      Config config = objectMapper.readValue(doc.toJson(), Config.class);
      mapDBValues.put(config.getName(), config.getValue());
    }

    assertThat("Count of entries is not correct", count, equalTo(4));

    assertThat("Campaign Code is not correct",
        mapDBValues.get("flash.sale.campaign.code"),
        equalTo(searchServiceData.getCampaignCode()));

    assertThat("Campaign Session id is not correct",
        mapDBValues.get("flash.sale.campaign.session.id"),
        equalTo(searchServiceData.getCampaignCode() + "_" + searchServiceData.getActiveSession()));

    assertThat("Campaign Label is not correct",
        mapDBValues.get("flash.sale.campaign.label"),
        equalTo(FLASH_SALE_IMAGE));

    assertThat("Campaign end time is not set",
        Long.parseLong(mapDBValues.get("flash.sale.campaign.end.time")),
        greaterThan(System.currentTimeMillis()));
  }

  @Given("^\\[search-service] set data to remove items from live flash sale$")
  public void setDataForFlashSaleRemove() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("flashSaleCampaignCode"));
    searchServiceData.setCampaignProductSku(searchServiceProperties.get(
        "flashSaleCampaignProductSku"));
    searchServiceData.setItemSkuForRemove(searchServiceProperties.get("flashSaleCampaignItemSku")
        .split(",")[1]);
    searchServiceData.setActiveSession(Integer.parseInt(searchServiceProperties.get(
        "flashSaleActiveSession")));
  }

  @When("^\\[search-service] publish the flash sale item remove event$")
  public void publishFlashSaleRemoveEvent() {
    kafkaHelper.publishFlashSaleRemoveEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getCampaignProductSku(),
        searchServiceData.getItemSkuForRemove(),
        searchServiceData.getActiveSession());
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if flash sale remove event is consumed and SOLR is updated$")
  public void verifySOLRAfterFlashSaleRemove() {

    String campField = "campaign_" + searchServiceData.getCampaignCode() + "_"
        + searchServiceData.getActiveSession();

    try {
      List<SolrResults> solrProdList = solrHelper.getSolrProd(campField + ":*",
          SELECT_HANDLER,
          "id",
          10,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION);

      assertThat("Size of result list is not correct", solrProdList.size(), equalTo(1));

      assertThat("Id is not removed from SOLR",
          solrProdList.get(0).getId(),
          not(equalTo(searchServiceData.getItemSkuForRemove())));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] set data to stop the flash sale$")
  public void setDataForStoppingFlashSale() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("flashSaleCampaignCode"));
    searchServiceData.setActiveSession(Integer.parseInt(searchServiceProperties.get(
        "flashSaleActiveSession")));
  }

  @When("^\\[search-service] publish the flash sale stop event$")
  public void publishFlashSaleStop() {
    kafkaHelper.publishFlashSaleStopEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getActiveSession());
    try {
      Thread.sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check flash sale info is removed from set config and config table$")
  public void validateDBAfterStop() throws IOException {
    FindIterable<Document> mongoDoc =
        mongoHelper.getMongoDocumentByQuery("set_config", "name", "campaign.live.list");

    for (Document doc : mongoDoc) {
      String json = doc.toJson();
      SetConfig setConfig = objectMapper.readValue(json, SetConfig.class);

      String[] value = setConfig.getValue();

      assertThat("Data not deleted from set_config",
          value,
          not(hasItemInArray(
              searchServiceData.getCampaignCode() + "_" + searchServiceData.getActiveSession())));
    }

    FindIterable<Document> configData =
        mongoHelper.getMongoDocumentByQuery("config_list", "NAME", "flash.sale.");

    int count = 0;
    for (Document ignored : configData) {
      count++;
    }

    assertThat("Data not deleted from config_list", count, equalTo(0));
  }

  @Given("^\\[search-service] set data to end the flash sale$")
  public void setDataToEndTheFlashSale() {
    searchServiceData.setCampaignCode(searchServiceProperties.get("flashSaleCampaignCode"));
    searchServiceData.setCampaignName(searchServiceProperties.get("flashSaleCampaignName"));
    searchServiceData.setActiveSession(Integer.parseInt(searchServiceProperties.get(
        "flashSaleActiveSession")));
    publishFlashSaleLiveEvent();
  }

  @When("^\\[search-service] publish the flash sale end event$")
  public void publishFlashSaleEndEvent() {
    kafkaHelper.publishFlashSaleEndEvent(searchServiceData.getCampaignCode(),
        searchServiceData.getActiveSession());
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check dynamic fields are removed from SOLR$")
  public void checkSOLRForFieldRemoval() throws Exception {

    String query = "campaign_" + searchServiceData.getCampaignCode() + "_" + searchServiceData.getActiveSession() + ":*";

    long count = solrHelper.getSolrProdCount(query,
        SELECT_HANDLER,
        SOLR_DEFAULT_COLLECTION,
        Collections.emptyList());

    assertThat("Count is non zero", count,equalTo(0L));

  }
}
