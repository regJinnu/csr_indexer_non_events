package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
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

  @Given("^\\[search-service] change the price of the sku in SOLR$")
  public void updateTestDataToSolr(){

    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {

      int status = updateSolrDataForAutomation(searchServiceData.getQueryForReindex(),
          SELECT_HANDLER,
          "id",
          1,
          "price");
      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      solrCommit(SOLR_DEFAULT_COLLECTION);

      double offerPrice =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
              .get(0)
              .getOfferPrice();

      double listPrice  =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
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
        searchServiceData.getSkuForReindex(),false,false);

    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] price information is properly updated for the Sku$")
  public void checkProductAfterReindexingByItemChangeEvent(){


    try {

      double offerPrice = SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "offerPrice", 1)
          .get(0)
          .getOfferPrice();

      double listPrice  =
          SolrHelper.getSolrProd(searchServiceData.getQueryForReindex(), SELECT_HANDLER, "listPrice", 1)
              .get(0)
              .getListPrice();

      log.warn("------offerPrice--{}---listPrice--{}---}", offerPrice, listPrice);
      assertThat("offer price not set", offerPrice, not(equalTo(4545455.45)));
      assertThat("list price not set",listPrice,not(equalTo(4545455.50)));


    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Given("^\\[search-service] test product is added in SOLR for '(.*)'$")
  public void addTestDataToSolrBeforeItemChangeEvent(String eventType){
        SolrHelper.addSolrDocumentForItemChangeEvent("AAA-60015-00008-00001","AAA-60015-00008","MTA-66666",eventType);
        SolrHelper.addSolrDocumentForItemChangeEvent("AAA-60015-00008-00002","AAA-60015-00008","MTA-66666",eventType);
    try {
      assertThat("Test Data Not inserted in SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(1L));
      assertThat("Test Data Not inserted in SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes item change event with isArchived is set to true$")
  public void searchConsumesItemChangeEventWithArchivedTrue(){

    kafkaHelper.publishItemChangeEvent("AAA-60015-00008-00001",
        "AAA-60015-00008",true,true);

    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
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
      solrCommit(SOLR_DEFAULT_COLLECTION);
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
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes only the item sku of test product from SOLR$")
  public void checkItemChangeOnlyDeletesItemSku(){
    try {
      assertThat("Test Data Not deleted from SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] deletes the test product from SOLR$")
  public void checkTestProdIsDeleted(){
    try {
      assertThat("Test Data Not deleted from SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00001",SELECT_HANDLER),
          equalTo(0L));
      assertThat("Test Data deleted from SOLR",
          SolrHelper.getSolrProdCount("id:AAA-60015-00008-00002",SELECT_HANDLER),
          equalTo(0L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Then("^\\[search-service] Db entry is created for the Sku in deleted product collection$")
  public void checkDBEntryCreatedForSkuInDeletedProductCollection(){

    int count=0;
    MongoHelper mongoHelper = new MongoHelper();
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
    MongoHelper mongoHelper = new MongoHelper();
    FindIterable<Document> mongoDocumentByQuery =
        mongoHelper.getMongoDocumentByQuery("deleted_products", "_id", "MTA-66666");
    for (Document doc : mongoDocumentByQuery){
      count++;
    }
    assertThat("DB entries created in Deleted Collection",count,equalTo(1));

  }

}
