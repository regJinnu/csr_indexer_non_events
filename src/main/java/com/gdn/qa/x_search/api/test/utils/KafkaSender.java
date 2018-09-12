package com.gdn.qa.x_search.api.test.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


/**
 * @author kumar on 01/08/18
 * @project X-search
 */

@Service
@Slf4j
public class KafkaSender {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public void send(String kafkaTopic, String message) {
    log.warn("---sending payload='{}' with topic='{}'",message,kafkaTopic);
    kafkaTemplate.send(kafkaTopic, message);
  }


}
