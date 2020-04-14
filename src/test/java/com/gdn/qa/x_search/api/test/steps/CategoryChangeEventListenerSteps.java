package com.gdn.qa.x_search.api.test.steps;


import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Slf4j
@CucumberStepsDefinition
public class CategoryChangeEventListenerSteps {

  @Autowired
  SearchServiceProperties searchServiceProperties;

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  ConfigHelper configHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  KafkaHelper kafkaHelper;

  @Given("^\\[search-service] change the categoryName of the sku in Normal and '(.*)' collection in case of categoryChange$")
  public void searchServiceChangeThecategoryNameOfTheSkuInNormalAndOtherCollectionInCaseOfCategoryChange(
      String others) {
    resetConfigs();

    if (others.contains("O2O")) {
      searchServiceData.setItemSkuForCategoryReindex(searchServiceProperties.get(
          "itemSkuForCategoryReindex"));
      searchServiceData.setCategoryName(searchServiceProperties.get("categoryName"));
      searchServiceData.setCategoryCode(searchServiceProperties.get("categoryCode"));
      searchServiceData.setActivated(searchServiceProperties.get("activated"));
      searchServiceData.setCatalogType(searchServiceProperties.get("catalogType"));

      try {
        int statusOfNormalCollectionUpdate = solrHelper.updateSolrDataForAutomation(
            "id:" + searchServiceData.getItemSkuForCategoryReindex(),
            SELECT_HANDLER,
            "id",
            1,
            "salesCatalogCategoryIdDescHierarchy",
            SOLR_DEFAULT_COLLECTION);

        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

        assertThat("Updating SOLR fields for test failed",
            statusOfNormalCollectionUpdate,
            equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getItemSkuForCategoryReindex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);

        String salesCatalogCategoryIdDescHierarchy =
            String.valueOf(solrResults.getSalesCatalogCategoryIdDescHierarchy());

        Long lastModifiedDate = solrResults.getLastUpdatedTime();

        log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}",
            salesCatalogCategoryIdDescHierarchy,
            lastModifiedDate);
        assertThat("salesCatalogCategoryIdDescHierarchy not set",
            salesCatalogCategoryIdDescHierarchy.replace("[", "").replace("]", ""),
            equalTo("VA-1000003;Vandana testing category TEST"));
        assertThat("lastModifiedDate not set", lastModifiedDate, equalTo(1100L));

        int statusOfO2OCollectionUpdate =

            solrHelper.updateSolrDataForAutomation(
                "id:" + searchServiceData.getItemSkuForCategoryReindex(),
                SELECT_HANDLER,
                "id",
                1,
                "salesCatalogCategoryIdDescHierarchy",
                SOLR_DEFAULT_COLLECTION_O2O);
        assertThat("Updating SOLR fields for test failed", statusOfO2OCollectionUpdate, equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

        SolrResults solrResultsO2O =
            solrHelper.getSolrProd("id:" + searchServiceData.getItemSkuForCategoryReindex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION_O2O).get(0);

        String salesCatalogCategoryIdDescHierarchyO2O =
            String.valueOf(solrResultsO2O.getSalesCatalogCategoryIdDescHierarchy());

        Long lastModifiedDateO2O = solrResultsO2O.getLastUpdatedTime();

        log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---",
            salesCatalogCategoryIdDescHierarchyO2O,
            lastModifiedDateO2O);

        assertThat("salesCatalogCategoryIdDescHierarchy has set",
            salesCatalogCategoryIdDescHierarchy.replace("[", "").replace("]", ""),
            equalTo("VA-1000003;Vandana testing category TEST"));
        assertThat("lastModifiedDate has set", lastModifiedDateO2O, equalTo(1100L));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (others.equals("CNC")) {
      searchServiceData.setDefCncItemSkuforCategoryIndex(searchServiceProperties.get(
          "defCncItemSkuforCategoryIndex"));
      searchServiceData.setDefCncPPforCategoryIndex(searchServiceProperties.get(
          "defCncPPforCategoryIndex"));
      searchServiceData.setPickUpPointforCategoryIndex(searchServiceProperties.get(
          "pickUpPointforCategoryIndex"));
      searchServiceData.setCategoryName(searchServiceProperties.get("categoryName"));
      searchServiceData.setCategoryCode(searchServiceProperties.get("categoryCode"));
      searchServiceData.setActivated(searchServiceProperties.get("activated"));
      searchServiceData.setCatalogType(searchServiceProperties.get("catalogType"));

      try {
        int statusOfNormalCollectionUpdate = solrHelper.updateSolrDataForAutomation(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex(),
            SELECT_HANDLER,
            "id",
            1,
            "salesCatalogCategoryIdDescHierarchyCNC",
            SOLR_DEFAULT_COLLECTION);

        assertThat("Updating SOLR fields for test failed",
            statusOfNormalCollectionUpdate,
            equalTo(0));

        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSkuforCategoryIndex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);

        String salesCatalogCategoryIdDescHierarchy =
            String.valueOf(solrResults.getSalesCatalogCategoryIdDescHierarchy());

        Long lastModifiedTime = solrResults.getLastUpdatedTime();

        log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}",
            salesCatalogCategoryIdDescHierarchy,
            lastModifiedTime);
        assertThat("salesCatalogCategoryIdDescHierarchy has set",
            salesCatalogCategoryIdDescHierarchy.replace("[", "").replace("]", ""),
            equalTo("TEST CNC Category"));
        assertThat("latmodifiedTime", lastModifiedTime, equalTo(1001L));

        int statusOfCNCCollectionUpdate = solrHelper.updateSolrDataForAutomation(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getPickUpPointforCategoryIndex(),
            SELECT_HANDLER,
            "id",
            1,
            "salesCatalogCategoryIdDescHierarchyCNC",
            SOLR_DEFAULT_COLLECTION_CNC);
        assertThat("Updating SOLR fields for test failed", statusOfCNCCollectionUpdate, equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);

        int statusOfCNCCollectionUpdate1 = solrHelper.updateSolrDataForAutomation(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getDefCncItemSkuforCategoryIndex(),
            SELECT_HANDLER,
            "id",
            1,
            "salesCatalogCategoryIdDescHierarchyCNC",
            SOLR_DEFAULT_COLLECTION_CNC);
        assertThat("Updating SOLR fields for test failed",
            statusOfCNCCollectionUpdate1,
            equalTo(0));
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);

        SolrResults solrResultsCNC = solrHelper.getSolrProd(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getPickUpPointforCategoryIndex(),
            SELECT_HANDLER,
            "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_CNC).get(0);

        String salesCatelogforL5_1 =
            String.valueOf(solrResultsCNC.getSalesCatalogCategoryIdDescHierarchy());


        long lastModifiedTimeCncL5_1 = solrResultsCNC.getLastUpdatedTime();


        SolrResults solrResultsCNC1 = solrHelper.getSolrProd(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getDefCncPPforCategoryIndex(),
            SELECT_HANDLER,
            "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_CNC).get(0);

        String salesCatelogforL5_2 =
            String.valueOf(solrResultsCNC1.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedTimeCncL5_2 = solrResultsCNC.getLastUpdatedTime();

        assertThat("salesCatalogCategoryIdDescHierarchy has set L5_1",
            salesCatelogforL5_1.replace("[", "").replace("]", ""),
            equalTo("TEST CNC Category"));
        assertThat("lastUpadtedTime has set for L5_1", lastModifiedTimeCncL5_1, equalTo(1001L));


        assertThat("salesCatalogCategoryIdDescHierarchy has set for L5_2",
            salesCatelogforL5_2.replace("[", "").replace("]", ""),
            equalTo("TEST CNC Category"));
        assertThat("lastUpadtedTime has set for L5_2", lastModifiedTimeCncL5_2, equalTo(1001L));


        log.warn(
            "------salesCatalogCategoryIdDescHierarchyCnc and lastModifiedTimeCnc for L5_1--{}---{}",
            salesCatelogforL5_1,
            lastModifiedTimeCncL5_1);
        log.warn(
            "------salesCatalogCategoryIdDescHierarchyCnc and lastModifiedTimeCnc for L5_2--{}---{}",
            salesCatelogforL5_2,
            lastModifiedTimeCncL5_2);

      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  @When("^\\[search-service] consumes category change event for that itemSku present in Normal and '(.*)' collection$")
  public void searchServiceConsumesCategoryChangeEventForThatItemSkuPresentInNormalAndOtherCollection(
      String others) {

    if (others.equals("O2O")) {
      kafkaHelper.publishCategoryChangeEvent(searchServiceData.getCategoryName(),
          searchServiceData.getCategoryCode(),
          true,
          searchServiceData.getCatalogType());

      try {
        Thread.sleep(30000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (others.equals("CNC")) {
      kafkaHelper.publishCategoryChangeEvent(searchServiceData.getCategoryName(),
          searchServiceData.getCategoryCode(),
          true,
          searchServiceData.getCatalogType());

      try {
        Thread.sleep(30000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @Then("^\\[search-service] category information is properly updated for Sku in Normal and '(.*)'collection for categoryChange$")
  public void searchServicecategoryInformationIsProperlyUpdatedForSkuInNormalAndOtherCollectionForCategoryChange(
      String others) {

    if (others.equals("O2O")) {
      try {

        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getItemSkuForCategoryReindex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);
        String salesCatalogCategoryIdDescHierarchy =
            String.valueOf(solrResults.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedDate = solrResults.getLastUpdatedTime();

        log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}",
            salesCatalogCategoryIdDescHierarchy,
            lastModifiedDate);
        assertThat("salesCatalogCategoryIdDescHierarchy not set",
            salesCatalogCategoryIdDescHierarchy,
            not(equalTo("[VA-1000003;Vandana testing category TEST]")));
        assertThat("lastModifiedDate not set", lastModifiedDate, not(equalTo(1100)));

        SolrResults solrResultsO2O =
            solrHelper.getSolrProd("id:" + searchServiceData.getItemSkuForCategoryReindex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION_O2O).get(0);

        String salesCatalogCategoryIdDescHierarchyO2O =
            String.valueOf(solrResultsO2O.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedDateO2O = solrResultsO2O.getLastUpdatedTime();

        assertThat("salesCatalogCategoryIdDescHierarchy not set",
            salesCatalogCategoryIdDescHierarchyO2O,
            not(equalTo("[VA-1000003;Vandana testing category TEST]")));
        assertThat("lastModifiedDate not set", lastModifiedDateO2O, not(equalTo(1100)));

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //CNC collection
    if (others.equals("CNC")) {
      try {
        Thread.sleep(3000);

        SolrResults solrResults =
            solrHelper.getSolrProd("id:" + searchServiceData.getDefCncItemSkuforCategoryIndex(),
                SELECT_HANDLER,
                "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
                1,
                Collections.emptyList(),
                SOLR_DEFAULT_COLLECTION).get(0);

        String salesCatalogCategoryIdDescHierarchy =
            String.valueOf(solrResults.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedTime = solrResults.getLastUpdatedTime();

        assertThat("salesCatalogCategoryIdDescHierarchy not set",
            salesCatalogCategoryIdDescHierarchy.replace("[", "").replace("]", ""),
            equalTo("TEST CNC Category"));

        SolrResults solrResultsCNC = solrHelper.getSolrProd(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getPickUpPointforCategoryIndex(),
            SELECT_HANDLER,
            "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_CNC).get(0);

        String salesCatelogforL5_1 =
            String.valueOf(solrResultsCNC.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedTimeCncL5_1 = solrResultsCNC.getLastUpdatedTime();

        SolrResults solrResultsCNC1 = solrHelper.getSolrProd(
            "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex()
                + searchServiceData.getDefCncPPforCategoryIndex(),
            SELECT_HANDLER,
            "salesCatalogCategoryIdDescHierarchy,lastUpdatedTime",
            1,
            Collections.emptyList(),
            SOLR_DEFAULT_COLLECTION_CNC).get(0);

        String salesCatelogforL5_2 =
            String.valueOf(solrResultsCNC1.getSalesCatalogCategoryIdDescHierarchy());

        long lastModifiedTimeCncL5_2 = solrResultsCNC1.getLastUpdatedTime();

        assertThat("salesCatalogCategoryIdDescHierarchy has set L5_1",
            salesCatelogforL5_1.replace("[", "").replace("]", ""),
            not(equalTo("TEST CNC Category")));
        assertThat("lastUpadtedTime has set for L5_1",
            lastModifiedTimeCncL5_1,
            not(equalTo(1001L)));


        assertThat("salesCatalogCategoryIdDescHierarchy has set for L5_2",
            salesCatelogforL5_2.replace("[", "").replace("]", ""),
            not(equalTo("TEST CNC Category")));

        assertThat("lastUpadtedTime has set for L5_2",
            lastModifiedTimeCncL5_2,
            not(equalTo(1001L)));


      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void resetConfigs() {
    configHelper.findAndUpdateConfig("reindex.status", "0");
    configHelper.findAndUpdateConfig("reindex.triggered", "false");
    configHelper.findAndUpdateConfig("force.stop.solr.updates", "false");
  }
}
