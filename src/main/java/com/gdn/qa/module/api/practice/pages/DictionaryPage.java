package com.gdn.qa.module.api.practice.pages;

import ch.lambdaj.function.convert.Converter;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Component
public class DictionaryPage extends PageObject {

  @FindBy(name = "search")
  private WebElementFacade searchTerms;

  @FindBy(name = "go")
  private WebElementFacade lookupButton;

  public void enter_keywords(String keyword) {
    searchTerms.type(keyword);
  }

  public void lookup_terms() {
    lookupButton.click();
  }


  public List<String> getDefinitions() {
    WebElementFacade definitionList = find(By.tagName("ol"));
    List<WebElement> results = definitionList.findElements(By.tagName("li"));
    return convert(results, toStrings());
  }

  private Converter<WebElement, String> toStrings() {
    return new Converter<WebElement, String>() {
      public String convert(WebElement from) {
        return from.getText();
      }
    };
  }
}
