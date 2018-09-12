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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * @author kumar on 02/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class BusinessPartnerEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;

  @Given("^\\[search-service] verify store closed start and end timestamp fields in SOLR for the product$")
  public void verifyStoreClosedInfoInSolr() {

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setBusinessPartnerCode(searchServiceProperties.get("businessPartnerCode"));

    try {

      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "closedStore");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);
      

      int isDelayShipping =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isDelayShipping", 1)
              .get(0)
              .getIsDelayShipping();

      long startDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "startDateStoreClosed", 1)
              .get(0)
              .getStartDateStoreClosed();

      long endDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "endDateStoreClosed", 1)
              .get(0)
              .getEndDateStoreClosed();

      log.warn("------isDelayShipping--{}---startDateStoreClosed--{}---endDateStoreClosed--{}", isDelayShipping,startDateStoreClosed,endDateStoreClosed);
      assertThat("isDelayShipping not set", isDelayShipping, equalTo(3));
      assertThat("startDateStoreClosed not set",startDateStoreClosed,equalTo(1111111111L));
      assertThat("endDateStoreClosed not set",endDateStoreClosed,equalTo(22222222L));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes store closed event with delay shipping as '(.*)'$")
  public void checkStoreEventIsConsumed(boolean isDelayShipping){

      kafkaHelper.publishStoreClosedEvent(searchServiceData.getBusinessPartnerCode(),isDelayShipping);

    try {
      Thread.sleep(60000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] store closed information is updated in SOLR$")
  public void checkStoreClosedInfoIsUpdated(){

    try {
      long startDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "startDateStoreClosed", 1)
              .get(0)
              .getStartDateStoreClosed();

      long endDateStoreClosed =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "endDateStoreClosed", 1)
              .get(0)
              .getEndDateStoreClosed();

      log.warn("---startDateStoreClosed--{}---endDateStoreClosed--{}",startDateStoreClosed,endDateStoreClosed);
      assertThat("startDateStoreClosed not set",startDateStoreClosed,not(equalTo(1111111111L)));
      assertThat("endDateStoreClosed not set",endDateStoreClosed,not(equalTo(22222222L)));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] delay shipping is set as '(.*)'$")
  public void checkDelayShipping(int isDelayShippingExpected){

    try {

      int isDelayShippingActual = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isDelayShipping", 1)
          .get(0)
          .getIsDelayShipping();

      log.warn("------isDelayShipping--{}---", isDelayShippingActual);
      assertThat("isDelayShipping not set", isDelayShippingActual, equalTo(isDelayShippingExpected));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] cnc is set as true in products for merchant$")
  public void setCncTrueForTestProduct(){

    SolrHelper.addSolrDocument();
    try {
      assertThat(SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001-PP-3001012",SELECT_HANDLER),equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  @When("^\\[search-service] consumes com.gdn.x.businesspartner.profile.update.fields event$")
  public void searchConsumesBPprofileUpdateEvent(){
    kafkaHelper.publishBPprofileFieldUpdateEvent("AAA-60015");
    try {
      Thread.sleep(60000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] cnc true is removed for all products under that merchant$")
  public void checkCncIsRemoved(){
    try {
      assertThat(SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001-PP-3001012",SELECT_HANDLER),equalTo(0L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
