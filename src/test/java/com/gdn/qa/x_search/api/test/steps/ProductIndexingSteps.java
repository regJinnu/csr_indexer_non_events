package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnRestSingleResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.updateSolrDataForAutomation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

@CucumberStepsDefinition
public class ProductIndexingSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  MongoHelper mongoHelper = new MongoHelper();
  Date lastModifiedActual ;
  Date lastModifiedUpdated ;


  static Logger LOGGER = LoggerFactory.getLogger(ProductIndexingSteps.class);

  @Given("^\\[search-service] failed Ids exist in the DB$")
  public void checkFailedIdsExist(){
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    assertThat("No failed Ids", countOfFailedIds, not(equalTo(0)));
    try {
       lastModifiedActual = SolrHelper.getSolrProd("level0Id:HK1-60001-00196","/select","lastModifiedDate",1).get(0).getLastModifiedDate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for processing failed Ids$")
  public void searchSendsReqForPrcessingFailedIds(){
    ResponseApi<GdnRestSingleResponse> response = searchServiceController.prepareRequestForProcessingFailedIds();
    searchServiceData.setProcessFailedIds(response);
  }

  @Then("^\\[search-service] removes the entries from DB$")
  public void checkEntriesAreRemovedFromDB(){
    long countOfFailedIds = mongoHelper.countOfRecordsInCollection("solr_failed_ids");
    assertThat("No failed Ids", countOfFailedIds, equalTo(0L));
  }

  @Then("^\\[search-service] indexes the Ids present in DB$")
  public void checkDbProductsAreReindexed(){

    try {
      solrCommit("productCollectionNew");
      lastModifiedUpdated = SolrHelper.getSolrProd("level0Id:HK1-60001-00196","/select","lastModifiedDate",1).get(0).getLastModifiedDate();
      LOGGER.debug("------Earlier Date---- lastModifiedActual ----:{}",lastModifiedActual);
      LOGGER.debug("------Update Date---- lastModifiedUpdated ----:{}",lastModifiedUpdated);
      assertThat("Last Modified Data is not Updated",lastModifiedUpdated,greaterThan(lastModifiedActual));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Given("^\\[search-service] product is OOS in SOLR and isInStock in Xproduct$")
  public void checkProductStatusInSOLR(){
    searchServiceData.setSkuForReindex(searchServiceProperties.get("skuForReindex"));
    searchServiceData.setProductCodeForReindex(searchServiceProperties.get("productCodeForReindex"));
    try {
      int status = updateSolrDataForAutomation("id:TH7-15791-00118-00001","/select","id",1);
      assertThat("Updating OOS in SOLR doc failed",status,equalTo(0));
      solrCommit("productCollectionNew");
      int oosFlag = SolrHelper.getSolrProd("id:TH7-15791-00118-00001","/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS in SOLR",oosFlag,equalTo(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] sends request for indexing the product using '(.*)'$")
  public void sendProductReindexRequest(String reqType){

    ResponseApi responseApi;

    if(reqType.equals("productCode"))
      responseApi=searchServiceController.prepareRequestForIndexing("productCodes",searchServiceData.getProductCodeForReindex());
    else if(reqType.equals("sku"))
      responseApi=searchServiceController.prepareRequestForIndexing("skus",searchServiceData.getSkuForReindex());
    else
      responseApi=searchServiceController.prepareRequestForIndexing("","");

    searchServiceData.setSearchServiceResponse(responseApi);
  }

  @Then("^\\[search-service] indexes the provided product$")
  public void checkIfProdWasReindexed(){

    ResponseApi responseApi = searchServiceData.getSearchServiceResponse();
    assertThat("Status Code Not 200", responseApi.getResponse().getStatusCode(), equalTo(200));
    try {
      solrCommit("productCollectionNew");
      int oosFlag = SolrHelper.getSolrProd("id:TH7-15791-00118-00001","/select","isInStock",1).get(0).getIsInStock();
      assertThat("Product not OOS in SOLR",oosFlag,equalTo(1));
    } catch (Exception e) {
      e.printStackTrace();
    }


  }


}
