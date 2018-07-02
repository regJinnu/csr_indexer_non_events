package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.ProductResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class ProductFromSOLRSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;
  @Given("^\\[search-service] prepare request to get product raw data by product id or by sku$")
  public void searchServicePrepareRequestToGetProductRawDataByProductIdOrBySku()  {
    searchServiceData.setProductID(searchServiceProperties.get("productID"));
  }

  @When("^\\[search-service] send request to get product raw data by product id or by sku$")
  public void searchServiceSendRequestToGetProductRawDataByProductIdOrBySku()  {
    ResponseApi<GdnRestSingleResponse<ProductResponse>> response=searchServiceController.bodyOfRequestToQueryWithProductID(searchServiceData.getProductID());
    searchServiceData.setFindDataByProductID(response);
  }

  @Then("^\\[search-service] request to get product raw data by product id or by sku response success should be '(.*)'$")
  public void searchServiceRequestToGetProductRawDataByProductIdOrBySkuResponseSuccessShouldBeTrue(Boolean isSuccess)
  {
    ResponseApi<GdnRestSingleResponse<ProductResponse>> response = searchServiceData.getFindDataByProductID();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
