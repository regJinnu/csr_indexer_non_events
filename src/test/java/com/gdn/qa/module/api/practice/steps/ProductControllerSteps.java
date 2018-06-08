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
public class ProductControllerSteps {
  @Autowired
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare query to fetch category list using properties using properties data$")
  public void searchServicePrepareQueryToFetchCategoryListUsingPropertiesUsingPropertiesData()
    {
  }

  @When("^\\[search-service] send query to fetch category list request$")
  public void searchServiceSendQueryToFetchCategoryListRequest()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getCategoryList();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch category list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchCategoryListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
    {
      ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
      boolean result = response.getResponseBody().isSuccess();
      assertThat("is Success is wrong", result, equalTo(isSuccess));
      String Response=response.getResponseBody().getErrorMessage();
     if(Response.contains("{\"name\":\"Kamera\",\"value\":\"53184\"}")){
       System.out.println("-------------------------Found Kamera with value 53184---------------------------");
     }
     else{
       System.out.println("--------------Not Found Kamera with value 53184--------------");
     }
  }

  @Given("^\\[search-service] prepare query to fetch merchant list using properties using properties data$")
  public void searchServicePrepareQueryToFetchMerchantListUsingPropertiesUsingPropertiesData()
    {
  }

  @When("^\\[search-service] send query to fetch merchant list request$")
  public void searchServiceSendQueryToFetchMerchantListRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getMerchantList();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch merchant list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchMerchantListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
       {
         ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
         boolean result = response.getResponseBody().isSuccess();
         assertThat("is Success is wrong", result, equalTo(isSuccess));
         String Response=response.getResponseBody().getErrorMessage();
         if(Response.contains("{\"name\":\"ABT-16335\"}")){
           System.out.println("------------------------- Found merchantId with ABT-16335 ---------------------------");
         }
         else{
           System.out.println("-------------- Merchant Not Found --------------");
         }
  }

  @Given("^\\[search-service] prepare query to get product list using properties using properties data$")
  public void searchServicePrepareQueryToGetProductListUsingPropertiesUsingPropertiesData()
     {
       searchserviceData.setCategoryId(searchserviceProperties.get("categoryId"));
       searchserviceData.setMerchant(searchserviceProperties.get("merchant"));
  }

  @When("^\\[search-service] send query to fetch product list request$")
  public void searchServiceSendQueryToFetchProductListRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.getProductList();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] query to fetch product list request response success should be '(.*)'$")
  public void searchServiceQueryToFetchProductListRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
   {
     ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
     boolean result = response.getResponseBody().isSuccess();
     assertThat("is Success is wrong", result, equalTo(isSuccess));
     String Response=response.getResponseBody().getErrorMessage();
  }
}
