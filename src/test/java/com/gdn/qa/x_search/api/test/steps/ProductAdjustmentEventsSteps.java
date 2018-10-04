package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.SolrHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SELECT_HANDLER;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_DEFAULT_COLLECTION;
import static com.gdn.qa.x_search.api.test.utils.SolrHelper.solrCommit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@CucumberStepsDefinition
public class ProductAdjustmentEventsSteps {
  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper;

  @Given("^\\[search-service] set all the values for publishing the product adjustment change events$")
  public void searchServiceSetAllTheValuesForPublishingTheProductAdjustmentChangeEvents() {
    searchServiceData.setAdjustmentName(searchServiceProperties.get("adjustmentName"));
    searchServiceData.setDescription(searchServiceProperties.get("description"));
    searchServiceData.setPromoValue(searchServiceProperties.get("promoValue"));
    searchServiceData.setPromoItemSKU(searchServiceProperties.get("promoItemSKU"));
    searchServiceData.setPromoActivated(Boolean.parseBoolean(searchServiceProperties.get(
        "promoActivated")));
    searchServiceData.setPromoItemSKUinSOLR(searchServiceProperties.get("promoItemSKUinSOLR"));
  }

  @When("^\\[search-service] publish the product adjustment change events$")
  public void searchServicePublishTheProductAdjustmentChangeEvents() {
    kafkaHelper.productAdjustmentChangeEvent(searchServiceData.getAdjustmentName(),
        searchServiceData.getDescription(),
        searchServiceData.getPromoItemSKU(),
        Long.valueOf(searchServiceData.getPromoValue()),
        searchServiceData.isPromoActivated());
    try {
      Thread.sleep(30000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the product adjustment change event is consumed and check in solr$")
  public void searchServiceCheckIfTheProductAdjustmentChangeEventIsConsumedAndCheckInSolr() {
    Double promoOfferPrice = null;
    try {
      promoOfferPrice = SolrHelper.getSolrProd(searchServiceData.getPromoItemSKUinSOLR(),
          SELECT_HANDLER,
          "salePrice",
          1).get(0).getSalePrice();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertThat(promoOfferPrice, equalTo(99000.0));
  }

  @Given("^\\[search-service] set all the values for publishing promoBundling Activated event$")
  public void searchServiceSetAllTheValuesForPublishingPromoBundlingActivatedEvent() {
    searchServiceData.setPromoBundlingId(searchServiceProperties.get("promoBundlingID"));
    searchServiceData.setPromoBundlingType(searchServiceProperties.get("promoBundlingType"));
    searchServiceData.setComplementaryProducts(searchServiceProperties.get("complementaryProducts"));
    searchServiceData.setPromoItemSKU(searchServiceProperties.get("promoItemSKU"));
    searchServiceData.setPromoItemSKUinSOLR(searchServiceProperties.get("promoItemSKUinSOLR"));
  }


  @When("^\\[search-service] publish the promo Bundling Activated event$")
  public void searchServicePublishThePromoBundlingActivatedEvent() {
    kafkaHelper.promoBundlingActivateEvent(searchServiceData.getPromoBundlingId(),
        searchServiceData.getPromoItemSKU(),
        searchServiceData.getPromoBundlingType(),
        Collections.singletonList(searchServiceData.getComplementaryProducts()));
    try {
      Thread.sleep(50000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Then("^\\[search-service] check if the promo Bundling Activated event is consumed and check in solr$")
  public void searchServiceCheckIfThePromoBundlingActivatedEventIsConsumedAndCheckInSolr() {
    String promoOffer= null;
    try {
      promoOffer=SolrHelper.getSolrProd(searchServiceData.getPromoItemSKUinSOLR(),SELECT_HANDLER,"activePromos",1)
          .get(0).getActivePromos();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertThat(promoOffer,containsString("combo"));
  }

  @Given("^\\[search-service] set all the values for publishing promoBundling Deactivated event$")
  public void searchServiceSetAllTheValuesForPublishingPromoBundlingDeactivatedEvent() {
    searchServiceData.setPromoItemSKU(searchServiceProperties.get("promoItemSKU"));
    searchServiceData.setPromoBundlingType(searchServiceProperties.get("promoBundlingType"));
  }

  @When("^\\[search-service] publish the promo Bundling Deactivated event$")
  public void searchServicePublishThePromoBundlingDeactivatedEvent() throws Throwable {
    kafkaHelper.promoBundlingDeactivatedEvent(searchServiceData.getPromoItemSKU(),
        searchServiceData.getPromoBundlingType());
    try {
      Thread.sleep(50000);
      solrCommit(SOLR_DEFAULT_COLLECTION);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Then("^\\[search-service] check if the promo Bundling Deactivated event is consumed and check in solr$")
  public void searchServiceCheckIfThePromoBundlingDeactivatedEventIsConsumedAndCheckInSolr() {
    String promoOffer= null;
    try {
      promoOffer = SolrHelper.getSolrProd(searchServiceData.getPromoItemSKUinSOLR(),SELECT_HANDLER,"activePromos",1)
          .get(0).getActivePromos();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertThat(promoOffer,equalTo(null));
  }
}
