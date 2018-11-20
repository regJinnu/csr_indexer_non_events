package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
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
  private SearchServiceController searchServiceController;

  @Autowired
 private SearchServiceProperties searchServiceProperties;

  @Autowired
 private SearchServiceData searchServiceData;

  @Autowired
  MongoHelper mongoHelper;

  @Given("^\\[search-service] prepare create synonym using properties using properties data$")
  public void searchServicePrepareCreateSynonymUsingPropertiesUsingPropertiesData() {
  }

  @When("^\\[search-service] send create synonym request$")
  public void searchServiceSendCreateSynonymRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.generateSynonyms();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] create synonym request response success should be '(.*)'$")
  public void searchServiceCreateSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalToIgnoringCase("process started"));
  }

  @Given("^\\[search-service] prepare find synonym by key using properties using properties data$")
  public void searchServicePrepareFindSynonymByKeyUsingPropertiesUsingPropertiesData() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send find synonym by key request$")
  public void searchServiceSendFindSynonymByKeyRequest() {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceController.findByKey();
    searchServiceData.setFindSynonym(response);
  }

  @Then("^\\[search-service] find synonym by key request response success should be '(.*)'$")
  public void searchServiceFindSynonymByKeyRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceData.getFindSynonym();
    boolean result = response.getResponseBody().isSuccess();
    String autoSynonymnId = response.getResponseBody().getValue().getId();
    searchServiceData.setAutoSynonymnId(autoSynonymnId);
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getKey(), equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getValue().getSynonyms(),
        equalToIgnoringCase("test1,test2"));
    assertThat(response.getResponseBody().getValue().getGroupName(),
        equalToIgnoringCase("synonyms_gdn"));
  }

  @Given("^\\[search-service] prepare find synonym by ID using properties using properties data$")
  public void searchServicePrepareFindSynonymByIDUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoSynonymnId(searchServiceData.getAutoSynonymnId());
  }

  @When("^\\[search-service] send find synonym by ID request$")
  public void searchServiceSendFindSynonymByIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findSynonymRequestByID();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service] find synonym by ID request response success should be '(.*)'$")
  public void searchServiceFindSynonymByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceData.getFindKeywordRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    //assertThat(response.getResponseBody().getValue().get);
  }

  @Given("^\\[search-service] prepare find synonym using properties using properties data$")
  public void searchServicePrepareFindSynonymUsingPropertiesUsingPropertiesData() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setPagenumberForIMlist(searchServiceProperties.get("pagenumberForIMlist"));

  }

  @When("^\\[search-service] send find synonym  request$")
  public void searchServiceSendFindSynonymRequest() {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.findSynonymRequestByWord();
    searchServiceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym request response success should be '(.*)'$")
  public void searchServiceFindSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceData.getFindSynonymnByWord();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getContent().get(0).getKey(),
        equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getContent().get(0).getSynonyms(),
        equalToIgnoringCase("test1,test2"));
  }

  @Given("^\\[search-service] prepare list synonym using properties using properties data$")
  public void searchServicePrepareListSynonymUsingPropertiesUsingPropertiesData() {
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setPagenumberForIMlist(searchServiceProperties.get("pagenumberForIMlist"));
  }

  @When("^\\[search-service] send list synonym  request$")
  public void searchServiceSendListSynonymRequest() {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.listSynonyms();
    searchServiceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym list request response success should be '(.*)'$")
  public void searchServiceFindSynonymListRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceData.getFindSynonymnByWord();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long totalCount = mongoHelper.countOfRecordsInCollection("synonyms_list");
    assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));
  }

  @Given("^\\[search-service] prepare delete synonym request using properties using properties data$")
  public void searchServicePrepareDeleteSynonymRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoSynonymnId(searchServiceData.getAutoSynonymnId());
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setSynonyms(searchServiceProperties.get("synonyms"));
  }

  @When("^\\[search-service] send delete synonym  request$")
  public void searchServiceSendDeleteSynonymRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfDeleteSynonym();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find synonym delete request response success should be '(.*)'$")
  public void searchServiceFindSynonymDeleteRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find synonym by wrong key using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongKeyUsingPropertiesUsingPropertiesData()
      {
        searchServiceData.setWrongname(searchServiceProperties.get("wrongname"));
  }

  @When("^\\[search-service] send find synonym by wrong  key request$")
  public void searchServiceSendFindSynonymByWrongKeyRequest() {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceController.findByWrongKey();
    searchServiceData.setFindSynonym(response);
  }

  @Then("^\\[search-service] find synonym by wrong key request response success should be false$")
  public void searchServiceFindSynonymByWrongKeyRequestResponseSuccessShouldBeFalse()
  {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceData.getFindSynonym();
   assertThat(response.getResponseBody().getErrorMessage(),equalToIgnoringCase("DATA_NOT_FOUND"));
   assertThat(response.getResponseBody().getErrorCode(),equalToIgnoringCase("empty data"));
  }


  @Given("^\\[search-service] prepare find synonym by wrong ID using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongIDUsingPropertiesUsingPropertiesData()
       {
         searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find synonym by wrong ID request$")
  public void searchServiceSendFindSynonymByWrongIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findSynonymRequestByWrongID();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service] find synonym by wrong ID request response success should be '(.*)'$")
  public void searchServiceFindSynonymByWrongIDRequestResponseSuccessShouldBeFalse(Boolean isSuccess)
     {
       ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
           searchServiceData.getFindKeywordRequest();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find synonym by wrong word using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongWordUsingPropertiesUsingPropertiesData()
 {
   searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
   searchServiceData.setPage(searchServiceProperties.get("page"));
   searchServiceData.setSize(searchServiceProperties.get("size"));
   searchServiceData.setPagenumberForIMlist(searchServiceProperties.get("pagenumberForIMlist"));
  }

  @When("^\\[search-service] send find synonym by wrong word request$")
  public void searchServiceSendFindSynonymByWrongWordRequest()  {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.findSynonymRequestByWrongWord();
    searchServiceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym by wrong word request response success should be '(.*)'$")
  public void searchServiceFindSynonymByWrongWordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
    {
      ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
          searchServiceData.getFindSynonymnByWord();
      boolean result = response.getResponseBody().isSuccess();
      assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
