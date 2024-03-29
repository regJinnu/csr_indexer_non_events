package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.ConfigHelper;
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


@Slf4j
@CucumberStepsDefinition
public class DeleteDanglingProductSteps {
  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  ConfigHelper configHelper;

  @Given("^\\[search-service] add test data into related collections$")
  public void addTestDataIntoRelatedCollections() {
    String eventType = "itemChangeEvent";
    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION);

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION_O2O);

    solrHelper.addSolrDocumentForItemChangeEvent(DANGLING_JOB_ITEMSKU,
        DANGLING_JOB_PRODUCTSKU,
        DANGLING_JOB_PRODUCTCODE,
        eventType,
        SOLR_DEFAULT_COLLECTION_CNC);

    try {
      assertThat("Test Data is not present in Normal coll",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,
              Collections.emptyList()),
          equalTo(1L));

      assertThat("Test Data is not present in O2O coll",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,
              Collections.emptyList()),
          equalTo(1L));

      assertThat("Test Data is not present in CNC coll",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,
              Collections.emptyList()),
          equalTo(1L));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] delete dangling products job has run$")
  public void deleteDanglingProductsJobHasRun() {
    configHelper.findAndUpdateConfig("delete.unpublished.products", "true");

    ResponseApi<GdnBaseRestResponse> response = searchServiceController.deleteDanglingProdJob();
    searchServiceData.setSearchServiceResponse(response);
    try {
      Thread.sleep(30000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_O2O);
      configHelper.findAndUpdateConfig("delete.unpublished.products", "false");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] verify that dangling docs have removed from all the collections$")
  public void verifyThatDanglingDocsHaveRemovedFromAllTheCollections() {
    try {
      assertThat("Test Data Not deleted from SOLR",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION,
              Collections.emptyList()),
          equalTo(0L));

      assertThat("Test Data Not deleted from SOLR in O2O coll",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_O2O,
              Collections.emptyList()),
          equalTo(0L));

      assertThat("Test Data Not deleted from SOLR in CNC coll",
          solrHelper.getSolrProdCount("id:" + DANGLING_JOB_ITEMSKU,
              SELECT_HANDLER,
              SOLR_DEFAULT_COLLECTION_CNC,
              Collections.emptyList()),
          equalTo(0L));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
