package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class StopwordIntegrationSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
 private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare delete stopword integration using properties using properties data$")
  public void searchServicePrepareDeleteStopwordIntegrationUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send delete stopword integration request$")
  public void searchServiceSendDeleteStopwordIntegrationRequest()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteStopwordIntegration();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete stopword integration request response success should be '(.*)'$")
  public void searchServiceDeleteStopwordIntegrationRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
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
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update stopword integration request response success should be '(.*)'$")
  public void searchServiceUpdateStopwordIntegrationRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
  { ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
