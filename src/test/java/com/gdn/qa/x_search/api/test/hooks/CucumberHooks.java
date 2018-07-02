package com.gdn.qa.x_search.api.test.hooks;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import cucumber.api.java.After;
import cucumber.api.java.Before;

@CucumberStepsDefinition
public class CucumberHooks {

  @After
  public void afterRun() {
    //it will executed after scenario run
  }

  @Before
  public void beforeRun() {
    //it will executed before scenario run
  }
}