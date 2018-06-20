package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.FeedExclusionEntityResponse;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@CucumberStepsDefinition
public class FeedExclusionSteps {
  @Autowired
 private SearchServiceController searchServiceController;

  @Autowired
 private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;


  @Given("^\\[search-service] prepare save feed exclusion using properties using properties data$")
  public void searchServicePrepareSaveFeedExclusionUsingPropertiesUsingPropertiesData() {
    searchServiceData.setFeedKey(searchServiceProperties.get("feedKey"));
    searchServiceData.setFeedType(searchServiceProperties.get("feedType"));
    searchServiceData.setFeedValue(searchServiceProperties.get("feedValue"));
  }

  @When("^\\[search-service] send save feed exclusion request$")
  public void searchServiceSendSaveFeedExclusionRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfFeedExclusionSaveRequest();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] save feed exclusion request response success should be '(.*)'$")
  public void searchServiceSaveFeedExclusionRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }


  @Given("^\\[search-service] prepare find feed exclusion using properties using properties data$")
  public void searchServicePrepareFindFeedExclusionUsingPropertiesUsingPropertiesData() {
    searchServiceData.setFeedValue(searchServiceProperties.get("feedValue"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find feed exclusion request$")
  public void searchServiceSendFindFeedExclusionRequest() {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceController.findFeedByWord();
    searchServiceData.setFindFeedRequest(response);
  }

  @Then("^\\[search-service] find feed exclusion request response success should be '(.*)'$")
  public void searchServiceFindFeedExclusionRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceData.getFindFeedRequest();
    boolean result = response.getResponseBody().isSuccess();

    String autoFeedId = response.getResponseBody().getContent().get(0).getId();
    System.out.println(autoFeedId);
    searchServiceData.setAutoFeedId(autoFeedId);
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find feed exclusion By ID using properties using properties data$")
  public void searchServicePrepareFindFeedExclusionByIDUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoFeedId(searchServiceData.getAutoFeedId());
  }

  @When("^\\[search-service] send find feed exclusion by ID request$")
  public void searchServiceSendFindFeedExclusionByIDRequest() {
    ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> response =
        searchServiceController.findFeedByID();
    searchServiceData.setFindFeedByIdRequest(response);
  }

  @Then("^\\[search-service] find feed exclusion request by ID response success should be true$")
  public void searchServiceFindFeedExclusionRequestByIDResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> response =
        searchServiceData.getFindFeedByIdRequest();
    assertThat(response.getResponseBody().getValue().getValue(), equalTo("bag"));
    assertThat(response.getResponseBody().getValue().getFeedType(), equalToIgnoringCase("google"));
    assertThat(response.getResponseBody().getValue().getKey(), equalTo("nameSearch"));
  }

  @Given("^\\[search-service] prepare update feed exclusion using properties using properties data$")
  public void searchServicePrepareUpdateFeedExclusionUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoFeedId(searchServiceData.getAutoFeedId());
    searchServiceData.setFeedKey(searchServiceProperties.get("feedKey"));
    searchServiceData.setUpdateFeedValue(searchServiceProperties.get("updatedFeedValue"));
    searchServiceData.setFeedType(searchServiceProperties.get("feedType"));
  }

  @When("^\\[search-service] send update feed exclusion by ID request$")
  public void searchServiceSendUpdateFeedExclusionByIDRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfFeedExclusionUpdateRequest();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update feed exclusion request response success should be '(.*)'$")
  public void searchServiceUpdateFeedExclusionRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find feed exclusion list using properties using properties data$")
  public void searchServicePrepareFindFeedExclusionListUsingPropertiesUsingPropertiesData() {
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setMongoURL(searchServiceProperties.get("mongoURL"));
    searchServiceData.setMongoDB(searchServiceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send find feed exclusion list$")
  public void searchServiceSendFindFeedExclusionList() {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceController.listAllFeedExclusions();
    searchServiceData.setFindFeedRequest(response);
  }

  @Then("^\\[search-service] find feed exclusion list request response success should be true$")
  public void searchServiceFindFeedExclusionListRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceData.getFindFeedRequest();
    MongoClientURI uri =
        new MongoClientURI(searchServiceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchServiceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("feed_exclusion_list");
    long totalCount = collection.count();
    System.out.println("----------------------------Total documents------------" + totalCount);
    assertThat(response.getResponseBody().getPageMetaData().getTotalRecords(), equalTo(totalCount));
  }


  @Given("^\\[search-service] prepare delete feed exclusion using properties using properties data$")
  public void searchServicePrepareDeleteFeedExclusionUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoFeedId(searchServiceData.getAutoFeedId());
    searchServiceData.setFeedKey(searchServiceProperties.get("feedKey"));
    searchServiceData.setUpdateFeedValue(searchServiceProperties.get("updatedFeedValue"));
    searchServiceData.setFeedType(searchServiceProperties.get("feedType"));
  }

  @When("^\\[search-service] send delete feed exclusion by ID request$")
  public void searchServiceSendDeleteFeedExclusionByIDRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfFeedExclusionDeleteRequest();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete feed exclusion request response success should be '(.*)'$")
  public void searchServiceDeleteFeedExclusionRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find feed exclusion which is not present in the db$")
  public void searchServicePrepareFindFeedExclusionWhichIsNotPresentInTheDb() {
    searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find feed exclusion request for feed which is absent in db$")
  public void searchServiceSendFindFeedExclusionRequestForFeedWhichIsAbsentInDb() {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceController.findFeedByWrongWord();
    searchServiceData.setFindFeedRequest(response);
  }

  @Then("^\\[search-service] check find feed exclusion request response when feed is not present$")
  public void searchServiceCheckFindFeedExclusionRequestResponseWhenFeedIsNotPresent() {
    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> response =
        searchServiceData.getFindFeedRequest();
    assertThat(response.getResponseBody().getErrorCode(), equalTo(null));
  }

  @Given("^\\[search-service] prepare find feed exclusion By wrong ID request$")
  public void searchServicePrepareFindFeedExclusionByWrongIDRequest() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find feed exclusion by wrong ID request$")
  public void searchServiceSendFindFeedExclusionByWrongIDRequest() {
    ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> response =
        searchServiceController.findFeedByWrongID();
    searchServiceData.setFindFeedByIdRequest(response);
  }

  @Then("^\\[search-service] find feed exclusion request by wrong ID response$")
  public void searchServiceFindFeedExclusionRequestByWrongIDResponse() {
    ResponseApi<GdnRestSingleResponse<FeedExclusionEntityResponse>> response =
        searchServiceData.getFindFeedByIdRequest();
    assertThat(response.getResponseBody().getErrorCode(), equalTo("500"));
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("DATA_NOT_FOUND"));
  }

  @Given("^\\[search-service] prepare update feed exclusion by providing wrong ID$")
  public void searchServicePrepareUpdateFeedExclusionByProvidingWrongID() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setFeedKey(searchServiceProperties.get("feedKey"));
    searchServiceData.setFeedValue(searchServiceProperties.get("feedValue"));
    searchServiceData.setFeedType(searchServiceProperties.get("feedType"));
  }

  @When("^\\[search-service] send update feed exclusion by wrong ID request$")
  public void searchServiceSendUpdateFeedExclusionByWrongIDRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfFeedExclusionUpdateRequestWithWrongID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update feed exclusion request response by providing wrong ID$")
  public void searchServiceUpdateFeedExclusionRequestResponseByProvidingWrongID() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("Can not find data :"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("500"));
  }
}

