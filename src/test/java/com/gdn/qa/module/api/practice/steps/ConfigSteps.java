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
 private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] prepare delete existing config request using properties using properties data$")
  public void searchServicePrepareDeleteExistingConfigRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setUsername(searchServiceProperties.get("username"));
    searchServiceData.setAutoid(searchServiceData.getAutoid());
    searchServiceData.setName(searchServiceProperties.get("name"));
  }

  @When("^\\[search-service] send delete config request$")
  public void searchServiceSendDeleteConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyofDeleteConfigRequest();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set delete config request response success should be '(.*)'$")
  public void searchServiceSetDeleteConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare save config request using properties using properties data$")
  public void searchServicePrepareSaveConfigRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setValue(searchServiceProperties.get("value"));
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setLabel(searchServiceProperties.get("label"));
  }

  @When("^\\[search-service] send save config request$")
  public void searchServiceSendSaveConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyOfRequestOfSaveConfig();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set save config request response success should be '(.*)'$")
  public void searchServiceSetSaveConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
  }

  @Given("^\\[search-service] prepare update existing config request using properties using properties data$")
  public void searchServicePrepareUpdateExistingConfigRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoid(searchServiceData.getAutoid());
    searchServiceData.setUpdatedValue(searchServiceProperties.get("updatedValue"));
    searchServiceData.setName(searchServiceProperties.get("name"));
    searchServiceData.setLabel(searchServiceProperties.get("label"));
  }

  @When("^\\[search-service] send update config request$")
  public void searchServiceSendUpdateConfigRequest() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestToUpdateTheConfig();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] set update config request response success should be '(.*)'$")
  public void searchServiceSetUpdateConfigRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));

  }

  @Given("^\\[search-service] prepare find config by name request using properties using properties data$")
  public void searchServicePrepareFindConfigByNameRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setName(searchServiceProperties.get("name"));
  }

  @When("^\\[search-service] send find config by name request$")
  public void searchServiceSendFindConfigByNameRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.findByNameResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config name by request response success should be '(.*)'$")
  public void searchServiceFindConfigNameByRequestResponseSuccessShouldBeTrue(boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));

    String autoId = response.getResponseBody().getValue().getId();
    searchServiceData.setAutoid(autoId);

    assertThat(response.getResponseBody().getValue().getName(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getValue(), equalTo("1"));
  }

  @Given("^\\[search-service] prepare find config by id request using properties using properties data$")
  public void searchServicePrepareFindConfigByIdRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setAutoid(searchServiceData.getAutoid());
  }

  @When("^\\[search-service] send find config by id request$")
  public void searchServiceSendFindConfigByIdRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.setFindByIDResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config id by request response success should be '(.*)'$")
  public void searchServiceFindConfigIdByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    assertThat(response.getResponseBody().getValue().getName(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo("test.api"));
    assertThat(response.getResponseBody().getValue().getValue(), equalTo("1"));

  }

  @Given("^\\[search-service] prepare find config by word request using properties using properties data$")
  public void searchServicePrepareFindConfigByWordRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setWord(searchServiceProperties.get("word"));
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
  }

  @When("^\\[search-service] send find config by word request$")
  public void searchServiceSendFindConfigByWordRequest() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.findByWordResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] find config word by request response success should be '(.*)'$")
  public void searchServiceFindConfigWordByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));


  }

  @Given("^\\[search-service] prepare get config list request using properties using properties data$")
  public void searchServicePrepareGetConfigListRequestUsingPropertiesUsingPropertiesData() {
    searchServiceData.setPage(searchServiceProperties.get("page"));
    searchServiceData.setSize(searchServiceProperties.get("size"));
    searchServiceData.setMongoURL(searchServiceProperties.get("mongoURL"));
    searchServiceData.setMongoDB(searchServiceProperties.get("mongoDB"));
  }

  @When("^\\[search-service] send get config list request$")
  public void searchServiceSendGetConfigListRequest() {
    ResponseApi<GdnRestListResponse<ConfigResponse>> response =
        searchServiceController.configListResponse();
    searchServiceData.setFindByListRequest(response);


  }

  @Then("^\\[search-service] get config list by request response success should be '(.*)'$")
  public void searchServiceGetConfigListByRequestResponseSuccessShouldBeTrue(Boolean isSuccess) {
    ResponseApi<GdnRestListResponse<ConfigResponse>> response =
        searchServiceData.getFindByListRequest();
    boolean result = response.getResponseBody().isSuccess();
    assertThat("is Success is wrong", result, equalTo(isSuccess));
    MongoClientURI uri =
        new MongoClientURI(searchServiceData.getMongoURL());
    MongoClient mongoClient = new MongoClient(uri);
    MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
    optionsBuilder.connectTimeout(30000);
    MongoDatabase db = mongoClient.getDatabase(searchServiceData.getMongoDB());
    MongoCollection<Document> collection = db.getCollection("config_list");
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("STORE_ID", "10001");
    long totalCount = collection.count(whereQuery);
    assertThat(response.getResponseBody().getPageMetaData().getTotalRecords(), equalTo(totalCount));
  }


  @Given("^\\[search-service] prepare delete existing config request by providing wrong id$")
  public void searchServicePrepareDeleteExistingConfigRequestByProvidingWrongId() {
    searchServiceData.setUsername(searchServiceProperties.get("username"));
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
    searchServiceData.setName(searchServiceProperties.get("name"));
  }

  @When("^\\[search-service] send delete config request with wrong id$")
  public void searchServiceSendDeleteConfigRequestWithWrongId() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceController.bodyofRequestWithWrongID();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check the response for deleting config with wrong id$")
  public void searchServiceCheckTheResponseForDeletingConfigWithWrongId() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :for Config with id : 12345"));
  }

  @Given("^\\[search-service] prepare save config request with empty body$")
  public void searchServicePrepareSaveConfigRequestWithEmptyBody() {
    searchServiceData.setEmptyid(searchServiceProperties.get("emptyid"));
    searchServiceData.setEmptyname(searchServiceProperties.get("emptyname"));
  }

  @When("^\\[search-service] send save config request with empty body$")
  public void searchServiceSendSaveConfigRequestWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestwithEmptyBody();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check response for saving config with empty body$")
  public void searchServiceCheckResponseForSavingConfigWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("UNSPECIFIED"));
  }

  @Given("^\\[search-service] prepare find config by name which is not present in the config list$")
  public void searchServicePrepareFindConfigByNameWhichIsNotPresentInTheConfigList() {
    searchServiceData.setWrongname(searchServiceProperties.get("wrongname"));
  }

  @When("^\\[search-service] send find config by name which is not present in the config list$")
  public void searchServiceSendFindConfigByNameWhichIsNotPresentInTheConfigList() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.findByWrongNameResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with name which is not present$")
  public void searchServiceCheckTheResponseForFindingConfigWithNameWhichIsNotPresent() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("Can not find data :"));
    //assertThat(response.getResponseBody().getErrorCode(),equalTo("Can not find data :"));
  }

  @Given("^\\[search-service] prepare find config by id request by providing wrong ID$")
  public void searchServicePrepareFindConfigByIdRequestByProvidingWrongID() {
    searchServiceData.setWrongid(searchServiceProperties.get("wrongid"));
  }

  @When("^\\[search-service] send find config by id request with wrong id$")
  public void searchServiceSendFindConfigByIdRequestWithWrongId() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.setFindByWrongIDResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with wrong id$")
  public void searchServiceCheckTheResponseForFindingConfigWithWrongId() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    assertThat(response.getResponseBody().getValue().getValue(), equalTo(null));
    assertThat(response.getResponseBody().getValue().getName(), equalTo(null));
    assertThat(response.getResponseBody().getValue().getLabel(), equalTo(null));
  }

  @Given("^\\[search-service] prepare find config by word request which is not present in the list$")
  public void searchServicePrepareFindConfigByWordRequestWhichIsNotPresentInTheList() {
    searchServiceData.setWrongword(searchServiceProperties.get("wrongword"));
  }

  @When("^\\[search-service] send find request config by non existing word in the config list$")
  public void searchServiceSendFindRequestConfigByNonExistingWordInTheConfigList() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceController.findByWrongWordResponse();
    searchServiceData.setFindByRequest(response);
  }

  @Then("^\\[search-service] check the response for finding config with wrong word$")
  public void searchServiceCheckTheResponseForFindingConfigWithWrongWord() {
    ResponseApi<GdnRestSingleResponse<ConfigResponse>> response =
        searchServiceData.getFindByRequest();
    assertThat(response.getResponseBody().getValue(), equalTo(null));

  }

  @Given("^\\[search-service] prepare update existing config request with empty body$")
  public void searchServicePrepareUpdateExistingConfigRequestWithEmptyBody() {
    searchServiceData.setEmptyid(searchServiceProperties.get("emptyid"));
    searchServiceData.setEmptyname(searchServiceProperties.get("emptyname"));
  }

  @When("^\\[search-service] send update config request with empty body$")
  public void searchServiceSendUpdateConfigRequestWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.bodyOfRequestToUpdateTheConfigWithEmptyBody();
    searchServiceData.setSearchServiceResponse(response);
  }

  @Then("^\\[search-service] check out the response of update config with empty body$")
  public void searchServiceCheckOutTheResponseOfUpdateConfigWithEmptyBody() {
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat(response.getResponseBody().getErrorMessage(), equalTo("DATA_NOT_FOUND"));
    assertThat(response.getResponseBody().getErrorCode(),
        equalTo("Can not find data :at config but try to update it. id :"));
  }


}
