package com.gdn.qa.x_search.api.test.utils;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kumar on 27/08/18
 * @project X-search
 */

@Slf4j
public class DownloadHelper {


  private String host;
  private Integer port;
  private String user;
  private String password;

  private Session session;
  private Channel channel;
  private ChannelSftp sftpChannel;

  public DownloadHelper(String host, Integer port, String user, String password) {
    this.host = host;
    this.port = port;
    this.user = user;
    this.password = password;

  }

  private void connect() {

    System.out.println("connecting..."+host);
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(user, host,port);
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(password);
      session.connect();

      channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;

    } catch (JSchException e) {
      e.printStackTrace();
    }

  }

  private void disconnect() {
    System.out.println("disconnecting...");
    sftpChannel.disconnect();
    channel.disconnect();
    session.disconnect();
  }

  public void upload(String fileName, String remoteDir) {

    FileInputStream fis = null;
    connect();
    try {
      // Change to output directory
      sftpChannel.cd(remoteDir);

      // Upload file
      File file = new File(fileName);
      fis = new FileInputStream(file);
      sftpChannel.put(fis, file.getName());

      fis.close();
      System.out.println("File uploaded successfully - "+ file.getAbsolutePath());

    } catch (Exception e) {
      e.printStackTrace();
    }
    disconnect();
  }

  public void download(String fileName, String localDir) {

    byte[] buffer = new byte[1024];
    BufferedInputStream bis;
    connect();
    try {
      // Change to output directory
      String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
      sftpChannel.cd(cdDir);

      File file = new File(fileName);
      bis = new BufferedInputStream(sftpChannel.get(file.getName()));

      File newFile = new File(localDir + "/" + file.getName());

      // Download file
      OutputStream os = new FileOutputStream(newFile);
      BufferedOutputStream bos = new BufferedOutputStream(os);
      int readCount;
      while ((readCount = bis.read(buffer)) > 0) {
        bos.write(buffer, 0, readCount);
      }
      bis.close();
      bos.close();
      System.out.println("File downloaded successfully - "+ file.getAbsolutePath());

    } catch (Exception e) {
      e.printStackTrace();
    }
    disconnect();
  }

  public Vector<ChannelSftp.LsEntry> checkNumberOfFiles(String directory){
    connect();
    try {
      sftpChannel.cd(directory);
      Vector<ChannelSftp.LsEntry> fileList = sftpChannel.ls("facebook-feed-*.txt");

      System.out.println("--Number of files---"+fileList.size());
      disconnect();
      return fileList;
    }

    catch (Exception e){
      e.printStackTrace();
    }
    disconnect();
    return null;
  }


  public String getDeltaDirName(String directory){
    connect();
    String deltaPath="";
    try {

      Vector<ChannelSftp.LsEntry> fileList = sftpChannel.ls(directory);

      for (ChannelSftp.LsEntry lsEntry:fileList) {
        if(lsEntry.getFilename().contains("delta_")){
        deltaPath = lsEntry.getFilename();
        break;
        }
      }
      disconnect();
      return deltaPath;
    }

    catch (Exception e){
      e.printStackTrace();
    }
    disconnect();
    return deltaPath;
  }


  public boolean purgeDirectory(File dir) {
   return dir.delete();
  }


  public int count(String filename) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
      byte[] c = new byte[1024];
      int count = 0;
      int readChars = 0;
      boolean endsWithoutNewLine = false;
      while ((readChars = is.read(c)) != -1) {
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n')
            ++count;
        }
        endsWithoutNewLine = (c[readChars - 1] != '\n');
      }
      if (endsWithoutNewLine) {
        ++count;
      }
      return count;
    }
  }

  public String getLine(String fileName, String patternToMatch) {

    Pattern pattern = Pattern.compile(patternToMatch);
    Matcher matcher = pattern.matcher(" ");

    StringBuffer stringBuffer = new StringBuffer();

    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
      String strCurrentLine;

      while ((strCurrentLine = bufferedReader.readLine())!=null){
        matcher.reset(strCurrentLine);
        if(matcher.find()){
          stringBuffer.append(strCurrentLine);
        }
      }
    } catch (Exception e) {
      log.error("Unable to read file:{}",e);
    }

    return stringBuffer.toString();
  }


}
