package com.gdn.qa.x_search.api.test.steps;


import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@CucumberStepsDefinition
public class SuggestionIndexControllerSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Given("^\\[search-service] entries with isPushedToSolr as 0 exists in searchKeyword collection$")
  public void checkDBentriesWithIsPushedToSolr(){

  }

  @When("^\\[search-service] sends request for suggestion collection delta reindex$")
  public void sendRequestForSuggestionReindex(){

  }

  @Then("^\\[search-service] indexes the Ids present in DB to SOLR suggestion collection$")
  public void suggestionIsIndexedInSolr(){

  }


}
