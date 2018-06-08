package com.gdn.qa.module.api.practice.hooks;

import com.gdn.qa.module.api.practice.CucumberStepsDefinition;
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