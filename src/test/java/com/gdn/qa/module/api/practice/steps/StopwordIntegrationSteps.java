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
public class StopwordIntegrationSteps {
  @Autowired
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare delete stopword integration using properties using properties data$")
  public void searchServicePrepareDeleteStopwordIntegrationUsingPropertiesUsingPropertiesData()
  {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send delete stopword integration request$")
  public void searchServiceSendDeleteStopwordIntegrationRequest()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteStopwordIntegration();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete stopword integration request response success should be '(.*)'$")
  public void searchServiceDeleteStopwordIntegrationRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
  {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare update stopword integration using properties using properties data$")
  public void searchServicePrepareUpdateStopwordIntegrationUsingPropertiesUsingPropertiesData()
     {

  }

  @When("^\\[search-service] send update stopword integration request$")
  public void searchServiceSendUpdateStopwordIntegrationRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.updateStopwordFromSolr();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update stopword integration request response success should be '(.*)'$")
  public void searchServiceUpdateStopwordIntegrationRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
  { ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
