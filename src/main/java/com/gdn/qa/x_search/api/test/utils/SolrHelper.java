package com.gdn.qa.x_search.api.test.utils;

import com.gdn.qa.x_search.api.test.api.services.SolrFieldNames;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import java.io.IOException;
import java.util.*;

import org.apache.solr.common.SolrInputDocument;


public class SolrHelper {

  public static final String SOLR_URL = "http://seoulsolr6-01.uatb.lokal:8983/solr/productCollectionNew";
  public static final String SOLR_URL_NO_PARAM = "http://seoulsolr6-01.uatb.lokal:8983/solr";

  public SolrHelper() {
  }

  public static HttpSolrClient initializeSolr(String url){
    HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(url).build();
    return httpSolrClient;
  }

  public static int solrCommit(String collectionName) throws Exception{
    return initializeSolr(SOLR_URL_NO_PARAM).commit(collectionName).getStatus();
  }

  public static SolrQuery initializeSolrQuery(String queryText, String requestHandler,int rows,String fields){
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(queryText);
    solrQuery.setRequestHandler(requestHandler);

    if(requestHandler.equals("/browse")){
    solrQuery.addFilterQuery("{!collapse field=level0Id sort='merchantScore desc'}");
    solrQuery.addFilterQuery("published:1 AND salesCatalogCategoryCount:[1 TO *]");
    }

    solrQuery.setRows(rows);
    solrQuery.setFields(fields);
    return solrQuery;
  }


  public static long getSolrProdCount(String queryText, String requestHandler) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(SOLR_URL);
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,0,"id");
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    return queryResponse.getResults().getNumFound();
  }

  public static List<SolrResults> getSolrProd(String queryText, String requestHandler,String field,int rows) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(SOLR_URL);
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList solrDocuments = queryResponse.getResults();
    DocumentObjectBinder binder = new DocumentObjectBinder();
    List<SolrResults> dataList = binder.getBeans(SolrResults.class, solrDocuments);
    return dataList;
  }

  public static int updateSolrDataForAutomation(String queryText, String requestHandler,String field, int rows)
      throws IOException, SolrServerException {

    HttpSolrClient httpSolrClient = initializeSolr(SOLR_URL);
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocument solrDocument = queryResponse.getResults().get(0);

    Map<String, Object> solrUpdate = new HashMap<>();
    solrUpdate.put(SolrFieldNames.ID, solrDocument.getFieldValue("id"));
    solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0" );

    SolrInputDocument solrInputDocument = new SolrInputDocument();

    for(Map.Entry<String, Object> entry : solrUpdate.entrySet())
    solrInputDocument.addField(entry.getKey(),entry.getValue());

    UpdateResponse updateResponse = httpSolrClient.add(solrInputDocument);

    return updateResponse.getStatus();
  }

/*  public static void main(String args[]){

    try {
      int status = updateSolrDataForAutomation("id:TH7-15791-00118-00001","/select","id",1);
      System.out.println("Status:"+status);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SolrServerException e) {
      e.printStackTrace();
    }

  }*/

}
