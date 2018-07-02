package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.CategoryIntentResponse;
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

@CucumberStepsDefinition
public class CategoryIntentControllerSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private  SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare request for deleting the searchTerm which has intent mining$")
  public void searchServicePrepareRequestForDeletingTheSearchTermWhichHasIntentMining() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send request to delete the search term which has intent mining$")
  public void searchServiceSendRequestToDeleteTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSearchTermWhichHasIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] Delete search term request response success should be '(.*)'$")
  public void searchServiceDeleteSearchTermRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for finding the searchTerm which has intent mining$")
  public void searchServicePrepareRequestForFindingTheSearchTermWhichHasIntentMining() {
    searchServiceData.setWord(searchServiceProperties.get("searchTerm"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find the search term which has intent mining$")
  public void searchServiceSendRequestToFindTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.findSearchTermWhichHasIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find search term request response success should be '(.*)'$")
  public void searchServiceFindSearchTermRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for finding category ID to which search term is mapped$")
  public void searchServicePrepareRequestForFindingCategoryIDToWhichSearchTermIsMapped() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send request to find the category id of the search term which has intent mining$")
  public void searchServiceSendRequestToFindTheCategoryIdOfTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceController.findCategoryIdForSearchTerm();
    searchServiceData.setFindByCategoryIdResponse(response);
  }

  @Then("^\\[search-service] check that the response of the request conatins categoryId$")
  public void searchServiceCheckThatTheResponseOfTheRequestConatinsCategoryId() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceData.getFindByCategoryIdResponse();
    assertThat(response.getResponseBody().getValue().getCategoryId(), equalTo("AN-1000001"));
    assertThat(response.getResponseBody().getValue().getSearchTerm(), equalTo("testingapi"));
    String searchTermToFind = response.getResponseBody().getValue().getSearchTerm();
    searchServiceData.setSearchTermToFind(searchTermToFind);
    String categoryIdToFind = response.getResponseBody().getValue().getCategoryId();
    searchServiceData.setCategoryIdToFind(categoryIdToFind);
  }

  @Given("^\\[search-service] prepare request for finding list of searchTerms which have intent mining$")
  public void searchServicePrepareRequestForFindingListOfSearchTermsWhichHaveIntentMining() {
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setMongoURL(searchServiceProperties.get("mongoURL"));
    searchServiceData.setMongoDB(searchServiceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send request to find the list of search terms which have intent mining$")
  public void searchServiceSendRequestToFindTheListOfSearchTermsWhichHaveIntentMining() {
    ResponseApi<GdnRestListResponse<CategoryIntentResponse>> response =
        searchServiceController.findlistOfSearchTerms();
    searchServiceData.setFindCategoryIntentList(response);
  }

  @Then("^\\[search-service] find list of searchTerms having IM request response success should be '(.*)'$")
  public void searchServiceFindListOfSearchTermsHavingIMRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<CategoryIntentResponse>> response =
        searchServiceData.getFindCategoryIntentList();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri =
        new MongoClientURI(searchServiceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchServiceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("category_intent");
    long totalCount = collection.count();
    assertThat(response.getResponseBody().getPageMetaData().getTotalRecords(), equalTo(totalCount));

  }

  @Given("^\\[search-service] prepare request for saving searchTerm which has intent mining$")
  public void searchServicePrepareRequestForSavingSearchTermWhichHasIntentMining() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setCategoryId(searchServiceProperties.get("categoryId"));
    searchServiceData.setTurnedOn(Boolean.valueOf(searchServiceProperties.get("turnedOn")));
  }

  @When("^\\[search-service] send request to save the search term which has intent mining$")
  public void searchServiceSendRequestToSaveTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveSearchTermWhichHasIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the save searchTerm having IM request response success should be '(.*)'$")
  public void searchServiceFindThatTheSaveSearchTermHavingIMRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for updating searchTerms which has intent mining into redis$")
  public void searchServicePrepareRequestForUpdatingSearchTermsWhichHasIntentMiningIntoRedis() {

  }

  @When("^\\[search-service] send request to update the search terms which have intent mining$")
  public void searchServiceSendRequestToUpdateTheSearchTermsWhichHaveIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.updateSearchTermWhichHasIntentMiningIntoRedis();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the update searchTerms having IM request response success should be '(.*)'$")
  public void searchServiceFindThatTheUpdateSearchTermsHavingIMRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("CategoryIntents successfully updated in redis."));
  }

  @Given("^\\[search-service] prepare request for validating the category$")
  public void searchServicePrepareRequestForValidatingTheCategory() {
    searchServiceData.setCategoryId(searchServiceProperties.get("categoryId"));
  }

  @When("^\\[search-service] send request to validate category$")
  public void searchServiceSendRequestToValidateCategory() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.validateCategoryID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the validate category request response success should be '(.*)'$")
  public void searchServiceFindThatTheValidateCategoryRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for deleting the searchTerm which doesnot intent mining$")
  public void searchServicePrepareRequestForDeletingTheSearchTermWhichDoesnotIntentMining() {
    searchServiceData.setSearchTermNotPresent(searchServiceProperties.get("searchTermNotPresent"));
  }

  @When("^\\[search-service] send request to delete the search term which doesnot intent mining$")
  public void searchServiceSendRequestToDeleteTheSearchTermWhichDoesnotIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSearchTermWhichDoesNotIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the Delete search term request response$")
  public void searchServiceCheckTheDeleteSearchTermRequestResponse() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("Can not find data :for CategoryIntent with searchTerm : waterbottle"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("500"));
  }

  @Given("^\\[search-service] prepare request for saving existing searchTerm$")
  public void searchServicePrepareRequestForSavingExistingSearchTerm() {
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setCategoryId(searchServiceProperties.get("categoryId"));
    searchServiceData.setTurnedOn(Boolean.valueOf(searchServiceProperties.get("turnedOn")));
  }

  @When("^\\[search-service] send request to save the existing search term which has intent mining$")
  public void searchServiceSendRequestToSaveTheExistingSearchTermWhichHasIntentMining()
     {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveSearchTermWhichHasIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for saving existing searchTerm again$")
  public void searchServiceCheckTheResponseForSavingExistingSearchTermAgain() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("Intent already exists"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("200"));
  }

  @Given("^\\[search-service] prepare request for finding category by search term which does not have intent mining$")
  public void searchServicePrepareRequestForFindingCategoryBySearchTermWhichDoesNotHaveIntentMining() {
    searchServiceData.setSearchTermNotPresent(searchServiceProperties.get("searchTermNotPresent"));
  }

  @When("^\\[search-service] send request to find the category id of the search term which doesnot have intent mining$")
  public void searchServiceSendRequestToFindTheCategoryIdOfTheSearchTermWhichDoesnotHaveIntentMining() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceController.findCategoryIdForSearchTermNotPresent();
    searchServiceData.setFindByCategoryIdResponse(response);
  }

  @Then("^\\[search-service] check that the response of the request to find categoryId of nonexisting searchTerm$")
  public void searchServiceCheckThatTheResponseOfTheRequestToFindCategoryIdOfNonexistingSearchTerm() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceData.getFindByCategoryIdResponse();
    assertThat(response.getResponseBody().getValue().getSearchTerm(), equalTo("waterbottle"));
    assertThat(response.getResponseBody().getValue().getCategoryId(), equalTo(null));
  }

  @Given("^\\[search-service] prepare request for finding the searchTerm which doesnot have intent mining$")
  public void searchServicePrepareRequestForFindingTheSearchTermWhichDoesnotHaveIntentMining() {
    searchServiceData.setSearchTermNotPresent(searchServiceProperties.get("searchTermNotPresent"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find the search term which doesnot intent mining$")
  public void searchServiceSendRequestToFindTheSearchTermWhichDoesnotIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.findSearchTermWhichDoesNotIntentMining();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find search term which doesnt have IM request response success should be '(.*)'$")
  public void searchServiceFindSearchTermWhichDoesntHaveIMRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for validating the invalid category$")
  public void searchServicePrepareRequestForValidatingTheInvalidCategory() {
    searchServiceData.setWrongcategoryId(searchServiceProperties.get("wrongcategoryId"));
  }

  @When("^\\[search-service] send request to validate invalidcategory$")
  public void searchServiceSendRequestToValidateInvalidcategory() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.validateInValidCategoryID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check response for trying to validate invalid category$")
  public void searchServiceCheckResponseForTryingToValidateInvalidCategory() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("false"));
  }
}
