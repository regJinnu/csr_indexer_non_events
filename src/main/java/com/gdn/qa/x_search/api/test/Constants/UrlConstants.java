package com.gdn.qa.x_search.api.test.Constants;

public interface UrlConstants {

  String fileSeparator = System.getProperty("file.separator");
  String userDir = System.getProperty("user.dir");
  String SOLR_DEFAULT_COLLECTION = "productCollectionNew";
  String SELECT_HANDLER = "/select";
  int MONGO_SERVER_PORT = 27017;
  String DOWNLOAD_GEN_FULL_FEED_FOR_FACEBOOK="/opt/output/facebook/1/";
  String LOCAL_STORAGE_LOCATION="src" + fileSeparator+ "test" + fileSeparator + "resources" + fileSeparator + "FacebookDownloadedFiles";
  String LOCAL_STORAGE_PATH = userDir + fileSeparator + LOCAL_STORAGE_LOCATION;
  int SERVER_PORT=2209;
  String SERVER_USERNAME="linux1-user";
  String SERVER_PASSWORD="blibliqaisthebest";
  String FB_UNSYNC="TOQ-15779-02169-00001,TOQ-15779-02169,Kamera2618 Namanya Diganti Sama Argo,kamera2618 namanya diganti sama argo,new,http://www.blibli.com/p/kamera2618-namanya-diganti-sama-argo/ps--TOQ-15779-02169,http://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium//398/3d1-racing-_kamera2618_full01.jpg,in stock,200000256,200000256,3d1 racing,blibli://product/TOQ-15779-02169,blibli://product/TOQ-15779-02169,Kamera,Kamera,Kamera DSLR,TOQ-15779-02169,1034231507,Blibli App - iOS,blibli.mobile.commerce,Blibli App - Android";
  String FB_SYNC = "FB1-0000001-00008-00001,FB1-0000001-00008,Facebook test multi merchant prod,facebook test multi merchant prod,new,http://www.blibli.com/p/facebook-test-multi-merchant-prod/pc--FBT-0000001,http://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/samsung_samsung-galaxy-note-5-gold-smartphone_full04.jpg,in stock,150000,100000,FB Brand 1,blibli://product/FBT-0000001,blibli://product/FBT-0000001,Handphone & Tablet,Handphone,Android,FB1-0000001-00008,1034231507,Blibli App - iOS,blibli.mobile.commerce,Blibli App - Android";
  String FILE_PATH = "/api/search/";
  String X_SEARCH_SERVICE="searchservice";
  String PRODUCT_LEVEL0ID= "level0Id";
  String PRODUCT_LEVEL1ID="level1Id";
  String REMOTE_PDF_FILE_LOCATION = "/data/siva/asset/nginx/html/seoul-uata/sitemap/";
  String PDF_FILE_NAME = "products-1.zip";
  String PDF_SERVER_IP = "172.18.69.33";
  int PDF_PORT = 21;
  String PDF_USERNAME = "jenkins";
  String PDF_PASSWORD = "jenkins";
  String PDF_EXTRACTION_PASSWORD = "Passw0rd";
  String SOLR_DEFAULT_COLLECTION_CNC= "cncCollectionSharded";
  String SOLR_DEFAULT_COLLECTION_O2O="o2oCollection";
  String DANGLING_JOB_PRODUCTSKU = "AAA-60015-00008";
  String DANGLING_JOB_ITEMSKU = "AAA-60015-00008-00001";
  String DANGLING_JOB_ITEMSKU_2="AAA-60015-00008-00002";
  String DANGLING_JOB_PRODUCTCODE = "MTA-66666";
  String PRISTINE_ID="PRI-0000-0001";
  String EVENT_ID="8fa49fb8-ffcc-42d7-8b01-7df55dd3ec51";
  String ACCOUNT_ID = "blibli-seoul";
  String USER_ID = "1916329177.U.9407511781281912.1549263794";
  String SESSION_ID = "723337755.S.7656656614297553.1576727494";
  String PAGE_TYPE = "home";
  String DEVICE_TYPE = "desktop";
  String DEVICE = "windows";
  String BROWSER = "chrome";
  String BROWSER_VERSION = "79.0";
}


