package com.gdn.qa.x_search.api.test.utils;

import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * @author kumar on 21/11/18
 * @project X-search
 */

@Component
public class ZipUnzipHelper {

  public void unzipFile(String zipFilePath,File destPath){

    byte[] buffer = new byte[1024];
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        File newFile = newFile(destPath, zipEntry);
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }

  public void extractPasswordZipFile(String zipFilePath,String zipPassword,String outputPath){

    final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("N/A", "zip");
    final File file = new File(zipFilePath);
    if(file.listFiles()!=null) {
      for (final File child : file.listFiles()) {
        try {
          ZipFile zipFile = new ZipFile(child);
          if (extensionFilter.accept(child)) {
            if (zipFile.isEncrypted()) {
              zipFile.setPassword(zipPassword);
            }
            List fileHeaderList = zipFile.getFileHeaders();

            for (int i = 0; i < fileHeaderList.size(); i++) {
              FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
              zipFile.extractFile(fileHeader, outputPath);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
