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
import com.gdn.x.search.rest.web.model.MerchantSortResponseDto;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@CucumberStepsDefinition
public class MerchantSortSteps {
  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private MongoHelper mongoHelper;

  @Given("^\\[search-service] prepare request to add new merchant sort mapping$")
  public void prepareRequestToAddNewMerchantSortMapping() {
    searchServiceData.setMerchantId(searchServiceProperties.get("merchantId"));
    searchServiceData.setMerchantName(searchServiceProperties.get("merchantName"));
    searchServiceData.setSortType(searchServiceProperties.get("merchantSortType"));
  }

  @When("^\\[search-service] send add new merchant sort mapping request$")
  public void sendAddNewMerchantSortMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfAddMerchantSort();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] new merchant sort mapping must be added$")
  public void newMerchantSortMappingMustBeAdded() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    Document searchDoc = new Document("merchantId", "TH7-15791");
    long docCount = mongoHelper.countByMongoquery("merchant_sort", searchDoc);
    assertThat("Added document not found in database", docCount, equalTo(1L));
  }

  @Given("^\\[search-service] prepare request to add new merchant sort mapping with incorrect merchant info$")
  public void prepareRequestToAddNewMerchantSortMappingWithIncorrectMerchantInfo() {
    searchServiceData.setMerchantId(searchServiceProperties.get("incorrectMerchantId"));
    searchServiceData.setMerchantName(searchServiceProperties.get("merchantName"));
    searchServiceData.setSortType(searchServiceProperties.get("merchantSortType"));
  }

  @When("^\\[search-service] send add new merchant sort mapping with incorrect merchant info request$")
  public void sendAddNewMerchantSortMappingWithIncorrectMerchantInfoRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfAddMerchantSort();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for adding merchant sort mapping with incorrect merchant info$")
  public void checkTheResponseForAddingMerchantSortMappingWithIncorrectMerchantInfo() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(),
        equalTo("INVALID MERCHANT ID IN THE REQUEST"));
  }

  @Given("^\\[search-service] prepare search merchant sort mapping request$")
  public void prepareSearchMerchantSortMappingRequest() {
    searchServiceData.setSearchKeyword(searchServiceProperties.get("merchantName"));
  }

  @When("^\\[search-service] send search merchant sort mapping request$")
  public void sendSearchMerchantSortMappingRequest() {
    ResponseApi<GdnRestListResponse<MerchantSortResponseDto>> response =
        searchServiceController.findMerchantSortMappingResponse();
    searchServiceData.setMerchantSortList(response);
  }

  @Then("^\\[search-service] search merchant sort mapping request response should contain requested document$")
  public void searchMerchantSortMappingRequestResponseShouldContainRequestedDocument() {
    ResponseApi<GdnRestListResponse<MerchantSortResponseDto>> response =
        searchServiceData.getMerchantSortList();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    assertThat("MerchantId Mismatch",
        response.getResponseBody().getContent().get(0).getMerchantId(),
        equalTo(searchServiceProperties.get("merchantId")));
    assertThat("MerchantName Mismatch",
        response.getResponseBody().getContent().get(0).getMerchantName(),
        equalTo(searchServiceProperties.get("merchantName")));
    assertThat("SortType Mismatch",
        response.getResponseBody().getContent().get(0).getSortType(),
        equalTo(Integer.parseInt(searchServiceProperties.get("merchantSortType"))));
  }

  @Given("^\\[search-service] prepare search merchant sort mapping by merchant id request$")
  public void prepareSearchMerchantSortMappingByMerchantIdRequest() {
    searchServiceData.setMerchantId(searchServiceProperties.get("merchantId"));
  }

  @When("^\\[search-service] send search merchant sort mapping by merchant id request$")
  public void sendSearchMerchantSortMappingByMerchantIdRequest() {
    ResponseApi<GdnRestSingleResponse<MerchantSortResponseDto>> response =
        searchServiceController.findMerchantSortMappingByIdResponse();
    searchServiceData.setMerchantSortFindByMerchantId(response);
  }

  @Then("^\\[search-service] search merchant sort mapping by merchant id request response should contain requested document$")
  public void searchMerchantSortMappingByMerchantIdRequestResponseShouldContainRequestedDocument() {
    ResponseApi<GdnRestSingleResponse<MerchantSortResponseDto>> response =
        searchServiceData.getMerchantSortFindByMerchantId();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    assertThat("MerchantId Mismatch",
        response.getResponseBody().getValue().getMerchantId(),
        equalTo(searchServiceProperties.get("merchantId")));
    assertThat("MerchantName Mismatch",
        response.getResponseBody().getValue().getMerchantName(),
        equalTo(searchServiceProperties.get("merchantName")));
    assertThat("SortType Mismatch",
        response.getResponseBody().getValue().getSortType(),
        equalTo(Integer.parseInt(searchServiceProperties.get("merchantSortType"))));
  }

  @Given("^\\[search-service] prepare search merchant sort mapping by incorrect merchant id request$")
  public void prepareSearchMerchantSortMappingByIncorrectMerchantIdRequest() {
    searchServiceData.setMerchantId(searchServiceProperties.get("incorrectMerchantId"));
  }

  @When("^\\[search-service] send search merchant sort mapping by incorrect merchant id request$")
  public void sendSearchMerchantSortMappingByIncorrectMerchantIdRequest() {
    ResponseApi<GdnRestSingleResponse<MerchantSortResponseDto>> response =
        searchServiceController.findMerchantSortMappingByIdResponse();
    searchServiceData.setMerchantSortFindByMerchantId(response);
  }

  @Then("^\\[search-service] check the response for searching merchant sort mapping by incorrect merchant id$")
  public void checkTheResponseForSearchingMerchantSortMappingByIncorrectMerchantId() {
    ResponseApi<GdnRestSingleResponse<MerchantSortResponseDto>> response =
        searchServiceData.getMerchantSortFindByMerchantId();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("INTERNAL ERROR"));
  }

  @Given("^\\[search-service] prepare list all merchant sort mapping request$")
  public void prepareListAllMerchantSortMappingRequest() {
    searchServiceData.setSize(searchServiceProperties.get("listSize"));
  }

  @When("^\\[search-service] send list all merchant sort mapping request$")
  public void sendListAllMerchantSortMappingRequest() {
    ResponseApi<GdnRestListResponse<MerchantSortResponseDto>> response =
        searchServiceController.merchantSortListResponse();
    searchServiceData.setMerchantSortList(response);
  }

  @Then("^\\[search-service] list all merchant sort mapping request response should have all documents$")
  public void listAllMerchantSortMappingRequestResponseShouldHaveAllDocuments() {
    ResponseApi<GdnRestListResponse<MerchantSortResponseDto>> response =
        searchServiceData.getMerchantSortList();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    MongoCollection<Document> collection = mongoHelper.initializeDatabase("merchant_sort");
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("_class", "com.gdn.x.search.entity.MerchantSortEntity");
    long totalCount = collection.count(whereQuery);
    assertThat("Response didn't match with database",
        response.getResponseBody().getPageMetaData().getTotalRecords(),
        equalTo(totalCount));
  }

  @Given("^\\[search-service] prepare update merchant sort mapping request$")
  public void prepareUpdateMerchantSortMappingRequest() {
    searchServiceData.setMerchantId(searchServiceProperties.get("merchantId"));
    searchServiceData.setMerchantName(searchServiceProperties.get("merchantName"));
    searchServiceData.setSortType(searchServiceProperties.get("updateMerchantSortType"));
  }

  @When("^\\[search-service] send update merchant sort mapping request$")
  public void sendUpdateMerchantSortMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestOfUpdateMerchantSort();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] merchant sort mapping must get updated$")
  public void merchantSortMappingMustGetUpdated() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    Document searchDoc = new Document("merchantId", "TH7-15791").append("sortType",
        Integer.parseInt(searchServiceProperties.get("updateMerchantSortType")));
    long docCount = mongoHelper.countByMongoquery("merchant_sort", searchDoc);
    assertThat("Updated document not found", docCount, equalTo(1L));
  }

  @Given("^\\[search-service] prepare fetch list of all the merchants request$")
  public void searchServicePrepareFetchListOfAllTheMerchantsRequest() {
  }

  @When("^\\[search-service] send fetch list of all the merchants request$")
  public void searchServiceSendFetchListOfAllTheMerchantsRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.listAllMerchants();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] fetch list of all the merchants request response should contain all merchant list$")
  public void fetchListOfAllTheMerchantsRequestResponseShouldContainAllMerchantList() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    String Response = response.getResponseBody().getErrorMessage();
    assertThat("Response not having known merchant",
        Response.contains("Thunder167"),
        equalTo(true));
  }

  @Given("^\\[search-service] prepare delete merchant sort mapping request$")
  public void prepareDeleteMerchantSortMappingRequest() {
    searchServiceData.setMerchantId(searchServiceProperties.get("merchantId"));
  }

  @When("^\\[search-service] send delete merchant sort mapping request$")
  public void sendDeleteMerchantSortMappingRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteMerchantSortMapping();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] requested mapping must get deleted$")
  public void requestedMappingMustGetDeleted() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    Document searchDoc = new Document("merchantid", "TH7-15791");
    long docCount = mongoHelper.countByMongoquery("merchant_sort", searchDoc);
    assertThat("Document not deleted", docCount, equalTo(0L));
  }

  @Given("^\\[search-service] prepare delete merchant sort mapping with incorrect id request$")
  public void prepareDeleteMerchantSortMappingWithIncorrectIdRequest() {
    searchServiceData.setMerchantId(searchServiceProperties.get("incorrectMerchantId"));
  }

  @When("^\\[search-service] send delete merchant sort mapping with incorrect id request$")
  public void sendDeleteMerchantSortMappingWithIncorrectIdRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteMerchantSortMapping();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for delete merchant sort mapping with incorrect id$")
  public void checkTheResponseForDeleteMerchantSortMappingWithIncorrectId() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Merchant sort mapping found",
        response.getResponseBody().getErrorMessage(),
        equalTo("GIVEN MERCHANT SORT MAPPING NOT FOUND"));
  }
}