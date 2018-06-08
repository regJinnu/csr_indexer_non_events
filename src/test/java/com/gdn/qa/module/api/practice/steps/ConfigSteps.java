package com.gdn.qa.module.api.practice.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
import com.gdn.qa.module.api.practice.api.services.SearchServiceController;
import com.gdn.qa.module.api.practice.data.SearchServiceData;
import com.gdn.qa.module.api.practice.properties.SearchServiceProperties;
import com.gdn.x.search.rest.web.model.ConfigResponse;
import com.mongodb.BasicDBObject;
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
public class ConfigSteps {
  @Autowired
  SearchServiceController searchServiceController;

  @Autowired
  SearchServiceProperties searchserviceProperties;

  @Autowired
  SearchServiceData searchserviceData;

  @Given("^\\[search-service] prepare delete existing config request using properties using properties data$")
  public void searchServicePrepareDeleteExistingConfigRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setUsername(searchserviceProperties.get("username"));
    searchserviceData.setAutoid(searchserviceData.getAutoid());
    searchserviceData.setName(searchserviceProperties.get("name"));
  }

  @When("^\\[search-service] send delete config request$")
  public void searchServiceSendDeleteConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyofDeleteConfigRequest();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set delete config request response success should be '(.*)'$")
  public void searchServiceSetDeleteConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare save config request using properties using properties data$")
  public void searchServicePrepareSaveConfigRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setValue(searchserviceProperties.get("value"));
    searchserviceData.setName(searchserviceProperties.get("name"));
    searchserviceData.setLabel(searchserviceProperties.get("label"));
  }

  @When("^\\[search-service] send save config request$")
  public void searchServiceSendSaveConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyOfRequest();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set save config request response success should be '(.*)'$")
  public void searchServiceSetSaveConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare update existing config request using properties using properties data$")
  public void searchServicePrepareUpdateExistingConfigRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setAutoid(searchserviceData.getAutoid());
    searchserviceData.setUpdatedValue(searchserviceProperties.get("updatedValue"));
    searchserviceData.setName(searchserviceProperties.get("name"));
    searchserviceData.setLabel(searchserviceProperties.get("label"));
  }

  @When("^\\[search-service] send update config request$")
  public void searchServiceSendUpdateConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfRequestToUpdateTheConfig();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set update config request response success should be '(.*)'$")
  public void searchServiceSetUpdateConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));

  }

  @Given("^\\[search-service] prepare find config by name request using properties using properties data$")
  public void searchServicePrepareFindConfigByNameRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setName(searchserviceProperties.get("name"));
  }

  @When("^\\[search-service] send find config by name request$")
  public void searchServiceSendFindConfigByNameRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.FindByNameResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config name by request response success should be '(.*)'$")
  public void searchServiceFindConfigNameByRequestResponseSuccessShouldBeTrue(boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));

    String autoId = response.getResponseBody().getValue().getId();
    searchserviceData.setAutoid(autoId);

    assertThat(response.getResponseBody().getValue().getName(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getValue(), equalTo("1"));
  }

  @Given("^\\[search-service] prepare find config by id request using properties using properties data$")
  public void searchServicePrepareFindConfigByIdRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setAutoid(searchserviceData.getAutoid());
  }

  @When("^\\[search-service] send find config by id request$")
  public void searchServiceSendFindConfigByIdRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.SetFindByIDResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config id by request response success should be '(.*)'$")
  public void searchServiceFindConfigIdByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getName(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getValue(), equalTo("1"));

  }

  @Given("^\\[search-service] prepare find config by word request using properties using properties data$")
  public void searchServicePrepareFindConfigByWordRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setWord(searchserviceProperties.get("word"));
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
  }

  @When("^\\[search-service] send find config by word request$")
  public void searchServiceSendFindConfigByWordRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.FindByWordResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config word by request response success should be '(.*)'$")
  public void searchServiceFindConfigWordByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    System.out.println("__________________________SuccessCode_____________________" + result);
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    System.out.println("-----------------Value--------------" + response.getResponseBody());


  }

  @Given("^\\[search-service] prepare get config list request using properties using properties data$")
  public void searchServicePrepareGetConfigListRequestUsingPropertiesUsingPropertiesData() {
    searchserviceData.setPage(searchserviceProperties.get("page"));
    searchserviceData.setSize(searchserviceProperties.get("size"));
  }

  @When("^\\[search-service] send get config list request$")
  public void searchServiceSendGetConfigListRequest() {
    ResponseApi<GdnRestListResponse<ConfigResponse>> response =
        searchServiceController.ConfigListResponse();
    searchserviceData.setFindByListRequest(response);


  }

  @Then("^\\[search-service] get config list by request response success should be '(.*)'$")
  public void searchServiceGetConfigListByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<ConfigResponse>> response =
        searchserviceData.getFindByListRequest();
    System.out.println(
        "----------------------------Response----------------------------" + response);
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri =
        new MongoClientURI("mongodb://search:search@mongodb-01.uata.lokal:27017/x_search");
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase("x_search");
    MongoCollection<Document> collection = db.getCollection("config_list");
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("STORE_ID", "10001");
    long totalCount = collection.count(whereQuery);
    System.out.println("----------------------------Total documents------------" + totalCount);
    assertThat(response.getResponseBody().getPageMetaData().getTotalRecords(), equalTo(totalCount));
    System.out.println("__________________________SuccessCode_____________________" + result);
  }


  @Given("^\\[search-service] prepare delete existing config request by providing wrong id$")
  public void searchServicePrepareDeleteExistingConfigRequestByProvidingWrongId() {
    searchserviceData.setUsername(searchserviceProperties.get("username"));
    searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
    searchserviceData.setName(searchserviceProperties.get("name"));
  }

  @When("^\\[search-service] send delete config request with wrong id$")
  public void searchServiceSendDeleteConfigRequestWithWrongId() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.BodyofRequestWithWrongID();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for deleting config with wrong id$")
  public void searchServiceCheckTheResponseForDeletingConfigWithWrongId() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :for Config with id : 12345"));
  }

  @Given("^\\[search-service] prepare save config request with empty body$")
  public void searchServicePrepareSaveConfigRequestWithEmptyBody() {
    searchserviceData.setEmptyid(searchserviceProperties.get("emptyid"));
    searchserviceData.setEmptyname(searchserviceProperties.get("emptyname"));
  }

  @When("^\\[search-service] send save config request with empty body$")
  public void searchServiceSendSaveConfigRequestWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfRequestwithEmptyBody();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check response for saving config with empty body$")
  public void searchServiceCheckResponseForSavingConfigWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("UNSPECIFIED"));
  }

  @Given("^\\[search-service] prepare find config by name which is not present in the config list$")
  public void searchServicePrepareFindConfigByNameWhichIsNotPresentInTheConfigList() {
    searchserviceData.setWrongname(searchserviceProperties.get("wrongname"));
  }

  @When("^\\[search-service] send find config by name which is not present in the config list$")
  public void searchServiceSendFindConfigByNameWhichIsNotPresentInTheConfigList() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.FindByWrongNameResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with name which is not present$")
  public void searchServiceCheckTheResponseForFindingConfigWithNameWhichIsNotPresent() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("Can not find data :"));
    //assertThat(response.getResponseBody().getErrorCode(),equalTo("Can not find data :"));
  }

  @Given("^\\[search-service] prepare find config by id request by providing wrong ID$")
  public void searchServicePrepareFindConfigByIdRequestByProvidingWrongID() {
    searchserviceData.setWrongid(searchserviceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find config by id request with wrong id$")
  public void searchServiceSendFindConfigByIdRequestWithWrongId() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.SetFindByWrongIDResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with wrong id$")
  public void searchServiceCheckTheResponseForFindingConfigWithWrongId() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    assertThat(response.getResponseBody().getValue().getValue(), equalTo(null));
    assertThat(response.getResponseBody().getValue().getName(), equalTo(null));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo(null));
  }

  @Given("^\\[search-service] prepare find config by word request which is not present in the list$")
  public void searchServicePrepareFindConfigByWordRequestWhichIsNotPresentInTheList() {
    searchserviceData.setWrongword(searchserviceProperties.get("wrongword"));
  }

  @When("^\\[search-service] send find request config by non existing word in the config list$")
  public void searchServiceSendFindRequestConfigByNonExistingWordInTheConfigList() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.FindByWrongWordResponse();
    searchserviceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with wrong word$")
  public void searchServiceCheckTheResponseForFindingConfigWithWrongWord() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchserviceData.getFindByRequest();
    assertThat(response.getResponseBody().getValue(), equalTo(null));

  }

  @Given("^\\[search-service] prepare update existing config request with empty body$")
  public void searchServicePrepareUpdateExistingConfigRequestWithEmptyBody() {
    searchserviceData.setEmptyid(searchserviceProperties.get("emptyid"));
    searchserviceData.setEmptyname(searchserviceProperties.get("emptyname"));
  }

  @When("^\\[search-service] send update config request with empty body$")
  public void searchServiceSendUpdateConfigRequestWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.BodyOfRequestToUpdateTheConfigWithEmptyBody();
    searchserviceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check out the response of update config with empty body$")
  public void searchServiceCheckOutTheResponseOfUpdateConfigWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response = searchserviceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :at config but try to update it. id :"));
  }


}
