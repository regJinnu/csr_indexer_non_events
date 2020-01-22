package com.gdn.qa.x_search.api.test.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDomainEventModel {

    private long timestamp;
    private String id;
    private String name;
    private String categoryCode;
    private Integer sequence;
    private byte[] description;
    private boolean display;
    private Integer logisticAdjustment;
    private boolean warranty;
    private boolean needIdentity;
    private boolean activated;
    private boolean viewable;
    private CatalogDomainEventModel catalogDomainEventModel;
    private String parentCategoryId;
}
