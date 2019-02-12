package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.x.search.rest.web.model.FlightResponse;
import com.gdn.x.search.rest.web.model.PlaceholderRuleResponse;
import com.gdn.x.search.rest.web.model.SearchRuleResponse;
import com.gdn.x.search.rest.web.model.TrainResponse;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberStepsDefinition
public class ContextualSearchSteps {


  public long valid;
  public long count= 0L;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] produce request to get all flights$")
  public void searchServiceProduceRequestToGetAllFlights() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    valid = mongoHelper.countOfRecordsInCollection("flight_dictionary");
  }

  @When("^\\[search-service] send get all flights request$")
  public void searchServiceSendGetAllFlightsRequest() {
    ResponseApi<GdnRestListResponse<FlightResponse>> response =
        searchServiceController.allFlights();
    searchServiceData.setAllFlights(response);
  }

  @Then("^\\[search-service] get all flights request response success should be '(.*)'$")
  public void searchServiceGetAllFlightsRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<FlightResponse>> response = searchServiceData.getAllFlights();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    for (int i = 0; i < response.getResponseBody().getContent().size(); i++) {
      count++;
      searchServiceData.setAutoFlightId(response.getResponseBody().getContent().get(i).getId());
    }
    assertThat(count, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to add a flight$")
  public void searchServicePrepareRequestToAddAFlight() {
    searchServiceData.setTrainSearchTerm(searchServiceProperties.get("trainSearchTerm"));
    searchServiceData.setTrainMapping(searchServiceProperties.get("trainMapping"));
    valid = mongoHelper.countOfRecordsInCollection("flight_dictionary");
  }

  @When("^\\[search-service] send add flight mapping request$")
  public void searchServiceSendAddFlightMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveFlight( "testingapi", "2");
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add flight request response success should be '(.*)'$")
  public void searchServiceAddFlightRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("flight_dictionary");
    assertThat(valid1, greaterThan(valid));
  }

  @Given("^\\[search-service] prepare request delete flight mapping$")
  public void searchServicePrepareRequestDeleteFlightMapping() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.getAutoFlightId();
    valid = mongoHelper.countOfRecordsInCollection("flight_dictionary");
  }

  @When("^\\[search-service] send delete flight mapping request$")
  public void searchServiceSendDeleteFlightMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteFlightMapping();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete flight mapping request response success should be '(.*)'$")
  public void searchServiceDeleteFlightMappingRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("flight_dictionary");
    assertThat(valid, greaterThan(valid1));
  }

  @Given("^\\[search-service] prepare request to add placeholder rules$")
  public void searchServicePrepareRequestToAddPlaceholderRules() {
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setType(searchServiceProperties.get("type"));
    valid = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
  }

  @When("^\\[search-service] send add placeholder rules request$")
  public void searchServiceSendAddPlaceholderRulesRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.addPlaceholderRules(
        "testapi",
        "testingapi",
        "cheap",
        "MASTER_PRODUCT");
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add placeholder rules request response success should be '(.*)'$")
  public void searchServiceAddPlaceholderRulesRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
    assertThat(valid1, greaterThan(valid));
  }

  @Given("^\\[search-service] produce request to get all placeholder$")
  public void searchServiceProduceRequestToGetAllPlaceholder() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    valid = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
  }

  @When("^\\[search-service] send get all placeholder request$")
  public void searchServiceSendGetAllPlaceholderRequest() {
    ResponseApi<GdnRestListResponse<PlaceholderRuleResponse>> response =
        searchServiceController.getAllPlaceholder();
    searchServiceData.setGetAllPlaceholder(response);
  }

  @Then("^\\[search-service] get all placeholder request response success should be '(.*)'$")
  public void searchServiceGetAllPlaceholderRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<PlaceholderRuleResponse>> response =
        searchServiceData.getGetAllPlaceholder();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    for (int i = 0; i < response.getResponseBody().getContent().size(); i++) {
      count++;
    searchServiceData.setAutoPlaceholderId(response.getResponseBody()
          .getContent()
          .get(i)
          .getId());
    }
    assertThat(count, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to delete placeholder$")
  public void searchServicePrepareRequestToDeletePlaceholder() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.getAutoPlaceholderId();
    valid = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
  }

  @When("^\\[search-service] send delete placeholder request$")
  public void searchServiceSendDeletePlaceholderRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deletePlaceholder();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete placeholder request response success should be '(.*)'$")
  public void searchServiceDeletePlaceholderRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
    assertThat(valid, greaterThan(valid1));
  }

  @Given("^\\[search-service] prepare request to update placeholder rule$")
  public void searchServicePrepareRequestToUpdatePlaceholderRule() {
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send update placeholder rule request$")
  public void searchServiceSendUpdatePlaceholderRuleRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.updatePlaceholder();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update placeholder rule request response success should be '(.*)'$")
  public void searchServiceUpdatePlaceholderRuleRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    FindIterable<Document> mongoIterator =
        mongoHelper.getMongoDocumentByQuery("placeholder_im_rule", "name", "test.api");
    for (Document doc : mongoIterator) {
      String docInStringFormat = doc.toString();
      assertThat(docInStringFormat, containsString("name=test.api"));
    }
  }

  @Given("^\\[search-service] prepare request to delete flight with wrong id$")
  public void searchServicePrepareRequestToDeleteFlightWithWrongId() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    valid = mongoHelper.countOfRecordsInCollection("flight_dictionary");
  }

  @When("^\\[search-service] send delete flight with wrong id request$")
  public void searchServiceSendDeleteFlightWithWrongIdRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteFlightWithWrongId();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete flight with wrong id request response should be true$")
  public void searchServiceDeleteFlightWithWrongIdRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("flight_dictionary");
    assertThat(valid1, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to add flight without giving mandatory fields$")
  public void searchServicePrepareRequestToAddFlightWithoutGivingMandatoryFields() {
    valid = mongoHelper.countOfRecordsInCollection("flight_dictionary");
  }

  @When("^\\[search-service] send add flight request without mandatory$")
  public void searchServiceSendAddFlightRequestWithoutMandatory() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveFlight( null, null);
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add flight without mandatory request response should be true$")
  public void searchServiceAddFlightWithoutMandatoryRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("flight_dictionary");
    assertThat(valid1, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to add placeholder without giving mandatory fields$")
  public void searchServicePrepareRequestToAddPlaceholderWithoutGivingMandatoryFields() {
    valid = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
  }

  @When("^\\[search-service] send add placeholder request without mandatory$")
  public void searchServiceSendAddPlaceholderRequestWithoutMandatory() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.addPlaceholderRules(null, null, null, null);
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add placeholder without mandatory request response should be true$")
  public void searchServiceAddPlaceholderWithoutMandatoryRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
    assertThat(valid1, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to update placeholder with non existing id$")
  public void searchServicePrepareRequestToUpdatePlaceholderWithNonExistingId() {
    searchServiceData.setId(searchServiceProperties.get("wrongid"));
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setSearchTerm(searchServiceProperties.get("searchTerm"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setType(searchServiceProperties.get("type"));
  }

  @When("^\\[search-service] send update placeholder with non existing id request$")
  public void searchServiceSendUpdatePlaceholderWithNonExistingIdRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.updatePlaceholderWithNonExistingId();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update placeholder with non existing id request response success should be true$")
  public void searchServiceUpdatePlaceholderWithNonExistingIdRequestResponseSuccessShouldBeTrue() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat(result, equalTo(false));
  }

  @Given("^\\[search-service] prepare request to delete placeholder with wrong id$")
  public void searchServicePrepareRequestToDeletePlaceholderWithWrongId() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    valid = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
  }

  @When("^\\[search-service] send delete placeholder with wrong id request$")
  public void searchServiceSendDeletePlaceholderWithWrongIdRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deletePlaceholderWithWrongId();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete placeholder with wrong id request response should be true$")
  public void searchServiceDeletePlaceholderWithWrongIdRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("placeholder_im_rule");
    assertThat(valid, equalTo(valid1));
  }

  @Given("^\\[search-service] prepare request to add search rule$")
  public void searchServicePrepareRequestToAddSearchRule() {
    searchServiceData.setSearchRulSearchTerm(searchServiceProperties.get("searchRulSearchTerm"));
    searchServiceData.setFilterQuery(searchServiceProperties.get("filterQuery"));
    searchServiceData.setSortType(searchServiceProperties.get("sortType"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setUrl(searchServiceProperties.get("url"));
    searchServiceData.setType(searchServiceProperties.get("type"));
    searchServiceData.setSpel(searchServiceProperties.get("spel"));
    valid = mongoHelper.countOfRecordsInCollection("search_rule");
  }

  @When("^\\[search-service] send add search rule request$")
  public void searchServiceSendAddSearchRuleRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.addSearchRule();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add search rule request response should be '(.*)'$")
  public void searchServiceAddSearchRuleRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("search_rule");
    assertThat(valid1, greaterThan(valid));
  }

  @Given("^\\[search-service] prepare request to get all search rule$")
  public void searchServicePrepareRequestToGetAllSearchRule() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    valid = mongoHelper.countOfRecordsInCollection("search_rule");
  }

  @When("^\\[search-service] send get all search rule request$")
  public void searchServiceSendGetAllSearchRuleRequest() {
    ResponseApi<GdnRestListResponse<SearchRuleResponse>> response =
        searchServiceController.getAllSearch();
    searchServiceData.setGetAllSearchRule(response);
  }

  @Then("^\\[search-service] get all search rule request response should be '(.*)'$")
  public void searchServiceGetAllSearchRuleRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<SearchRuleResponse>> response =
        searchServiceData.getGetAllSearchRule();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long count = 0L;
    for (int i = 0; i < response.getResponseBody().getContent().size(); i++) {
      count++;
      searchServiceData.setAutoSearchId(response.getResponseBody().getContent().get(i).getId());
    }
    assertThat(count, equalTo(valid));
  }

  @Given("^\\[search-service] prepare delete search rule request$")
  public void searchServicePrepareDeleteSearchRuleRequest() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.getAutoSearchId();
    valid = mongoHelper.countOfRecordsInCollection("search_rule");
  }

  @When("^\\[search-service] send delete search rule request$")
  public void searchServiceSendDeleteSearchRuleRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteSearchRule();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete search rule request response success should be '(.*)'$")
  public void searchServiceDeleteSearchRuleRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("search_rule");
    assertThat(valid1, lessThan(valid));
  }

  @Given("^\\[search-service] prepare request to rerank search rule$")
  public void searchServicePrepareRequestToRerankSearchRule() {
    searchServiceData.setSearchRulSearchTerm(searchServiceProperties.get("searchRulSearchTerm"));
    searchServiceData.setFilterQuery(searchServiceProperties.get("filterQuery"));
    searchServiceData.setSortType(searchServiceProperties.get("sortType"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setUrl(searchServiceProperties.get("url"));
    searchServiceData.setType(searchServiceProperties.get("type"));
    searchServiceData.setSpel(searchServiceProperties.get("spel"));
  }

  @When("^\\[search-service] send rerank search rule request$")
  public void searchServiceSendRerankSearchRuleRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.rerankSearchRule();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] rerank search rule request response should be '(.*)'$")
  public void searchServiceRerankSearchRuleRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare request to update search rule$")
  public void searchServicePrepareRequestToUpdateSearchRule() {
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setFilterQuery(searchServiceProperties.get("filterQuery"));
    searchServiceData.setSortType(searchServiceProperties.get("sortType"));
    searchServiceData.setEffectiveSearchPattern(searchServiceProperties.get("effectiveSearchPattern"));
    searchServiceData.setUrl(searchServiceProperties.get("url"));
    searchServiceData.setType(searchServiceProperties.get("type"));
    searchServiceData.setSpel(searchServiceProperties.get("spel"));
  }

  @When("^\\[search-service] send update search rule request$")
  public void searchServiceSendUpdateSearchRuleRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.updateSearchRule();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] update search rule request response should be '(.*)'$")
  public void searchServiceUpdateSearchRuleRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    FindIterable<Document> mongoIterator =
        mongoHelper.getMongoDocumentByQuery("search_rule", "search_pattern", "test.api");
    for (Document doc : mongoIterator) {
      String docInStringFormat = doc.toString();
      assertThat(docInStringFormat, containsString("search_pattern=test.api"));
    }

  }

  @Given("^\\[search-service] prepare add train mapping request$")
  public void searchServicePrepareAddTrainMappingRequest() {
    searchServiceData.setTrainSearchTerm(searchServiceProperties.get("trainSearchTerm"));
    searchServiceData.setTrainMapping(searchServiceProperties.get("trainMapping"));
    valid = mongoHelper.countOfRecordsInCollection("train_dictionary");
  }

  @When("^\\[search-service] send add train mapping request$")
  public void searchServiceSendAddTrainMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.addTrainMapping("testingapi", "2");
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] add train mapping request response should be '(.*)'$")
  public void searchServiceAddTrainMappingRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("train_dictionary");
    assertThat(valid1, greaterThan(valid));
  }

  @Given("^\\[search-service] prepare get all train mapping request$")
  public void searchServicePrepareGetAllTrainMappingRequest() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    valid = mongoHelper.countOfRecordsInCollection("train_dictionary");
  }

  @When("^\\[search-service] send get all train mapping request$")
  public void searchServiceSendGetAllTrainMappingRequest() {
    ResponseApi<GdnRestListResponse<TrainResponse>> response =
        searchServiceController.getAllTrain();
    searchServiceData.setGetAllTrain(response);
  }

  @Then("^\\[search-service] get all train mapping request response should be '(.*)'$")
  public void searchServiceGetAllTrainMappingRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<TrainResponse>> response = searchServiceData.getGetAllTrain();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long count = 0L;
    for (int i = 0; i < response.getResponseBody().getContent().size(); i++) {
      count++;
      searchServiceData.setAutoTrainId(response.getResponseBody().getContent().get(i).getId());
    }
    assertThat(count, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to delete train mapping$")
  public void searchServicePrepareRequestToDeleteTrainMapping() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.getAutoTrainId();
    valid = mongoHelper.countOfRecordsInCollection("train_dictionary");
  }

  @When("^\\[search-service] send delete train mapping request$")
  public void searchServiceSendDeleteTrainMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteTrainMapping();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete train mapping request response should be '(.*)'$")
  public void searchServiceDeleteTrainMappingRequestResponseShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    long valid1 = mongoHelper.countOfRecordsInCollection("train_dictionary");
    assertThat(valid, greaterThan(valid1));
  }

  @Given("^\\[search-service] prepare request to delete search rule with wrong id$")
  public void searchServicePrepareRequestToDeleteSearchRuleWithWrongId() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    valid = mongoHelper.countOfRecordsInCollection("search_rule");
  }

  @When("^\\[search-service] send delete search rule with wrong id request$")
  public void searchServiceSendDeleteSearchRuleWithWrongIdRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteSearchRuleWithWrongId();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete search rule with wrong id request response should be true$")
  public void searchServiceDeleteSearchRuleWithWrongIdRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("search_rule");
    assertThat(valid1, equalTo(valid));
  }

  @Given("^\\[search-service] prepare request to delete train mapping with wrong id$")
  public void searchServicePrepareRequestToDeleteTrainMappingWithWrongId() {
    searchServiceData.setAuthenticator(searchServiceProperties.get("authenticator"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    valid = mongoHelper.countOfRecordsInCollection("train_dictionary");
  }

  @When("^\\[search-service] send delete train mapping with wrong id request$")
  public void searchServiceSendDeleteTrainMappingWithWrongIdRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.deleteTrainMappingWithWrongId();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] delete train mapping with wrong id request response should be true$")
  public void searchServiceDeleteTrainMappingWithWrongIdRequestResponseShouldBeTrue() {
    long valid1 = mongoHelper.countOfRecordsInCollection("train_dictionary");
    assertThat(valid1, equalTo(valid));
  }
}



