package com.gdn.qa.x_search.api.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.LOCAL_STORAGE_LOCATION;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.LOCAL_STORAGE_PATH;

/**
 * @author kumar on 04/09/18
 * @project X-search
 */

public class ProcessShellCommands {

  private static final String SCRIPT_PATH = "shellScripts/";

  public static String getShellScriptActualOutput(String Filename,String prodType) throws Exception{
    ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+Filename,LOCAL_STORAGE_PATH,prodType);
    Process process=processBuilder.start();
    int exitValue=process.waitFor();
    String line;
    if (exitValue != 0) {
      new BufferedInputStream(process.getErrorStream());
      throw new RuntimeException("Unable to get data from File!");
    }
    else {
      BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
      if((line = reader.readLine())!= null)
        return line;
    }
    return "abcd";
  }

}
