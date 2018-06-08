package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.KeywordResponse;
import com.gdn.x.search.rest.web.model.SynonymsResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@CucumberStepsDefinition
public class SynonymSteps {
  @Autowired
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare create synonym using properties using properties data$")
  public void searchServicePrepareCreateSynonymUsingPropertiesUsingPropertiesData()
  {
  }

  @When("^\\[search-service] send create synonym request$")
  public void searchServiceSendCreateSynonymRequest(){
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.generateSynonyms();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] create synonym request response success should be '(.*)'$")
  public void searchServiceCreateSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),equalToIgnoringCase("process started"));
  }

  @Given("^\\[search-service] prepare find synonym by key using properties using properties data$")
  public void searchServicePrepareFindSynonymByKeyUsingPropertiesUsingPropertiesData()
  {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send find synonym by key request$")
  public void searchServiceSendFindSynonymByKeyRequest()  {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response=searchServiceController.findByKey();
    searchserviceData.setFindSynonym(response);
  }

  @Then("^\\[search-service] find synonym by key request response success should be '(.*)'$")
  public void searchServiceFindSynonymByKeyRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response = searchserviceData.getFindSynonym();
    boolean result = response.getResponseBody().isSuccess();
    String AutoSynonymnId= response.getResponseBody().getValue().getId();
    searchserviceData.setAutoSynonymnId("AutoSynonymnId");
    System.out.println("-------------------------------ID-------------------------------"+AutoSynonymnId);
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getKey(),equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getValue().getSynonyms(),equalToIgnoringCase("test1,test2"));
    assertThat(response.getResponseBody().getValue().getGroupName(),equalToIgnoringCase("synonyms_gdn"));
  }

  @Given("^\\[search-service] prepare find synonym by ID using properties using properties data$")
  public void searchServicePrepareFindSynonymByIDUsingPropertiesUsingPropertiesData()
     {
     searchserviceData.setAutoSynonymnId(searchserviceData.getAutoSynonymnId());
  }

  @When("^\\[search-service] send find synonym by ID request$")
  public void searchServiceSendFindSynonymByIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindSynonymRequestByID();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service] find synonym by ID request response success should be '(.*)'$")
  public void searchServiceFindSynonymByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    String Response=response.getResponseBody().getErrorMessage();
  }
}
