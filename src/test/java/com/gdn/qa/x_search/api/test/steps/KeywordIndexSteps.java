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
import static org.hamcrest.Matchers.equalToIgnoringCase;

@CucumberStepsDefinition
public class KeywordIndexSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;
  @Given("^\\[search-service] prepare request to delete search keywords having click count below a specified limit$")
  public void searchServicePrepareRequestToDeleteSearchKeywordsHavingClickCountBelowASpecifiedLimit()
       {
  }

  @When("^\\[search-service] send request to delete search keywords having click count below a specified limit$")
  public void searchServiceSendRequestToDeleteSearchKeywordsHavingClickCountBelowASpecifiedLimit()
  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.cleanUp();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] request to delete search keywords having click count below a specified limit response success should be '(.*)'$")
  public void searchServiceRequestToDeleteSearchKeywordsHavingClickCountBelowASpecifiedLimitResponseSuccessShouldBeTrue(Boolean isSuccess)
   {
     ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
     boolean result = response.getResponseBody().isSuccess();
     assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to get debug info for keyword to category Mapping$")
  public void searchServicePrepareRequestToGetDebugInfoForKeywordToCategoryMapping()
  {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send request to get debug info for keyword to category Mapping$")
  public void searchServiceSendRequestToGetDebugInfoForKeywordToCategoryMapping()  {

    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.debug();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] request to get debug info for keyword to category Mapping response success should be '(.*)'$")
  public void searchServiceRequestToGetDebugInfoForKeywordToCategoryMappingResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to run delta indexing of keywords$")
  public void searchServicePrepareRequestToRunDeltaIndexingOfKeywords() {

  }

  @When("^\\[search-service] send request to run delta indexing of keywords$")
  public void searchServiceSendRequestToRunDeltaIndexingOfKeywords()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deltaIndex();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] request to run delta indexing of keywords response success should be '(.*)'$")
  public void searchServiceRequestToRunDeltaIndexingOfKeywordsResponseSuccessShouldBeTrue(Boolean isSuccess)
   {
     ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
     boolean result = response.getResponseBody().isSuccess();
     assertThat("is Success is wrong", result, equalTo(isSuccess));
     assertThat(response.getResponseBody().getErrorMessage(),equalToIgnoringCase("Suggestion Collection delta index started."));
  }
}
