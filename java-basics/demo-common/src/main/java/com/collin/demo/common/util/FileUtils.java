package com.collin.demo.common.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件工具
 *
 * @author Song gh on 2023/8/28.
 */
public class FileUtils {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /** 锁定并写入文件(防止并发), 无内容时则仅创建文件 */
    public static void lockThenWriteToFile(String fileName, List<String> contentList) {
        // 尝试锁定文件
        try (RandomAccessFile fileWriter = new RandomAccessFile(fileName, "rw");
             FileChannel channel = fileWriter.getChannel();
             FileLock lock = channel.tryLock()) {

            // 在文件末尾附加
            String lineSeparator = System.lineSeparator();
            if (lock != null && contentList != null) {
                fileWriter.seek(fileWriter.length());
                for (String content : contentList) {
                    String line = content + lineSeparator;
                    fileWriter.write(line.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException("锁定或写入文件失败", e);
        }
    }

    /**
     * 解压 zip 文件及其内部文件
     *
     * @param zipFilePath 压缩文件路径
     * @param destDirPath 目标文件路径
     */
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

                // 创建文件夹
                if (zipEntry.isDirectory() && (!newFile.isDirectory() && !newFile.mkdirs())) {
                    throw new IOException("创建文件夹失败: " + newFile);
                }
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

                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException e) {
            throw new UnsupportedOperationException("解压文件失败: " + zipFilePath);
        }
        return destDir;
    }

    /** 转换 File 为 MultipartFile */
    public static MultipartFile convertFileToMultipart(File file) {
        MultipartFile multipartFile;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), APPLICATION_OCTET_STREAM, fileInputStream);
        } catch (IOException e) {
            throw new UnsupportedOperationException("转换 File 为 MultipartFile 失败");
        }
        return multipartFile;
    }

    /** 转换 MultipartFile 为 File */
    public static File convertMultipartToFile(MultipartFile multipartFile, String filePath) {
        File file = new File(filePath);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new UnsupportedOperationException("转换 MultipartFile 为 File 失败");
        }
        return file;
    }

    private FileUtils() {
    }
}