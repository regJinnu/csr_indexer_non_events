package com.gdn.qa.x_search.api.test.models;

import org.apache.solr.client.solrj.beans.Field;

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

  public SolrResults(String nameSearch, String brandSearch, String description, String level0Id,
      String sku, boolean isSynchronised, int categorySequenceAN,Date lastModifiedDate,int isInStock,
      String rating,int reviewCount,long reviewAndRatingServiceLastUpdatedTimestamp,
      String location,String merchantCommissionType,Double merchantRating,
      long startDateStoreClosed,long endDateStoreClosed, int isDelayShipping) {
    this.nameSearch = nameSearch;
    this.brandSearch = brandSearch;
    this.description = description;
    this.level0Id = level0Id;
    this.sku = sku;
    this.isSynchronised = isSynchronised;
    this.lastModifiedDate = lastModifiedDate;
    this.categorySequenceAN=categorySequenceAN;
    this.isInStock=isInStock;
    this.reviewCount=reviewCount;
    this.rating=rating;
    this.reviewAndRatingServiceLastUpdatedTimestamp = reviewAndRatingServiceLastUpdatedTimestamp;
    this.merchantCommissionType = merchantCommissionType;
    this.merchantRating = merchantRating;
    this.location = location;
    this.isDelayShipping = isDelayShipping;
    this.startDateStoreClosed = startDateStoreClosed;
    this.endDateStoreClosed = endDateStoreClosed;
  }

  public SolrResults(){

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
