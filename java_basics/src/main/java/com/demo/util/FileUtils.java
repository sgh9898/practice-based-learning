package com.demo.util;

import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件工具
 *
 * @author Song gh on 2023/8/28.
 */
public class FileUtils {

    /** 解压 zip 文件 */
    public static File unzip(String zipFilePath, String destDirPath) {
        File destDir = new File(destDirPath);
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 解析压缩文件
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                // 校验
                if (!newFile.getCanonicalPath().startsWith(destDir.getCanonicalPath() + File.separator)) {
                    throw new IOException("目标文件夹超出范围: " + zipEntry.getName());
                }
                if (zipEntry.isDirectory()) {
                    // 需要创建文件夹
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("创建文件夹失败: " + newFile);
                    }
                } else {
                    // 解压文件
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("创建文件夹失败: " + parent);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.close();
                }
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("解压文件失败: " + zipFilePath);
        }
        return destDir;
    }

    /** 转换 File 为 MultipartFile */
    public static MultipartFile convertFileToMultipart(File file) {
        MultipartFile multipartFile;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("转换 File 为 MultipartFile 失败");
        }
        return multipartFile;
    }

    /** 转换 MultipartFile 为 File */
    public static File convertMultipartToFile(MultipartFile multipartFile, String filePath) {
        File file = new File(filePath);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("转换 MultipartFile 为 File 失败");
        }
        return file;
    }
}