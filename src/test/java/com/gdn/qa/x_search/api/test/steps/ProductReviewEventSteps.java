package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@CucumberStepsDefinition
public class ProductReviewEventSteps {

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  SearchServiceProperties searchServiceProperties;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  KafkaHelper kafkaHelper;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  ConfigHelper configHelper;

  long reviewAndRatingTimestampBeforeEvent;
  long reviewAndRatingTimestampBeforeEventO2O;
  long reviewAndRatingTimestampAfterEvent;
  long reviewAndRatingTimestampAfterEventO2O;
  long countOfDocsInCollection;

  @Given("^\\[search-service] prepare request for processing product review event$")
  public void searchServicePrepareRequestForProcessingProductReviewEvent() {
    searchServiceData.setStoreId(10001);
    searchServiceData.setProductID(searchServiceProperties.get("idForProductReview"));
    searchServiceData.setMetaDataType("SKU");
    searchServiceData.setAverageRating(5);
    searchServiceData.setRatings(new int[] {0, 0, 0, 0, 1});
    searchServiceData.setRatingPercentages(new double[] {0.0, 0.0, 0.0, 0.0, 100.0});
    searchServiceData.setReviewCount(10);
  }

  @When("^\\[search-service] send request for processing product review event$")
  public void searchServiceSendRequestForProcessingProductReviewEvent() throws Exception {
    String query = searchServiceProperties.get("queryForProductReviewEvent");
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "reviewAndRating",
        SOLR_DEFAULT_COLLECTION);
    assertThat("Updating review and rating in SOLR doc failed", status, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    int reviewCount =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getReviewCount();
    String rating =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getRating();
    reviewAndRatingTimestampBeforeEvent = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "reviewAndRatingServiceLastUpdatedTimestamp",
        1,
        SOLR_DEFAULT_COLLECTION).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();

    assertThat("Test Product not set in SOLR", reviewCount, equalTo(100));
    assertThat("Test Product not set in SOLR", rating, equalTo("23"));

    int statusO2O = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "reviewAndRating",
        SOLR_DEFAULT_COLLECTION_O2O);
    assertThat("Updating review and rating in SOLR doc failed", statusO2O, equalTo(0));
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
    int reviewCountO2O =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getReviewCount();
    String ratingO2O =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getRating();
    reviewAndRatingTimestampBeforeEventO2O = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "reviewAndRatingServiceLastUpdatedTimestamp",
        1,
        SOLR_DEFAULT_COLLECTION_O2O).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();

    assertThat("Test Product not set in SOLR", reviewCountO2O, equalTo(100));
    assertThat("Test Product not set in SOLR", ratingO2O, equalTo("23"));

    kafkaHelper.publishProductReviewEvent(searchServiceData.getStoreId(),
        searchServiceData.getProductID(),
        searchServiceData.getMetaDataType(),
        searchServiceData.getAverageRating(),
        searchServiceData.getRatings(),
        searchServiceData.getRatingPercentages(),
        searchServiceData.getReviewCount());

    Thread.sleep(30000);
  }

  @Then("^\\[search-service] check if product review event is processed and solr is updated$")
  public void searchServiceCheckIfProductReviewEventIsProcessedAndSolrIsUpdated() throws Exception {
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);

    String query = searchServiceProperties.get("queryForProductReviewEvent");
    int reviewCount =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getReviewCount();
    String rating =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION)
            .get(0)
            .getRating();
    reviewAndRatingTimestampAfterEvent = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "reviewAndRatingServiceLastUpdatedTimestamp",
        1,
        SOLR_DEFAULT_COLLECTION).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();
    assertThat("Test Product not updated after event processing", reviewCount, equalTo(10));
    assertThat("Test Product not updated after event processing", rating, equalTo("5"));
    assertThat("reviewAndRatingTimestamp is not Updated",
        reviewAndRatingTimestampAfterEvent,
        greaterThan(reviewAndRatingTimestampBeforeEvent));

    int reviewCountO2O =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getReviewCount();
    String ratingO2O =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION_O2O)
            .get(0)
            .getRating();
    reviewAndRatingTimestampAfterEventO2O = solrHelper.getSolrProd(query,
        SELECT_HANDLER,
        "reviewAndRatingServiceLastUpdatedTimestamp",
        1,
        SOLR_DEFAULT_COLLECTION_O2O).get(0).getReviewAndRatingServiceLastUpdatedTimestamp();
    assertThat("Test Product not updated after event processing", reviewCountO2O, equalTo(10));
    assertThat("Test Product not updated after event processing", ratingO2O, equalTo("5"));
    assertThat("reviewAndRatingTimestamp is not Updated",
        reviewAndRatingTimestampAfterEventO2O,
        greaterThan(reviewAndRatingTimestampBeforeEventO2O));
  }

  @And("^\\[search-service] id get stored in delta table for processing in cnc collection$")
  public void searchServiceIdGetStoredInDeltaTableForProcessingInCncCollection() {
    FindIterable<Document> productReviewDocument = mongoHelper.
        getMongoDocumentByQuery("indexing_list_new",
            "code",
            searchServiceProperties.get("idForProductReview"));
    String collectionFieldValue =
        mongoHelper.getSpecificFieldfromMongoDocument(productReviewDocument, "collection");
    assertThat("collection field value incorrect", collectionFieldValue, equalTo("CNC"));
  }

  @Given("^\\[search-service] update config to exclude product review id from processing$")
  public void searchServiceUpdateConfigToExcludeProductReviewIdFromProcessing() {
    configHelper.findAndUpdateConfig("disable.delta.event.list", "productReviewEventListener");
  }

  @Then("^\\[search-service] product review id should be present in delta collection$")
  public void searchServiceProductReviewIdShouldBePresentInDeltaCollection() {
    long countOfDocsInCollectionAfterDeltaProcessing =
        mongoHelper.countOfRecordsInCollection("indexing_list_new");
    assertThat("Number of documents are equal",
        countOfDocsInCollectionAfterDeltaProcessing,
        lessThan(countOfDocsInCollection));
    Document searchDoc = new Document("code", searchServiceProperties.get("idForProductReview"));
    long countOfProductReviewIds = mongoHelper.countByMongoquery("indexing_list_new", searchDoc);
    assertThat("Specified document not found", countOfProductReviewIds, equalTo(1L));
  }

  @And("^\\[search-service] documents are present in IndexingListNew collection$")
  public void searchServiceDocumentsArePresentInIndexingListNewCollection()
      throws ParseException, InterruptedException {

    searchServiceData.setQueryForProductCode(searchServiceProperties.get("queryForProductCode"));
    searchServiceData.setItemSkuForStoredDelta(searchServiceProperties.get("itemSkuForStoredDelta"));
    searchServiceData.setQueryForReindexOfDeletedProd(searchServiceProperties.get(
        "queryForReindexOfDeletedProd"));
    Thread.sleep(10000);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    Date date = null;
    date = dateFormat.parse("2018-07-30T11:45:39.235Z");

    log.error("----PC---{}", searchServiceData.getQueryForProductCode().split(":")[1]);
    log.error("----iSku---{}", searchServiceData.getItemSkuForStoredDelta().split(":")[1]);

    Document storedDeltaDoc1 =
        new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
            searchServiceData.getQueryForProductCode().split(":")[1])
            .append("type", "productCode")
            .append("isFailed", "0")
            .append("eventType", "ALL")
            .append("eventName", "productChangeEventListener")
            .append("version", 0)
            .append("CREATED_DATE", date)
            .append("CREATED_BY", "user-dev-src")
            .append("UPDATED_DATE", date)
            .append("UPDATED_BY", "user-dev-src")
            .append("MARK_FOR_DELETE", false);

    Document storedDeltaDoc2 =
        new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
            searchServiceData.getItemSkuForStoredDelta().split(":")[1])
            .append("type", "id")
            .append("isFailed", "0")
            .append("eventType", "LOCATION_AND_INVENTORY_SERVICE")
            .append("eventName", "productNonOutOfStockEventListener")
            .append("version", 0)
            .append("CREATED_DATE", date)
            .append("CREATED_BY", "user-dev-src")
            .append("UPDATED_DATE", date)
            .append("UPDATED_BY", "user-dev-src")
            .append("MARK_FOR_DELETE", false);

    Document storedDeltaDoc3 =
        new Document("_class", "com.gdn.x.search.entity.EventIndexingEntity").append("code",
            searchServiceData.getQueryForReindexOfDeletedProd().split(":")[1])
            .append("type", "id")
            .append("isFailed", "0")
            .append("eventType", "LOCATION_AND_INVENTORY_SERVICE")
            .append("eventName", "productNonOutOfStockEventListener")
            .append("version", 0)
            .append("CREATED_DATE", date)
            .append("CREATED_BY", "user-dev-src")
            .append("UPDATED_DATE", date)
            .append("UPDATED_BY", "user-dev-src")
            .append("MARK_FOR_DELETE", false);

    mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc1);
    mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc2);
    mongoHelper.insertInMongo("indexing_list_new", storedDeltaDoc3);

    long countOfStoredEvents = mongoHelper.countOfRecordsInCollection("indexing_list_new");
    assertThat("No Stored events exists in Mongo", countOfStoredEvents, greaterThanOrEqualTo(0L));

    countOfDocsInCollection = mongoHelper.countOfRecordsInCollection("indexing_list_new");
  }

  @Given("^\\[search-service] product review event on cnc collection is present in delta table$")
  public void searchServiceProductReviewEventOnCncCollectionIsPresentInDeltaTable()
      throws Exception {
    FindIterable<Document> productReviewDocument = mongoHelper.getMongoDocumentByQuery(
        "indexing_list_new",
        "code",
        searchServiceProperties.get("idForProductReview"));
    String collectionValue =
        mongoHelper.getSpecificFieldfromMongoDocument(productReviewDocument, "collection");
    assertThat("collection field value incorrect", collectionValue, equalTo("CNC"));

    String query = searchServiceProperties.get("queryForProductReviewEventCNC");
    int status = solrHelper.updateSolrDataForAutomation(query,
        SELECT_HANDLER,
        "id",
        1,
        "reviewAndRating",
        SOLR_DEFAULT_COLLECTION_CNC);
    assertThat("Updating review and rating in SOLR doc failed", status, equalTo(0));
    Thread.sleep(20000);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
    int reviewCount =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getReviewCount();
    String rating =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getRating();

    assertThat("Test Product not set in SOLR", reviewCount, equalTo(100));
    assertThat("Test Product not set in SOLR", rating, equalTo("23"));
  }

  @Then("^\\[search-service] product review event should be processed and cncCollection must be updated$")
  public void searchServiceProductReviewEventShouldBeProcessedAndCncCollectionMustBeUpdated()
      throws Exception {
    Thread.sleep(30000);
    solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);

    String query = searchServiceProperties.get("queryForProductReviewEventCNC");
    int reviewCount =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "reviewCount", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getReviewCount();
    String rating =
        solrHelper.getSolrProd(query, SELECT_HANDLER, "rating", 1, SOLR_DEFAULT_COLLECTION_CNC)
            .get(0)
            .getRating();

    assertThat("Test Product not updated after event processing", reviewCount, equalTo(0));
    assertThat("Test Product not updated after event processing", rating, equalTo("0"));
  }
}
