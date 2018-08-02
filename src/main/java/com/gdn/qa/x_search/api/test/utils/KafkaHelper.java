package com.gdn.qa.x_search.api.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.data.OOSEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
        kafkaSender.send("com.gdn.x.inventory.stock.oos.event",objectMapper.writeValueAsString(oosEvent));
      else if (type.equals("nonOOS"))
        kafkaSender.send("com.gdn.x.inventory.stock.non.oos.event",objectMapper.writeValueAsString(oosEvent));

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

}
