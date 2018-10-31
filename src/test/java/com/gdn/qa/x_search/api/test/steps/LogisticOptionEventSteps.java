package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kumar on 14/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class LogisticOptionEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;
  
  @Autowired
  SolrHelper solrHelper;

  @Given("^\\[search-service] update merchant commission type and logistic option for test product$")
  public void setMerchantCommTypeAndLogOptForTestProd(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),SELECT_HANDLER,"id",1,"logisticOption");
      assertThat("Updating Logistic options and commission type fields in SOLR failed",status,equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      String commissionType = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
         SELECT_HANDLER,"merchantCommissionType",1).get(0).getMerchantCommissionType();

      String logisticOption = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,"logisticOptions",1).get(0).getLogisticOptions().get(0);

      String location = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,"location",1).get(0).getLocation();


      log.warn("-----Product merchantCommissionType---{}----LogisticOptions---{}-",commissionType,logisticOption);
      assertThat("Test commission type not set",commissionType,equalTo("TEST_COMM_TYPE"));
      assertThat("Test logistic option not set",logisticOption,equalTo("TEST_LOGISTIC_OPTION"));
      assertThat("Tes location not set",location,equalTo("TEST_LOCATION"));


    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes logistic option event for a merchant containing test product$")
  public void searchConsumesLogisticOptionChangeEvent(){
    kafkaHelper.publishLogisticOptionChange("TOQ-16110","EXPRESS","CM");
    try {
      Thread.sleep(60000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] run api to reindex products in product atomic reindex queue$")
  public void runProductAtomicIndexJob(){
    ResponseApi<GdnBaseRestResponse> responseApi =
        searchServiceController.prepareRequestForAtomicReindexQueue();
    searchServiceData.setSearchServiceResponse(responseApi);
  }

  @Then("^\\[search-service] merchant commission type and logistic option for test product is updated$")
  public void checkMerchantCommTypeAndLogOptForTestProdAfterEventReindex(){

    try {

      try {
        Thread.sleep(60000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      } catch (Exception e) {
        e.printStackTrace();
      }
        String commissionType = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,"merchantCommissionType",1).get(0).getMerchantCommissionType();

      String logisticOption = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,"logisticOptions",1).get(0).getLogisticOptions().get(0);

      String location = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,"location",1).get(0).getLocation();

      log.warn("-----Product merchantCommissionType---{}----LogisticOptions---{}-",commissionType,logisticOption);
      assertThat("Location is not changed after reindex",location,equalTo("Origin-Jakarta"));
      assertThat("Test logistic option not set",logisticOption,equalTo("EXPRESS"));
      assertThat("Test commission type not set",commissionType,equalTo("CM"));


    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] remove all entries from Product Reindex Atomic Queue and Product Atomic Reindex Data Candidate$")
  public void removeAllEntriesFromConcernedTables(){

  }

  @When("^\\[search-service] receives ORIGIN CHANGE event$")
  public void searchConsumesOriginChangeEvent(){
    kafkaHelper.publishLogisticProductOriginsChangeEvent();
  }

  @When("^\\[search-service] run api to convert High to Low")
  public void runApiToConvertHighToLow()
  {}

}
