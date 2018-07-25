package com.gdn.qa.x_search.api.test.utils;


import com.gdn.qa.x_search.api.test.data.MongoData;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.MONGO_SERVER_ADDRESS;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongoHelper {

  MongoData mongoData;


  public MongoCollection<Document> initializeDatabase(String collectionName){

    ServerAddress serverAddress = new ServerAddress(MONGO_SERVER_ADDRESS,27017);
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

  public FindIterable<Document> getMongoDocumentByQuery(String collectionName,String queryField,String value){

    MongoCollection<Document> collection = initializeDatabase(collectionName);
    Document query = new Document(queryField,value);
    String pattern = ".*" + query.getString(queryField) + ".*";
    FindIterable<Document> mongoDoc = collection.find(regex(queryField,pattern,"i"));
    return mongoDoc;
/*    for (Document doc : mongoDoc){
      JSONObject jsonObject = new JSONObject(doc);
      Gson gson = new Gson();
      mongoData = gson.fromJson(jsonObject.toString(), MongoData.class);
      System.out.println(mongoData.getNAME());
      System.out.println(mongoData.getVALUE());
    }*/
  }

  public void updateMongo(String collectionName,String queryField,String queryValue,String updateField,String updateValue){
    MongoCollection<Document> collection = initializeDatabase(collectionName);
    collection.updateOne(eq(queryField,queryValue),combine(set(updateField,updateValue)));
  }

  public void insertInMongo(String collectionName,Document document){
    MongoCollection<Document> collection = initializeDatabase(collectionName);
    collection.insertOne(document);
  }

  public void deleteFromMongo(String collectionName,String queryField,String queryValue){
    MongoCollection<Document> collection = initializeDatabase(collectionName);
    collection.deleteOne(eq(queryField,queryValue));
  }

}
