package com.gdn.qa.x_search.api.test.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.data.MongoData;
import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongoHelper {

  MongoData mongoData;


  public MongoCollection<Document> initializeDatabase(String collectionName){

    ServerAddress serverAddress = new ServerAddress("mongodb-01.uatb.lokal",27017);
    MongoCredential mongoCredential = MongoCredential.createCredential("search","x_search","search".toCharArray());
    MongoClient mongoClient=new MongoClient(serverAddress, new ArrayList<MongoCredential>() {{ add(mongoCredential); }});
    MongoDatabase mongoDatabase=mongoClient.getDatabase("x_search");
    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
    return collection;
  }

  public long countOfRecordsInCollection(String collectionName){

    MongoCollection<Document> collection = initializeDatabase(collectionName);
    return collection.count();
  }

  public void getMongoDocumentByQuery(String collectionName,String queryField,String value){

    MongoCollection<Document> collection = initializeDatabase(collectionName);
    Document query = new Document(queryField,value);
    String pattern = ".*" + query.getString(queryField) + ".*";
    FindIterable<Document> mongoDoc = collection.find(regex(queryField,pattern,"i"));
    for (Document doc : mongoDoc){
      JSONObject jsonObject = new JSONObject(doc);
      Gson gson = new Gson();
      mongoData = gson.fromJson(jsonObject.toString(), MongoData.class);
      System.out.println(mongoData.getNAME());
      System.out.println(mongoData.getVALUE());
    }
  }

  public void updateMongo(String collectionName,String queryField,String queryValue,String updateField,String updateValue){
    MongoCollection<Document> collection = initializeDatabase(collectionName);
    collection.updateOne(eq(queryField,queryValue),combine(set(updateField,updateValue)));
  }

  public static void main(String args[]){

    MongoHelper mongoHelper = new MongoHelper();
    System.out.println("Count from DB:"+mongoHelper.countOfRecordsInCollection("config_list"));
    System.out.println("-------Before Update--------");
    mongoHelper.getMongoDocumentByQuery("config_list", "NAME", "product.popularity.max.product.id.per.request");
    mongoHelper.updateMongo("config_list","NAME","product.popularity.max.product.id.per.request","VALUE","2000");
    System.out.println("-------After Update--------");
    mongoHelper.getMongoDocumentByQuery("config_list", "NAME", "product.popularity.max.product.id.per.request");
  }

}
