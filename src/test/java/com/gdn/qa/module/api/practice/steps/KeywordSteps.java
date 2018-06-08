package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.KeywordResponse;
import com.gdn.x.search.rest.web.model.ValidateIdAndGetNameResponse;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare save keyword using properties using properties data$")
  public void searchServicePrepareSaveKeywordUsingPropertiesUsingPropertiesData() {
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
    searchserviceData.setNegativeKeyword(searchserviceProperties.get("negativeKeyword"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send save keyword request$")
  public void searchServiceSendSaveKeywordRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyOfSaveKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] save keyword request response success should be '(.*)'$")
  public void searchServiceSaveKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to get list of keywords$")
  public void searchServicePrepareRequestToGetListOfKeywords() {
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setMongoURL(searchserviceProperties.get("mongoURL"));
    searchserviceData.setMongoDB(searchserviceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send listing keyword request$")
  public void searchServiceSendListingKeywordRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.FindListOfKeywords();
    searchserviceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service] list keyword request response success should be '(.*)'$")
  public void searchServiceListKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchserviceData.getListOfKeywordsRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri =
        new MongoClientURI(searchserviceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchserviceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("keyword_list");
    long totalCount = collection.count();
    System.out.println(
        collection.count() + "__________________NUMBER FROM DB_______________________________");
    assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));

  }

  @Given("^\\[search-service] prepare request to get find by keyword$")
  public void searchServicePrepareRequestToGetFindByKeyword() {
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
  }

  @When("^\\[search-service] send find by keyword request$")
  public void searchServiceSendFindByKeywordRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindByKeywordRequest();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find by keyword request response success should be true$")
  public void searchServiceFindByKeywordRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testautomation"));
    // assertThat(response.getResponseBody().getValue().getNegativeKeyword(), equalTo("backcase"));
    String autoKeywordId = response.getResponseBody().getValue().getId();
    searchserviceData.setAutoKeywordId(autoKeywordId);
    Date autoUpdatedDate = response.getResponseBody().getValue().getUpdatedDate();
    searchserviceData.setAutoUpdatedDate(autoUpdatedDate);
    System.out.println(
        "------------------------------------------------------ID----------------------------------"
            + autoKeywordId);

  }

  @Given("^\\[search-service] prepare request to get keyword find by ID$")
  public void searchServicePrepareRequestToGetKeywordFindByID() {
    searchserviceData.setAutoKeywordId(searchserviceData.getAutoKeywordId());
  }

  @When("^\\[search-service] send find keyword by ID request$")
  public void searchServiceSendFindKeywordByIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindKeywordRequestByID();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find keyword by id request response success should be true$")
  public void searchServiceFindKeywordByIdRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getValue().getNegativeKeyword(), equalTo("backcase"));
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testautomation"));

  }

  @Given("^\\[search-service] prepare request to get keyword find by date$")
  public void searchServicePrepareRequestToGetKeywordFindByDate() {
    System.out.println("--------------------DATE--------------------------------"
        + searchserviceData.getAutoUpdatedDate());
    searchserviceData.setAutoUpdatedDate(searchserviceData.getAutoUpdatedDate());
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));

  }

  @When("^\\[search-service] send find keyword by date request$")
  public void searchServiceSendFindKeywordByDateRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.FindKeywordsByDate();
    searchserviceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find keyword by date request response success should be true$")
  public void searchServiceFindKeywordByDateRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchserviceData.getListOfKeywordsRequest();
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
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find if the product exists$")
  public void searchServiceSendRequestToFindIfTheProductExists() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.FindIfProductExists();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the product exists request response success should be '(.*)'$")
  public void searchServiceFindIfTheProductExistsRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to update the existing product$")
  public void searchServicePrepareRequestToUpdateTheExistingProduct() {
    searchserviceData.setAutoKeywordId(searchserviceData.getAutoKeywordId());
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
    searchserviceData.setUpdatedNegativeKeyword(searchserviceProperties.get("updatedNegativeKeyword"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setType(searchserviceProperties.get("type"));

  }

  @When("^\\[search-service] send request to update the existing product$")
  public void searchServiceSendRequestToUpdateTheExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfUpdateExistingKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the update the existing product request response success should be '(.*)'$")
  public void searchServiceFindIfTheUpdateTheExistingProductRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to validate id and get name$")
  public void searchServicePrepareRequestToValidateIdAndGetName() {
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send request to validate id and get name$")
  public void searchServiceSendRequestToValidateIdAndGetName() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceController.ValidateIdAndGetName();
    searchserviceData.setValidateIdAndGetName(response);
  }

  @Then("^\\[search-service]find if the validate ID and Get Name request response success should be '(.*)'$")
  public void searchServiceFindIfTheValidateIDAndGetNameRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchserviceData.getValidateIdAndGetName();
    assertThat(response.getResponseBody().getValue().getCategoryProductName(),
        equalTo("test test"));
    assertThat(response.getResponseBody().getValue().getCategoryProductId(),
        equalTo("MTA-0306233"));
    assertThat(response.getResponseBody().getValue().getKeyword(), equalTo("testautomation"));
    //assertThat(response.getResponseBody().getValue().getNegativeKeyword(), equalTo("backcase"));
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to find keyword$")
  public void searchServicePrepareRequestToFindKeyword() {
    searchserviceData.setIndex(searchserviceProperties.get("index"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find keyword$")
  public void searchServiceSendRequestToFindKeyword() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.FindListOfKeywordsWithKeyValuePair();
    searchserviceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find if the find keyword request response success should be '(.*)'$")
  public void searchServiceFindIfTheFindKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchserviceData.getListOfKeywordsRequest();
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
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
  }

  @When("^\\[search-service] send request to find keyword by querying in wrong way$")
  public void searchServiceSendRequestToFindKeywordByQueryingInWrongWay() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.FindListOfKeywordsWithWrongInput();
    searchserviceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find if the find keyword by querying in wrong way request response success should be true$")
  public void searchServiceFindIfTheFindKeywordByQueryingInWrongWayRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchserviceData.getListOfKeywordsRequest();
    assertThat(response.getResponseBody().isSuccess(), equalTo(false));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("empty data"));
  }

  @Given("^\\[search-service] prepare request to get find by non existing keyword$")
  public void searchServicePrepareRequestToGetFindByNonExistingKeyword() {
    searchserviceData.setWrongword(searchserviceProperties.get("wrongword"));
  }

  @When("^\\[search-service] send find by non existing keyword request$")
  public void searchServiceSendFindByNonExistingKeywordRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindByNonExistingKeywordRequest();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find by non existing keyword request response success should be true$")
  public void searchServiceFindByNonExistingKeywordRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("unspecified"));
    assertThat(response.getResponseBody().isSuccess(), equalTo(false));
  }

  @Given("^\\[search-service] prepare request to get keyword find by wrong ID$")
  public void searchServicePrepareRequestToGetKeywordFindByWrongID() {
    searchserviceData.setWrongid(searchserviceData.getWrongid());
  }

  @When("^\\[search-service] send find keyword by wrong ID request$")
  public void searchServiceSendFindKeywordByWrongIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindKeywordRequestByWrongID();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service]find keyword by wrong id request response success should be true$")
  public void searchServiceFindKeywordByWrongIdRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(), equalToIgnoringCase("empty data"));
  }

  @Given("^\\[search-service] prepare request to get keyword find by wrong date$")
  public void searchServicePrepareRequestToGetKeywordFindByWrongDate() {
    searchserviceData.setKeywordForsearchingWithDateStamp(searchserviceProperties.get("keywordForsearchingWithDateStamp"));
    searchserviceData.setWrongdate(searchserviceProperties.get("wrongdate"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
  }

  @When("^\\[search-service] send find keyword by wrong  date request$")
  public void searchServiceSendFindKeywordByWrongDateRequest() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchServiceController.FindKeywordsByWrongDate();
    searchserviceData.setListOfKeywordsRequest(response);
  }

  @Then("^\\[search-service]find keyword by wrong date request response success should be true$")
  public void searchServiceFindKeywordByWrongDateRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<KeywordResponse>> response =
        searchserviceData.getListOfKeywordsRequest();
    assertThat(response.getResponseBody().isSuccess(), equalTo(true));
    //assertThat(response.getResponseBody().getContent(), equalTo([]));
  }

  @Given("^\\[search-service] prepare request to find non existing product$")
  public void searchServicePrepareRequestToFindNonExistingProduct() {
    searchserviceData.setWrongcategoryId(searchserviceProperties.get("wrongcategoryId"));
  }

  @When("^\\[search-service] send request to find if the non existing product$")
  public void searchServiceSendRequestToFindIfTheNonExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.findForNonExistingProduct();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]check response for request to find keyword which is not existing$")
  public void searchServiceCheckResponseForRequestToFindKeywordWhichIsNotExisting() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("true"));
  }

  @Given("^\\[search-service] prepare request to update the non existing product$")
  public void searchServicePrepareRequestToUpdateTheNonExistingProduct() {
    searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
    searchserviceData.setUpdatedNegativeKeyword(searchserviceProperties.get("updatedNegativeKeyword"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send request to update the non existing product$")
  public void searchServiceSendRequestToUpdateTheNonExistingProduct() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfUpdateNonExistingKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the update the non existing product request response success should be false$")
  public void searchServiceFindIfTheUpdateTheNonExistingProductRequestResponseSuccessShouldBeFalse() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalToIgnoringCase("unspecified"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :at keyword but try to update it. id :12345"));
  }

  @Given("^\\[search-service] prepare request by providing invalidate id$")
  public void searchServicePrepareRequestByProvidingInvalidateId() {
    searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send request by providing invalidate id$")
  public void searchServiceSendRequestByProvidingInvalidateId() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchServiceController.ValidateNonExistingIdAndGetName();
    searchserviceData.setValidateIdAndGetName(response);
  }

  @Then("^\\[search-service]check the response by paasing invalid ID as input$")
  public void searchServiceCheckTheResponseByPaasingInvalidIDAsInput() {
    ResponseApi<GdnRestSingleResponse<ValidateIdAndGetNameResponse>> response =
        searchserviceData.getValidateIdAndGetName();
    assertThat(response.getResponseBody().getErrorCode(), equalToIgnoringCase("VALIDATION_ERROR"));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalToIgnoringCase("cannot find category / product id : 12345"));
  }

  @Given("^\\[search-service] prepare request to delete keyword$")
  public void searchServicePrepareRequestToDeleteKeyword() {
    searchserviceData.setAutoKeywordId(searchserviceData.getAutoKeywordId());
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
    searchserviceData.setNegativeKeyword(searchserviceProperties.get("negativeKeyword"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send request to delete keyword$")
  public void searchServiceSendRequestToDeleteKeyword() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyOfDeleteKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the delete keyword request response success should be '(.*)'$")
  public void searchServiceFindIfTheDeleteKeywordRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to delete non existing keyword$")
  public void searchServicePrepareRequestToDeleteNonExistingKeyword() {
    searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
    searchserviceData.setKeyword(searchserviceProperties.get("keyword"));
    searchserviceData.setNegativeKeyword(searchserviceProperties.get("negativeKeyword"));
    searchserviceData.setCategoryProductId(searchserviceProperties.get("categoryProductId"));
    searchserviceData.setCategoryProductName(searchserviceProperties.get("categoryProductName"));
    searchserviceData.setType(searchserviceProperties.get("type"));
  }

  @When("^\\[search-service] send request to delete non existing keyword$")
  public void searchServiceSendRequestToDeleteNonExistingKeyword() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfDeleteNonExistingKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find if the non existing delete keyword request response$")
  public void searchServiceFindIfTheNonExistingDeleteKeywordRequestResponse() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
  }

  @Given("^\\[search-service] prepare request to upload keyword$")
  public void searchServicePrepareRequestToUploadKeyword() {
    searchserviceData.setEmail(searchserviceProperties.get("email"));
   searchserviceData.setPath(searchserviceProperties.get("path"));
  }

  @When("^\\[search-service] send request to upload keyword$")
  public void searchServiceSendRequestToUploadKeyword() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.uploadKeyword();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service]find the upload keyword request response is '(.*)'$")
  public void searchServiceFindTheUploadKeywordRequestResponseIsTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("File upload in progress"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
