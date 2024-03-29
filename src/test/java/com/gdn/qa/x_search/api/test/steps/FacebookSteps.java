package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.FeedController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.*;
import com.gdn.x.product.domain.event.model.PristineDataItemEventModel;
import com.jcraft.jsch.ChannelSftp;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author kumar on 24/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class FacebookSteps {

  @Autowired
  private FeedController feedController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  KafkaHelper kafkaHelper ;

  @Given("^\\[search-service] exists api for storing all ids in redis$")
  public void searchServiceExistsApiForStoringAllIdsInRedis() {

  }

  @When("^\\[search-service] runs facebook all-ids api$")
  public void searchServiceRunsFacebookAllIdsApi() {

    ResponseApi<GdnBaseRestResponse> gdnBaseRestResponseResponseApi =
        feedController.prepareFacebookPopulateAllIdsRequest();
    searchServiceData.setSearchServiceResponse(gdnBaseRestResponseResponseApi);
  }

  @Then("^\\[search-service] all documents are removed from product feed map collection$")
  public void searchServiceAllDocumentsAreRemovedFromProductFeedMapCollection() {

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long product_feed_map_count = mongoHelper.countOfRecordsInCollection("product_feed_map");

    log.error("----Documents in product_feed_map_count-----"+product_feed_map_count);

    assertThat("All documents not deleted from product_feed_map collection",product_feed_map_count,equalTo(0L));

  }

  @Then("^\\[search-service] data is populated in redis$")
  public void searchServiceDataIsPopulatedInRedis(){

    ResponseApi<GdnBaseRestResponse> searchServiceResponse = searchServiceData.getSearchServiceResponse();

    assertThat("Response code not 200",searchServiceResponse.getResponse().getStatusCode(),equalTo(200));

    Integer errorCode = Integer.valueOf(searchServiceResponse.getResponseBody().getErrorCode());

   // searchServiceData.setErrorMessage(errorCode);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(LOCAL_STORAGE_LOCATION+"Count.txt");
      DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
      outStream.writeUTF(searchServiceResponse.getResponseBody().getErrorCode());
      outStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.error("----Ids stored in Redis-----"+errorCode);

    assertThat("No entries stored in redis",errorCode,greaterThan(0));


  }

  @Given("^\\[search-service] exists api to run facebook full feed$")
  public void searchServiceExistsApiToRunFacebookFullFeed(){

    String count="";
    DataInputStream reader;
    try (FileInputStream fis = new FileInputStream(LOCAL_STORAGE_LOCATION + "Count.txt")) {
      reader = new DataInputStream(fis);
      count = reader.readUTF();
      reader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


    log.error("---Ids in redis---"+count);

    FindIterable<Document> findIterable = mongoHelper.getMongoDocumentByQuery("config_list",
        "NAME",
        "facebook.feed.last.updated.date");

    String facebookLastUpdatedTime = mongoHelper.getSpecificFieldfromMongoDocument(findIterable,"VALUE");
    searchServiceData.setErrorMessage(Integer.valueOf(count));
    searchServiceData.setFacebookFeedLastUpdatedTime(facebookLastUpdatedTime);

  }

  @When("^\\[search-service] runs api to generate full feed$")
  public void searchServiceRunsApiToGenerateFullFeed(){

    ResponseApi<GdnBaseRestResponse> gdnBaseRestResponseResponseApi =
        feedController.prepareFacebookFullFeedRequest();
    searchServiceData.setSearchServiceResponse(gdnBaseRestResponseResponseApi);

  }

  @Then("^\\[search-service] new files are created in specified location$")
  public void searchServiceNewFilesAreCreatedInSpecifiedLocation(){
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

     String SERVER_IP = searchServiceProperties.get("SERVER_IP");

    DownloadHelper downloadHelper = new DownloadHelper(SERVER_IP, SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD);

    Vector<ChannelSftp.LsEntry> files =
        downloadHelper.checkNumberOfFiles(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK);

    assertThat("No files generated",files.size(),greaterThan(0));

  }

  @Then("^\\[search-service] count of records written is equal to ids stored in redis$")
  public void searchServiceCountOfRecordsWrittenIsEqualToIdsStoredInRedis(){

    String SERVER_IP = searchServiceProperties.get("SERVER_IP");

    DownloadHelper downloadHelper = new DownloadHelper(SERVER_IP, SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD);

    Vector<ChannelSftp.LsEntry> files =
        downloadHelper.checkNumberOfFiles(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK);

    File dir = new File(LOCAL_STORAGE_LOCATION);
    File dirAbsolutePath = new File(dir.getAbsolutePath());

    try {
      FileUtils.cleanDirectory(dirAbsolutePath);
    } catch (IOException e) {
      log.error("Failed to delete files in local directory");
    }

//    assertThat("Files not deleted in local system",downloadHelper.purgeDirectory(dir),equalTo(true));

    for (ChannelSftp.LsEntry lsEntry:files) {

      downloadHelper.download(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK+lsEntry.getFilename(),LOCAL_STORAGE_LOCATION);

    }

/*    int countOfRecordsInFile = 0;

    File[] listOfFiles = dir.listFiles();

    if (listOfFiles != null) {
      for (File listOfFile : listOfFiles) {
        if (listOfFile.isFile()) {
          try {
            countOfRecordsInFile += downloadHelper.count(listOfFile.getAbsolutePath());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }*/

    File[] fileList = dir.listFiles();
    if (fileList == null) {
      return;
    }

    Integer countOfRecordsInFile = Arrays.stream(fileList)
        .filter(File::isFile)
        .map(File::getAbsolutePath)
        .map(this::getFileCount)
        .reduce((count1, count2) -> count1 + count2)
        .orElse(0);

    assertThat("No of Records in File", countOfRecordsInFile-files.size(), equalTo(searchServiceData.getErrorMessage()));

  }

  private Integer getFileCount(String filename) {
    try {

       String SERVER_IP = searchServiceProperties.get("SERVER_IP");
       DownloadHelper downloadHelper = new DownloadHelper(SERVER_IP, SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD);
      return downloadHelper.count(filename);
    } catch (IOException e) {
      log.error("Error occurred while getting count", e);
    }
    return 0;
  }

  @Then("^\\[search-service] written sync records are proper$")
  public void searchServiceWrittenSyncRecordsAreProper(){

    try {

      searchServiceData.setQueryForProductCode(searchServiceProperties.get("queryForProductCode"));
      searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

      boolean isSynchronised =
          solrHelper.getSolrProd(searchServiceData.getQueryForProductCode(), SELECT_HANDLER,
              "isSynchronised", 1,Collections.emptyList(),SOLR_DEFAULT_COLLECTION).get(0).isSynchronised();

      assertThat("Product is not synchronised",isSynchronised,equalTo(true));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getProductCodeForReindex());

      assertThat("Sync prod not found",output.trim(),equalTo("6"));

    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  @Then("^\\[search-service] written unsync records are proper$")
  public void searchServiceWrittenUnsyncRecordsAreProper(){

    try {

      searchServiceData.setUnSyncProduct(searchServiceProperties.get("unSyncProduct"));
      searchServiceData.setQueryForUnsyncProduct(searchServiceProperties.get("queryForUnsyncProduct"));

      boolean isSynchronised =
          solrHelper.getSolrProd(searchServiceData.getQueryForUnsyncProduct(), SELECT_HANDLER,
              "isSynchronised", 1,Collections.emptyList(),SOLR_DEFAULT_COLLECTION).get(0).isSynchronised();

      assertThat("Product is not unsync",isSynchronised,equalTo(false));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getUnSyncProduct());

      assertThat("Unsync prod not found",output.trim(),equalTo("1"));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] all items for same product are not written only default is written$")
  public void searchServiceAllItemsForSameProductAreNotWrittenOnlyDefaultIsWritte(){

   /* try {


      searchServiceData.setDefaultProd(searchServiceProperties.get("defaultProd"));


      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getDefaultProd());

      assertThat("More than 1 entry for same product",output.trim(),equalTo("1"));

    } catch (Exception e) {
      e.printStackTrace();
    }*/

  }

  @Then("^\\[search-service] records not satisfying the solr query are not written in file$")
  public void searchServiceRecordsNotSatisfyingTheSolrQueryAreNotWrittenInFile(){

    try {

      searchServiceData.setFbOOSProd(searchServiceProperties.get("fbOOSProd"));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getFbOOSProd());

      assertThat("OOS product part of feed",output.trim(),equalTo("0"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] records satisfying exclusion are not written in file$")
  public void searchServiceRecordsSatisfyingExclusionAreNotWrittenInFile(){

    try {

      searchServiceData.setFbExcludedProd(searchServiceProperties.get("fbExcludedProd"));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getFbExcludedProd());

      assertThat("Excluded product part of feed",output.trim(),equalTo("0"));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] facebook feed last updated time is updated in config_list$")
  public void checkLastUpdatedTimeIsUpdated(){

    FindIterable<Document> findIterable = mongoHelper.getMongoDocumentByQuery("config_list",
        "NAME",
        "facebook.feed.last.updated.date");

    String facebookLastUpdatedTimeNew = mongoHelper.getSpecificFieldfromMongoDocument(findIterable,"VALUE");

    log.error("--facebookLastUpdatedTime-{}-----facebookLastUpdatedTimeNew-{}-----",searchServiceData.getFacebookFeedLastUpdatedTime(),facebookLastUpdatedTimeNew);

    assertThat("last modified date is not updated",facebookLastUpdatedTimeNew,greaterThan(searchServiceData.getFacebookFeedLastUpdatedTime()));


  }

  @Then("^\\[search-service] all fields are populated in the feed$")
  public void checkAllFieldsArePresentWithValue(){

    try {

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "getFacebookRecords.sh",
          searchServiceData.getUnSyncProduct());

      assertThat("Unsync Prod Details not as expected",output.trim(),equalTo(FB_UNSYNC));

      searchServiceData.setDefaultProd(searchServiceProperties.get("defaultProd"));

      String outputSync = ProcessShellCommands.getShellScriptActualOutput(
          "getFacebookRecords.sh",
          searchServiceData.getDefaultProd());

      assertThat("Sync Prod Details not as expected",outputSync.trim(),equalTo(FB_SYNC));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] exists api to run facebook delta feed$")
  public void searchServiceExistsApiToRunFacebookDeltaFeed()  {

    FindIterable<Document> findIterable = mongoHelper.getMongoDocumentByQuery("config_list",
        "NAME",
        "facebook.feed.last.updated.date");

    String facebookLastUpdatedTime = mongoHelper.getSpecificFieldfromMongoDocument(findIterable,"VALUE");
    searchServiceData.setFacebookFeedLastUpdatedTime(facebookLastUpdatedTime);

  }

  @Given("^\\[search-service] data is updated in SOLR$")
  public void searchServiceDataIsUpdatedInSOLR() {

    searchServiceData.setItemSkuForReindex(searchServiceProperties.get("itemSkuForReindex"));
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setQueryForReindex(searchServiceProperties.get("queryForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    kafkaHelper.publishItemChangeEvent(searchServiceData.getItemSkuForReindex(),
        searchServiceData.getSkuForReindex(),false,false,
        Collections.EMPTY_LIST,Collections.EMPTY_SET,false,
        new PristineDataItemEventModel(),Collections.EMPTY_SET);

    String itemSku = searchServiceData.getQueryForReindex().split(":")[1];
    kafkaHelper.publishOOSEvent(itemSku,"TH7-15791","nonOOS");

    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @When("^\\[search-service] runs api to generate facebook delta feed$")
  public void searchServiceRunsApiToGenerateFacebookDeltaFeed() {

    ResponseApi<GdnBaseRestResponse> gdnBaseRestResponseResponseApi =
        feedController.prepareFacebookDeltaFeedRequest();
    searchServiceData.setSearchServiceResponse(gdnBaseRestResponseResponseApi);
    assertThat("Response code not 200",searchServiceData.getSearchServiceResponse().getResponse().getStatusCode(),equalTo(200));
  }


  @Then("^\\[search-service] new delta directory with files are created in specified location$")
  public void searchServiceNewDeltaDirectoryWithFilesAreCreatedInSpecifiedLocation() {

    String SERVER_IP = searchServiceProperties.get("SERVER_IP");

    DownloadHelper downloadHelper = new DownloadHelper(SERVER_IP, SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD);

   String deltaPath = DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK + downloadHelper.getDeltaDirName(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK);

   log.error("-----Delta Path---{}",deltaPath);

    Vector<ChannelSftp.LsEntry> files =
        downloadHelper.checkNumberOfFiles(deltaPath);

    log.error("----Number of files in deltaPath---{}",files.size());

    assertThat("No files generated",files.size(),greaterThan(0));

    File dir = new File(LOCAL_STORAGE_LOCATION);

    try {
      FileUtils.cleanDirectory(dir);
    } catch (IOException e) {
      log.error("Failed to delete files in local directory");
    }

    for (ChannelSftp.LsEntry lsEntry:files) {
      log.error("------Trying to download file-----{}:",deltaPath +"/" +lsEntry.getFilename());
      downloadHelper.download(deltaPath +"/" +lsEntry.getFilename(),LOCAL_STORAGE_LOCATION);

    }

  }


  @Then("^\\[search-service] products which are updated are written in files$")
  public void searchServiceProductsWhichAreUpdatedAreWrittenInFiles(){

    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

    log.error("-----value to search---{}:",searchServiceData.getProductCodeForReindex());

    String output = null;
    try {
      output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getProductCodeForReindex());
     log.error("----Output---{}",output);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertThat("Updated prod not found in delta",output.trim(),equalTo("1"));

  }
}
