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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class ScheduledEventsSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare request to delete the unpublished products$")
  public void searchServicePrepareRequestToDeleteTheUnpublishedProducts() {

  }

  @When("^\\[search-service] send request to delete the unpublished products$")
  public void searchServiceSendRequestToDeleteTheUnpublishedProducts() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteUnpublished();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] fetch delete the unpublished products response success should be '(.*)'$")
  public void searchServiceFetchDeleteTheUnpublishedProductsResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        containsString("Deleting of unpublished products finished"));
  }

  @Given("^\\[search-service] prepare request to fetch scheduled events from mongo and do atomic update to solr$")
  public void searchServicePrepareRequestToFetchScheduledEventsFromMongoAndDoAtomicUpdateToSolr() {

  }

  @When("^\\[search-service] send request to fetch scheduled events from mongo and do atomic update to solr$")
  public void searchServiceSendRequestToFetchScheduledEventsFromMongoAndDoAtomicUpdateToSolr() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.fetchTheListOfUnpublishedProducts();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] fetch scheduled events from mongo and do atomic update to solr response success should be '(.*)'$")
  public void searchServiceFetchScheduledEventsFromMongoAndDoAtomicUpdateToSolrResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));

  }
}
