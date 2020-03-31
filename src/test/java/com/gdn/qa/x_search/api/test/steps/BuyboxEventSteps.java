package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Slf4j
@CucumberStepsDefinition

public class BuyboxEventSteps {
  @Autowired
  KafkaHelper kafkaHelper;
  @Autowired
  SolrHelper solrHelper;
  @Autowired
  private SearchServiceController searchServiceController;
  @Autowired
  private SearchServiceProperties searchServiceProperties;
  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] fetch params required to send buybox event$")
  public void fetchParamsRequiredToSendBuyboxEvent() {
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setBuyboxScore(Double.valueOf(searchServiceProperties.get("buyboxScore")));
    try {

      int statusOfNormalCollectionUpdate =
       solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "buyBox",
          SOLR_DEFAULT_COLLECTION);

      assertThat("Updating SOLR fields for test failed",
          statusOfNormalCollectionUpdate,
          equalTo(0));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] send buybox related event through kafka$")
  public void sendBuyboxRelatedEventThroughKafka() {
    kafkaHelper.buyBoxEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getBuyboxScore());
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] verify that buybox score is updated in solr$")
  public void verifyThatBuyboxScoreIsUpdatedInSolr() {
    try {
      Double buyboxScore= solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "buyboxScore",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getBuyboxScore();

      Long lastUpdatedTime= solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "lastUpdatedTime",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getLastUpdatedTime();

      assertThat("buyBoxScore not updated", buyboxScore, equalTo(100.0));
      assertThat("lastUpdatedTime not updated", lastUpdatedTime,not(equalTo(1234l)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] fetch params required to send buybox event for cnc prod$")
  public void fetchParamsRequiredToSendBuyboxEventForCncProd() {
    searchServiceData.setDefCncItemSku1(searchServiceProperties.get("defCncItemSku1"));
    searchServiceData.setBuyboxScore(Double.valueOf(searchServiceProperties.get("buyboxScore")));
    searchServiceData.setDefCncId(searchServiceProperties.get("defCncId"));
  }

  @When("^\\[search-service] send buybox related event through kafka for cnc prod$")
  public void sendBuyboxRelatedEventThroughKafkaForCncProd() {
    kafkaHelper.buyBoxEvent(searchServiceData.getDefCncItemSku1(),
        searchServiceData.getBuyboxScore());
    try {
      Thread.sleep(50000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] verify that buybox score is not updated in solr$")
  public void verifyThatBuyboxScoreIsNotUpdatedInSolr() {
    try {
      String query =
          searchServiceData.getDefCncId();

      Double buyboxScoreInCnc= solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "buyboxScore",
          1,
          SOLR_DEFAULT_COLLECTION_CNC).get(0).getBuyboxScore();

      Double buyboxScore = solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSku1(),
          SELECT_HANDLER,
          "buyboxScore",
          1,
          SOLR_DEFAULT_COLLECTION).get(0).getBuyboxScore();

      assertThat("buyBoxScore not updated", buyboxScore, equalTo(100.0));
      assertThat("buyBoxScore not updated", buyboxScoreInCnc, equalTo(-1.0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
