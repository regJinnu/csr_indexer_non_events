package com.gdn.qa.x_search.api.test.steps;

import com.gdn.common.web.wrapper.response.GdnBaseRestResponse;
import com.gdn.qa.automation.core.restassured.ResponseApi;
import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * @author kumar on 2019-05-03
 * @project X-search
 */
@Slf4j
@CucumberStepsDefinition
public class OfflineItemChangeSteps {

  @Autowired
  KafkaHelper kafkaHelper;

  @Autowired
  SolrHelper solrHelper;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  SearchServiceData searchServiceData;

  @Autowired
  SearchServiceController searchServiceController;

  @Given("^\\[search-service] update the SOLR doc for offline prod$")
  public void updateDataForOfflineProdInSOLR() {

    searchServiceData.setItemSkuForOffline(searchServiceProperties.get("itemSkuForOffline"));
    searchServiceData.setPickupPointCode(searchServiceProperties.get("pickupPointCode"));
    String query = "itemSku:" + searchServiceData.getItemSkuForOffline();

    try {
      int status = solrHelper.updateSolrDataForAutomation(query,
          SELECT_HANDLER,
          "id",
          3,
          "price",
          SOLR_DEFAULT_COLLECTION_CNC);

      int statusForNormalCollection = solrHelper.updateSolrDataForAutomation(query,
          SELECT_HANDLER,
          "id",
          3,
          "price",
          SOLR_DEFAULT_COLLECTION);

      assertThat("Updating SOLR fields for test failed", status, equalTo(0));
      assertThat("Updating SOLR fields for test failed in NOrmal Collection", statusForNormalCollection, equalTo(0));

      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);

      List<SolrResults> solrProd = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice",
          10, Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_CNC);

      for (SolrResults solrResults : solrProd) {
        assertThat("Offer Price is not set", solrResults.getOfferPrice(), equalTo(4545455.45));
        assertThat("List Price is not set", solrResults.getListPrice(), equalTo(4545455.50));
      }

      List<SolrResults> solrProdInNormalCol = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice",
          10,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION);

      for (int i = 0; i < solrProd.size(); i++) {
        assertThat("Offer Price is not set", solrProdInNormalCol.get(i).getOfferPrice(), equalTo(4545455.45));
        assertThat("List Price is not set", solrProdInNormalCol.get(i).getListPrice(), equalTo(4545455.50));
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @When("^\\[search-service] consumes offline item change event for that itemSku (.*) pickup point code$")
  public void searchServiceConsumesOfflineItemChangeEventForThatItemSku(String type) {

    Map<String, String> params = new HashMap<>();
    if (type.equals("with")) {
      params.put("pickupPointCode", searchServiceData.getPickupPointCode());
      params.put("uniqueId",
          searchServiceData.getItemSkuForOffline() + "-" + searchServiceData.getPickupPointCode());
    } else {
      params.put("pickupPointCode", null);
      params.put("uniqueId", searchServiceData.getItemSkuForOffline() + "-");
    }

    params.put("itemSku", searchServiceData.getItemSkuForOffline());
    params.put("productSku", "KIK-60001-00004");
    params.put("merchantCode", "KIK-60001");

    kafkaHelper.publishOfflineItemChangeEvent(params);
    try {
      Thread.sleep(40000);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION_CNC);
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResponseApi<GdnBaseRestResponse> responseForDefCncJob = searchServiceController.defaultCncJob();
    searchServiceData.setSearchServiceResponse(responseForDefCncJob);
    ResponseApi<GdnBaseRestResponse> response = searchServiceData.getSearchServiceResponse();
    assertThat("Request failed", response.getResponse().getStatusCode(), equalTo(200));
    assertThat("Response failed", response.getResponseBody().isSuccess(), equalTo(true));

    try {
      solrHelper.solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] SOLR doc is updated only for offline item associated to pickup point code$")
  public void searchServiceSOLRDocIsProperlyUpdatedForTheSku() {
    String query = "itemSku:" + searchServiceData.getItemSkuForOffline();

    try {
      List<SolrResults> solrProd = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice",
          10,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION_CNC);

      for (SolrResults solrResults : solrProd) {
        if (solrResults.getId().equals("KIK-60001-00004-00001-PP-3001140")) {
          assertThat("Offer Price is not set", solrResults.getOfferPrice(), (equalTo(4545455.45)));
          assertThat("List Price is not set", solrResults.getListPrice(), (equalTo(4545455.50)));
        }
        if (solrResults.getId().equals("KIK-60001-00004-00001-PP-3001139")) {
          assertThat("Offer Price is not set",
              solrResults.getOfferPrice(),
              not(equalTo(4545455.45)));
          assertThat("List Price is not set", solrResults.getListPrice(), not(equalTo(4545455.50)));
        }
      }
      List<SolrResults> solrProdInNormalCollection = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice",
          10,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION);
      for (int i = 0; i < solrProd.size(); i++) {
        assertThat("Offer Price is not set", solrProdInNormalCollection.get(i).getOfferPrice(), (equalTo(4545455.45)));
        assertThat("List Price is not set", solrProdInNormalCollection.get(i).getListPrice(), equalTo(4545455.50));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] all solr doc associated to the itemSku are updated$")
  public void allSolrDocAssociatedToTheItemSkuAreUpdated() {
    String query = "itemSku:" + searchServiceData.getItemSkuForOffline();
    try {
      List<SolrResults> solrProd = solrHelper.getSolrProd(query,
          SELECT_HANDLER,
          "id,offerPrice,listPrice",
          10,Collections.emptyList(),
          SOLR_DEFAULT_COLLECTION);
      for (SolrResults solrResults : solrProd) {
        assertThat("Offer Price is not set", solrResults.getOfferPrice(), not(equalTo(4545455.45)));
        assertThat("List Price is not set", solrResults.getListPrice(), not(equalTo(4545455.50)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
