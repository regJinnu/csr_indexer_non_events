package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.gdn.x.product.domain.event.enums.ItemChangeEventType;
import com.gdn.x.product.domain.event.model.*;
import com.mongodb.client.FindIterable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.x.product.domain.event.enums.ItemChangeEventType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * @author kumar on 20/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class ItemChangeEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  ConfigHelper configHelper;

  @Given("^\\[search-service] change the price of the sku in SOLR$")
  public void updateTestDataToSolr(){

    resetConfigs();

    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "price");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      double offerPrice =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
              .get(0)
              .getOfferPrice();

      double listPrice  =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
              .get(0)
              .getListPrice();

      log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
      assertThat("offer price not set", offerPrice, equalTo(4545455.45));
      assertThat("list price not set",listPrice,equalTo(4545455.50));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes item change event for that itemSku$")
  public void searchConsumesItemChangeEvent(){

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        Collections.EMPTY_LIST,Collections.EMPTY_SET,false,
        new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] price information is properly updated for the Sku with itemChangeEventType and '(.*)' discount schedule$")
  public void checkProductAfterReindexingByItemChangeEvent(String type){

    try {

        double offerPrice =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
            .get(0)
            .getOfferPrice();

        double listPrice  =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
                .get(0)
                .getListPrice();

        double salePrice =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,"salePrice",1)
                .get(0)
                .getSalePrice();

        double discount =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),SELECT_HANDLER,"discount",1)
                .get(0)
                .getDiscount();

        String discountString =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),SELECT_HANDLER,"discountString",1)
                 .get(0)
                 .getDiscountString();

        log.warn("------offerPrice--{}---listPrice--{}---salePrice--{}-", offerPrice, listPrice,salePrice);
        log.warn("------discount--{}---discountString--{}---}", discount, discountString);
        assertThat("offer price not set", offerPrice, equalTo(9000.0));
        assertThat("list price not set", listPrice, equalTo(10000.0));
        if(type.toLowerCase().trim().equals("valid")) {
          assertThat("sale price not set", salePrice, equalTo(8900.0));
          assertThat("discount not set", discountString, equalTo("11"));
          assertThat("discount not set", discount, equalTo(11.0));
        }
        else
        {
          assertThat("sale price not set", salePrice, equalTo(9000.0));
          assertThat("discount not set", discountString, equalTo("10"));
          assertThat("discount not set", discount, equalTo(10.0));
        }

      unInitialize();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Given("^\\[search-service] test product is added in SOLR for '(.*)'$")
  public void addTestDataToSolrBeforeItemChangeEvent(String eventType){

    resetConfigs();

        solrHelper.addSolrDocumentForItemChangeEvent("AAA-60015-00008-00001","AAA-60015-00008","MTA-66666",eventType);
        solrHelper.addSolrDocumentForItemChangeEvent("AAA-60015-00008-00002","AAA-60015-00008","MTA-66666",eventType);
    try {
      assertThat("Test Data Not inserted in SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(1L));
      assertThat("Test Data Not inserted in SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with isArchived is set to true$")
  public void searchConsumesItemChangeEventWithArchivedTrue(){

    kafkaHelper.publishItemChangeEvent("AAA-60015-00008-00001",
        "AAA-60015-00008",true,true,
        Collections.EMPTY_LIST,Collections.EMPTY_SET,false,
        new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @When("^\\[search-service] consumes product change event for that sku$")
  public void searchConsumesProductChangeEvent(){

    kafkaHelper.publishProductChangeEvent(searchServiceData.getProductCodeForReindex(),
        searchServiceData.getSkuForReindex(),false,true);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with markForDelete set to true$")
  public void searchConsumesProductChangeEventWithMarkForDelete(){

    kafkaHelper.publishProductChangeEvent("MTA-66666",
        "AAA-60015-00008",true,true);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes only the item sku of test product from SOLR$")
  public void checkItemChangeOnlyDeletesItemSku(){
    try {
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes the test product from SOLR$")
  public void checkTestProdIsDeleted(){
    try {
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          solrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(0L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] Db entry is created for the Sku in deleted product collection$")
  public void checkDBEntryCreatedForSkuInDeletedProductCollection(){

    int count=0;

    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("deleted_products", "_id", "AAA-60015-00008");
    for (Document doc : mongoDocumentByQuery){
      count++;
    }
    assertThat("DB entries created in Deleted Collection",count,equalTo(1));

  }

  @Then("^\\[search-service] Db entry is created for the productCode in deleted product collection$")
  public void checkDBEntryCreatedForProdCodeInDeletedProductCollection(){

    int count=0;

    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("deleted_products", "_id", "MTA-66666");
    for (Document doc : mongoDocumentByQuery){
      count++;
    }
    assertThat("DB entries created in Deleted Collection",count,equalTo(1));

  }

  @When("^\\[search-service] consumes item change event for that itemSku with price change in itemChangeEventType and '(.*)' discount schedule$")
  public void itemChangeEventWithPriceChangeInEventType(String type) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet();
    DiscountPrice discountPrice = new DiscountPrice();
    if(type.toLowerCase().trim().equals("valid")){
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start = dtOrg.minusDays(1);
      DateTime end = dtOrg.plusDays(1);
      discountPrice.setDiscountPrice(100);
      discountPrice.setStartDateTime(start.toDate());
      discountPrice.setEndDateTime(end.toDate());
      List<DiscountPrice> discountPriceList = new ArrayList<>();
      discountPriceList.add(discountPrice);
      price.setListOfDiscountPrices(discountPriceList);
    }
    else if(type.toLowerCase().trim().equals("invalid")){
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start = dtOrg.minusDays(2);
      DateTime end = dtOrg.minusDays(1);
      discountPrice.setDiscountPrice(200);
      discountPrice.setStartDateTime(start.toDate());
      discountPrice.setEndDateTime(end.toDate());
      List<DiscountPrice> discountPriceList = new ArrayList<>();
      discountPriceList.add(discountPrice);
      price.setListOfDiscountPrices(discountPriceList);
    }
    priceSet.add(price);
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),false,false,
          itemChangeEventStepsList,priceSet,false,new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] price information is properly updated for the Sku$")
  public void checkProductAfterReindexingByPrdChangeEvent(){
    try {

        double offerPrice = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
            .get(0)
            .getOfferPrice();

        double listPrice  =
            solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
                .get(0)
                .getListPrice();

        log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
        assertThat("offer price not set", offerPrice, not(equalTo(4545455.45)));
        assertThat("list price not set",listPrice,not(equalTo(4545455.50)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] update the off2On field in SOLR for the sku$")
  public void searchServiceUpdateTheOffOnFieldInSOLRForTheSku(){

    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    resetConfigs();

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "offToOn");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int off2On =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "off2On", 1)
              .get(0)
              .getOff2On();

      log.warn("------off2on--{}------}", off2On);
      assertThat("off2on not set", off2On, equalTo(4));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as OFFLINE_ITEM_FLAG_CHANGE and offToOn flag value as '(.*)'$")
  public void eventWithItemChangeEventTypeAsOff2On(boolean flag){
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(OFFLINE_ITEM_FLAG_CHANGE);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,flag,new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] o2o flag is updated to '(.*)'$")
  public void checkO2OFlag(boolean flag){

    try {

      int off2On =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "off2On", 1)
              .get(0)
              .getOff2On();

      log.warn("------off2on--{}------", off2On);
      if(flag)
        assertThat("off2on not set", off2On, equalTo(1));
      else
        assertThat("off2on not set", off2On, equalTo(0));

      unInitialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] check isSynchronised field in SOLR for the sku$")
  public void searchServiceUpdateIsSynchronisedFieldInSOLRForTheSku(){
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      boolean isSynchronised =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isSynchronised", 1)
              .get(0)
              .getIsSynchronised();

        assertThat("isSynchronised not set to true", isSynchronised, equalTo(true));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^\\[search-service] consumes item change event with itemChangeEventType as SYNC_UNSYNC_FLAG_CHANGE '(.*)' PristineDataItem$")
  public void itemChangeEventWithTypeAsSYNC_UNSYNC(String type) {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(SYNC_UNSYNC_FLAG_CHANGE);
    if(type.toLowerCase().trim().equals("without"))
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,false,
          new PristineDataItemEventModel(),Collections.EMPTY_SET);
    else{
      PristineDataItemEventModel pristineDataItemModel = new PristineDataItemEventModel();
      pristineDataItemModel.setPristineId("PRI-0000-0001");
      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),false,false,
          itemChangeEventStepsList,Collections.EMPTY_SET,false,
          pristineDataItemModel,Collections.EMPTY_SET);
    }

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] isSynchronised flag is updated to false$")
  public void searchServiceIsSynchronisedFlagIsUpdatedToFalse(){
    try {

      boolean isSynchronised =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "isSynchronised", 1)
              .get(0)
              .getIsSynchronised();

      assertThat("isSynchronised is set to true", isSynchronised, equalTo(false));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] level(\\d+)Id is set to '(.*)'$")
  public void searchServiceLevelIdIsSetToProductSku(int arg0,String type){
    try {
    String level0Id =
        solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),SELECT_HANDLER,"level0Id",1)
        .get(0)
        .getlevel0Id();

      if (type.trim().toLowerCase().equals("productsku"))
        assertThat("Level0Id is not set to sku", level0Id, equalTo(searchServiceData.getSkuForReindex()));
      else
        assertThat("Level0Id is not set to Pristine Id", level0Id, equalTo("PRI-0000-0001"));

      ResponseApi<GdnBaseRestResponse> responseApi = searchServiceController.prepareRequestForIndexing(
          "skus",
          searchServiceData.getSkuForReindex());

      assertThat("response is not 200",responseApi.getResponse().getStatusCode(),equalTo(200));

        Thread.sleep(10000);
        solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      unInitialize();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Given("^\\[search-service] check name and level(\\d+)Id field in SOLR for the sku$")
  public void searchServiceCheckNameAndLevelIdFieldInSOLRForTheSku(int arg0){
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      String name =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "nameSearch", 1)
              .get(0)
              .getNameSearch();

      String level0Id =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "level0Id", 1)
              .get(0)
              .getlevel0Id();

      assertThat("Name is not set", name, equalTo("Product Testing"));
      assertThat("Level0Id is not set", level0Id, equalTo(
          searchServiceData.getProductCodeForReindex()));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^\\[search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE$")
  public void itemChangeEventTypeAsPRISTINE_MAPPING_CHANGE(){
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(PRISTINE_MAPPING_CHANGE);

    SalesCategorySequence salesCategorySequence = new SalesCategorySequence();
    salesCategorySequence.setSequence(10);
    salesCategorySequence.setCategoryCode("TE-100003");
    List<SalesCategorySequence> salesCategorySequences = new ArrayList<>();
    salesCategorySequences.add(salesCategorySequence);


    SalesCategorySequence salesCategorySequenceOldC1 = new SalesCategorySequence();
    salesCategorySequenceOldC1.setCategoryCode("54912");
    salesCategorySequenceOldC1.setSequence(0);

    SalesCategorySequence salesCategorySequenceOldC2 = new SalesCategorySequence();
    salesCategorySequenceOldC2.setCategoryCode("54913");
    salesCategorySequenceOldC2.setSequence(0);

    SalesCategorySequence salesCategorySequenceOldC3 = new SalesCategorySequence();
    salesCategorySequenceOldC3.setCategoryCode("54914");
    salesCategorySequenceOldC3.setSequence(0);

    List<SalesCategorySequence> oldSalesCategorySequences = new ArrayList<>();
    oldSalesCategorySequences.add(salesCategorySequenceOldC1);
    oldSalesCategorySequences.add(salesCategorySequenceOldC2);
    oldSalesCategorySequences.add(salesCategorySequenceOldC3);

    List<ItemCategoryVO> itemCategories = new ArrayList<>();

    ItemCategoryVO c1 = new ItemCategoryVO();
    c1.setProductCategoryCode("TE-100001");
    c1.setCategory("Test Category C1");
    c1.setLevel(1);

   // itemCategories.add(c1); This is commented to handle bug SEARCH-2143 which will be fixed in future sprint

    ItemCategoryVO c2 = new ItemCategoryVO();
    c2.setProductCategoryCode("TE-100002");
    c2.setCategory("Test Category C2");
    c2.setLevel(2);

    //itemCategories.add(c2); This is commented to handle bug SEARCH-2143 which will be fixed in future sprint

    ItemCategoryVO c3 = new ItemCategoryVO();
    c3.setProductCategoryCode("TE-100003");
    c3.setCategory("Test Category C3");
    c3.setLevel(3);

    itemCategories.add(c3);
    itemCategories.add(c2);
    itemCategories.add(c1);

    ItemCatalogVO itemCatalogVO = new ItemCatalogVO();
    itemCatalogVO.setCatalogId("12051");
    itemCatalogVO.setItemCategories(itemCategories);

    List<ItemCatalogVO> pristineCategoriesHierarchy = new ArrayList<>();
    pristineCategoriesHierarchy.add(itemCatalogVO);

    PristineDataItemEventModel pristineDataItemModel = new PristineDataItemEventModel();
    pristineDataItemModel.setPristineId("PRI-0000-0001");
    pristineDataItemModel.setPristineProductName("Pristine Product Testing");
    pristineDataItemModel.setSalesCategorySequences(salesCategorySequences);
    pristineDataItemModel.setPristineCategoriesHierarchy(pristineCategoriesHierarchy);
    pristineDataItemModel.setOldSalesCategorySequences(oldSalesCategorySequences);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,false,
        pristineDataItemModel,Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] pristine name,id and sales catalog is updated accordingly$")
  public void pristineDataIsUpdatedAccordingly(){

    try {
      String name =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "nameSearch", 1)
              .get(0)
              .getNameSearch();

      String level0Id =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "level0Id", 1)
              .get(0)
              .getlevel0Id();

      String salesCatalogHierarchy =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "salesCatalogCategoryIdDescHierarchy",1)
              .get(0)
              .getSalesCatalogCategoryIdDescHierarchy()
              .get(0);

      int categorySeq =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "categorySequenceTE-100003",1)
              .get(0)
              .getCategorySequenceTE();

      String description =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "description",1)
              .get(0)
              .getDescription();

      log.error("----name--{}-description-{}---query-{}",name,description,searchServiceData.getQueryForReindex());

      assertThat("Name is not set", name, equalTo("Pristine Product Testing"));
      assertThat("Level0Id is not set to Pristine Id", level0Id, equalTo("PRI-0000-0001"));
      assertThat("Sales Catalog is not set", salesCatalogHierarchy,
          equalTo("TE-100001;Test Category C1/TE-100002;Test Category C2/TE-100003;Test Category C3"));
      assertThat("Sales Catalog sequence is not set",categorySeq,equalTo(10));

     // unInitialize();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] check buyable,published field in SOLR for the sku$")
  public void checkBuyablePublishedFieldInSOLRForTheSku() {
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "buyableAndPublished");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int buyable =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "buyable", 1)
              .get(0)
              .getBuyable();

      int published =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
              "published", 1)
              .get(0)
              .getPublished();

      assertThat("Buyable is not set", buyable, equalTo(4));
      assertThat("Published is not set", published, equalTo(4));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @When("^\\[search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE with '(.*)' schedule$")
  public void itemChangeEventTypeAsITEM_DATA_CHANGE(String type) {

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);

    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();
    
    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(true);

    if(!type.toLowerCase().equals("no")){
      Date date = new Date();
      DateTime dtOrg = new DateTime(date);
      DateTime start;
      DateTime end;
      if (type.toLowerCase().equals("already running")){
       start = dtOrg.minusDays(1);
       end = dtOrg.plusDays(2);
      }
      else {
        start = dtOrg.plusDays(2);
        end = dtOrg.plusDays(3);
      }

      ItemBuyableSchedule itemBuyableSchedule = new ItemBuyableSchedule();
      itemBuyableSchedule.setStartDateTime(start.toDate());
      itemBuyableSchedule.setEndDateTime(end.toDate());
      itemBuyableSchedule.setBuyable(false);

      ItemDiscoverableSchedule itemDiscoverableSchedule = new ItemDiscoverableSchedule();
      itemDiscoverableSchedule.setStartDateTime(start.toDate());
      itemDiscoverableSchedule.setEndDateTime(end.toDate());
      itemDiscoverableSchedule.setDiscoverable(false);

      itemViewConfig.setItemBuyableSchedules(itemBuyableSchedule);
      itemViewConfig.setItemDiscoverableSchedules(itemDiscoverableSchedule);
    }
    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,false,
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] buyable,published field are added in SOLR with '(.*)' schedule in DB$")
  public void buyablePublishedFieldAreAddedInSOLRAndDB(String type){

    try {
      int buyable =
        solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
            "buyable", 1)
            .get(0)
            .getBuyable();

      int published =
        solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,
            "published", 1)
            .get(0)
            .getPublished();

      FindIterable<Document> mongoDocumentByQuery = mongoHelper.getMongoDocumentByQuery(
          "scheduled_events",
          "documentId",
          searchServiceData.getItemSkuForReindex());

      switch (type){
        case "no":
            assertThat("Buyable not set",buyable,equalTo(1));
            assertThat("Published not set",published,equalTo(1));
            assertThat("Count is not zero",countInDb(mongoDocumentByQuery),equalTo(0));
          break;
        case "already running":
            assertThat("Buyable not set",buyable,equalTo(0));
            assertThat("Published not set",published,equalTo(0));
            assertThat("Count is not zero",countInDb(mongoDocumentByQuery),equalTo(2));
          break;
        case "future":
            assertThat("Buyable not set",buyable,equalTo(1));
            assertThat("Published not set",published,equalTo(1));
            assertThat("Count is not zero",countInDb(mongoDocumentByQuery),equalTo(4));
          break;
      }

    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public int countInDb(FindIterable<Document> mongoDocumentByQuery){
    int count=0;
    for (Document doc:mongoDocumentByQuery
    ) {
      count++;
    }
    return count;
  }

  public void resetConfigs(){
    configHelper.findAndUpdateConfig("reindex.status","0");
    configHelper.findAndUpdateConfig("reindex.triggered","false");
    configHelper.findAndUpdateConfig("force.stop.solr.updates","false");
  }

  public String getValue(FindIterable<Document> mongoDocumentByQuery,String field){
    String value =
        mongoHelper.getSpecificFieldfromMongoDocument(mongoDocumentByQuery, field);
    log.error("----field-{}---value-{}--",field,value);
    return value;
  }

  @Given("^\\[search-service] update fields in SOLR to test data$")
  public void searchServiceUpdateFieldsInSOLRToTestData(){
    resetConfigs();
    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    try {

      int status = solrHelper.updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "categoryReindex");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      int reviewCount = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "reviewCount",
          1).get(0).getReviewCount();

      String rating = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "rating",
          1).get(0).getRating();

      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isInStock",
          1).get(0).getIsInStock();

      String merchantCommissionType =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
              SELECT_HANDLER,
              "merchantCommissionType",
              1).get(0).getMerchantCommissionType();
      Double merchantRating = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "merchantRating",
          1).get(0).getMerchantRating();

      String location = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "location",
          1).get(0).getLocation();

      log.warn(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location);

      assertThat("Test Product not set in SOLR", reviewCount, equalTo(10));
      assertThat("Test Product not set in SOLR", rating, equalTo("40"));
      assertThat("Test Product not set in SOLR", oosFlag, equalTo(0));
      assertThat("Test Product not set in SOLR", merchantRating, equalTo(30.0));
      assertThat("Test Product not set in SOLR", merchantCommissionType, equalTo("CC"));
      assertThat("Test Product not set in SOLR", location, equalTo("Origin-ABC"));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as '(.*)'")
  public void itemChangeEventTypeAsSHIPPING_CHANGE(String type) {
    
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    if(type.equals("SHIPPING_CHANGE"))
      itemChangeEventStepsList.add(SHIPPING_CHANGE);
    else if(type.equals("ARCHIVED_FLAG_CHANGE"))
      itemChangeEventStepsList.add(ARCHIVED_FLAG_CHANGE);

      kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
          searchServiceData.getSkuForReindex(),false,false,
          itemChangeEventStepsList,Collections.EMPTY_SET,false,
          new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] complete SOLR doc is updated instead of atomic update$")
  public void checkAllSOLRFields(){

    try {

      int reviewCount = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "reviewCount",
          1).get(0).getReviewCount();

      String rating = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "rating",
          1).get(0).getRating();

      int oosFlag = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "isInStock",
          1).get(0).getIsInStock();

      String merchantCommissionType =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
              SELECT_HANDLER,
              "merchantCommissionType",
              1).get(0).getMerchantCommissionType();
      Double merchantRating = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "merchantRating",
          1).get(0).getMerchantRating();

      String location = solrHelper.getSolrProd(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "location",
          1).get(0).getLocation();

      log.error(
          "--reviewCount--{}---rating--{}--reviewCount--{}--oosFlag--{}--merchantRating---{}--merchantCommissionType---{}--location--{}--",
          reviewCount,
          rating,
          reviewCount,
          oosFlag,
          merchantRating,
          merchantCommissionType,
          location);

      assertThat("ReviewCount not updated after reindex", reviewCount, not(equalTo(10)));
      assertThat("Rating not updated after reindex", rating, not(equalTo("40")));
      assertThat("isInStock not updated after reindex", oosFlag, equalTo(1));
      assertThat("Merchant Rating not updated after reindex", merchantRating, not(equalTo(30.0)));
      assertThat("Merchant Comm Type not updated after reindex", merchantCommissionType, not(equalTo("CC")));
      assertThat("Location not updated after reindex", location, not(equalTo("Origin-ABC")));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType with both shipping and price change$")
  public void itemChangeEventTypeWithBothShippingAndPriceChange(){

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(SHIPPING_CHANGE);
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet();
    priceSet.add(price);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,priceSet,false,new PristineDataItemEventModel(),Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] complete SOLR doc is updated instead of atomic update for price$")
  public void checkAtomicUpdateIsIgnored(){
    checkAllSOLRFields();
    try {

      double offerPrice =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
              .get(0)
              .getOfferPrice();

      double listPrice  =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
              .get(0)
              .getListPrice();

      double salePrice =
          solrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER,"salePrice",1)
              .get(0)
              .getSalePrice();

      log.warn("------offerPrice--{}---listPrice--{}---salePrice--{}-", offerPrice, listPrice,salePrice);

      assertThat("offer price not set", offerPrice,
          anyOf(not(equalTo(9000.0)),not(equalTo(4545455.45))));
      assertThat("list price not set", listPrice,anyOf(not(equalTo(4545455.50)),
          not(equalTo(10000.0))));
      assertThat("sale price not set", salePrice,
          anyOf(not(equalTo(9000.0)),not(equalTo(4545455.45))));

    }
      catch (Exception e){
      e.printStackTrace();
      }
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType with both data and price change$")
  public void itemChangeEventTypeWithBothDataAndPriceChange(){

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);
    itemChangeEventStepsList.add(ITEM_PRICE_CHANGE);
    Price price = new Price();
    price.setListPrice(10000);
    price.setOfferPrice(9000);
    Set<Price> priceSet = new HashSet();
    priceSet.add(price);


    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();

    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(true);
    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,priceSet,false,
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void unInitialize(){
    try{
      ResponseApi<GdnBaseRestResponse> responseApi = searchServiceController.prepareRequestForIndexing(
          "skus",
          searchServiceData.getSkuForReindex());

      assertThat("response is not 200",responseApi.getResponse().getStatusCode(),equalTo(200));

      Thread.sleep(10000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    }
    catch (Exception e){
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] remove the SOLR doc from SOLR$")
  public void removeTheSOLRDocFromSOLR() {
      resetConfigs();
      searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
      searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
      searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
      searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
      solrHelper.deleteSolrDocByQuery(searchServiceData.getQueryForReindex());
  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as ITEM_DATA_CHANGE and published as '(.*)'$")
  public void searchConsumesItemChangeWithPublishedAsTrue(boolean publishedFlag) {

    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(ITEM_DATA_CHANGE);

    Set<ItemViewConfig> itemViewConfigs = new HashSet<>();

    ItemViewConfig itemViewConfig = new ItemViewConfig();
    itemViewConfig.setBuyable(true);
    itemViewConfig.setDiscoverable(publishedFlag);

    itemViewConfigs.add(itemViewConfig);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,false,
        new PristineDataItemEventModel(),
        itemViewConfigs);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @When("^\\[search-service] consumes item change event with itemChangeEventType as PRISTINE_MAPPING_CHANGE and no PristineDataItem$")
  public void itemChangeWithPristineMapChangeAndNoPristineData() {
    List<ItemChangeEventType> itemChangeEventStepsList = new ArrayList<>();
    itemChangeEventStepsList.add(PRISTINE_MAPPING_CHANGE);

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        itemChangeEventStepsList,Collections.EMPTY_SET,false,
        null,Collections.EMPTY_SET);

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
