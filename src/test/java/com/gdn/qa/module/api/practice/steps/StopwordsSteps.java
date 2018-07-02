package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.StopWordResponse;
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
public class StopwordsSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;
  @Given("^\\[search-service] prepare save stopword using properties using properties data$")
  public void searchServicePrepareSaveStopwordUsingPropertiesUsingPropertiesData()
      {
        searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
        searchServiceData.setStopwordgroup(searchServiceProperties.get("stopwordgroup"));
        searchServiceData.setSync(searchServiceProperties.get("sync"));
  }

  @When("^\\[search-service] send save stopword request$")
  public void searchServiceSendSaveStopwordRequest()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfSaveStopword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] save stopword request response success should be '(.*)'$")
  public void searchServiceSaveStopwordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find stopword by word using properties using properties data$")
  public void searchServicePrepareFindStopwordByWordUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find stopword by word request$")
  public void searchServiceSendFindStopwordByWordRequest() {
    ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceController.findStopwordByword();
    searchServiceData.setFindStopword(response);
  }

  @Then("^\\[search-service] find stopword request response success should be '(.*)'$")
  public void searchServiceFindStopwordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceData.getFindStopword();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getContent().get(0).getStopWord(),equalToIgnoringCase("testingapi"));
    String autoStopwordId=response.getResponseBody().getContent().get(0).getId();
    searchServiceData.setAutoStopwordID(autoStopwordId);
  }


  @Given("^\\[search-service] prepare find stopword by ID using properties using properties data$")
  public void searchServicePrepareFindStopwordByIDUsingPropertiesUsingPropertiesData()
   {
     searchServiceData.setAutoStopwordID(searchServiceData.getAutoStopwordID());
  }

  @When("^\\[search-service] send find stopword by ID request$")
  public void searchServiceSendFindStopwordByIDRequest(){
    ResponseApi<GdnRestSingleResponse<StopWordResponse>> response=searchServiceController.findStopwordByID();
    searchServiceData.setFindStopWordByID(response);
  }

  @Then("^\\[search-service] find stopword by ID request response success should be '(.*)'$")
  public void searchServiceFindStopwordByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<StopWordResponse>> response=searchServiceData.getFindStopWordByID();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getStopWord(),equalToIgnoringCase("testingapi"));
  }

  @Given("^\\[search-service] prepare listing stopword using properties using properties data$")
  public void searchServicePrepareListingStopwordUsingPropertiesUsingPropertiesData()
      {
        searchServiceData.setPage(searchServiceProperties.get("page"));
        searchServiceData.setSize(searchServiceProperties.get("size"));
        searchServiceData.setMongoURL(searchServiceProperties.get("mongoURL"));
        searchServiceData.setMongoDB(searchServiceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send list stopword  request$")
  public void searchServiceSendListStopwordRequest() {
    ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceController.listStopword();
    searchServiceData.setFindStopword(response);
  }

  @Then("^\\[search-service] find listing stopword request response success should be '(.*)'$")
  public void searchServiceFindListingStopwordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
      {
        ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceData.getFindStopword();
        boolean result = response.getResponseBody().isSuccess();
        assertThat("is Success is wrong", result, equalTo(isSuccess));
        MongoClientURI uri =
            new MongoClientURI(searchServiceData.getMongoURL());
        MongoClient mongoClient = new MongoClient(uri);
        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.connectTimeout(30000);
        MongoDatabase db = mongoClient.getDatabase(searchServiceData.getMongoDB());
        MongoCollection<Document> collection = db.getCollection("stopword_list");
        long totalCount = collection.count();
        assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));
  }

  @Given("^\\[search-service] prepare update stopword using properties using properties data$")
  public void searchServicePrepareUpdateStopwordUsingPropertiesUsingPropertiesData()
    {
      searchServiceData.setAutoStopwordID(searchServiceData.getAutoStopwordID());
      searchServiceData.setUpdateFeedValue(searchServiceProperties.get("updatedFeedValue"));
  }

  @When("^\\[search-service] send update stopword by ID request$")
  public void searchServiceSendUpdateStopwordByIDRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfUpdateStopword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update stopword by ID request response success should be '(.*)'$")
  public void searchServiceUpdateStopwordByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess)  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare delete stopword using properties using properties data$")
  public void searchServicePrepareDeleteStopwordUsingPropertiesUsingPropertiesData()
     {
       searchServiceData.setAutoStopwordID(searchServiceData.getAutoStopwordID());
       searchServiceData.setUpdateFeedValue(searchServiceProperties.get("updatedFeedValue"));
  }

  @When("^\\[search-service] send delete stopword by ID request$")
  public void searchServiceSendDeleteStopwordByIDRequest(){
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfDeleteStopword();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete stopword by ID request response success should be '(.*)'$")
  public void searchServiceDeleteStopwordByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find stopword by wrong word using properties using properties data$")
  public void searchServicePrepareFindStopwordByWrongWordUsingPropertiesUsingPropertiesData()
   {
     searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
     searchServiceData.setPage(searchServiceProperties.get("page"));
     searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find stopword by wrong word request$")
  public void searchServiceSendFindStopwordByWrongWordRequest()  {
    ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceController.findStopwordByWrongword();
    searchServiceData.setFindStopword(response);
  }

  @Then("^\\[search-service] find stopword by giving wrong wrong request response success should be '(.*)'$")
  public void searchServiceFindStopwordByGivingWrongWrongRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
     {
       ResponseApi<GdnRestListResponse<StopWordResponse>> response=searchServiceData.getFindStopword();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
       assertThat(response.getResponseBody().getErrorCode(),equalTo(null));
  }

  @Given("^\\[search-service] prepare find stopword by wrong  ID using properties using properties data$")
  public void searchServicePrepareFindStopwordByWrongIDUsingPropertiesUsingPropertiesData()
     {
       searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find stopword by wrong ID request$")
  public void searchServiceSendFindStopwordByWrongIDRequest()  {
    ResponseApi<GdnRestSingleResponse<StopWordResponse>> response=searchServiceController.findStopwordByWrongID();
    searchServiceData.setFindStopWordByID(response);
  }

  @Then("^\\[search-service] find stopword by wrong ID request response success should be '(.*)'$")
  public void searchServiceFindStopwordByWrongIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
     {
       ResponseApi<GdnRestSingleResponse<StopWordResponse>> response=searchServiceData.getFindStopWordByID();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
       assertThat(response.getResponseBody().getValue().getStopWord(),equalTo(null));
  }

  @Given("^\\[search-service] prepare update stopword by wrong ID using properties using properties data$")
  public void searchServicePrepareUpdateStopwordByWrongIDUsingPropertiesUsingPropertiesData()
  {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setUpdateFeedValue(searchServiceProperties.get("updatedFeedValue"));
  }

  @When("^\\[search-service] send update stopword by wrong ID request$")
  public void searchServiceSendUpdateStopwordByWrongIDRequest()  {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfUpdateStopwordByWrongID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update stopword by wrong ID request response success should be '(.*)'$")
  public void searchServiceUpdateStopwordByWrongIDRequestResponseSuccessShouldBeFalse(Boolean isSuccess)
     {
       ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
       assertThat(response.getResponseBody().getErrorMessage(),equalToIgnoringCase("DATA_NOT_FOUND"));
       assertThat(response.getResponseBody().getErrorCode(),equalToIgnoringCase("Can not find data :at stopword but try to update it. with word :card"));
  }
}
