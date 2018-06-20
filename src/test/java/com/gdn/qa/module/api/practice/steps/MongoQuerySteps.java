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
public class MongoQuerySteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare request to query to mongo and fetch classes$")
  public void searchServicePrepareRequestToQueryToMongoAndFetchClasses()  {

  }

  @When("^\\[search-service] send request to query to mongo and fetch classes$")
  public void searchServiceSendRequestToQueryToMongoAndFetchClasses() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.mongoClasses();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] request to query to mongo and fetch classes response success should be '(.*)'$")
  public void searchServiceRequestToQueryToMongoAndFetchClassesResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
        assertThat(response.getResponseBody().getErrorMessage().contains("synonyms_list"),equalTo(true));
  }

  @Given("^\\[search-service] prepare request to query to mongo$")
  public void searchServicePrepareRequestToQueryToMongo() {
    searchServiceData.setKey(searchServiceProperties.get("key"));
    searchServiceData.setMongoValue(searchServiceProperties.get("mongoValue"));
    searchServiceData.setClassName(searchServiceProperties.get("className"));
    searchServiceData.setQueryType(searchServiceProperties.get("queryType"));
  }

  @When("^\\[search-service] send request to query to mongo$")
  public void searchServiceSendRequestToQueryToMongo()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.mongoQuery();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] request to query to mongo response success should be '(.*)'$")
  public void searchServiceRequestToQueryToMongoResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
