package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class SynonymsIntegrationSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
 private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;
  @Given("^\\[search-service] prepare delete synonyms using properties using properties data$")
  public void searchServicePrepareDeleteSynonymsUsingPropertiesUsingPropertiesData()
    {
      searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send delete synonym request$")
  public void searchServiceSendDeleteSynonymRequest()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSynonymFromSolr();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] create delete synonym request response success should be '(.*)'$")
  public void searchServiceCreateDeleteSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
     {
       ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare update synonyms to solr using properties using properties data$")
  public void searchServicePrepareUpdateSynonymsToSolrUsingPropertiesUsingPropertiesData()
    {
      searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
  }

  @When("^\\[search-service] send update synonyms to solr request$")
  public void searchServiceSendUpdateSynonymsToSolrRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.updateSynonymFromSolr();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] create update synonym to solr request response success should be '(.*)'$")
  public void searchServiceCreateUpdateSynonymToSolrRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
