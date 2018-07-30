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

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_URL;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.SOLR_URL_NO_PARAM;


public class SolrHelper {

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

  public static int updateSolrDataForAutomation(String queryText, String requestHandler,String field, int rows,String caseToBeUpdated)
      throws IOException, SolrServerException {

    HttpSolrClient httpSolrClient = initializeSolr(SOLR_URL);
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocument solrDocument = queryResponse.getResults().get(0);

    Map<String, Object> solrUpdate = new HashMap<>();
    solrUpdate.put(SolrFieldNames.ID, solrDocument.getFieldValue("id"));

    switch (caseToBeUpdated){
      case "oos":
        solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0" );
        break;
      case "reviewAndRating":
        solrUpdate.put(SolrFieldNames.RATING, "0" );
        solrUpdate.put(SolrFieldNames.REVIEW_COUNT,0);
        break;
      case "categoryReindex":
        solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0" );
        solrUpdate.put(SolrFieldNames.RATING, "4" );
        solrUpdate.put(SolrFieldNames.REVIEW_COUNT,10);
        solrUpdate.put(SolrFieldNames.MERCHANT_COMMISSION_TYPE,"CC");
        solrUpdate.put(SolrFieldNames.MERCHANT_RATING,3.0);
        solrUpdate.put(SolrFieldNames.LOCATION,"Origin-ABC");
        break;
    }

    SolrInputDocument solrInputDocument = new SolrInputDocument();
    solrInputDocument.addField(SolrFieldNames.ID,solrUpdate.remove(SolrFieldNames.ID));
    for(Map.Entry<String, Object> entry : solrUpdate.entrySet())
    solrInputDocument.addField(entry.getKey(),Collections.singletonMap("set",entry.getValue()));

    UpdateResponse updateResponse = httpSolrClient.add(solrInputDocument);

    return updateResponse.getStatus();
  }

  public static void deleteSolrDocByQuery(String solrQuery){
    HttpSolrClient httpSolrClient = initializeSolr(SOLR_URL);
    try {
      httpSolrClient.deleteByQuery(solrQuery);
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
