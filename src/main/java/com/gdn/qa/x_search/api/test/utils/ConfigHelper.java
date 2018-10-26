package com.gdn.qa.x_search.api.test.utils;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.REDIS_HOST;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.REDIS_PORT;

/**
 * @author kumar on 17/09/18
 * @project X-search
 */
public class ConfigHelper {

  MongoHelper mongoHelper = new MongoHelper();

  public void setForceStop(String flag) {
    mongoHelper.updateMongo("config_list","NAME","force.stop.solr.updates","VALUE",flag);
    mongoHelper.updateMongo("config_list","NAME","process.delta.during.reindex","VALUE","false");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RedisHelper.deleteAll(REDIS_HOST,REDIS_PORT);
  }

  public void addToWhitelist(String service){

    mongoHelper.updateMongo("config_list","NAME","whitelist.events","VALUE",service);
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RedisHelper.deleteAll(REDIS_HOST,REDIS_PORT);
  }

  public void setPVOFF(String flag) {
    if (flag.equals(true)) {
      mongoHelper.updateMongo("config_list", "NAME", "product.level.id", "VALUE", "level1Id");
      mongoHelper.updateMongo("config_list", "NAME", "service.product.level.id", "VALUE", "level1Id");
    } else {
      mongoHelper.updateMongo("config_list", "NAME", "product.level.id", "VALUE", "level0Id");
      mongoHelper.updateMongo("config_list", "NAME", "service.product.level.id", "VALUE", "level0Id");
    }
  }

}
