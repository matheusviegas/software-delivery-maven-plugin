package br.com.mvsouza.plugins.util;

import br.com.mvsouza.plugins.ReleaseConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author msouza
 */
public class ZipUtil {

    public static void zipDirectory(ReleaseConfig config, File directoryToZip, String finalZipName) {
        try {
            List<String> filesToZip = new ArrayList<String>();

            populateFilesList(config, directoryToZip, filesToZip);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(finalZipName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesToZip) {
                System.out.println("Zipping " + filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(directoryToZip.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            Logger.getLogger(ZipUtil.class.getSimpleName()).log(Level.SEVERE, "Error while zipping files", e);
        }
    }

    private static void populateFilesList(ReleaseConfig config, File dir, List<String> filesListInDir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        List<String> filesToInclude = Arrays.asList(config.getArtifacts());

        for (File file : files) {
            if (file.isFile() && filesToInclude.contains(file.getName())) {
                filesListInDir.add(file.getAbsolutePath());
            } else {
                populateFilesList(config, file, filesListInDir);
            }
        }
    }

}
