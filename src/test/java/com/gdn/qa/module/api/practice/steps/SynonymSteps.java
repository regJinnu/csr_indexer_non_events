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
import com.gdn.x.search.rest.web.model.SynonymsResponse;
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
public class SynonymSteps {
  @Autowired
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare create synonym using properties using properties data$")
  public void searchServicePrepareCreateSynonymUsingPropertiesUsingPropertiesData() {
  }

  @When("^\\[search-service] send create synonym request$")
  public void searchServiceSendCreateSynonymRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.generateSynonyms();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] create synonym request response success should be '(.*)'$")
  public void searchServiceCreateSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalToIgnoringCase("process started"));
  }

  @Given("^\\[search-service] prepare find synonym by key using properties using properties data$")
  public void searchServicePrepareFindSynonymByKeyUsingPropertiesUsingPropertiesData() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send find synonym by key request$")
  public void searchServiceSendFindSynonymByKeyRequest() {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceController.findByKey();
    searchserviceData.setFindSynonym(response);
  }

  @Then("^\\[search-service] find synonym by key request response success should be '(.*)'$")
  public void searchServiceFindSynonymByKeyRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchserviceData.getFindSynonym();
    boolean result = response.getResponseBody().isSuccess();
    String autoSynonymnId = response.getResponseBody().getValue().getId();
    searchserviceData.setAutoSynonymnId(autoSynonymnId);
    System.out.println(
        "-------------------------------ID-------------------------------" + autoSynonymnId);
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getKey(), equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getValue().getSynonyms(),
        equalToIgnoringCase("test1,test2"));
    assertThat(response.getResponseBody().getValue().getGroupName(),
        equalToIgnoringCase("synonyms_gdn"));
  }

  @Given("^\\[search-service] prepare find synonym by ID using properties using properties data$")
  public void searchServicePrepareFindSynonymByIDUsingPropertiesUsingPropertiesData() {
    System.out.println(
        "--------------ID----------------------" + searchserviceData.getAutoSynonymnId());
    searchserviceData.setAutoSynonymnId(searchserviceData.getAutoSynonymnId());
  }

  @When("^\\[search-service] send find synonym by ID request$")
  public void searchServiceSendFindSynonymByIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindSynonymRequestByID();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service] find synonym by ID request response success should be '(.*)'$")
  public void searchServiceFindSynonymByIDRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchserviceData.getFindKeywordRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    //assertThat(response.getResponseBody().getValue().get);
  }

  @Given("^\\[search-service] prepare find synonym using properties using properties data$")
  public void searchServicePrepareFindSynonymUsingPropertiesUsingPropertiesData() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setPagenumberForIMlist(searchserviceProperties.get("pagenumberForIMlist"));

  }

  @When("^\\[search-service] send find synonym  request$")
  public void searchServiceSendFindSynonymRequest() {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.FindSynonymRequestByWord();
    searchserviceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym request response success should be '(.*)'$")
  public void searchServiceFindSynonymRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchserviceData.getFindSynonymnByWord();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getContent().get(0).getKey(),
        equalToIgnoringCase("testingapi"));
    assertThat(response.getResponseBody().getContent().get(0).getSynonyms(),
        equalToIgnoringCase("test1,test2"));
  }

  @Given("^\\[search-service] prepare list synonym using properties using properties data$")
  public void searchServicePrepareListSynonymUsingPropertiesUsingPropertiesData() {
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setPagenumberForIMlist(searchserviceProperties.get("pagenumberForIMlist"));
    searchserviceData.setMongoURL(searchserviceProperties.get("mongoURL"));
    searchserviceData.setMongoDB(searchserviceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send list synonym  request$")
  public void searchServiceSendListSynonymRequest() {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.listSynonyms();
    searchserviceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym list request response success should be '(.*)'$")
  public void searchServiceFindSynonymListRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchserviceData.getFindSynonymnByWord();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri = new MongoClientURI(searchserviceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchserviceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("synonyms_list");
    long totalCount = collection.count();
    System.out.println(
        collection.count() + "__________________NUMBER FROM DB_______________________________");
    assertThat(totalCount, equalTo(response.getResponseBody().getPageMetaData().getTotalRecords()));
  }

  @Given("^\\[search-service] prepare delete synonym request using properties using properties data$")
  public void searchServicePrepareDeleteSynonymRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setAutoSynonymnId(searchserviceData.getAutoSynonymnId());
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
    searchserviceData.setSynonyms(searchserviceProperties.get("synonyms"));
  }

  @When("^\\[search-service] send delete synonym  request$")
  public void searchServiceSendDeleteSynonymRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyOfDeleteSynonym();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find synonym delete request response success should be '(.*)'$")
  public void searchServiceFindSynonymDeleteRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find synonym by wrong key using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongKeyUsingPropertiesUsingPropertiesData()
      {
        searchserviceData.setWrongname(searchserviceProperties.get("wrongname"));
  }

  @When("^\\[search-service] send find synonym by wrong  key request$")
  public void searchServiceSendFindSynonymByWrongKeyRequest() {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchServiceController.findByWrongKey();
    searchserviceData.setFindSynonym(response);
  }

  @Then("^\\[search-service] find synonym by wrong key request response success should be false$")
  public void searchServiceFindSynonymByWrongKeyRequestResponseSuccessShouldBeFalse()
  {
    ResponseApi<GdnRestSingleResponse<SynonymsResponse>> response =
        searchserviceData.getFindSynonym();
   assertThat(response.getResponseBody().getErrorMessage(),equalToIgnoringCase("DATA_NOT_FOUND"));
   assertThat(response.getResponseBody().getErrorCode(),equalToIgnoringCase("empty data"));
  }


  @Given("^\\[search-service] prepare find synonym by wrong ID using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongIDUsingPropertiesUsingPropertiesData()
       {
         searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find synonym by wrong ID request$")
  public void searchServiceSendFindSynonymByWrongIDRequest() {
    ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
        searchServiceController.FindSynonymRequestByWrongID();
    searchserviceData.setFindKeywordRequest(response);
  }

  @Then("^\\[search-service] find synonym by wrong ID request response success should be '(.*)'$")
  public void searchServiceFindSynonymByWrongIDRequestResponseSuccessShouldBeFalse(Boolean isSuccess)
     {
       ResponseApi<GdnRestSingleResponse<KeywordResponse>> response =
           searchserviceData.getFindKeywordRequest();
       boolean result = response.getResponseBody().isSuccess();
       assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare find synonym by wrong word using properties using properties data$")
  public void searchServicePrepareFindSynonymByWrongWordUsingPropertiesUsingPropertiesData()
 {
   searchserviceData.setWrongword(searchserviceProperties.get("wrongword"));
   searchserviceData.setPage(searchserviceProperties.get("page"));
   searchserviceData.setSize(searchserviceProperties.get("size"));
   searchserviceData.setPagenumberForIMlist(searchserviceProperties.get("pagenumberForIMlist"));
  }

  @When("^\\[search-service] send find synonym by wrong word request$")
  public void searchServiceSendFindSynonymByWrongWordRequest()  {
    ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
        searchServiceController.FindSynonymRequestByWrongWord();
    searchserviceData.setFindSynonymnByWord(response);
  }

  @Then("^\\[search-service] find synonym by wrong word request response success should be '(.*)'$")
  public void searchServiceFindSynonymByWrongWordRequestResponseSuccessShouldBeTrue(Boolean isSuccess)
    {
      ResponseApi<GdnRestListResponse<SynonymsResponse>> response =
          searchserviceData.getFindSynonymnByWord();
      boolean result = response.getResponseBody().isSuccess();
      assertThat("is Success is wrong", result, equalTo(isSuccess));
  }
}
