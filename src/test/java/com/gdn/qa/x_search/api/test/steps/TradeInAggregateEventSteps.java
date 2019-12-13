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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class TradeInAggregateEventSteps {

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  SearchServiceProperties searchServiceProperties;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  KafkaHelper kafkaHelper;

  @Given("^\\[search-service] prepare request for processing trade in aggregate (.*) event$")
  public void searchServicePrepareRequestForProcessingTradeInAggregateEvent(String active) {
    searchServiceData.setId(searchServiceProperties.get("idForTradeInEvent"));
    searchServiceData.setProductSku(searchServiceProperties.get("idForTradeInEvent"));
    searchServiceData.setProductName(searchServiceProperties.get("productNameForTradeInEvent"));
    searchServiceData.setActive(Boolean.parseBoolean(active));
  }

  @And("^\\[search-service] set trade in value as (.*) of test product based on (.*)$")
  public void searchServiceSetTradeInFieldOfTestProduct(String preEventTradeInValue, String caseToBeUpdated) throws Exception {
    String query = searchServiceProperties.get("solrQueryForTradeInEvent");
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        caseToBeUpdated,
        SOLR_DEFAULT_COLLECTION);
    assertThat("Updating tradeInEligible in SOLR doc failed", status, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    boolean tradeInEligible =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "tradeInEligible", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getTradeInEligible();

    assertThat("Test Product not set in SOLR", tradeInEligible, equalTo(Boolean.parseBoolean(preEventTradeInValue)));
  }

  @When("^\\[search-service] send request for processing trade in aggregate event$")
  public void searchServiceSendRequestForProcessingTradeInAggregateEvent() throws Exception {
    kafkaHelper.publishTradeInAggregateEvent(searchServiceData.getId(),
        searchServiceData.getProductSku(),
        searchServiceData.getProductName(),
        searchServiceData.isActive());
    Thread.sleep(30000);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
  }


  @Then("^\\[search-service] check if trade in aggregate event is processed and solr is updated with (.*) as value$")
  public void searchServiceCheckIfTradeInAggregateEventIsProcessedAndSolrIsUpdated(String active)
      throws Exception {
    String query = searchServiceProperties.get("solrQueryForTradeInEvent");
    boolean tradeInEligible =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "tradeInEligible", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getTradeInEligible();
    assertThat("Test Product not updated after event processing", tradeInEligible, equalTo(Boolean.parseBoolean(active)));
  }
}
