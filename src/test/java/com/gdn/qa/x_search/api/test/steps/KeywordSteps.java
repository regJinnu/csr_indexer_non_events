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
import com.gdn.x.search.rest.web.model.ValidateIdAndGetNameResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@CucumberStepsDefinition
public class KeywordSteps {
  @Autowired
 private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
 private SearchServiceData searchServiceData;

  MongoHelper mongoHelper = new MongoHelper();

  @Given("^\\[search-service] prepare save keyword using properties using properties data$")
  public void searchServicePrepareSaveKeywordUsingPropertiesUsingPropertiesData() {
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
    searchServiceData.setNegativeKeyword(searchServiceProperties.get("negativeKeyword"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send save keyword request$")
  public void searchServiceSendSaveKeywordRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfSaveKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] save keyword request response success should be '(.*)'$")
  public void searchServiceSaveKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to get list of keywords$")
  public void searchServicePrepareRequestToGetListOfKeywords() {
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));

  }

  @When("^\\[search-service] send listing keyword request$")
  public void searchServiceSendListingKeywordRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.findListOfKeywords();
    searchServiceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service] list keyword request response success should be '(.*)'$")
  public void searchServiceListKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceData.getListOfKeywordsRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoCollection<Document> collection =
        mongoHelper.initializeDatabase("keyword_list");
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("STORE_ID", "10001");
    long totalCount = collection.count(whereQuery);
    assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));

  }

  @Given("^\\[search-service] prepare request to get find by keyword$")
  public void searchServicePrepareRequestToGetFindByKeyword() {
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
  }

  @When("^\\[search-service] send find by keyword request$")
  public void searchServiceSendFindByKeywordRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findByKeywordRequest();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find by keyword request response success should be true$")
  public void searchServiceFindByKeywordRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testautomation"));
    String autoKeywordId = response.getResponseBody().getValue().getId();
    searchServiceData.setAutoKeywordId(autoKeywordId);
    Date autoUpdatedDate = response.getResponseBody().getValue().getCreatedDate();
    searchServiceData.setAutoUpdatedDate(autoUpdatedDate);

  }

  @Given("^\\[search-service] prepare request to get keyword find by ID$")
  public void searchServicePrepareRequestToGetKeywordFindByID() {
    searchServiceData.setAutoKeywordId(searchServiceData.getAutoKeywordId());
  }

  @When("^\\[search-service] send find keyword by ID request$")
  public void searchServiceSendFindKeywordByIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findKeywordRequestByID();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find keyword by id request response success should be true$")
  public void searchServiceFindKeywordByIdRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getValue().getNegativeKeyword(), equalTo("backcase"));
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testautomation"));

  }

  @Given("^\\[search-service] prepare request to get keyword find by date$")
  public void searchServicePrepareRequestToGetKeywordFindByDate() {
    searchServiceData.setAutoUpdatedDate(searchServiceData.getAutoUpdatedDate());
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));

  }

  @When("^\\[search-service] send find keyword by date request$")
  public void searchServiceSendFindKeywordByDateRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.findKeywordsByDate();
    searchServiceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find keyword by date request response success should be true$")
  public void searchServiceFindKeywordByDateRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceData.getListOfKeywordsRequest();
    assertThat(response.getResponseBody().getContent().get(0).getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getContent().get(0).getNegativeKeyword(),
        equalTo("backcase"));
    assertThat(response.getResponseBody().getContent().get(0).getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getContent().get(0).getKeyword(),
        equalTo("testautomation"));
  }

  @Given("^\\[search-service] prepare request to find if the product exists$")
  public void searchServicePrepareRequestToFindIfTheProductExists() {
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find if the product exists$")
  public void searchServiceSendRequestToFindIfTheProductExists() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.findIfProductExists();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the product exists request response success should be '(.*)'$")
  public void searchServiceFindIfTheProductExistsRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to update the existing product$")
  public void searchServicePrepareRequestToUpdateTheExistingProduct() {
    searchServiceData.setAutoKeywordId(searchServiceData.getAutoKeywordId());
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
    searchServiceData.setUpdatedNegativeKeyword(searchServiceProperties.get("updatedNegativeKeyword"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setType(searchServiceProperties.get("type"));

  }

  @When("^\\[search-service] send request to update the existing product$")
  public void searchServiceSendRequestToUpdateTheExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfUpdateExistingKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the update the existing product request response success should be '(.*)'$")
  public void searchServiceFindIfTheUpdateTheExistingProductRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to validate id and get name$")
  public void searchServicePrepareRequestToValidateIdAndGetName() {
    searchServiceData.setValidate(searchServiceProperties.get("validate"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send request to validate id and get name$")
  public void searchServiceSendRequestToValidateIdAndGetName() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceController.validateIdAndGetName();
    searchServiceData.setValidateIdAndGetName(response);
  }

  @Then("^\\[search-service]find if the validate ID and Get Name request response success should be '(.*)'$")
  public void searchServiceFindIfTheValidateIDAndGetNameRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceData.getValidateIdAndGetName();
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("BBB_DS 1"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306144"));
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to find keyword$")
  public void searchServicePrepareRequestToFindKeyword() {
    searchServiceData.setIndex(searchServiceProperties.get("index"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find keyword$")
  public void searchServiceSendRequestToFindKeyword() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.findListOfKeywordsWithKeyValuePair();
    searchServiceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find if the find keyword request response success should be '(.*)'$")
  public void searchServiceFindIfTheFindKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceData.getListOfKeywordsRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getContent().get(0).getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getContent().get(0).getNegativeKeyword(),
        equalTo("screenguard"));
    assertThat(response.getResponseBody().getContent().get(0).getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getContent().get(0).getKeyword(),
        equalTo("testautomation"));
  }

  @Given("^\\[search-service] prepare request to find keyword by querying in wrong way$")
  public void searchServicePrepareRequestToFindKeywordByQueryingInWrongWay() {
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find keyword by querying in wrong way$")
  public void searchServiceSendRequestToFindKeywordByQueryingInWrongWay() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.findListOfKeywordsWithWrongInput();
    searchServiceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find if the find keyword by querying in wrong way request response success should be true$")
  public void searchServiceFindIfTheFindKeywordByQueryingInWrongWayRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceData.getListOfKeywordsRequest();
    assertThat(response.getResponseBody().isSuccess(), equalTo(false));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("empty data"));
  }

  @Given("^\\[search-service] prepare request to get find by non existing keyword$")
  public void searchServicePrepareRequestToGetFindByNonExistingKeyword() {
    searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
  }

  @When("^\\[search-service] send find by non existing keyword request$")
  public void searchServiceSendFindByNonExistingKeywordRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findByNonExistingKeywordRequest();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find by non existing keyword request response success should be true$")
  public void searchServiceFindByNonExistingKeywordRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("unspecified"));
    assertThat(response.getResponseBody().isSuccess(), equalTo(false));
  }

  @Given("^\\[search-service] prepare request to get keyword find by wrong ID$")
  public void searchServicePrepareRequestToGetKeywordFindByWrongID() {
    searchServiceData.setWrongid(searchServiceData.getWrongid());
  }

  @When("^\\[search-service] send find keyword by wrong ID request$")
  public void searchServiceSendFindKeywordByWrongIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.findKeywordRequestByWrongID();
    searchServiceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find keyword by wrong id request response success should be true$")
  public void searchServiceFindKeywordByWrongIdRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(), equalToIgnoringCase("empty data"));
  }

  @Given("^\\[search-service] prepare request to get keyword find by wrong date$")
  public void searchServicePrepareRequestToGetKeywordFindByWrongDate() {
    searchServiceData.setKeywordForsearchingWithDateStamp(searchServiceProperties.get("keywordForsearchingWithDateStamp"));
    searchServiceData.setWrongdate(searchServiceProperties.get("wrongdate"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find keyword by wrong  date request$")
  public void searchServiceSendFindKeywordByWrongDateRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.findKeywordsByWrongDate();
    searchServiceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find keyword by wrong date request response success should be true$")
  public void searchServiceFindKeywordByWrongDateRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceData.getListOfKeywordsRequest();
    assertThat(response.getResponseBody().isSuccess(), equalTo(true));
    //assertThat(response.getResponseBody().getContent(), equalTo([]));
  }

  @Given("^\\[search-service] prepare request to find non existing product$")
  public void searchServicePrepareRequestToFindNonExistingProduct() {
    searchServiceData.setWrongcategoryId(searchServiceProperties.get("wrongcategoryId"));
  }

  @When("^\\[search-service] send request to find if the non existing product$")
  public void searchServiceSendRequestToFindIfTheNonExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.findForNonExistingProduct();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]check response for request to find keyword which is not existing$")
  public void searchServiceCheckResponseForRequestToFindKeywordWhichIsNotExisting() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("true"));
  }

  @Given("^\\[search-service] prepare request to update the non existing product$")
  public void searchServicePrepareRequestToUpdateTheNonExistingProduct() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
    searchServiceData.setUpdatedNegativeKeyword(searchServiceProperties.get("updatedNegativeKeyword"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send request to update the non existing product$")
  public void searchServiceSendRequestToUpdateTheNonExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfUpdateNonExistingKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the update the non existing product request response success should be false$")
  public void searchServiceFindIfTheUpdateTheNonExistingProductRequestResponseSuccessShouldBeFalse() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("unspecified"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :at keyword but try to update it. id :12345"));
  }

  @Given("^\\[search-service] prepare request by providing invalidate id$")
  public void searchServicePrepareRequestByProvidingInvalidateId() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send request by providing invalidate id$")
  public void searchServiceSendRequestByProvidingInvalidateId() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceController.validateNonExistingIdAndGetName();
    searchServiceData.setValidateIdAndGetName(response);
  }

  @Then("^\\[search-service]check the response by paasing invalid ID as input$")
  public void searchServiceCheckTheResponseByPaasingInvalidIDAsInput() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceData.getValidateIdAndGetName();
    assertThat(response.getResponseBody().getErrorCode(), equalToIgnoringCase("VALIDATION_ERROR"));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalToIgnoringCase("cannot find category / product id : 12345"));
  }

  @Given("^\\[search-service] prepare request to delete keyword$")
  public void searchServicePrepareRequestToDeleteKeyword() {
    searchServiceData.setAutoKeywordId(searchServiceData.getAutoKeywordId());
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
    searchServiceData.setNegativeKeyword(searchServiceProperties.get("negativeKeyword"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send request to delete keyword$")
  public void searchServiceSendRequestToDeleteKeyword() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfDeleteKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the delete keyword request response success should be '(.*)'$")
  public void searchServiceFindIfTheDeleteKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to delete non existing keyword$")
  public void searchServicePrepareRequestToDeleteNonExistingKeyword() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setKeyword(searchServiceProperties.get("keyword"));
    searchServiceData.setNegativeKeyword(searchServiceProperties.get("negativeKeyword"));
    searchServiceData.setCategoryProductId(searchServiceProperties.get("categoryProductId"));
    searchServiceData.setCategoryProductName(searchServiceProperties.get("categoryProductName"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send request to delete non existing keyword$")
  public void searchServiceSendRequestToDeleteNonExistingKeyword() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfDeleteNonExistingKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the non existing delete keyword request response$")
  public void searchServiceFindIfTheNonExistingDeleteKeywordRequestResponse() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
  }

  @Given("^\\[search-service] prepare request to upload keyword$")
  public void searchServicePrepareRequestToUploadKeyword() {
    searchServiceData.setEmail(searchServiceProperties.get("email"));
    searchServiceData.setPath(searchServiceProperties.get("path"));
  }

  @When("^\\[search-service] send request to upload keyword$")
  public void searchServiceSendRequestToUploadKeyword() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.uploadKeyword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find the upload keyword request response is '(.*)'$")
  public void searchServiceFindTheUploadKeywordRequestResponseIsTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("File upload in progress"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
