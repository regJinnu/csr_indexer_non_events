package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.SetConfigResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@CucumberStepsDefinition
public class SetConfigSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare request to fetch the saved config using properties using properties data$")
  public void searchServicePrepareRequestToFetchTheSavedConfigUsingPropertiesUsingPropertiesData()
      {
        searchServiceData.setSetConfig(searchServiceProperties.get("setConfig"));
  }

  @When("^\\[search-service] send request to fetch saved config$")
  public void searchServiceSendRequestToFetchSavedConfig()  {
    ResponseApi<GdnRestSingleResponse<SetConfigResponse>> response=searchServiceController.setConfigRequest();
    searchServiceData.setSetConfigResponse(response);
  }

  @Then("^\\[search-service] fetch saved config request response success should be '(.*)'$")
  public void searchServiceFetchSavedConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnRestSingleResponse<SetConfigResponse>> response= searchServiceData.getSetConfigResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getName(),equalToIgnoringCase("campaign.live.list"));
    //assertThat(response.getResponseBody().getValue().getValue().toString(),equalTo("[TEST-00154, CAMP-00074, CAMP-00154]"));
  }

  @Given("^\\[search-service] prepare request to update the field cache using properties using properties data$")
  public void searchServicePrepareRequestToUpdateTheFieldCacheUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setFieldName(searchServiceProperties.get("fieldName"));
  }

  @When("^\\[search-service] send request to update field cache$")
  public void searchServiceSendRequestToUpdateFieldCache()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.updateFieldCache();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update field cache request response success should be '(.*)'$")
  public void searchServiceUpdateFieldCacheRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response=searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage().contains("Cache reloading is completed"),equalTo(true));
  }

  @Given("^\\[search-service] prepare request to update the non existing field cache using properties using properties data$")
  public void searchServicePrepareRequestToUpdateTheNonExistingFieldCacheUsingPropertiesUsingPropertiesData()
   {
     searchServiceData.setWrongname(searchServiceProperties.get("wrongname"));
  }

  @When("^\\[search-service] send request to update non existing field cache$")
  public void searchServiceSendRequestToUpdateNonExistingFieldCache() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.updateNonExistingFieldCache();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update non existing field cache request response success should be false$")
  public void searchServiceUpdateNonExistingFieldCacheRequestResponseSuccessShouldBeFalse()
  {
    ResponseApi<GdnBaseRestResponse> response=searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", response.getResponse().getStatusCode(), equalTo(500));
  //  assertThat(response.getResponseBody().getErrorMessage(),containsString("undefined field"));
   // assertThat(response.getResponseBody().getErrorCode(),equalTo("UNSPECIFIED"));
  }
}
