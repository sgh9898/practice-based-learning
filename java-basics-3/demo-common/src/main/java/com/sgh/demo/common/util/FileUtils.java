package com.sgh.demo.common.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件工具
 *
 * @author Song gh on 2023/8/28.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

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

    /**
     * 文件下载
     *
     * @param filePath 完整的文件路径
     */
    public static void downloadFile(HttpServletResponse response, String filePath) {
        // 读取文件
        InputStream inStream;
        try {
            inStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("文件不存在, 路径: " + filePath);
        }
        // 设置输出格式
        response.reset();
        response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + filePath + "\"");
        // 循环取出流中的数据
        byte[] bytes = new byte[100];
        int len;
        try {
            while ((len = inStream.read(bytes)) > 0)
                response.getOutputStream().write(bytes, 0, len);
            inStream.close();
        } catch (IOException e) {
            throw new UnsupportedOperationException("文件下载失败, 路径: " + filePath);
        }
    }

    /**
     * 校验文件后缀是否合规
     *
     * @param originalFileName 文件全名
     * @param suffixSet        允许的文件后缀
     */
    public static void checkSuffix(@Nullable String originalFileName, Collection<String> suffixSet) {
        if (StringUtils.isBlank(originalFileName) || suffixSet == null) {
            throw new IllegalArgumentException("文件名为空或未配置后缀校验规则");
        }
        String suffix = FilenameUtils.getExtension(originalFileName);
        if (StringUtils.isBlank(suffix) || !suffixSet.contains(suffix)) {
            String type = StringUtils.join(suffixSet, ", ");
            throw new IllegalArgumentException("仅允许 " + type + " 类型的文件, 请重新上传");
        }
    }

    /**
     * 校验文件是否存在
     *
     * @param filePath 完整的文件路径
     */
    public static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /** 获取当前项目的根目录 */
    public static String getProjectRoot() {
        return System.getProperty("user.dir");
    }
}