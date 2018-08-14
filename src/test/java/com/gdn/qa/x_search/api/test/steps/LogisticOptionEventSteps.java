package com.gdn.qa.x_search.api.test.steps;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.api.services.SearchServiceController;
import com.gdn.qa.x_search.api.test.data.SearchServiceData;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import com.gdn.qa.x_search.api.test.utils.KafkaHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author kumar on 14/08/18
 * @project X-search
 */

@Slf4j
@CucumberStepsDefinition
public class LogisticOptionEventSteps {

  @Autowired
  private SearchServiceController searchServiceController;

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  @Autowired
  private SearchServiceData searchServiceData;

  @Autowired
  KafkaHelper kafkaHelper ;

  @Given("^\\[search-service] update merchant commission type and logistic option for test product$")
  public void setMerchantCommTypeAndLogOptForTestProd(){

  }

  @When("^\\[search-service] consumes logistic option event for a merchant containing test product$")
  public void searchConsumesLogisticOptionChangeEvent(){

  }

  @When("^\\[search-service] run api to reindex products in product atomic reindex queue$")
  public void runProductAtomicIndexJob(){

  }

  @Then("^\\[search-service] merchant commission type and logistic option for test product is updated$")
  public void checkMerchantCommTypeAndLogOptForTestProdAfterEventReindex(){

  }

}
