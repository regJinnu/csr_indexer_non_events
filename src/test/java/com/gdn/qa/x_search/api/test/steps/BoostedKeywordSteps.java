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

import com.gdn.x.search.rest.web.model.BoostedKeywordResponse;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberStepsDefinition
public class BoostedKeywordSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  MongoHelper mongoHelper;

  @Given("^\\[search-service] prepare request to find boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToFindBoostedKeywordUsingPropertiesUsingPropertiesData() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find boosted keyword$")
  public void searchServiceSendRequestToFindBoostedKeyword() {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceController.findBoostedKeyword();
    searchServiceData.setFindBoostedKeyword(response);
  }

  @Then("^\\[search-service] find boosted keyword request response success should be '(.*)'$")
  public void searchServiceFindBoostedKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeyword();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getContent().get(0).getKeyword(),
        equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getContent().get(0).getProducts(),
        equalTo("MTA-0309256"));
    String autoBoostedKeywordId = response.getResponseBody().getContent().get(0).getId();
    searchServiceData.setAutoBoostedKeywordID(autoBoostedKeywordId);

  }


  @Given("^\\[search-service] prepare request to find boosted keyword by ID using properties using properties data$")
  public void searchServicePrepareRequestToFindBoostedKeywordByIDUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoBoostedKeywordID(searchServiceData.getAutoBoostedKeywordID());
  }

  @When("^\\[search-service] send request to find boosted keyword by ID$")
  public void searchServiceSendRequestToFindBoostedKeywordByID() {
    ResponseApi<GdnRestSingleResponse<BoostedKeywordResponse>> response =
        searchServiceController.findBoostedKeywordByID();
    searchServiceData.setFindBoostedKeywordByID(response);
  }

  @Then("^\\[search-service] find boosted keyword by ID request response success should be '(.*)'$")
  public void searchServiceFindBoostedKeywordByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeywordByID();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testingapi"));
    assertThat(response.getResponseBody().getValue().getProducts(),
        equalTo("MTA-0309256"));
  }

  @Given("^\\[search-service] prepare request to list boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToListBoostedKeywordUsingPropertiesUsingPropertiesData() {
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send request to list boosted keyword$")
  public void searchServiceSendRequestToListBoostedKeyword() {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceController.listBoostedKeyword();
    searchServiceData.setFindBoostedKeyword(response);
  }

  @Then("^\\[search-service] list boosted keyword request response success should be '(.*)'$")
  public void searchServiceListBoostedKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeyword();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long totalCount = mongoHelper.countOfRecordsInCollection("keyword_boost_keyword_list");
    assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));

  }

  @Given("^\\[search-service] prepare request to update boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToUpdateBoostedKeywordUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoBoostedKeywordID(searchServiceData.getAutoBoostedKeywordID());
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setProductID(searchServiceProperties.get("productID"));
  }


  @When("^\\[search-service] send request to update boosted keyword$")
  public void searchServiceSendRequestToUpdateBoostedKeyword() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfUpdateBoostedKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update boosted keyword request response success should be '(.*)'$")
  public void searchServiceUpdateBoostedKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to list all the boosted keywords using properties using properties data$")
  public void searchServicePrepareRequestToListAllTheBoostedKeywordsUsingPropertiesUsingPropertiesData() {
  }

  @When("^\\[search-service] send request to list all the boosted keywords$")
  public void searchServiceSendRequestToListAllTheBoostedKeywords() {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceController.listAllBoostedKeyword();
    searchServiceData.setFindBoostedKeyword(response);
  }

  @Then("^\\[search-service] list all the boosted keywords request response success should be '(.*)'$")
  public void searchServiceListAllTheBoostedKeywordsRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeyword();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    String Response = response.getResponseBody().getContent().toString();
    assertThat(Response.contains("testingapi"), equalTo(true));
    assertThat(Response.contains("MTA-0309256"), equalTo(true));
  }

  @Given("^\\[search-service] prepare request to upload boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToUploadBoostedKeywordUsingPropertiesUsingPropertiesData() {
    searchServiceData.setEmail(searchServiceProperties.get("email"));
    searchServiceData.setPathForBoostedKeyword(searchServiceProperties.get("pathForBoostedKeyword"));
  }

  @When("^\\[search-service] send request to upload boosted keyword$")
  public void searchServiceSendRequestToUploadBoostedKeyword() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.uploadBoostedKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] upload the boosted keywords request response success should be '(.*)'$")
  public void searchServiceUploadTheBoostedKeywordsRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),equalTo("File upload in progress"));
    FindIterable<Document>
        mongoIterator=mongoHelper.getMongoDocumentByQuery("keyword_boost_keyword_list","keyword","uvw");
    for (Document doc : mongoIterator){
      String docInStringFormat=doc.toString();
      assertThat(docInStringFormat ,containsString("keyword=uvw"));
      assertThat(docInStringFormat ,containsString("products=MTA-0309256,MTA-0307450"));
    }
   }

  @Given("^\\[search-service] prepare request to delete boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToDeleteBoostedKeywordUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setAutoBoostedKeywordID(searchServiceData.getAutoBoostedKeywordID());
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setProductID(searchServiceProperties.get("productID"));
  }

  @When("^\\[search-service] send request to delete boosted keyword$")
  public void searchServiceSendRequestToDeleteBoostedKeyword()  {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfDeleteBoostedKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete the boosted keyword request response success should be '(.*)'$")
  public void searchServiceDeleteTheBoostedKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to validate boosted keyword using properties using properties data$")
  public void searchServicePrepareRequestToValidateBoostedKeywordUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setProductID(searchServiceProperties.get("productID"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send request to validate boosted keyword$")
  public void searchServiceSendRequestToValidateBoostedKeyword() {
    ResponseApi<GdnRestSingleResponse> response =
        searchServiceController.bodyOfRequestOfValidateID(searchServiceData.getWrongid(),searchServiceData.getProductID());
    searchServiceData.setValidateID(response);
  }

  @Then("^\\[search-service] validate the boosted keyword request response success should be '(.*)'$")
  public void searchServiceValidateTheBoostedKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
     {
       ResponseApi<GdnRestSingleResponse> response = searchServiceData.getValidateID();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
       assertThat(response.getResponseBody().getErrorMessage(),equalTo("MTA-0309256"));
  }

  @Given("^\\[search-service] prepare request to perform multi delete using properties using properties data$")
  public void searchServicePrepareRequestToPerformMultiDeleteUsingPropertiesUsingPropertiesData()
  {
}

  @When("^\\[search-service] send request to perform multi delete$")
  public void searchServiceSendRequestToPerformMultiDelete() throws Exception {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfMultiDelete();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] validate the multi delete request response success should be '(.*)'$")
  public void searchServiceValidateTheMultiDeleteRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to find boosted keyword which is not present using properties using properties data$")
  public void searchServicePrepareRequestToFindBoostedKeywordWhichIsNotPresentUsingPropertiesUsingPropertiesData()
   { searchServiceData.setWrongname(searchServiceProperties.get("wrongname"));
     searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find boosted keyword which is not present$")
  public void searchServiceSendRequestToFindBoostedKeywordWhichIsNotPresent()  {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceController.findWrongBoostedKeyword();
    searchServiceData.setFindBoostedKeyword(response);
  }

  @Then("^\\[search-service] find boosted keyword which is not present request response$")
  public void searchServiceFindBoostedKeywordWhichIsNotPresentRequestResponse()  {
    ResponseApi<GdnRestListResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeyword();
    assertThat(response.getResponseBody().getErrorMessage(),equalTo(null));
  }

  @Given("^\\[search-service] prepare request to find boosted keyword by wrong ID using properties using properties data$")
  public void searchServicePrepareRequestToFindBoostedKeywordByWrongIDUsingPropertiesUsingPropertiesData()
  {
   searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send request to find boosted keyword by wrong ID$")
  public void searchServiceSendRequestToFindBoostedKeywordByWrongID()  {
    ResponseApi<GdnRestSingleResponse<BoostedKeywordResponse>> response =
        searchServiceController.findBoostedKeywordByWrongID();
    searchServiceData.setFindBoostedKeywordByID(response);
  }

  @Then("^\\[search-service] find boosted keyword by wrong ID request response$")
  public void searchServiceFindBoostedKeywordByWrongIDRequestResponse()  {
    ResponseApi<GdnRestSingleResponse<BoostedKeywordResponse>> response =
        searchServiceData.getFindBoostedKeywordByID();
    assertThat(response.getResponseBody().getErrorMessage(),equalTo("UNSPECIFIED"));
    assertThat(response.getResponseBody().getErrorCode(),equalTo(null));
  }

  @Given("^\\[search-service] prepare request to update boosted keyword with wrong id using properties using properties data$")
  public void searchServicePrepareRequestToUpdateBoostedKeywordWithWrongIdUsingPropertiesUsingPropertiesData()
      { searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
        searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
        searchServiceData.setProductID(searchServiceProperties.get("productID"));

  }

  @When("^\\[search-service] send request to update boosted keyword with wrong id$")
  public void searchServiceSendRequestToUpdateBoostedKeywordWithWrongId() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfUpdateBoostedKeywordWithWrongID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check update boosted keyword request with wrong id response$")
  public void searchServiceCheckUpdateBoostedKeywordRequestWithWrongIdResponse()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(),equalTo("NONE"));

  }

  @Given("^\\[search-service] prepare request to upload the file of wrong format using properties using properties data$")
  public void searchServicePrepareRequestToUploadTheFileOfWrongFormatUsingPropertiesUsingPropertiesData()
    {
      searchServiceData.setEmail(searchServiceProperties.get("email"));
      searchServiceData.setWrongFile(searchServiceProperties.get("wrongFile"));
  }

  @When("^\\[search-service] send request to upload the file of wrong format$")
  public void searchServiceSendRequestToUploadTheFileOfWrongFormat()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.uploadWrongFile();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] upload the file of wrong format request response success should be '(.*)'$")
  public void searchServiceUploadTheFileOfWrongFormatRequestResponseSuccessShouldBeFalse(Boolean isSuccess)
      {
        ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
        assertThat(response.getResponseBody().getErrorMessage(),equalTo("file not supported"));
  }
}

