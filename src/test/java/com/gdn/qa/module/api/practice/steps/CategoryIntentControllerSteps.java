package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
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
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare request for deleting the searchTerm which has intent mining$")
  public void searchServicePrepareRequestForDeletingTheSearchTermWhichHasIntentMining() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send request to delete the search term which has intent mining$")
  public void searchServiceSendRequestToDeleteTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSearchTermWhichHasIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] Delete search term request response success should be '(.*)'$")
  public void searchServiceDeleteSearchTermRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for finding the searchTerm which has intent mining$")
  public void searchServicePrepareRequestForFindingTheSearchTermWhichHasIntentMining() {
    searchserviceData.setWord(searchserviceProperties.get("searchTerm"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find the search term which has intent mining$")
  public void searchServiceSendRequestToFindTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.findSearchTermWhichHasIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find search term request response success should be '(.*)'$")
  public void searchServiceFindSearchTermRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for finding category ID to which search term is mapped$")
  public void searchServicePrepareRequestForFindingCategoryIDToWhichSearchTermIsMapped() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
  }

  @When("^\\[search-service] send request to find the category id of the search term which has intent mining$")
  public void searchServiceSendRequestToFindTheCategoryIdOfTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceController.findCategoryIdForSearchTerm();
    searchserviceData.setFindByCategoryIdResponse(response);
  }

  @Then("^\\[search-service] check that the response of the request conatins categoryId$")
  public void searchServiceCheckThatTheResponseOfTheRequestConatinsCategoryId() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchserviceData.getFindByCategoryIdResponse();
    assertThat(response.getResponseBody().getValue().getCategoryId(), equalTo("AN-1000001"));
    assertThat(response.getResponseBody().getValue().getSearchTerm(), equalTo("testingapi"));
    String searchTermToFind = response.getResponseBody().getValue().getSearchTerm();
    searchserviceData.setSearchTermToFind(searchTermToFind);
    String categoryIdToFind = response.getResponseBody().getValue().getCategoryId();
    searchserviceData.setCategoryIdToFind(categoryIdToFind);
  }

  @Given("^\\[search-service] prepare request for finding list of searchTerms which have intent mining$")
  public void searchServicePrepareRequestForFindingListOfSearchTermsWhichHaveIntentMining() {
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
    searchserviceData.setMongoURL(searchserviceProperties.get("mongoURL"));
    searchserviceData.setMongoDB(searchserviceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send request to find the list of search terms which have intent mining$")
  public void searchServiceSendRequestToFindTheListOfSearchTermsWhichHaveIntentMining() {
    ResponseApi<GdnRestListResponse<CategoryIntentResponse>> response =
        searchServiceController.findlistOfSearchTerms();
    searchserviceData.setFindCategoryIntentList(response);
  }

  @Then("^\\[search-service] find list of searchTerms having IM request response success should be '(.*)'$")
  public void searchServiceFindListOfSearchTermsHavingIMRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<CategoryIntentResponse>> response =
        searchserviceData.getFindCategoryIntentList();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri =
        new MongoClientURI(searchserviceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchserviceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("category_intent");
    long totalCount = collection.count();
    System.out.println("----------------------------Total documents------------" + totalCount);
    assertThat(response.getResponseBody().getPageMetaData().getTotalRecords(), equalTo(totalCount));

  }

  @Given("^\\[search-service] prepare request for saving searchTerm which has intent mining$")
  public void searchServicePrepareRequestForSavingSearchTermWhichHasIntentMining() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
    searchserviceData.setCategoryId(searchserviceProperties.get("categoryId"));
    searchserviceData.setTurnedOn(Boolean.valueOf(searchserviceProperties.get("turnedOn")));
  }

  @When("^\\[search-service] send request to save the search term which has intent mining$")
  public void searchServiceSendRequestToSaveTheSearchTermWhichHasIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveSearchTermWhichHasIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the save searchTerm having IM request response success should be '(.*)'$")
  public void searchServiceFindThatTheSaveSearchTermHavingIMRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for updating searchTerms which has intent mining into redis$")
  public void searchServicePrepareRequestForUpdatingSearchTermsWhichHasIntentMiningIntoRedis() {

  }

  @When("^\\[search-service] send request to update the search terms which have intent mining$")
  public void searchServiceSendRequestToUpdateTheSearchTermsWhichHaveIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.UpdateSearchTermWhichHasIntentMiningIntoRedis();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the update searchTerms having IM request response success should be '(.*)'$")
  public void searchServiceFindThatTheUpdateSearchTermsHavingIMRequestResponseSuccessShouldBeTrue(
      Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("CategoryIntents successfully updated in redis."));
  }

  @Given("^\\[search-service] prepare request for validating the category$")
  public void searchServicePrepareRequestForValidatingTheCategory() {
    searchserviceData.setCategoryId(searchserviceProperties.get("categoryId"));
  }

  @When("^\\[search-service] send request to validate category$")
  public void searchServiceSendRequestToValidateCategory() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.validateCategoryID();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find that the validate category request response success should be '(.*)'$")
  public void searchServiceFindThatTheValidateCategoryRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for deleting the searchTerm which doesnot intent mining$")
  public void searchServicePrepareRequestForDeletingTheSearchTermWhichDoesnotIntentMining() {
    searchserviceData.setSearchTermNotPresent(searchserviceProperties.get("searchTermNotPresent"));
  }

  @When("^\\[search-service] send request to delete the search term which doesnot intent mining$")
  public void searchServiceSendRequestToDeleteTheSearchTermWhichDoesnotIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSearchTermWhichDoesNotIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the Delete search term request response$")
  public void searchServiceCheckTheDeleteSearchTermRequestResponse() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("Can not find data :for CategoryIntent with searchTerm : waterbottle"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("500"));
  }

  @Given("^\\[search-service] prepare request for saving existing searchTerm$")
  public void searchServicePrepareRequestForSavingExistingSearchTerm() {
    searchserviceData.setSearchTerm(searchserviceProperties.get("searchTerm"));
    searchserviceData.setCategoryId(searchserviceProperties.get("categoryId"));
    searchserviceData.setTurnedOn(Boolean.valueOf(searchserviceProperties.get("turnedOn")));
  }

  @When("^\\[search-service] send request to save the existing search term which has intent mining$")
  public void searchServiceSendRequestToSaveTheExistingSearchTermWhichHasIntentMining()
      throws Throwable {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveSearchTermWhichHasIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for saving existing searchTerm again$")
  public void searchServiceCheckTheResponseForSavingExistingSearchTermAgain() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("Intent already exists"));
    assertThat(response.getResponseBody().getErrorCode(), equalTo("200"));
  }

  @Given("^\\[search-service] prepare request for finding category by search term which does not have intent mining$")
  public void searchServicePrepareRequestForFindingCategoryBySearchTermWhichDoesNotHaveIntentMining() {
    searchserviceData.setSearchTermNotPresent(searchserviceProperties.get("searchTermNotPresent"));
  }

  @When("^\\[search-service] send request to find the category id of the search term which doesnot have intent mining$")
  public void searchServiceSendRequestToFindTheCategoryIdOfTheSearchTermWhichDoesnotHaveIntentMining() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchServiceController.findCategoryIdForSearchTermNotPresent();
    searchserviceData.setFindByCategoryIdResponse(response);
  }

  @Then("^\\[search-service] check that the response of the request to find categoryId of nonexisting searchTerm$")
  public void searchServiceCheckThatTheResponseOfTheRequestToFindCategoryIdOfNonexistingSearchTerm() {
    ResponseApi<GdnRestSingleResponse<CategoryIntentResponse>> response =
        searchserviceData.getFindByCategoryIdResponse();
    assertThat(response.getResponseBody().getValue().getSearchTerm(), equalTo("waterbottle"));
    assertThat(response.getResponseBody().getValue().getCategoryId(), equalTo(null));
  }

  @Given("^\\[search-service] prepare request for finding the searchTerm which doesnot have intent mining$")
  public void searchServicePrepareRequestForFindingTheSearchTermWhichDoesnotHaveIntentMining() {
    searchserviceData.setSearchTermNotPresent(searchserviceProperties.get("searchTermNotPresent"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
  }

  @When("^\\[search-service] send request to find the search term which doesnot intent mining$")
  public void searchServiceSendRequestToFindTheSearchTermWhichDoesnotIntentMining() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.findSearchTermWhichDoesNotIntentMining();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] find search term which doesnt have IM request response success should be '(.*)'$")
  public void searchServiceFindSearchTermWhichDoesntHaveIMRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request for validating the invalid category$")
  public void searchServicePrepareRequestForValidatingTheInvalidCategory() {
    searchserviceData.setWrongcategoryId(searchserviceProperties.get("wrongcategoryId"));
  }

  @When("^\\[search-service] send request to validate invalidcategory$")
  public void searchServiceSendRequestToValidateInvalidcategory() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.validateInValidCategoryID();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check response for trying to validate invalid category$")
  public void searchServiceCheckResponseForTryingToValidateInvalidCategory() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("false"));
  }
}
