package com.gdn.qa.x_search.api.test;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(
    basePackages = {"com.gdn.qa", "net.thucydides", "net.serenitybdd"}
)public class Application {
}
