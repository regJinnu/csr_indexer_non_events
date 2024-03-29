package com.gdn.qa.x_search.api.test.utils;

import com.gdn.qa.x_search.api.test.api.services.SolrFieldNames;
import com.gdn.qa.x_search.api.test.models.SolrResults;
import com.gdn.qa.x_search.api.test.properties.SearchServiceProperties;
import lombok.NoArgsConstructor;
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
import scala.annotation.meta.field;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class SolrHelper {

  @Autowired
  private SearchServiceProperties searchServiceProperties;

  public static HttpSolrClient initializeSolr(String url) {
    return new HttpSolrClient.Builder(url).build();
  }

  public int solrCommit(String collectionName) throws Exception {
    return initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM")).commit(collectionName)
        .getStatus();
  }


  public static SolrQuery initializeSolrQuery(String queryText,
      String requestHandler,
      int rows,
      String fields,
      List<String> fqs) {
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(queryText);
    solrQuery.setRequestHandler(requestHandler);
    if (requestHandler.equals("/browse")) {
      solrQuery.addFilterQuery("{!collapse field=level0Id sort='buyboxScore desc'}");
      solrQuery.addFilterQuery("published:1 AND salesCatalogCategoryCount:[1 TO *]");
    }
    if (fqs.size() > 0) {
      for (String fq : fqs) {
        solrQuery.addFilterQuery(fq);
      }
    }
    solrQuery.setRows(rows);
    solrQuery.setFields(fields);
    return solrQuery;
  }

  public long getSolrProdCount(String queryText,
      String requestHandler,
      String collectionName,
      List<String> fqs) throws Exception {
    HttpSolrClient httpSolrClient =
        initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM") + "/" + collectionName);
    SolrQuery solrQuery = initializeSolrQuery(queryText, requestHandler, 0, "id", fqs);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    return queryResponse.getResults().getNumFound();
  }

  public List<SolrResults> getSolrProd(String queryText,
      String requestHandler,
      String field,
      int rows,
      List<String> fq,
      String collectionName) throws Exception {
    String url = searchServiceProperties.get("SOLR_URL_NO_PARAM") + "/" + collectionName;
    HttpSolrClient httpSolrClient = initializeSolr(url);
    SolrQuery solrQuery = initializeSolrQuery(queryText, requestHandler, rows, field, fq);
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList solrDocuments = queryResponse.getResults();
    DocumentObjectBinder binder = new DocumentObjectBinder();
    return binder.getBeans(SolrResults.class, solrDocuments);
  }

  public int updateSolrDataForAutomation(String queryText,
      String requestHandler,
      String field,
      int rows,
      String caseToBeUpdated,
      String collectionName) throws IOException, SolrServerException {
    HttpSolrClient httpSolrClient =
        initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM") + "/" + collectionName);
    SolrQuery solrQuery =
        initializeSolrQuery(queryText, requestHandler, rows, field, Collections.emptyList());
    QueryResponse queryResponse = httpSolrClient.query(solrQuery);
    SolrDocumentList results = queryResponse.getResults();
    UpdateResponse updateResponse = null;
    for (SolrDocument solrDocument : results) {
      Map<String, Object> solrUpdate = new HashMap<>();
      solrUpdate.put(SolrFieldNames.ID, solrDocument.getFieldValue("id"));

      switch (caseToBeUpdated) {
        case "oos":
          solrUpdate.put(SolrFieldNames.STOCK_LOCATION, null);
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "0");
          break;
        case "nonOOS":
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "1");
          break;
        case "reviewAndRating":
          solrUpdate.put(SolrFieldNames.RATING, "23");
          solrUpdate.put(SolrFieldNames.REVIEW_COUNT, 100);
          break;
        case "categoryReindex":
          solrUpdate.put(SolrFieldNames.STOCK_LOCATION, null);
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "5");
          solrUpdate.put(SolrFieldNames.RATING, "40");
          solrUpdate.put(SolrFieldNames.REVIEW_COUNT, 10);
          solrUpdate.put(SolrFieldNames.MERCHANT_COMMISSION_TYPE, "TC");
          solrUpdate.put(SolrFieldNames.MERCHANT_RATING, 30.0);
          solrUpdate.put(SolrFieldNames.LOCATION, "Origin-ABC");
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, "1234");
          break;
        case "closedStore":
          solrUpdate.put(SolrFieldNames.START_DATE_STORE_CLOSED, 1111111111L);
          solrUpdate.put(SolrFieldNames.END_DATE_STORE_CLOSED, 22222222L);
          solrUpdate.put(SolrFieldNames.IS_DELAY_SHIPPING, 3);
          break;
        case "logisticOption":
          solrUpdate.put(SolrFieldNames.MERCHANT_COMMISSION_TYPE, "TEST_COMM_TYPE");
          solrUpdate.put(SolrFieldNames.LOGISTIC_OPTIONS, "TEST_LOGISTIC_OPTION");
          solrUpdate.put(SolrFieldNames.LOCATION, "TEST_LOCATION");
          break;
        case "price":
          solrUpdate.put(SolrFieldNames.OFFER_PRICE, 4545455.45);
          solrUpdate.put(SolrFieldNames.LIST_PRICE, 4545455.50);
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1234);
          break;
        case "offToOn":
          solrUpdate.put(SolrFieldNames.OFF_2_ON, 4);
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1234);
          break;
        case "buyableAndPublished":
          solrUpdate.put(SolrFieldNames.BUYABLE, 4);
          solrUpdate.put(SolrFieldNames.PUBLISHED, 4);
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1234);
          break;
        case "defCncOfferPrice":
          solrUpdate.put(SolrFieldNames.OFFER_PRICE, 3000);
          solrUpdate.put(SolrFieldNames.LIST_PRICE, 3000);
          solrUpdate.put(SolrFieldNames.SALE_PRICE, 3000);
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1234);
          break;
        case "tradeInEligible":
          solrUpdate.put(SolrFieldNames.TRADE_IN_ELIGIBLE, false);
          break;
        case "tradeInInEligible":
          solrUpdate.put(SolrFieldNames.TRADE_IN_ELIGIBLE, true);
          break;
        case "salesCatalogCategoryIdDescHierarchy":
          solrUpdate.put(SolrFieldNames.SALES_CATALOG_CATEGORY_ID_DESC_HIERARCHY,
              "VA-1000003;Vandana testing category TEST");
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1100);
          break;
        case "salesCatalogCategoryIdDescHierarchyCNC":
          solrUpdate.put(SolrFieldNames.SALES_CATALOG_CATEGORY_ID_DESC_HIERARCHY,
              "TEST CNC Category");
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1001);
          break;
        case "buyBox":
          solrUpdate.put(SolrFieldNames.LAST_UPDATED_TIME, 1234);
          break;
        case "inventoryChange":
          solrUpdate.put(SolrFieldNames.All_LOCATION, null);
          solrUpdate.put(SolrFieldNames.STOCK_LOCATION, null);
          break;
        case "randomInStockValue":
          solrUpdate.put(SolrFieldNames.STOCK_LOCATION, null);
          solrUpdate.put(SolrFieldNames.IS_IN_STOCK, "5");
          break;
        case "merchantVoucherCount":
          solrUpdate.put(SolrFieldNames.VOUCHER_COUNT, 100);
          break;
        case "officialStore":
          solrUpdate.put(SolrFieldNames.OFFICIAL, false);
          solrUpdate.put(SolrFieldNames.BRAND_CATALOG, "abc");
          solrUpdate.put(SolrFieldNames.STORE_CATALOG, "abc");
          break;
        default:
          break;
      }

      SolrInputDocument solrInputDocument = new SolrInputDocument();
      solrInputDocument.addField(SolrFieldNames.ID, solrUpdate.remove(SolrFieldNames.ID));
      for (Map.Entry<String, Object> entry : solrUpdate.entrySet())
        solrInputDocument.addField(entry.getKey(),
            Collections.singletonMap("set", entry.getValue()));
      updateResponse = httpSolrClient.add(solrInputDocument);
    }
    return updateResponse.getStatus();
  }

  public void deleteSolrDocByQuery(String solrQuery, String collectionName) {
    HttpSolrClient httpSolrClient =
        initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM") + "/" + collectionName);
    try {
      httpSolrClient.deleteByQuery(solrQuery);
    } catch (SolrServerException | IOException e) {
      e.printStackTrace();
    }
  }

  public void addSolrDocument() {
    HttpSolrClient httpSolrClient = initializeSolr(searchServiceProperties.get("CNC_SOLR_URL"));
    SolrInputDocument solrInputDocument = new SolrInputDocument();
    solrInputDocument.addField("id", "AAA-60015-00008-00001-PP-3001012");
    solrInputDocument.addField("merchantCode", "AAA-60015");
    solrInputDocument.addField("cnc", "true");
    try {
      httpSolrClient.add(solrInputDocument);
      httpSolrClient.commit();
    } catch (SolrServerException | IOException e) {
      e.printStackTrace();
    }
  }

  public void addSolrDocumentForItemChangeEvent(String itemSku,
      String sku,
      String productCode,
      String eventType,
      String collectionName) {

    HttpSolrClient httpSolrClient =
        initializeSolr(searchServiceProperties.get("SOLR_URL_NO_PARAM") + "/" + collectionName);

    SolrInputDocument solrInputDocument = new SolrInputDocument();
    solrInputDocument.addField("id", itemSku);
    solrInputDocument.addField("sku", sku);
    solrInputDocument.addField("productCode", productCode);
    solrInputDocument.addField("published",0);
    solrInputDocument.addField("buyable",0);
    solrInputDocument.addField("off2On",0);

    if (eventType.equals("itemChangeEvent")) {
      solrInputDocument.addField("level0Id", sku);
      solrInputDocument.addField("level1Id", sku);
    } else {
      solrInputDocument.addField("level0Id", productCode);
      solrInputDocument.addField("level1Id", productCode);
    }
    try {
      httpSolrClient.add(solrInputDocument);
      httpSolrClient.commit();
    } catch (SolrServerException | IOException e) {
      e.printStackTrace();
    }
  }
}
