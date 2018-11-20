package com.gdn.qa.x_search.api.test.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author kumar on 03/10/18
 * @project X-search
 */

@Service
public class RedisHelperNew {

  @Autowired
  @Qualifier("searchConfigTemplate")
  private RedisTemplate redisTemplate;

  public void delete(String key){
    redisTemplate.opsForValue().getOperations().delete(key);
  }

  public Set<String> getAllKeys(String pattern){
    return redisTemplate.keys(pattern);
  }

  public void deleteAll(){
    redisTemplate.delete(getAllKeys("*"));
  }

  public String getValue(String key){
    return redisTemplate.opsForValue().get(key).toString();
  }
}
