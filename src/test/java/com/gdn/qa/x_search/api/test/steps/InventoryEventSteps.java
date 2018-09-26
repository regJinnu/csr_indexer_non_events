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
import org.springframework.beans.factory.annotation.Autowired;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 01/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class InventoryEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;

  @Given("^\\[search-service] verify product is in stock in SOLR$")
  public void checkProductIsInStock(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));

    try {
      
      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"nonOOS");
      assertThat("Updating isInStock field in SOLR failed",status,equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----Product Set non OOS before test---{}",oosFlag);
      assertThat("Product non OOS",oosFlag,equalTo(1));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes oos event for the product$")
  public void searchConsumesOOSEvent(){

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
   kafkaHelper.publishOOSEvent(itemSku,"TH7-15791","oos");

    try {
      Thread.sleep(10000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes oos in SOLR$")
  public void checkProductIsOOSAfterEvent(){

    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      log.warn("-----IsInStock After reindex by event---{}",oosFlag);
      assertThat("Product not OOS",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] verify product is out of stock in SOLR$")
  public void checkProductIsOutOfStock(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));

    try {

      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),"/select","id",1,"oos");
      assertThat("Updating isInStock field in SOLR failed",status,equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);

      int oosFlag = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product OOS",oosFlag,equalTo(0));

      log.warn("-----Product Set OOS before test---{}",oosFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes non oos event for the product$")
  public void searchConsumesNonOOSEvent(){

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
    kafkaHelper.publishOOSEvent(itemSku,"TH7-15791","nonOOS");

    try {
      Thread.sleep(10000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] product becomes in stock in SOLR$")
  public void checkProductIsNotOOSAfterEvent(){

    int oosFlag = 0;
    try {
      oosFlag =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(),"/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS",oosFlag,equalTo(1));
      log.warn("-----IsInStock After reindex by event---{}",oosFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
