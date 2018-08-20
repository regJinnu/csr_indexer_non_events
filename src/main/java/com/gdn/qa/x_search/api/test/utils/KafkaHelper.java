package com.gdn.qa.x_search.api.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.data.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author kumar on 01/08/18
 * @project X-search
 */
@Slf4j
@Service
public class KafkaHelper {

@Autowired
  private KafkaSender kafkaSender;

@Autowired
private ApplicationContext applicationContext;


  private ObjectMapper objectMapper = new ObjectMapper();

  public void publishOOSEvent(String level2Id,String level2MerchantCode,String type){

    OOSEvent oosEvent = OOSEvent.builder().
        level2Id(level2Id).
        level2MerchantCode(level2MerchantCode).
        storeId("10001").
        uniqueId(level2Id).
        timestamp(System.currentTimeMillis()).build();

    try {

      if(type.equals("oos"))
        kafkaSender.send("com.gdn.x.inventory.stock.oos.event",
            objectMapper.writeValueAsString(oosEvent));
      else if (type.equals("nonOOS"))
        kafkaSender.send("com.gdn.x.inventory.stock.non.oos.event",
            objectMapper.writeValueAsString(oosEvent));

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishStoreClosedEvent(String businessPartnerCode,boolean isDelayShipping) {

    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(2);

    BPStoreClosedEvent bpStoreClosedEvent = BPStoreClosedEvent.builder().
        timestamp(System.currentTimeMillis()).
        businessPartnerCode(businessPartnerCode).
        startDate(dtStart.getMillis()).
        endDate(dtEnd.getMillis()).
        publishType("EXECUTE_START").
        startDateWithBufferClosingStoreDay(dtEnd.getMillis()).
        delayShipping(isDelayShipping).build();

    try {
      kafkaSender.send("com.gdn.x.bpservice.store.closing.publish",objectMapper.writeValueAsString(bpStoreClosedEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishBPprofileFieldUpdateEvent(String businessPartnerCode){

    CompanyV0 companyV0 = CompanyV0.builder().
        changedFields(Collections.singleton(CompanyChangeFields.CNC_ACTIVATED)).
        cncActivated(false).
        build();

    BPprofileUpdateFields bPprofileUpdateFields = BPprofileUpdateFields.builder().
        timestamp(System.currentTimeMillis()).
        businessPartnerCode(businessPartnerCode).
        company(companyV0).
        build();

    try {
      kafkaSender.send("com.gdn.x.businesspartner.profile.update.fields",
          objectMapper.writeValueAsString(bPprofileUpdateFields));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishLogisticOptionChange(String merchant,String logisticOption,String commType){

    ArrayList<String> commissionType = new ArrayList<>();
    commissionType.add(commType);

    ArrayList<String> merchantList = new ArrayList<>();
    merchantList.add(merchant);

    ArrayList<String> logisticProdCodeList = new ArrayList<>();
    logisticProdCodeList.add(logisticOption);

    LogisticOptionChange logisticOptionChange = LogisticOptionChange.builder().
        timestamp(System.currentTimeMillis()).
        commissionTypeList(commissionType).
        merchantIdList(merchantList).
        logisticProductCodeList(logisticProdCodeList).
        logisticOptionCode("EXPRESS").
        build();

    try {
      kafkaSender.send("com.gdn.x.shipping.domain.logistic.option.change.event",
          objectMapper.writeValueAsString(logisticOptionChange));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishLogisticProductOriginsChangeEvent(){
    List<String> originList = new ArrayList<>(Arrays.asList("Origin-Jakarta","Origin-Karawang","Origin-Bekasi","Origin-Depok",
        "Origin-Bandung","Origin-Bogor","Origin-Tangerang","Origin-Semarang","Origin-Surabaya","Origin-Medan",
        "Origin-Denpasar","Origin-Kuta","Origin-Makasar","Origin-Yogyakarta","Origin-Warungku Angke",
        "Origin-Solo","Origin-Sidoardjo","Origin-Malang","Origin-Padang","Origin-Palembang"));


  }

}
