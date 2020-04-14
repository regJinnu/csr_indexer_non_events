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
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

  @Autowired
  ConfigHelper configHelper;

  @Given("^\\[search-service] update merchant commission type and logistic option for test product$")
  public void setMerchantCommTypeAndLogOptForTestProd(){

    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setListOfMerchants(searchServiceProperties.get("listOfMerchantIds"));
    searchServiceData.setBusinessPartnerCode(searchServiceProperties.get("businessPartnerCode"));
    searchServiceData.setCommissionType(searchServiceProperties.get("commissionType"));
    searchServiceData.setLogisticOption(searchServiceProperties.get("logisticOption"));
    searchServiceData.setLogisticOptionIncorrect(searchServiceProperties.get("logisticOptionIncorrect"));
    searchServiceData.setLogisticProductCode(searchServiceProperties.get("logisticProductCode"));
    searchServiceData.setLogisticProductCodeForEvent(searchServiceProperties.get("logisticProductCodeForEvent"));
    searchServiceData.setLogisticOptionList(searchServiceProperties.get("logisticOptionList"));
    searchServiceData.setLogisticProductCodeForOrigin(searchServiceProperties.get("logisticProductCodeForOrigin"));
    searchServiceData.setOriginLocation(searchServiceProperties.get("originLocation"));

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),SELECT_HANDLER,"id",1,"logisticOption",SOLR_DEFAULT_COLLECTION);
      assertThat("Updating Logistic options and commission type fields in SOLR failed",status,equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "merchantCommissionType,logisticOptions,location",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      String commissionType = solrResults.getMerchantCommissionType();

      String logisticOption = solrResults.getLogisticOptions().get(0);

      String location = solrResults.getLocation();


      log.warn("-----Product merchantCommissionType---{}----LogisticOptions---{}-",commissionType,logisticOption);
      assertThat("Test commission type not set",commissionType,equalTo("TEST_COMM_TYPE"));
      assertThat("Test logistic option not set",logisticOption,equalTo("TEST_LOGISTIC_OPTION"));
      assertThat("Test location not set",location,equalTo("TEST_LOCATION"));


    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes logistic '(.*)' event for a merchant containing test product when merchant count is '(.*)' than 10$")
  public void searchConsumesLogisticOptionChangeEvent(String eventType,String caseType){
    if (caseType.toLowerCase().contains("less") && eventType.toLowerCase().equals("option"))
    kafkaHelper.publishLogisticOptionChange(searchServiceData.getBusinessPartnerCode(),
        searchServiceData.getLogisticOption(),
        searchServiceData.getCommissionType(),
        searchServiceData.getLogisticProductCode());
    else if (caseType.toLowerCase().contains("more") && eventType.toLowerCase().equals("option")){
      kafkaHelper.publishLogisticOptionChange(searchServiceData.getListOfMerchants(),
          searchServiceData.getLogisticOption(),
          searchServiceData.getCommissionType(),
          searchServiceData.getLogisticProductCode());
    }
    else if (caseType.toLowerCase().contains("less") && eventType.toLowerCase().equals("product"))
      kafkaHelper.publishLogisticProductChange(searchServiceData.getBusinessPartnerCode(),
          searchServiceData.getLogisticOptionList(),
          searchServiceData.getCommissionType(),
          searchServiceData.getLogisticProductCodeForEvent());
    else if (caseType.toLowerCase().contains("more") && eventType.toLowerCase().equals("product")){
      kafkaHelper.publishLogisticOptionChange(searchServiceData.getListOfMerchants(),
          searchServiceData.getLogisticOptionList(),
          searchServiceData.getCommissionType(),
          searchServiceData.getLogisticProductCodeForEvent());
    }
    else if(caseType.toLowerCase().contains("more") && eventType.toLowerCase().equals("origin")){
      kafkaHelper.publishLogisticProductOriginsChangeEvent(searchServiceData.getLogisticProductCodeForOrigin());
    }
    else if(caseType.toLowerCase().contains("less") && eventType.toLowerCase().equals("origin")){
      kafkaHelper.publishLogisticProductOriginsChangeEvent(searchServiceData.getLogisticProductCodeForOrigin(),
          searchServiceData.getOriginLocation());
    }
    try {
      Thread.sleep(120000);
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
    try {
      Thread.sleep(60000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] merchant commission type and logistic option for test product is updated$")
  public void checkMerchantCommTypeAndLogOptForTestProdAfterEventReindex(){

    try {

      try {
        Thread.sleep(120000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      } catch (Exception e) {
        e.printStackTrace();
      }

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "merchantCommissionType,logisticOptions,location",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      String commissionType = solrResults.getMerchantCommissionType();

      String logisticOption = solrResults.getLogisticOptions().get(0);

      String location = solrResults.getLocation();

      log.warn("-----Product merchantCommissionType---{}----LogisticOptions---{}-",commissionType,logisticOption);
      assertThat("Location is not changed after reindex",location,equalTo("Origin-Jakarta"));
      assertThat("Test commission type not set",commissionType,equalTo("CM"));
      assertThat("Test logistic option not set",logisticOption,not(containsString("TEST_LOGISTIC_OPTION")));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes logistic '(.*)' event with data not present in config$")
  public void consumesEventWithUnConfiguredLogisticOption(String eventType){
    switch (eventType) {
      case "option":
        kafkaHelper.publishLogisticOptionChange(searchServiceData.getBusinessPartnerCode(),
            searchServiceData.getLogisticOptionIncorrect(),
            searchServiceData.getCommissionType(),
            searchServiceData.getLogisticProductCode());
        break;
      case "product":
        kafkaHelper.publishLogisticProductChange(searchServiceData.getBusinessPartnerCode(),
            searchServiceData.getLogisticOptionIncorrect(),
            searchServiceData.getCommissionType(),
            searchServiceData.getLogisticProductCodeForEvent());
        break;
      case "origin":
        kafkaHelper.publishLogisticProductOriginsChangeEvent(searchServiceData.getLogisticProductCodeForOrigin(),"BLIBLI_EXPRESS");
        break;
    }

    try {
      Thread.sleep(60000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] merchant commission type,location and logistic option for test product is not updated$")
  public void testProductIsNotUpdated(){
    try {

      SolrResults solrResults = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "merchantCommissionType,logisticOptions,location",
          1,
          Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION).get(0);

      String commissionType = solrResults.getMerchantCommissionType();

      String logisticOption = solrResults.getLogisticOptions().get(0);

      String location = solrResults.getLocation();

      log.warn("-----Product merchantCommissionType---{}----LogisticOptions---{}-",commissionType,logisticOption);
      assertThat("Test commission type not set",commissionType,equalTo("TEST_COMM_TYPE"));
      assertThat("Test logistic option not set",logisticOption,equalTo("TEST_LOGISTIC_OPTION"));
      assertThat("Test location not set",location,equalTo("TEST_LOCATION"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
