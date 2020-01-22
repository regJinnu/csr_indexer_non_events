package com.gdn.qa.x_search.api.test.models;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CatalogDomainEventModel {
    private String name;
    private String catalogCode;
    private String catalogType;
}
