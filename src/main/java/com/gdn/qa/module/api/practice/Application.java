package com.gdn.qa.module.api.practice;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(
    basePackages = {"com.gdn.qa", "net.thucydides", "net.serenitybdd"}
)public class Application {
}
