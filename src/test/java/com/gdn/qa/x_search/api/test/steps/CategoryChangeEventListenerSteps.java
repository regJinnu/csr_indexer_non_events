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
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Slf4j
@CucumberStepsDefinition
public class CategoryChangeEventListenerSteps {

    @Autowired SearchServiceProperties searchServiceProperties;

    @Autowired SearchServiceData searchServiceData;

    @Autowired ConfigHelper configHelper;

    @Autowired SolrHelper solrHelper;

    @Autowired KafkaHelper kafkaHelper;

    @Given("^\\[search-service] change the categoryName of the sku in Normal and '(.*)' collection in case of categoryChange$")
    public void searchServiceChangeThecategoryNameOfTheSkuInNormalAndOtherCollectionInCaseOfCategoryChange(String others)
    {
        resetConfigs();

        if(others.contains("O2O")){
            searchServiceData.setItemSkuForCategoryReindex(searchServiceProperties.get("itemSkuForCategoryReindex"));
            searchServiceData.setCategoryName(searchServiceProperties.get("categoryName"));
            searchServiceData.setCategoryCode(searchServiceProperties.get("categoryCode"));
            searchServiceData.setActivated(searchServiceProperties.get("activated"));
            searchServiceData.setCatalogType(searchServiceProperties.get("catalogType"));

            try {
                int statusOfNormalCollectionUpdate=
                    solrHelper.updateSolrDataForAutomation("id:"+searchServiceData.getItemSkuForCategoryReindex(),
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

                String salesCatalogCategoryIdDescHierarchy= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION).get(0).getSalesCatalogCategoryIdDescHierarchy());

                Long lastModifiedDate=solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION).get(0).getLastUpdatedTime();

                log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}", salesCatalogCategoryIdDescHierarchy, lastModifiedDate);
                assertThat("salesCatalogCategoryIdDescHierarchy not set", salesCatalogCategoryIdDescHierarchy.replace("[","").replace("]",""), equalTo("VA-1000003;Vandana testing category TEST"));
                assertThat("lastModifiedDate not set", lastModifiedDate, equalTo(1100l));

                int statusOfO2OCollectionUpdate =

                    solrHelper.updateSolrDataForAutomation("id:"+searchServiceData.getItemSkuForCategoryReindex(),
                        SELECT_HANDLER,
                        "id",
                        1,
                        "salesCatalogCategoryIdDescHierarchy",
                        SOLR_DEFAULT_COLLECTION_O2O);
                assertThat("Updating SOLR fields for test failed", statusOfO2OCollectionUpdate, equalTo(0));
                solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

                String salesCatalogCategoryIdDescHierarchyO2O= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION_O2O).get(0).getSalesCatalogCategoryIdDescHierarchy());

                Long lastModifiedDateO2O=solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION_O2O).get(0).getLastUpdatedTime();

                log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}", salesCatalogCategoryIdDescHierarchyO2O, lastModifiedDateO2O);
                assertThat("salesCatalogCategoryIdDescHierarchy has set", salesCatalogCategoryIdDescHierarchy.replace("[","").replace("]",""), equalTo("VA-1000003;Vandana testing category TEST"));
                assertThat("lastModifiedDate has set", lastModifiedDateO2O, equalTo(1100l));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(others.equals("CNC")){
            searchServiceData.setDefCncItemSkuforCategoryIndex(searchServiceProperties.get("defCncItemSkuforCategoryIndex"));
            searchServiceData.setDefCncPPforCategoryIndex(searchServiceProperties.get("defCncPPforCategoryIndex"));
            searchServiceData.setPickUpPointforCategoryIndex(searchServiceProperties.get("pickUpPointforCategoryIndex"));
            searchServiceData.setCategoryName(searchServiceProperties.get("categoryName"));
            searchServiceData.setCategoryCode(searchServiceProperties.get("categoryCode"));
            searchServiceData.setActivated(searchServiceProperties.get("activated"));
            searchServiceData.setCatalogType(searchServiceProperties.get("catalogType"));

            int statusOfNormalCollectionUpdate = 0;
            try {
                statusOfNormalCollectionUpdate =
                    solrHelper.updateSolrDataForAutomation("id:" + searchServiceData.getDefCncItemSkuforCategoryIndex(),
                        SELECT_HANDLER,
                        "id",
                        1,
                        "salesCatalogCategoryIdDescHierarchyCNC",
                        SOLR_DEFAULT_COLLECTION);

                assertThat("Updating SOLR fields for test failed",
                    statusOfNormalCollectionUpdate,
                    equalTo(0));

                solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

                String salesCatalogCategoryIdDescHierarchy= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION).get(0).getSalesCatalogCategoryIdDescHierarchy());

                Long lastModifiedTime=solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex()
                    ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION).get(0).getLastUpdatedTime();

                log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}", salesCatalogCategoryIdDescHierarchy, lastModifiedTime);
                assertThat("salesCatalogCategoryIdDescHierarchy has set", salesCatalogCategoryIdDescHierarchy.replace("[","").replace("]",""), equalTo("TEST CNC Category"));
                assertThat("latmodifiedTime", lastModifiedTime, equalTo(1001l));

                int statusOfCNCCollectionUpdate = solrHelper.updateSolrDataForAutomation(
                    "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getPickUpPointforCategoryIndex(),
                    SELECT_HANDLER,
                    "id",
                    1,
                    "salesCatalogCategoryIdDescHierarchyCNC",
                    SOLR_DEFAULT_COLLECTION_CNC);
                assertThat("Updating SOLR fields for test failed", statusOfCNCCollectionUpdate, equalTo(0));
                solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);

                int statusOfCNCCollectionUpdate1 = solrHelper.updateSolrDataForAutomation(
                    "id:" + searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getDefCncItemSkuforCategoryIndex(),
                    SELECT_HANDLER,
                    "id",
                    1,
                    "salesCatalogCategoryIdDescHierarchyCNC",
                    SOLR_DEFAULT_COLLECTION_CNC);
                assertThat("Updating SOLR fields for test failed", statusOfCNCCollectionUpdate1, equalTo(0));
                solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);


                String salesCatelogforL5_1= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getPickUpPointforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String lastModifiedTimeCncL5_1= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getPickUpPointforCategoryIndex()
                    ,SELECT_HANDLER,
                    "lastUpdatedTime",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String salesCatelogforL5_2= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getDefCncPPforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String lastModifiedTimeCncL5_2= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getDefCncPPforCategoryIndex()
                    ,SELECT_HANDLER,
                    "lastUpdatedTime",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());

                assertThat("salesCatalogCategoryIdDescHierarchy has set L5_1", salesCatelogforL5_1.replace("[","").replace("]",""), equalTo("TEST CNC Category"));
                assertThat("lastUpadtedTime has set for L5_1", lastModifiedTimeCncL5_1, equalTo(1001l));


                assertThat("salesCatalogCategoryIdDescHierarchy has set for L5_2", salesCatelogforL5_2.replace("[","").replace("]",""), equalTo("TEST CNC Category"));
                assertThat("lastUpadtedTime has set for L5_2", lastModifiedTimeCncL5_2, equalTo(1001l));


                log.warn("------salesCatalogCategoryIdDescHierarchyCnc and lastModifiedTimeCnc for L5_1--{}---}",salesCatelogforL5_1,lastModifiedTimeCncL5_1 );
                log.warn("------salesCatalogCategoryIdDescHierarchyCnc and lastModifiedTimeCnc for L5_2--{}---}",salesCatelogforL5_2,lastModifiedTimeCncL5_2 );

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @When("^\\[search-service] consumes category change event for that itemSku present in Normal and '(.*)' collection$")
    public void searchServiceConsumesCategoryChangeEventForThatItemSkuPresentInNormalAndOtherCollection(String others)
         {

             if(others.equals("O2O")){
                 kafkaHelper.publishCategoryChangeEvent(searchServiceData.getCategoryName(),searchServiceData.getCategoryCode(),
                     true,searchServiceData.getCatalogType());

                 try {
                     Thread.sleep(30000);
                     solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
                     solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

             if(others.equals("CNC")){
                 kafkaHelper.publishCategoryChangeEvent(searchServiceData.getCategoryName(),searchServiceData.getCategoryCode(),
                     true,searchServiceData.getCatalogType());

                 try {
                     Thread.sleep(30000);
                     solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
                     solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNCNEW);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

         }

    @Then("^\\[search-service] category information is properly updated for Sku in Normal and '(.*)'collection for categoryChange$")
    public void searchServicecategoryInformationIsProperlyUpdatedForSkuInNormalAndOtherCollectionForCategoryChange(String others) {

        if (others.equals("O2O")) {
            try {
                String salesCatalogCategoryIdDescHierarchy= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION).get(0).getSalesCatalogCategoryIdDescHierarchy());

                long lastModifiedDate=solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION).get(0).getLastUpdatedTime();

                log.warn("------salesCatalogCategoryIdDescHierarchy--{}---lastModifiedDate--{}---}", salesCatalogCategoryIdDescHierarchy, lastModifiedDate);
                assertThat("salesCatalogCategoryIdDescHierarchy not set", salesCatalogCategoryIdDescHierarchy, not(equalTo("[VA-1000003;Vandana testing category TEST]")));
                assertThat("lastModifiedDate not set", lastModifiedDate, not(equalTo(1100)));


                String salesCatalogCategoryIdDescHierarchyO2O= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION).get(0).getSalesCatalogCategoryIdDescHierarchy());

                Date lastModifiedDateO2O=solrHelper.getSolrProd("id:"+searchServiceData.getItemSkuForCategoryReindex()
                    ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION).get(0).getLastModifiedDate();
                assertThat("salesCatalogCategoryIdDescHierarchy not set", salesCatalogCategoryIdDescHierarchyO2O, not(equalTo("[VA-1000003;Vandana testing category TEST]")));
                assertThat("lastModifiedDate not set", lastModifiedDateO2O, not(equalTo(1100)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

   //CNC collection
        if(others.equals("CNC")) {
            try {
                Thread.sleep(3000l);
                String salesCatalogCategoryIdDescHierarchy= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION).get(0).getSalesCatalogCategoryIdDescHierarchy());

                Long lastModifiedTime=solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex()
                    ,SELECT_HANDLER,"lastUpdatedTime",
                    1,SOLR_DEFAULT_COLLECTION).get(0).getLastUpdatedTime();


                assertThat("salesCatalogCategoryIdDescHierarchy not set", salesCatalogCategoryIdDescHierarchy.replace("[","").replace("]",""), equalTo("TEST CNC Category"));

                String salesCatelogforL5_1= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getPickUpPointforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String lastModifiedTimeCncL5_1= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getPickUpPointforCategoryIndex()
                    ,SELECT_HANDLER,
                    "lastUpdatedTime",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String salesCatelogforL5_2= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getDefCncPPforCategoryIndex()
                    ,SELECT_HANDLER,
                    "salesCatalogCategoryIdDescHierarchy",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());


                String lastModifiedTimeCncL5_2= String.valueOf(solrHelper.getSolrProd("id:"+searchServiceData.getDefCncItemSkuforCategoryIndex() + searchServiceData.getDefCncPPforCategoryIndex()
                    ,SELECT_HANDLER,
                    "lastUpdatedTime",
                    1,
                    SOLR_DEFAULT_COLLECTION_CNC).get(0).getSalesCatalogCategoryIdDescHierarchy());

                assertThat("salesCatalogCategoryIdDescHierarchy has set L5_1", salesCatelogforL5_1.replace("[","").replace("]",""), not(equalTo("TEST CNC Category")));
                assertThat("lastUpadtedTime has set for L5_1", lastModifiedTimeCncL5_1, not(equalTo(1001l)));


                assertThat("salesCatalogCategoryIdDescHierarchy has set for L5_2", salesCatelogforL5_2.replace("[","").replace("]",""), not(equalTo("TEST CNC Category")));
                assertThat("lastUpadtedTime has set for L5_2", lastModifiedTimeCncL5_2, not(equalTo(1001l)));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetConfigs(){
        configHelper.findAndUpdateConfig("reindex.status","0");
        configHelper.findAndUpdateConfig("reindex.triggered","false");
        configHelper.findAndUpdateConfig("force.stop.solr.updates","false");
    }
}
