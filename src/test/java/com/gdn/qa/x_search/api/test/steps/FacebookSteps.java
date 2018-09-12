package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.FeedController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.DownloadHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.ProcessShellCommands;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import com.jcraft.jsch.ChannelSftp;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

  MongoHelper mongoHelper = new MongoHelper();

  DownloadHelper downloadHelper = new DownloadHelper(SERVER_IP, SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD);

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

    long product_feed_map_count = mongoHelper.countOfRecordsInCollection("product_feed_map");

    log.error("----Documents in product_feed_map_count-----"+product_feed_map_count);

    assertThat("All documents not deleted from product_feed_map collection",product_feed_map_count,equalTo(0L));

  }

  @Then("^\\[search-service] data is populated in redis$")
  public void searchServiceDataIsPopulatedInRedis(){

    ResponseApi<GdnBaseRestResponse> searchServiceResponse = searchServiceData.getSearchServiceResponse();

    assertThat("Response code not 200",searchServiceResponse.getResponse().getStatusCode(),equalTo(200));

    Integer errorCode = Integer.valueOf(searchServiceResponse.getResponseBody().getErrorCode());

    searchServiceData.setErrorMessage(errorCode);

    log.error("----Ids stored in Redis-----"+errorCode);

    assertThat("No entries stored in redis",errorCode,greaterThan(0));


  }

  @Given("^\\[search-service] exists api to run facebook full feed$")
  public void searchServiceExistsApiToRunFacebookFullFeed(){

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

    Vector<ChannelSftp.LsEntry> files =
        downloadHelper.checkNumberOfFiles(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK);

    assertThat("No files generated",files.size(),greaterThan(0));

  }

  @Then("^\\[search-service] count of records written is equal to ids stored in redis$")
  public void searchServiceCountOfRecordsWrittenIsEqualToIdsStoredInRedis(){

    Vector<ChannelSftp.LsEntry> files =
        downloadHelper.checkNumberOfFiles(DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK);

    File dir = new File(LOCAL_STORAGE_LOCATION);

    assertThat("Files not deleted in local system",downloadHelper.purgeDirectory(dir),equalTo(true));

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

    assertThat("No of Records in File", countOfRecordsInFile, equalTo(searchServiceData.getErrorMessage()));

  }

  private Integer getFileCount(String filename) {
    try {
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
          SolrHelper.getSolrProd(searchServiceData.getQueryForProductCode(), SELECT_HANDLER,
              "isSynchronised", 1).get(0).getIsSynchronised();

      assertThat("Product is not synchronised",isSynchronised,equalTo(true));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getProductCodeForReindex());

      assertThat("Sync prod not found",output,equalTo("1"));

    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  @Then("^\\[search-service] written unsync records are proper$")
  public void searchServiceWrittenUnsyncRecordsAreProper(){

    try {

      searchServiceData.setQueryForProductCode(searchServiceProperties.get("queryForProductCode"));
      searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));

      boolean isSynchronised =
          SolrHelper.getSolrProd(searchServiceData.getQueryForProductCode(), SELECT_HANDLER,
              "isSynchronised", 1).get(0).getIsSynchronised();

      assertThat("Product is not synchronised",isSynchronised,equalTo(true));

      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh",
          searchServiceData.getProductCodeForReindex());

      assertThat("Sync prod not found",output,equalTo("1"));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] all items for same product are not written only default is written$")
  public void searchServiceAllItemsForSameProductAreNotWrittenOnlyDefaultIsWritte(){

  }

  @Then("^\\[search-service] records not satisfying the solr query are not written in file$")
  public void searchServiceRecordsNotSatisfyingTheSolrQueryAreNotWrittenInFile(){

  }

  @Then("^\\[search-service] records satisfying exclusion are not written in file$")
  public void searchServiceRecordsSatisfyingExclusionAreNotWrittenInFile(){

  }
}
