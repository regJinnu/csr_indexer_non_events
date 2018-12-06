package com.gdn.qa.x_search.api.test.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.*;

/**
 * @author kumar on 23/11/18
 * @project X-search
 */

@Slf4j
public class FTPHelper {


  private static int port = PDF_PORT;
  private static String server_ip = PDF_SERVER_IP;
  private static String user =  PDF_USERNAME;
  private static String password = PDF_PASSWORD;

    public boolean checkFileExists(String directory, String fileName) {

      boolean flag = false;

      FTPClient ftpClient = initializeFTP();

    try {
      ftpClient.changeWorkingDirectory(directory);
      String[] strings = ftpClient.listNames();
      if (strings != null) {

        for (String str:strings
             ) {
          if (str.toLowerCase().equals(fileName)){
            flag = true;
            break;
          }
        }

      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    return flag;
    }

    public void getFileUsingFtp (String directory, String fileName) {

      FTPClient ftpClient = initializeFTP();
      FileOutputStream fileOutputStream = null;
      try{
        ftpClient.changeWorkingDirectory(directory);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        if (ftpFiles != null) {
          fileOutputStream = new FileOutputStream(LOCAL_STORAGE_LOCATION + fileName);
          ftpClient.retrieveFile(fileName, fileOutputStream);
          fileOutputStream.close();
        }
        ftpClient.disconnect();
      } catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

    private static FTPClient initializeFTP(){
      FTPClient ftpClient = new FTPClient();
      try {
        ftpClient.connect(server_ip, port);
        showServerReply(ftpClient);
        int replyCode = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(replyCode)) {
          log.error("---Login to server failed---");
        }

        boolean success = ftpClient.login(user, password);
        showServerReply(ftpClient);

        if (!success) {
          log.error("----Could not login to the server---");
        }

        return ftpClient;

    }catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return ftpClient;
    }

    private static void showServerReply (FTPClient ftpClient){
      String[] replies = ftpClient.getReplyStrings();
      if (replies != null && replies.length > 0) {
        for (String aReply : replies) {
          log.error("SERVER: {}", aReply);
        }
      }
    }
}

