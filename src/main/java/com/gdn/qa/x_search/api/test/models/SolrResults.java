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
  private String nameSearch;
  @Field("categorySequenceAN-1000001")
  private int categorySequenceAN;

  public SolrResults(String nameSearch, String brandSearch, String description, String level0Id,
      String sku, boolean isSynchronised, int categorySequenceAN,Date lastModifiedDate,int isInStock) {
    this.nameSearch = nameSearch;
    this.brandSearch = brandSearch;
    this.description = description;
    this.level0Id = level0Id;
    this.sku = sku;
    this.isSynchronised = isSynchronised;
    this.lastModifiedDate = lastModifiedDate;
    this.categorySequenceAN=categorySequenceAN;
    this.isInStock=isInStock;
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
