package com.gdn.qa.x_search.api.test.models;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Date;

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


  public SolrResults(String brandSearch,
      String description,
      String level0Id,
      String sku,
      boolean isSynchronised,
      Date lastModifiedDate,
      int isInStock,
      int reviewCount,
      String rating,
      long reviewAndRatingServiceLastUpdatedTimestamp,
      String nameSearch,
      String merchantCommissionType,
      Double merchantRating,
      String location,
      int categorySequenceAN,
      long startDateStoreClosed,
      long endDateStoreClosed,
      int isDelayShipping,
      ArrayList<String> logisticOptions,
      Double offerPrice,
      Double listPrice,String campaignName,boolean storeClose,ArrayList<String> activePromos,
      String pristineFacet,String pristineCameraFacet,String pristineHandphoneFacet) {
    this.pristineFacet=pristineFacet;
     this.brandSearch = brandSearch;
    this.description = description;
    this.level0Id = level0Id;
    this.sku = sku;
    this.isSynchronised = isSynchronised;
    this.lastModifiedDate = lastModifiedDate;
    this.isInStock = isInStock;
    this.reviewCount = reviewCount;
    this.rating = rating;
    this.reviewAndRatingServiceLastUpdatedTimestamp = reviewAndRatingServiceLastUpdatedTimestamp;
    this.nameSearch = nameSearch;
    this.merchantCommissionType = merchantCommissionType;
    this.merchantRating = merchantRating;
    this.location = location;
    this.categorySequenceAN = categorySequenceAN;
    this.startDateStoreClosed = startDateStoreClosed;
    this.endDateStoreClosed = endDateStoreClosed;
    this.isDelayShipping = isDelayShipping;
    this.logisticOptions = logisticOptions;
    this.offerPrice = offerPrice;
    this.listPrice = listPrice;
    this.campaignName = campaignName;
    this.storeClose = storeClose;
    this.salePrice = salePrice;
    this.activePromos=activePromos;
    this.pristineCameraFacet=pristineCameraFacet;
    this.pristineHandphoneFacet=pristineHandphoneFacet;
  }


  public SolrResults(){

  }

  public String getPristineHandphoneFacet() {
    return pristineHandphoneFacet ;
  }

  public void setPristineHandphoneFacet(String pristineHandphoneFacet) {
    this.pristineHandphoneFacet = pristineHandphoneFacet;
  }

  public String getPristineFacet() {
    return pristineFacet ;
  }

  public void setPristineFacet(String pristineFacet) {
    this.pristineFacet = pristineFacet;
  }

  public String getPristineCameraFacet() {
    return pristineCameraFacet ;
  }

  public void setPristineCameraFacet(String pristineCameraFacet) {
    this.pristineFacet = pristineCameraFacet;
  }


  public ArrayList<String> getActivePromos() {
    return activePromos;
  }

  public void setActivePromos(ArrayList<String> activePromos) {
    this.activePromos = activePromos;
  }


  public boolean isStoreClose() {
    return storeClose;
  }

  public void setStoreClose(boolean storeClose) {
    this.storeClose = storeClose;
  }

  public String getCampaignName() {
    return campaignName;
  }


  public void setCampaignName(String campaignName) {
    this.campaignName = campaignName;
  }

  public int getCategorySequenceAN(){
    return categorySequenceAN;
  }

  public void setCategorySequenceAN(int categorySequenceAN){
    this.categorySequenceAN=categorySequenceAN;
  }

  public int getIsInStock() {
    return isInStock;
  }

  public void setIsInStock(int isInStock) {
    this.isInStock = isInStock;
  }


  public Double getOfferPrice() {
    return offerPrice;
  }

  public void setOfferPrice(Double offerPrice) {
    this.offerPrice = offerPrice;
  }


  public Double getSalePrice() {
    return salePrice;
  }

  public void setSalePrice(Double salePrice) {
    this.salePrice = salePrice;
  }

  public Double getListPrice() {
    return listPrice;
  }

  public void setListPrice(Double listPrice) {
    this.listPrice = listPrice;
  }


  public String getNameSearch() {
    return nameSearch;
  }

  public void setNameSearch(String nameSearch) {
    this.nameSearch = nameSearch;
  }

  public String getbrandSearch() {
    return brandSearch;
  }

  public void setbrandSearch(String brandSearch) {
    this.brandSearch = brandSearch;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getlevel0Id() {
    return level0Id;
  }

  public void setlevel0Id(String level0Id) {
    this.level0Id = level0Id;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public boolean getIsSynchronised() {
    return isSynchronised;
  }

  public void setIsSynchronised(boolean isSynchronised) {
    this.isSynchronised = isSynchronised;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public int getReviewCount() {
    return reviewCount;
  }

  public void setReviewCount(int reviewCount) {
    this.reviewCount = reviewCount;
  }

  public String getRating() {
    return rating;
  }

  public void setRating(String rating) {
    this.rating = rating;
  }

  public long getReviewAndRatingServiceLastUpdatedTimestamp() {
    return reviewAndRatingServiceLastUpdatedTimestamp;
  }

  public void setReviewAndRatingServiceLastUpdatedTimestamp(long reviewAndRatingServiceLastUpdatedTimestamp) {
    this.reviewAndRatingServiceLastUpdatedTimestamp = reviewAndRatingServiceLastUpdatedTimestamp;
  }

  public String getMerchantCommissionType() {
    return merchantCommissionType;
  }

  public void setMerchantCommissionType(String merchantCommissionType) {
    this.merchantCommissionType = merchantCommissionType;
  }

  public Double getMerchantRating() {
    return merchantRating;
  }

  public void setMerchantRating(Double merchantRating) {
    this.merchantRating = merchantRating;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }


  public long getStartDateStoreClosed() {
    return startDateStoreClosed;
  }

  public void setStartDateStoreClosed(long startDateStoreClosed) {
    this.startDateStoreClosed = startDateStoreClosed;
  }

  public long getEndDateStoreClosed() {
    return endDateStoreClosed;
  }

  public void setEndDateStoreClosed(long endDateStoreClosed) {
    this.endDateStoreClosed = endDateStoreClosed;
  }

  public int getIsDelayShipping() {
    return isDelayShipping;
  }

  public void setIsDelayShipping(int isDelayShipping) {
    this.isDelayShipping = isDelayShipping;
  }


  public ArrayList<String> getLogisticOptions() {
    return logisticOptions;
  }

  public void setLogisticOptions(ArrayList<String> logisticOptions) {
    this.logisticOptions = logisticOptions;
  }




  @Override
  public String toString() {
    return "SolrResults{" +
        "nameSearch='" + nameSearch + '\'' +
        ", brandSearch='" + brandSearch + '\'' +
        ", description='" + description + '\'' +
        ", level0Id='" + level0Id + '\'' +
        ", sku='" + sku + '\'' +
        ", isSynchronised=" + isSynchronised +
        '}';
  }
  

}
