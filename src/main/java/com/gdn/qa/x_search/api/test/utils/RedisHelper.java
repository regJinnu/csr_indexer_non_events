package com.gdn.qa.x_search.api.test.utils;

import redis.clients.jedis.Jedis;

public class RedisHelper {

  private static Jedis jedis;

  private static void init(String redisHost,String redisPort) {
    jedis = new Jedis(redisHost,Integer.parseInt(redisPort));
    System.out.println("Connection to server sucessfully");
  }

  public static void deleteKey(String redisHost,String redisPort,String key){
    init(redisHost,redisPort);
    jedis.del(key);
  }

  public static void deleteAll(String redisHost,String redisPort){
    init(redisHost,redisPort);
    jedis.flushAll();
  }

  public static void redisHealthCheck(String redisHost,String redisPort){
    init(redisHost,redisPort);
    System.out.println("Server is running: "+jedis.ping());
  }

  public static String getValue(String redisHost,String redisPort,String key){
    init(redisHost,redisPort);
    return jedis.get(key);
  }

}
