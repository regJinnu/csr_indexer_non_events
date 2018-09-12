package com.gdn.qa.x_search.api.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author kumar on 04/09/18
 * @project X-search
 */

public class ProcessShellCommands {

  private static final String SCRIPT_PATH = "shellScripts/";

  public static String getShellScriptActualOutput(String Filename,String prodType) throws Exception{
    ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+Filename,prodType);
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

  public static void main(String[] args) {
    try {
      String output = ProcessShellCommands.getShellScriptActualOutput(
          "verifyFacebookRecords.sh", "MTA-0309046");

      System.out.println("Output:"+output);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
