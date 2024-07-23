package com.sgh.demo.general.email;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 邮件工具类
 *
 * @author Song gh
 * @version 2024/7/9
 */
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
     * 发送邮件
     *
     * @param recipient 收件人邮箱
     * @param subject   邮件标题
     * @param content   邮件内容
     */
    public static void sendEmail(@NotBlank String recipient, String subject, String content) {
        sendEmail(new String[]{recipient}, subject, content);
    }

    /**
     * 群发邮件
     *
     * @param recipients 收件人邮箱
     * @param subject    邮件标题
     * @param content    邮件内容
     */
    public static void sendEmail(@NotEmpty Collection<String> recipients, String subject, String content) {
        sendEmail(recipients.toArray(new String[0]), subject, content);
    }

    /**
     * 群发邮件
     *
     * @param recipients 收件人邮箱
     * @param subject    邮件标题
     * @param content    邮件内容
     */
    public static void sendEmail(@NotEmpty String[] recipients, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(recipients);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        sendEmail(simpleMailMessage);
    }

    /** 发送邮件 */
    public static void sendEmail(SimpleMailMessage simpleMailMessage) {
        mailSender.send(simpleMailMessage);
    }

    /** 初始化, 将 spring 管理的变量配置为静态参数 */
    @PostConstruct
    private void init() {
        setMailSender(tempJavaMailSender);
        setFrom(tempFrom);
    }
}
