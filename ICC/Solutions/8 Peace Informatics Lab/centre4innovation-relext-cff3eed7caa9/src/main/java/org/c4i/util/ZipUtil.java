package org.c4i.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip utilities.
 * see: http://www.baeldung.com/java-compress-and-uncompress
 * @author Arvid Halma
 */
public class ZipUtil {

    public static void zip(String destinationFile, String sourceFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File source = new File(sourceFile);
        FileInputStream fis = new FileInputStream(source);
        ZipEntry zipEntry = new ZipEntry(source.getName());
        zipOut.putNextEntry(zipEntry);
        final byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public static void zip(String destinationFile, Collection<String> sourceFiles) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : sourceFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }

    /**
     * To zip sub-directories, we iterate through them recursively.
     * Every time we find a directory, we append its name to the descendants ZipEntry name to save the hierarchy.
     * @param destinationFile e.g. result.zip
     * @param sourceFile e.g. input.txt
     * @throws IOException when the input could not be read or the output not be written
     */
    public static void zipDir(String destinationFile, String sourceFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);

        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static void unzip(String zipFile, String targetDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
            String fileName = zipEntry.getName();
            File newFile = new File(targetDir, fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

}
