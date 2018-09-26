package com.gdn.qa.x_search.api.test.hooks;

import com.gdn.qa.x_search.api.test.CucumberStepsDefinition;
import com.gdn.qa.x_search.api.test.utils.MongoHelper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@CucumberStepsDefinition
public class CucumberHooks {

  MongoHelper mongoHelper = new MongoHelper();
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @After
  public void afterRun() {
    //it will executed after scenario run

 //   mongoHelper.deleteFromMongo("synonyms_list","KEY","testingapi");
 //   mongoHelper.deleteFromMongo("keyword_boost_keyword_list","keyword","testingapi");
  }

  @Before("@FindSynonymnByKey")
  public void beforeSynonymRun() {

    try {
      Date date = dateFormat.parse("2017-09-19T05:19:45.468Z");

      Document synDoc = new Document("_class","com.gdn.x.search.entity.SynonymsEntity")
          .append("KEY" , "testingapi")
          .append("SYNONYMS" , "test1,test2")
          .append("GROUP_NAME" , "synonyms_gdn")
          .append("SYNC" , "1")
          .append("version" ,8)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("STORE_ID" , "10001")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("synonyms_list",synDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }


  @Before("@LogisticOriginChangeEvent")
  public void beforeLogisticOriginChangeEvent(){
    mongoHelper.deleteAllFromMongo("product_atomic_reindex_queue");
    mongoHelper.deleteFromMongo("product_atomic_reindex_data_candidate","solrfieldName" , "location");
  }


  @Before("@FindBoostedKeyword")
  public void beforeBoostedKeyword(){

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");

      Document keywordBoostDoc = new Document("_class" , "com.gdn.x.search.entity.KeywordBoostProduct")
          .append("keyword" , "testingapi")
          .append("products" , "MTA-0309256")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("STORE_ID" , "10001")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list",keywordBoostDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Before("@ProcessFailedIds")
  public void beforeProcessFailedIds(){

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");
      Document failedIdsDoc = new Document("_class" , "com.gdn.x.search.entity.SolrFailedIds")
          .append("product_id" , "MTA-0309046")
          .append("id_type" , "PRODUCT_CODE")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("solr_failed_ids",failedIdsDoc);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Before("@Multidelete")
  public void beforeMultiDelete(){

    Date date = null;
    try {
      date = dateFormat.parse("2017-09-19T05:19:45.468Z");
      Document multiDeleteKeywordBoostDoc1 = new Document("_class" , "com.gdn.x.search.entity.KeywordBoostProduct")
          .append("_id","98765")
          .append("keyword" , "automation")
          .append("products" , "MTA-0309256,MTA-0306144")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("STORE_ID" , "10001")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list",multiDeleteKeywordBoostDoc1);

      Document multiDeleteKeywordBoostDoc2 = new Document("_class" , "com.gdn.x.search.entity.KeywordBoostProduct")
          .append("_id","43210")
          .append("keyword" , "testing")
          .append("products" , "MTA-0309256,MTA-0306144")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("STORE_ID" , "10001")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("keyword_boost_keyword_list",multiDeleteKeywordBoostDoc2);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Before("@ProcessDeltaStoredEvents")
  public void beforeProcessingStoredEvents(){

    Date date = null;
    try {
      date = dateFormat.parse("2018-07-30T11:45:39.235Z");

      Document storedDeltaDoc1 = new Document("_class" , "com.gdn.x.search.entity.EventIndexingEntity")
          .append("code" , "MTA-0309046")
          .append("type" , "productCode")
          .append("processHost", "1")
          .append("isFailed", "0")
          .append("eventType" , "ALL")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("MARK_FOR_DELETE" , false);

      Document storedDeltaDoc2 = new Document("_class" , "com.gdn.x.search.entity.EventIndexingEntity")
          .append("code" , "TH7-15791-00118-00001")
          .append("type" , "id")
          .append("processHost", "1")
          .append("isFailed", "0")
          .append("eventType" , "LOCATION_AND_INVENTORY_SERVICE")
          .append("version" , 0)
          .append("CREATED_DATE" , date)
          .append("CREATED_BY" , "user-dev-src")
          .append("UPDATED_DATE" , date)
          .append("UPDATED_BY" , "user-dev-src")
          .append("MARK_FOR_DELETE" , false);

      mongoHelper.insertInMongo("indexing_list_new",storedDeltaDoc1);
      mongoHelper.insertInMongo("indexing_list_new",storedDeltaDoc2);

    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Before("@ItemChangeDeleteEvent")
  public void beforeItemChangeDeleteEvent(){
    mongoHelper.deleteFromMongo("deleted_products","_id","AAA-60015-00008");
  }

  @Before("@ProductChangeDeleteEvent")
  public void beforeProductChangeDeleteEvent(){
    mongoHelper.deleteFromMongo("deleted_products","_id","MTA-66666");
  }

  @Before("@AddPlaceholderRules")
  public void beforeAddingDeleteDuplicatePlaceholder(){
    System.out.println("you are in cucumberhook");
    mongoHelper.deleteFromMongo("placeholder_im_rule","_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@AddFlight")
  public void beforeAddingDeleteDuplicateFlight(){
    System.out.println("you are in cucumberhook");
    mongoHelper.deleteFromMongo("flight_dictionary","_id","5b0649b782ce7044d664bcc6");
  }

  @Before("@AddSearchRule")
  public void beforeAddingDeleteDuplicateSearchRule(){
    mongoHelper.deleteFromMongo("search_rule","_id", "5b0649b782ce7044d664bcc6");
  }

  @Before("@AddTrainMapping")
  public void beforeAddingDeleteDuplicateTrainMapping(){
    mongoHelper.deleteFromMongo("train_dictionary","_id", "5b0649b782ce7044d664bcc6");
  }

}