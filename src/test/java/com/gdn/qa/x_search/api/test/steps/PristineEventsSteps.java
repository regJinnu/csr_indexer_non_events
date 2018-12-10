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

  @Given("^\\[search-service] set all the values for publishing the pristine event for '(.*)' category$")
  public void searchServiceSetAllTheValuesForPublishingThePristineEventForPristineCategory(String pristine) {

    configHelper.findAndUpdateConfig("product.level.id", PRODUCT_LEVEL0ID);
    configHelper.findAndUpdateConfig("service.product.level.id", PRODUCT_LEVEL0ID);

    if (pristine.equals("handphone")) {

      searchServiceData.setProductIdforPristineHandphone(searchServiceProperties.get(
          "productIdforPristineHandphone"));
      searchServiceData.setHandphoneProductItemId(searchServiceProperties.get(
          "handphoneProductItemId"));
      searchServiceData.setHandphoneBlibliCategoryHierarchy(Collections.singletonList(
          searchServiceProperties.get("handphoneBlibliCategoryHierarchy")));
      searchServiceData.setHandphonePristineID(searchServiceProperties.get("handphonePristineID"));
      searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));


    } else if (pristine.equals("camera")) {

      searchServiceData.setProductIdforPristineCamera(searchServiceProperties.get(
          "productIdforPristineCamera"));
      searchServiceData.setCameraBlibliCategoryHierarchy(Collections.singletonList(
          searchServiceProperties.get("cameraBlibliCategoryHierarchy")));
      searchServiceData.setCameraProductItemId(searchServiceProperties.get("cameraProductItemId"));
      searchServiceData.setCameraPristineID(searchServiceProperties.get("cameraPristineID"));
      searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));

    } else if (pristine.equals("computer")) {
      searchServiceData.setProductIdforPristine(searchServiceProperties.get("productIdforPristine"));
      searchServiceData.setItemCount(searchServiceProperties.get("itemCount"));
      searchServiceData.setBlibliCategoryHierarchy(Collections.singletonList(searchServiceProperties
          .get("blibliCategoryHierarchy")));
      searchServiceData.setProductItemId(searchServiceProperties.get("productItemId"));
      searchServiceData.setPristineID(searchServiceProperties.get("pristineID"));
    }

  }

  @When("^\\[search-service] publish the pristine event for '(.*)' by providing '(.*)' '(.*)' '(.*)' values for each category$")
  public void searchServicePublishThePristineEventForPristineByProvidingCategoryPristineAttributesNamePristineAttributesValueValuesForEachCategory(
      String pristine,
      String category,
      String PristineAttributesName,
      String PristineAttributesValue) {

    if (pristine.equals("handphone")) {
      log.debug("-------------------------------HANDPHONE CATEGORY-------------------------------------");
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

    } else if (pristine.equals("camera")) {
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
    } else if (pristine.equals("computer")) {
      log.debug("--------------------------------COMPUTER CATEGORY---------------------------------");
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
    }

  }

  @Then("^\\[search-service] verify if the '(.*)' is updated for that particular ID with '(.*)' for '(.*)' category in SOLR$")
  public void searchServiceVerifyIfThePristineAttributesNameIsUpdatedForThatParticularIDWithPristineAttributesValueForPristineCategoryInSOLR(
      String pristineAttributesName,
      String pristineAttributesValue,
      String pristine) {

    configHelper.findAndUpdateConfig("product.level.id", PRODUCT_LEVEL0ID);
    configHelper.findAndUpdateConfig("service.product.level.id", PRODUCT_LEVEL0ID);

    if(pristine.equals("handphone")){
      log.debug("------------------HANDPHONE CATEGORY------------------");
      String pristineFacetInSOLR = null;
      try {
        pristineFacetInSOLR = solrHelper.getSolrProd(PRODUCT_LEVEL0ID+":" + searchServiceData.getHandphonePristineID(),
            SELECT_HANDLER,
            "PRISTINE_"+pristineAttributesName,
            1).get(0).getPristineHandphoneFacet();
      } catch (Exception e) {
        e.printStackTrace();
      }
      assertThat(pristineFacetInSOLR, equalTo(pristineAttributesValue));
    }
    else if(pristine.equals("camera")){
      log.debug("------------------CAMERA CATEGORY------------------");
      try {
        String pristineFacetInSOLR =
            solrHelper.getSolrProd(PRODUCT_LEVEL0ID+":" +searchServiceData.getCameraPristineID(),
                SELECT_HANDLER,
                "PRISTINE_"+pristineAttributesName,
                1).get(0).getPristineCameraFacet();
        assertThat(pristineFacetInSOLR, equalTo(pristineAttributesValue));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    else if(pristine.equals("computer")){
      try {
        log.debug("------------------COMPUTER CATEGORY------------------");
        String pristineFacetInSOLR =
            solrHelper.getSolrProd(PRODUCT_LEVEL0ID+":" + searchServiceData.getPristineID(),
                SELECT_HANDLER,
                "PRISTINE_"+pristineAttributesName,
                1).get(0).getPristineFacet();
        assertThat(pristineFacetInSOLR, equalTo(pristineAttributesValue));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
