package com.gdn.qa.x_search.api.test.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.common.web.wrapper.response.GdnRestListResponse;
import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.*;

import com.gdn.qa.x_search.api.test.models.ProductDataFeed;
import com.gdn.x.product.rest.web.model.response.SimpleStringResponse;
import com.gdn.x.search.rest.web.model.FeedExclusionEntityResponse;
import com.google.gson.Gson;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
import java.util.Map;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author kumar on 20/11/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class ProductDataFeedSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  MongoHelper mongoHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  ZipUnzipHelper unzipHelper;

  @Given("^\\[search-service] prepare request to run product data feed$")
  public void preparePDFRequest(){
   searchServiceData.setFeedKey(searchServiceProperties.get("pdfFeedKey"));
   searchServiceData.setFeedType(searchServiceProperties.get("pdfFeedType"));
   searchServiceData.setFeedValue(searchServiceProperties.get("pdfFeedValue"));

    ResponseApi<GdnRestListResponse<FeedExclusionEntityResponse>> exclusionExist =
        searchServiceController.findFeedByWord("abhinav");

    if(exclusionExist.getResponseBody().getPageMetaData().getTotalRecords()==0)
    {
    ResponseApi<GdnBaseRestResponse> response =
        searchServiceController.saveFeedExclusion(searchServiceData.getFeedType(),
            searchServiceData.getFeedKey(),searchServiceData.getFeedValue(),true);
    searchServiceData.setSearchServiceResponse(response);
    assertThat("Status code not 200",
        searchServiceData.getSearchServiceResponse().getResponse().getStatusCode(),equalTo(200));
    }
  }

  @When("^\\[search-service] send request to run pdf feed$")
  public void sendRequestToRunPdfFeed() {
    ResponseApi<GdnRestSingleResponse<SimpleStringResponse>> response =
        searchServiceController.runProductDataFeed();
    searchServiceData.setProductDataFeed(response);
    assertThat("Status code not 200",
        searchServiceData.getProductDataFeed().getResponse().getStatusCode(),equalTo(200));
  }

  @Then("^\\[search-service] file is written to ftp server$")
  public void fileIsWrittenToFtpServer()  {
    FTPHelper ftpHelper = new FTPHelper();
    boolean fileExists = ftpHelper.checkFileExists(REMOTE_PDF_FILE_LOCATION, PDF_FILE_NAME);
    assertThat("File not existing in remote server",fileExists,equalTo(true));
  }

  @Then("^\\[search-service] product count matches with solr query$")
  public void productCountMatchesWithSolrQuery(){
    try {
      long solrProdCount = solrHelper.getSolrProdCountWithFq("*:*",
          "/browse",
          "nameSearch:"+"\""+ searchServiceData.getFeedValue()+"\"");

      FTPHelper ftpHelper = new FTPHelper();

      File dir = new File(LOCAL_STORAGE_LOCATION);

      try {
        FileUtils.cleanDirectory(dir);
      } catch (IOException e) {
        log.error("Failed to delete files in local directory");
      }

      ftpHelper.getFileUsingFtp(REMOTE_PDF_FILE_LOCATION, PDF_FILE_NAME);

      File[] listOfFiles = dir.listFiles();
      assertThat("No files downloaded",listOfFiles.length,not(equalTo(0)));

        unzipHelper.extractPasswordZipFile(LOCAL_STORAGE_LOCATION+PDF_FILE_NAME,"Passw0rd"
            ,LOCAL_STORAGE_LOCATION+PDF_FILE_NAME);

        int countOfRecordsInFile = 0;

      for (File listOfFile : listOfFiles) {
        if (listOfFile.isFile()) {
          try {
            countOfRecordsInFile += DownloadHelper.count(listOfFile.getAbsolutePath());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
    }

      assertThat("No of Records in File does not match with solr",countOfRecordsInFile,
          equalTo(solrProdCount));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] products mentioned as exclusion are not written in the feed$")
  public void exclusionAreNotWrittenInTheFeed(){
    File jsonFile = new File(LOCAL_STORAGE_LOCATION+PDF_FILE_NAME);
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
      Gson gson = new Gson();
      Map<String, List<ProductDataFeed>> productDataFeed = gson.fromJson(bufferedReader, Map.class);

      for (Map.Entry<String,List<ProductDataFeed>> entry : productDataFeed.entrySet()){
        List<ProductDataFeed> productDataFeeds = entry.getValue();
        for (ProductDataFeed prod:productDataFeeds
             ) {
          assertThat("Product name does not match with exclusion",
              prod.getName().toLowerCase(),
              containsString("abhinav automation test prod"));
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }



  @Then("^\\[search-service] only default products are written in the file$")
  public void defaultProductsAreWrittenInTheFile()  {
    
  }

  @Then("^\\[search-service] product details matches with data in solr$")
  public void productDetailsMatchesWithDataInSolr()  {
    
  }
}
