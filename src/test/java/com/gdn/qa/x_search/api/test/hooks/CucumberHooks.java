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
          .append("product_id" , "MTA-0305736")
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
}