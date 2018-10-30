package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class PristineEventsSteps {
  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper;

  ConfigHelper configHelper = new ConfigHelper();

  @Given("^\\[search-service] set all the values for publishing the pristine event for Computer category$")
  public void searchServiceSetAllTheValuesForPublishingThePristineEvent() {
    configHelper.setPVOFF("true");
    searchServiceData.setProductIdforPristine(searchServiceProperties.get("productIdforPristine"));
    searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));
    searchServiceData.setCategory(searchServiceProperties.get("category"));
    searchServiceData.setBlibliCategoryHierarchy(Collections.singletonList(searchServiceProperties.get(
        "blibliCategoryHierarchy")));
    searchServiceData.setProductItemId(searchServiceProperties.get("productItemId"));
    searchServiceData.setPristineID(searchServiceProperties.get("pristineID"));
    searchServiceData.setPristineAttributesName(searchServiceProperties.get("pristineAttributesName"));
    searchServiceData.setPristineAttributesValue(searchServiceProperties.get(
        "pristineAttributesValue"));
  }

  @When("^\\[search-service] publish the pristine event for Computer category$")
  public void searchServicePublishThePristineEvent() {
    kafkaHelper.pristineEventForComputer(searchServiceData.getProductIdforPristine(),
        searchServiceData.getProductItemId(),
        searchServiceData.getPristineAttributesName(),
        searchServiceData.getPristineAttributesValue(),
        searchServiceData.getBlibliCategoryHierarchy(),
        searchServiceData.getCategory(),
        searchServiceData.getPristineID(),
        searchServiceData.getItemCount());
    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the event is consumed by checking the solr field$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingTheSolrField() {
    try {
      String pristineFacetInSOLR =
          SolrHelper.getSolrProd("level1Id:" + searchServiceData.getPristineID(),
              SELECT_HANDLER,
              "PRISTINE_COMPUTER_BRAND",
              1).get(0).getPristineFacet();
      assertThat(pristineFacetInSOLR, equalTo("hp"));
      configHelper.setPVOFF("false");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] set all the values for publishing the pristine event for camera category$")
  public void searchServiceSetAllTheValuesForPublishingThePristineEventForCameraCategory() {
    configHelper.setPVOFF("true");
    searchServiceData.setProductIdforPristineCamera(searchServiceProperties.get(
        "productIdforPristineCamera"));
    searchServiceData.setCameraCategory(searchServiceProperties.get("cameraCategory"));
    searchServiceData.setCameraBlibliCategoryHierarchy(Collections.singletonList(
        searchServiceProperties.get("cameraBlibliCategoryHierarchy")));
    searchServiceData.setCameraProductItemId(searchServiceProperties.get("cameraProductItemId"));
    searchServiceData.setCameraPristineID(searchServiceProperties.get("cameraPristineID"));
    searchServiceData.setCameraPristineAttributesName(searchServiceProperties.get(
        "cameraPristineAttributesName"));
    searchServiceData.setCameraPristineAttributesValue(searchServiceProperties.get(
        "cameraPristineAttributesValue"));
    searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));
  }

  @When("^\\[search-service] publish the pristine event for camera category$")
  public void searchServicePublishThePristineEventForCameraCategory() {
    kafkaHelper.pristineEventForCamera(searchServiceData.getProductIdforPristineCamera(),
        searchServiceData.getCameraProductItemId(),
        searchServiceData.getCameraPristineAttributesName(),
        searchServiceData.getCameraPristineAttributesValue(),
        searchServiceData.getCameraBlibliCategoryHierarchy(),
        searchServiceData.getCameraCategory(),
        searchServiceData.getCameraPristineID(),
        searchServiceData.getItemCount());
    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] check if the event is consumed by checking the solr field for camera category$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingTheSolrFieldForCameraCategory() {
    try {
      String pristineFacetInSOLR =
          SolrHelper.getSolrProd("level1Id:" + searchServiceData.getCameraPristineID(),
              SELECT_HANDLER,
              "PRISTINE_CAMERA_MODEL",
              1).get(0).getPristineCameraFacet();
      assertThat(pristineFacetInSOLR, equalTo("x5"));
      configHelper.setPVOFF("false");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @Given("^\\[search-service] set all the values for publishing the pristine event for handphone category$")
  public void searchServiceSetAllTheValuesForPublishingThePristineEventForHandphoneCategory() {
    configHelper.setPVOFF("true");
    searchServiceData.setProductIdforPristineHandphone(searchServiceProperties.get(
        "productIdforPristineHandphone"));
    searchServiceData.setHandphoneProductItemId(searchServiceProperties.get("handphoneProductItemId"));
    searchServiceData.setHandphonePristineAttributesName(searchServiceProperties.get(
        "handphonePristineAttributesName"));
    searchServiceData.setHandphonePristineAttributesValue(searchServiceProperties.get(
        "handphonePristineAttributesValue"));
    searchServiceData.setHandphoneBlibliCategoryHierarchy(Collections.singletonList(
        searchServiceProperties.get("handphoneBlibliCategoryHierarchy")));
    searchServiceData.setHandphoneCategory(searchServiceProperties.get("handphoneCategory"));
    searchServiceData.setHandphonePristineID(searchServiceProperties.get("handphonePristineID"));
    searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));
    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] publish the pristine event for handphone category$")
  public void searchServicePublishThePristineEventForHandphoneCategory() {
    kafkaHelper.pristineEventForHandphone(searchServiceData.getProductIdforPristineHandphone(),
        searchServiceData.getHandphoneProductItemId(),
        searchServiceData.getHandphonePristineAttributesName(),
        searchServiceData.getHandphonePristineAttributesValue(),
        searchServiceData.getHandphoneBlibliCategoryHierarchy(),
        searchServiceData.getHandphoneCategory(),
        searchServiceData.getHandphonePristineID(),
        searchServiceData.getItemCount());
  }

  @Then("^\\[search-service] check if the event is consumed by checking the solr field for handphone category$")
  public void searchServiceCheckIfTheEventIsConsumedByCheckingTheSolrFieldForHandphoneCategory() {
    try {
      String pristineFacetInSOLR =
          SolrHelper.getSolrProd("level1Id:" + searchServiceData.getHandphonePristineID(),
              SELECT_HANDLER,
              "PRISTINE_HANDPHONE_OPERATING_SYSTEM",
              1).get(0).getPristineHandphoneFacet();
      assertThat(pristineFacetInSOLR, equalTo("ANDROID"));
      configHelper.setPVOFF("false");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
