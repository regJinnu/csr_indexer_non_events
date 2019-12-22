package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import com.mongodb.client.FindIterable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@Slf4j
@CucumberStepsDefinition
public class BwaSearchEventSteps {

    @Autowired
    private SearchServiceProperties searchServiceProperties;

    @Autowired
    private SearchServiceData searchServiceData;

    @Autowired
    KafkaHelper kafkaHelper;

    @Autowired
    MongoHelper mongoHelper;


    @Given("^\\[search-service] getting search count from db before event$")
    public void searchServiceGettingSearchCountFromDbBeforeEvent() {
        FindIterable<Document> mongoDocumentByQuery =
                mongoHelper.getMongoDocumentByQuery("search_keyword", "searchTerm", searchServiceProperties.get("searchInternalKeyword"));
        for (Document doc : mongoDocumentByQuery) {
            if (doc.get("searchTerm").equals(searchServiceProperties.get("searchInternalKeyword"))) {
                searchServiceData.setLastUpdatedTime((Date) doc.get("UPDATED_DATE"));
                searchServiceData.setCount((Long) doc.get("searchCount"));
            }
        }
    }

    @And("^\\[search-service] set all the values for publishing the bwa search event$")
    public void searchServiceSetAllTheValuesForPublishingTheBwaSearchEvent() {

        searchServiceData.setAccountId(ACCOUNT_ID);
        searchServiceData.setUserId(USER_ID);
        searchServiceData.setSessionid(SESSION_ID);
        searchServiceData.setKeyword(searchServiceProperties.get("searchInternalKeyword"));
        searchServiceData.setCategoryProductId(searchServiceProperties.get("searchCategoryId"));
        searchServiceData.setCategoryProductName(searchServiceProperties.get("searchCategoryName"));
        searchServiceData.setClientMemberId(searchServiceProperties.get("clientmemberid"));
        searchServiceData.setUrl(searchServiceProperties.get("searchPageUrl"));
        searchServiceData.setPageType(PAGE_TYPE);
        searchServiceData.setDeviceType(DEVICE_TYPE);
        searchServiceData.setDevice(DEVICE);
        searchServiceData.setBrowser(BROWSER);
        searchServiceData.setBrowserVersion(BROWSER_VERSION);

    }

    @When("^\\[search-service] publish the bwa search event$")
    public void searchServicePublishTheBwaSearchEvent() {
        Map<String, String> payload = new HashMap<>();
        payload.put("accountId", searchServiceData.getAccountId());
        payload.put("userId", searchServiceData.getUserId());
        payload.put("sessionId", searchServiceData.getSessionid());
        payload.put("keyword", searchServiceData.getKeyword());
        payload.put("categoryId", searchServiceData.getCategoryProductId());
        payload.put("categoryName", searchServiceData.getCategoryProductName());
        payload.put("clientMemberId", searchServiceData.getClientMemberId());
        payload.put("url", searchServiceData.getUrl());
        payload.put("pageType", searchServiceData.getPageType());
        payload.put("deviceType", searchServiceData.getDeviceType());
        payload.put("device", searchServiceData.getDevice());
        payload.put("browser", searchServiceData.getBrowser());
        payload.put("browserType", searchServiceData.getBrowserVersion());
        kafkaHelper.publishSearchBwaEvent(payload);
    }

    @Then("^\\[search-service] check if the search count increased in db$")
    public void searchServiceCheckIfTheSearchCountIncreasedInDb() {
        FindIterable<Document> mongoDocumentByQuery =
                mongoHelper.getMongoDocumentByQuery("search_keyword", "searchTerm", "oppo");
        for (Document doc : mongoDocumentByQuery) {
            if (doc.get("searchTerm").equals(searchServiceProperties.get("searchInternalKeyword"))) {
                assertThat("Search Count value is not increased", (Long) doc.get("searchCount"), greaterThan(searchServiceData.getCount()));
                assertThat("Last Updated Time is not increased", (Date) doc.get("UPDATED_DATE"), greaterThan(searchServiceData.getLastUpdatedTime()));
            }
        }
    }
}
