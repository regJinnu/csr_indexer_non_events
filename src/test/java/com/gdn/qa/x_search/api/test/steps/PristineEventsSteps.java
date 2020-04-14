package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Slf4j
@CucumberStepsDefinition
public class PristineEventsSteps {
  @Autowired
  KafkaHelper kafkaHelper;
  @Autowired
  ConfigHelper configHelper;
  @Autowired
  SolrHelper solrHelper;

  @Autowired
  private SearchServiceProperties searchServiceProperties;
  @Autowired
  private SearchServiceData searchServiceData;
  @Autowired
  private SearchServiceController searchServiceController;

  @Given("^\\[search-service] set all the values for publishing the pristine event for '(.*)' category$")
  public void setAllTheValuesForPublishingThePristineEvent(String pristine) {

    configHelper.findAndUpdateConfig("product.level.id", PRODUCT_LEVEL0ID);
    configHelper.findAndUpdateConfig("service.product.level.id", PRODUCT_LEVEL0ID);

    switch (pristine) {
      case "handphone": {

        searchServiceData.setProductIdforPristineHandphone(searchServiceProperties.get(
            "productIdforPristineHandphone"));
        searchServiceData.setHandphoneProductItemId(searchServiceProperties.get(
            "handphoneProductItemId"));
        searchServiceData.setHandphoneBlibliCategoryHierarchy(Collections.singletonList(
            searchServiceProperties.get("handphoneBlibliCategoryHierarchy")));
        searchServiceData.setHandphonePristineID(searchServiceProperties.get("handphonePristineID"));
        searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));

        ResponseApi responseApi = searchServiceController.prepareRequestForIndexing("productCodes",
            searchServiceData.getHandphonePristineID());
        searchServiceData.setSearchServiceResponse(responseApi);
        responseApi = searchServiceData.getSearchServiceResponse();
        assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
        try {
          Thread.sleep(20000);
          solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        } catch (Exception e) {
          e.printStackTrace();
        }


        break;
      }
      case "camera": {

        searchServiceData.setProductIdforPristineCamera(searchServiceProperties.get(
            "productIdforPristineCamera"));
        searchServiceData.setCameraBlibliCategoryHierarchy(Collections.singletonList(
            searchServiceProperties.get("cameraBlibliCategoryHierarchy")));
        searchServiceData.setCameraProductItemId(searchServiceProperties.get("cameraProductItemId"));
        searchServiceData.setCameraPristineID(searchServiceProperties.get("cameraPristineID"));
        searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));

        ResponseApi responseApi = searchServiceController.prepareRequestForIndexing("productCodes",
            searchServiceData.getCameraPristineID());
        searchServiceData.setSearchServiceResponse(responseApi);
        responseApi = searchServiceData.getSearchServiceResponse();
        assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
        try {
          Thread.sleep(20000);
          solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        } catch (Exception e) {
          e.printStackTrace();
        }

        break;
      }
      case "computer":

        searchServiceData.setProductIdforPristine(searchServiceProperties.get("productIdforPristine"));
        searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));
        searchServiceData.setBlibliCategoryHierarchy(Collections.singletonList(
            searchServiceProperties.get("blibliCategoryHierarchy")));
        searchServiceData.setProductItemId(searchServiceProperties.get("productItemId"));
        searchServiceData.setPristineID(searchServiceProperties.get("pristineID"));
        break;
    }

    ResponseApi responseApi = searchServiceController.prepareRequestForIndexing("productCodes",
        searchServiceData.getPristineID());
    searchServiceData.setSearchServiceResponse(responseApi);
    responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      Thread.sleep(20000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] publish the pristine event for '(.*)' by providing '(.*)' '(.*)' '(.*)' values for each category$")
  public void publishThePristineEvent(String pristine,
      String category,
      String PristineAttributesName,
      String PristineAttributesValue) {

    switch (pristine) {
      case "handphone": {
        log.debug(
            "-------------------------------HANDPHONE CATEGORY-------------------------------------");
        String productIdforPristine = searchServiceData.getProductIdforPristineHandphone();
        String productItemId = searchServiceData.getHandphoneProductItemId();
        List<String> blibliCategoryHierarchy =
            searchServiceData.getHandphoneBlibliCategoryHierarchy();
        String pristineID = searchServiceData.getHandphonePristineID();
        String itemCount = searchServiceData.getItemCount();
        kafkaHelper.pristineEvent(productIdforPristine,
            productItemId,
            PristineAttributesName,
            PristineAttributesValue,
            blibliCategoryHierarchy,
            category,
            pristineID,
            itemCount);
        try {
          Thread.sleep(30000);
          solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        } catch (Exception e) {
          e.printStackTrace();
        }

        break;
      }
      case "camera": {
        log.debug("--------------------------- CAMERA CATEGORY ----------------------------------");
        String productIdforPristine = searchServiceData.getProductIdforPristineCamera();
        String productItemId = searchServiceData.getCameraProductItemId();
        List<String> blibliCategoryHierarchy = searchServiceData.getCameraBlibliCategoryHierarchy();
        String pristineID = searchServiceData.getCameraPristineID();
        String itemCount = searchServiceData.getItemCount();
        kafkaHelper.pristineEvent(productIdforPristine,
            productItemId,
            PristineAttributesName,
            PristineAttributesValue,
            blibliCategoryHierarchy,
            category,
            pristineID,
            itemCount);
        try {
          Thread.sleep(30000);
          solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      }
      case "computer": {
        log.debug(
            "--------------------------------COMPUTER CATEGORY---------------------------------");
        String productIdforPristine = searchServiceData.getProductIdforPristine();
        String productItemId = searchServiceData.getProductItemId();
        List<String> blibliCategoryHierarchy = searchServiceData.getBlibliCategoryHierarchy();
        String pristineID = searchServiceData.getPristineID();
        String itemCount = searchServiceData.getItemCount();
        kafkaHelper.pristineEvent(productIdforPristine,
            productItemId,
            PristineAttributesName,
            PristineAttributesValue,
            blibliCategoryHierarchy,
            category,
            pristineID,
            itemCount);
        try {
          Thread.sleep(30000);
          solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      }
    }

  }

  @Then("^\\[search-service] verify if the '(.*)' is updated for that particular ID with '(.*)' for '(.*)' category in SOLR$")
  public void verifyIfThePristineAttributesNameIsUpdatedInSOLR(String pristineAttributesName,
      String pristineAttributesValue,
      String pristine) {

    switch (pristine) {
      case "handphone":
        log.debug("------------------HANDPHONE CATEGORY------------------");

        try {
          String pristineFacetInSOLR = solrHelper.getSolrProd(
              PRODUCT_LEVEL0ID + ":" + searchServiceData.getHandphonePristineID(),
              SELECT_HANDLER,
              "PRISTINE_" + pristineAttributesName,
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION).get(0).getPristineHandphoneFacet();
          assertThat(pristineFacetInSOLR, equalTo(pristineAttributesValue));
          break;
        } catch (Exception e) {
          e.printStackTrace();
        }
      case "camera":
        log.debug("------------------CAMERA CATEGORY------------------");
        try {
          String pristineFacetCamera = solrHelper.getSolrProd(
              PRODUCT_LEVEL0ID + ":" + searchServiceData.getCameraPristineID(),
              SELECT_HANDLER,
              "PRISTINE_" + pristineAttributesName,
              1,
              Collections.emptyList(),
              SOLR_DEFAULT_COLLECTION).get(0).getPristineCameraFacet();
          assertThat(pristineFacetCamera, equalTo(pristineAttributesValue));
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      case "computer":
        try {
          log.debug("------------------COMPUTER CATEGORY------------------");
          String pristineFacetComputer =
              solrHelper.getSolrProd(PRODUCT_LEVEL0ID + ":" + searchServiceData.getPristineID(),
                  SELECT_HANDLER,
                  "PRISTINE_" + pristineAttributesName,
                  1,
                  Collections.emptyList(),
                  SOLR_DEFAULT_COLLECTION).get(0).getPristineFacet();
          assertThat(pristineFacetComputer, equalTo(pristineAttributesValue));
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
    }
  }
}
