
package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.SearchTermCategoryClickThroughResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class ClickThroughDataSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
   private SearchServiceData searchServiceData;


  @Given("^\\[search-service] prepare fetching click through data from BRS and index to solr suggestionCollection$")
  public void searchServicePrepareFetchingClickThroughDataFromBRSAndIndexToSolrSuggestionCollection() {
    searchServiceData.setUsername(searchServiceProperties.get("username"));
  }

  @When("^\\[search-service] send request to fetch the data and update into redis and solr$")
  public void searchServiceSendRequestToFetchTheDataAndUpdateIntoRedisAndSolr() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.fetchClickthroughDataFromBRS();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] fetch click through data request response success should be '(.*)'$")
  public void searchServiceFetchClickThroughDataRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare fetching click through data from BRS and store it redis$")
  public void searchServicePrepareFetchingClickThroughDataFromBRSAndStoreItRedis() {
    searchServiceData.setUsername(searchServiceProperties.get("username"));
  }

  @When("^\\[search-service] send request to fetch the data and update into redis$")
  public void searchServiceSendRequestToFetchTheDataAndUpdateIntoRedis() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.fetchClickthroughDataAndStoreIntoRedis();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] fetch click through data and store it into redis request response success should be '(.*)'$")
  public void searchServiceFetchClickThroughDataAndStoreItIntoRedisRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("Storing click-through data & detectedCategory to redis started."));
  }

  @Given("^\\[search-service] prepare fetching click and detected category from redis$")
  public void searchServicePrepareFetchingClickAndDetectedCategoryFromRedis() {
    searchServiceData.setPagenumberForIMlist(searchServiceProperties.get("setPagenumberForIMlist"));
  }

  @When("^\\[search-service] send request to fetch the detected category from redis$")
  public void searchServiceSendRequestToFetchTheDetectedCategoryFromRedis() {
    ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>> response =
        searchServiceController.fetchListOfSearchTermsWhichHasImFromRedis();
    searchServiceData.setCategoryClickThroughResponse(response);
  }

  @Then("^\\[search-service] fetch click through data and detected category from redis request response success should be true$")
  public void searchServiceFetchClickThroughDataAndDetectedCategoryFromRedisRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<SearchTermCategoryClickThroughResponse>> response =
        searchServiceData.getCategoryClickThroughResponse();
    assertThat(response.getResponseBody().getErrorCode(), equalTo("200"));
    assertThat(response.getResponseBody()
        .getValue()
        .getSearchTermCategoryClickThroughMap()
        .get("testingapi")
        .getSearchTerm(), equalTo("testingapi"));
    assertThat(response.getResponseBody()
        .getValue()
        .getSearchTermCategoryClickThroughMap()
        .get("testingapi")
        .getDetectedCategory(), equalTo("AN-1000001"));
  }

}




