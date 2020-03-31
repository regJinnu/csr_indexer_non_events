package com.gdn.qa.x_search.api.test.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SolrResults {

  @Field
  private String brandSearch;
  @Field
  private String description;
  @Field
  private String level0Id;
  @Field
  private String sku;
  @Field
  private boolean isSynchronised;
  @Field
  private Date lastModifiedDate;
  @Field
  private int isInStock;
  @Field
  private int reviewCount;
  @Field
  private String rating;
  @Field
  private long reviewAndRatingServiceLastUpdatedTimestamp;
  @Field
  private String nameSearch;
  @Field
  private String merchantCommissionType;
  @Field
  private Double merchantRating;
  @Field
  private String location;
  @Field("categorySequenceAN-1000001")
  private int categorySequenceAN;
  @Field
  private long startDateStoreClosed;
  @Field
  private long endDateStoreClosed;
  @Field
  private int isDelayShipping;
  @Field
  private ArrayList<String> logisticOptions;
  @Field
  private Double offerPrice;
  @Field
  private Double listPrice;
  @Field("campaign_CAMP-0001")
  private String campaignName;
  @Field
  private boolean storeClose;

  @Field
  private ArrayList<String> activePromos;

  @Field
  private Double salePrice;

  @Field("PRISTINE_COMPUTER_BRAND")
  private String pristineFacet;

  @Field("PRISTINE_CAMERA_MODEL")
  private String pristineCameraFacet;

  @Field("PRISTINE_HANDPHONE_OPERATING_SYSTEM")
  private String pristineHandphoneFacet;

  @Field("discount")
  private Double discount;

  @Field("discountString")
  private String discountString;

  @Field("off2On")
  private int off2On;

  @Field("salesCatalogCategoryIdDescHierarchy")
  private ArrayList<String> salesCatalogCategoryIdDescHierarchy;

  @Field("categorySequenceTE-100003")
  private int categorySequenceTE;

  @Field("published")
  private int published;

  @Field("buyable")
  private int buyable;

  @Field("id")
  private String id;

  @Field("lastUpdatedTime")
  private long lastUpdatedTime;

  @Field
  private boolean tradeInEligible;

  @Field("buyboxScore")
  private Double buyboxScore;

  @Field
  private ArrayList<String> allLocation;

  @Field
  private ArrayList<String> stockLocation;

  @Field("merchantVoucherCount")
  private int merchantVoucherCount;

  @Field("isOfficial")
  private boolean isOfficial;

  @Field("brandCatalog")
  private ArrayList<String> brandCatalog;

  @Field("storeCatalog")
  private ArrayList<String> storeCatalog;

}
