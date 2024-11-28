package com.sgh.demo.general.email;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * 邮件工具类
 *
 * @author Song gh
 * @version 2024/11/7
 */
@Slf4j
@Component
public class EmailUtils {

    /** 邮件发送工具 */
    @Setter(AccessLevel.PRIVATE)
    private static JavaMailSender mailSender;

    /** 发件人 */
    @Setter(AccessLevel.PRIVATE)
    private static String from;

    /** [仅用于初始化] 发件人 */
    @Value("${spring.mail.username:#{null}}")
    private String tempFrom;

    /** [仅用于初始化] 邮件发送工具 */
    @Resource
    private JavaMailSender tempJavaMailSender;

    /**
     * 发送简单邮件(默认发件人, 无附件)
     *
     * @param recipient 收件人邮箱
     * @param subject   邮件标题
     * @param content   邮件内容
     */
    public static void sendEmail(@NotBlank String recipient, String subject, String content) {
        sendEmail(from, recipient, subject, content, null);
    }

    /**
     * 发送简单邮件(无附件)
     *
     * @param sender    发件人邮箱
     * @param recipient 收件人邮箱
     * @param subject   邮件标题
     * @param content   邮件内容
     */
    public static void sendEmail(@NotBlank String sender, @NotBlank String recipient, String subject, String content) {
        sendEmail(sender, recipient, subject, content, null);
    }

    /**
     * 发送带附件的邮件
     *
     * @param sender    发件人邮箱
     * @param recipient 收件人邮箱
     * @param subject   邮件标题
     * @param content   邮件内容
     * @param filePath  附件地址
     */
    public static void sendEmail(@NotBlank String sender, @NotBlank String recipient, String subject, String content, String filePath) {
        sendEmail(sender, List.of(new String[]{recipient}), subject, content, List.of(new String[]{filePath}));
    }

    /**
     * 群发简单邮件(默认发件人, 无附件)
     *
     * @param recipients 收件人邮箱
     * @param subject    邮件标题
     * @param content    邮件内容
     */
    public static void sendEmail(@NotEmpty Collection<String> recipients, String subject, String content) {
        sendEmail(from, recipients, subject, content, null);
    }

    /**
     * 群发简单邮件(无附件)
     *
     * @param sender     发件人邮箱
     * @param recipients 收件人邮箱
     * @param subject    邮件标题
     * @param content    邮件内容
     */
    public static void sendEmail(@NotBlank String sender, @NotEmpty Collection<String> recipients, String subject, String content) {
        sendEmail(sender, recipients, subject, content, null);
    }

    /**
     * 群发带附件的邮件
     *
     * @param sender     发件人邮箱
     * @param recipients 收件人邮箱
     * @param subject    邮件标题
     * @param content    邮件内容
     * @param filePaths  附件地址
     */
    public static void sendEmail(@NonNull String sender, @NonNull Collection<String> recipients, String subject, String content, Collection<String> filePaths) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            // 基础信息
            messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipients.toArray(new String[0]));
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);

            // 附件
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    FileSystemResource file = new FileSystemResource(filePath);
                    String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
                    messageHelper.addAttachment(fileName, file);
                }
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("发送邮件失败", e);
            throw new IllegalArgumentException("发送邮件失败", e);
        }
    }


    /** 初始化, 将 spring 管理的变量配置为静态参数 */
    @PostConstruct
    private void init() {
        setMailSender(tempJavaMailSender);
        setFrom(tempFrom);
    }
}
