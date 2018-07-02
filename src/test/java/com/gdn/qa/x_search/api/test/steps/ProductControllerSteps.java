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
public class ProductControllerSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare query to fetch category list using properties using properties data$")
  public void searchServicePrepareQueryToFetchCategoryListUsingPropertiesUsingPropertiesData()
    {
  }

  @When("^\\[search-service] send query to fetch category list request$")
  public void searchServiceSendQueryToFetchCategoryListRequest()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getCategoryList();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch category list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchCategoryListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
    {
      ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
      boolean result = response.getResponseBody().isSuccess();
      assertThat("is Success is wrong", result, equalTo(isSuccess));
      String Response=response.getResponseBody().getErrorMessage();
      assertThat(Response.contains("{\"name\":\"Kamera\",\"value\":\"53184\"}"),equalTo(true));
  }

  @Given("^\\[search-service] prepare query to fetch merchant list using properties using properties data$")
  public void searchServicePrepareQueryToFetchMerchantListUsingPropertiesUsingPropertiesData()
    {
  }

  @When("^\\[search-service] send query to fetch merchant list request$")
  public void searchServiceSendQueryToFetchMerchantListRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getMerchantList();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch merchant list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchMerchantListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
       {
         ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
         boolean result = response.getResponseBody().isSuccess();
         assertThat("is Success is wrong", result, equalTo(isSuccess));
         String Response=response.getResponseBody().getErrorMessage();
         assertThat(Response.contains("{\"name\":\"ABT-16335\"}"),equalTo(true));
  }

  @Given("^\\[search-service] prepare query to get product list using properties using properties data$")
  public void searchServicePrepareQueryToGetProductListUsingPropertiesUsingPropertiesData()
     {
       searchServiceData.setCategoryId(searchServiceProperties.get("categoryId"));
       searchServiceData.setMerchant(searchServiceProperties.get("merchant"));
  }

  @When("^\\[search-service] send query to fetch product list request$")
  public void searchServiceSendQueryToFetchProductListRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getProductList();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch product list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchProductListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
   {
     ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
     boolean result = response.getResponseBody().isSuccess();
     assertThat("is Success is wrong", result, equalTo(isSuccess));
     String Response=response.getResponseBody().getErrorMessage();
  }
}
