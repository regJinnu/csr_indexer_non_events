package com.gdn.qa.x_search.api.test.utils;

import com.gdn.qa.x_search.api.test.api.services.SolrFieldNames;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SolrHelper {


  @Autowired
  private SearchServiceProperties searchServiceProperties;


  public SolrHelper() {
  }

  public static HttpSolrClient initializeSolr(String url){
    HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(url).build();
    return httpSolrClient;
  }

 public int solrCommit(String collectionName) throws Exception{
    return initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM")).commit(collectionName).getStatus();
  }



 public static SolrQuery initializeSolrQuery(String queryText, String requestHandler,int rows,String fields,String fq){
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(queryText);
    solrQuery.setRequestHandler(requestHandler);
    if(requestHandler.equals("/browse")){
    solrQuery.addFilterQuery("{!collapse field=level0Id sort='merchantScore desc'}");
    solrQuery.addFilterQuery("published:1 AND salesCatalogCategoryCount:[1 TO *]");
    }
    if (fq!=null && !fq.isEmpty())
    {
      solrQuery.addFilterQuery(fq);
    }
    solrQuery.setRows(rows);
    solrQuery.setFields(fields);
    return solrQuery;
  }

  public long getSolrProdCount(String queryText, String requestHandler) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,0,"id","");
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    return queryResponse.getResults().getNumFound();
  }


  public long getSolrProdCountWithFq(String queryText, String requestHandler,String fq) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,0,"id",fq);
    System.out.println("----SolrQuery---"+solrQuery.toString());
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    return queryResponse.getResults().getNumFound();
  }

  public List<SolrResults>  getSolrProd(String queryText, String requestHandler,String field,int rows) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field,"");
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList solrDocuments = queryResponse.getResults();
    DocumentObjectBinder binder = new DocumentObjectBinder();
    List<SolrResults> dataList = binder.getBeans(SolrResults.class, solrDocuments);
    return dataList;
  }

  public List<SolrResults>  getSolrProd(String queryText, String requestHandler,String field,int rows,String fq) throws Exception {
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field,fq);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList solrDocuments = queryResponse.getResults();
    DocumentObjectBinder binder = new DocumentObjectBinder();
    List<SolrResults> dataList = binder.getBeans(SolrResults.class, solrDocuments);
    return dataList;
  }

  public int updateSolrDataForAutomation(String queryText, String requestHandler,String field, int rows,String caseToBeUpdated)
      throws IOException, SolrServerException {

    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrQuery solrQuery = initializeSolrQuery(queryText,requestHandler,rows,field,"");
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList results = queryResponse.getResults();
    UpdateResponse updateResponse = null;
    for (SolrDocument solrDocument:results
         ) {
      Map<String, Object> solrUpdate = new HashMap<>();
      solrUpdate.put(SolrFieldNames.ID, solrDocument.getFieldValue("id"));

      switch (caseToBeUpdated){
        case "oos":
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0" );
          break;
        case "nonOOS":
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "1" );
          break;
        case "reviewAndRating":
          solrUpdate.put(SolrFieldNames.RATING, "23" );
          solrUpdate.put(SolrFieldNames.REVIEW_COUNT,100);
          break;
        case "categoryReindex":
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0" );
          solrUpdate.put(SolrFieldNames.RATING, "40" );
          solrUpdate.put(SolrFieldNames.REVIEW_COUNT,10);
          solrUpdate.put(SolrFieldNames.MERCHANT_COMMISSION_TYPE,"CC");
          solrUpdate.put(SolrFieldNames.MERCHANT_RATING,30.0);
          solrUpdate.put(SolrFieldNames.LOCATION,"Origin-ABC");
          break;
        case "closedStore":
          solrUpdate.put(SolrFieldNames.START_DATE_STORE_CLOSED,1111111111L);
          solrUpdate.put(SolrFieldNames.END_DATE_STORE_CLOSED,22222222L);
          solrUpdate.put(SolrFieldNames.IS_DELAY_SHIPPING,3);
          break;
        case "logisticOption":
          solrUpdate.put(SolrFieldNames.MERCHANT_COMMISSION_TYPE,"TEST_COMM_TYPE");
          solrUpdate.put(SolrFieldNames.LOGISTIC_OPTIONS,"TEST_LOGISTIC_OPTION");
          solrUpdate.put(SolrFieldNames.LOCATION,"TEST_LOCATION");
          break;
        case "price":
          solrUpdate.put(SolrFieldNames.OFFER_PRICE,4545455.45);
          solrUpdate.put(SolrFieldNames.LIST_PRICE,4545455.50);
          break;
        case "offToOn":
          solrUpdate.put(SolrFieldNames.OFF_2_ON,4);
          break;
        case "buyableAndPublished":
          solrUpdate.put(SolrFieldNames.BUYABLE,4);
          solrUpdate.put(SolrFieldNames.PUBLISHED,4);
          break;
        default:
          break;
      }

      SolrInputDocument solrInputDocument = new SolrInputDocument();
      solrInputDocument.addField(SolrFieldNames.ID,solrUpdate.remove(SolrFieldNames.ID));
      for(Map.Entry<String, Object> entry : solrUpdate.entrySet())
        solrInputDocument.addField(entry.getKey(), Collections.singletonMap("set",entry.getValue()));

       updateResponse = httpSolrClient.add(solrInputDocument);
    }

    return updateResponse.getStatus();
  }

  public void deleteSolrDocByQuery(String solrQuery){
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    try {
      httpSolrClient.deleteByQuery(solrQuery);
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addSolrDocument(){
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrInputDocument solrInputDocument = new SolrInputDocument();
    solrInputDocument.addField("id","AAA-60015-00008-00001-PP-3001012");
    solrInputDocument.addField("merchantCode","AAA-60015");
    solrInputDocument.addField("cnc","true");
    try {
      UpdateResponse updateResponse = httpSolrClient.add(solrInputDocument);
      httpSolrClient.commit();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addSolrDocumentForItemChangeEvent(String itemSku,String sku,String productCode,String eventType){
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("SOLR_URL"));
    SolrInputDocument solrInputDocument = new SolrInputDocument();
    solrInputDocument.addField("id",itemSku);
    solrInputDocument.addField("sku",sku);
    solrInputDocument.addField("productCode",productCode);
    if(eventType.equals("itemChangeEvent")){
      solrInputDocument.addField("level0Id",sku);
      solrInputDocument.addField("level1Id",sku);
    }
    else{
      solrInputDocument.addField("level0Id",productCode);
      solrInputDocument.addField("level1Id",productCode);
    }
    try {
      UpdateResponse updateResponse = httpSolrClient.add(solrInputDocument);
      httpSolrClient.commit();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
