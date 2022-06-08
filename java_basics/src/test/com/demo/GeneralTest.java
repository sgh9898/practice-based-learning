package com.demo;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 普通测试
 *
 * @author Song gh on 2022/5/6.
 */
class GeneralTest {

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJC4BMkDUM5e2A5H96zbA4sfKMofzjb7/3IitFMGGVOXXYUi15YieiTUCphgL7tPYJVbwenwAIgrzEchCS7lyUdjwcJ06x0JIcsJIDLe3fMol+LSTH8vx78TrwfXvRm8dQtOnmXAYaULT17HK6lUFEGEmdib+vpIt7zADOaDXYJ9AgMBAAECgYAA7Wz6bM8Dw4/W55cqwGyRY627PeDwcUT90kMdlRhsdLfgtoxzJd1qhwFaYKNtq+COlHv1p9gZB07T1d5dMpPLotuO283SLCoPxybT3SomlW6z2iUrz0ykZL89kizV85PmwuiDqTylKippEIgqgQwqFH0T/SnJM2jwJ3YpUPz08QJBAMsGnfvp/WTXWlp1A3lVwRjKir0jtZ1Mzw577GeBVv3F3Y5gSMdisPxywgvC1loznEATIs+a70UIxoVDNd6wu4sCQQC2erS1iuPq2lKvhMaQMJPW8SHthq53Yr5pYnTI+drfsDDqIbphpbfUm0C6qM1cNoK8gYr8DiUawrt4xFbzvxsXAkBPPzT5eLsk2n51IomJmfR2ZdDDxSWF0c5ce/ip6i13fv1dLq4ZzacB0xV1G8cpjE2oIRAMcxCEJMnAiJyFYPzDAkAaFjapUV6931I8x1V/nYI1EynPhBaC+LnR5QJfDOEOY2jKv+GePgumuD8rsCATk7Ni8X4GBJunVLlqTV9E30gnAkBMdL0DNRnyPRmdEba0W5wP05cfnqf4ajxf2xFObpwu88+F2g41Ax3xgdYj/2ZXrAqo9vg2kJM/oKiBlFh/CqAK";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQuATJA1DOXtgOR/es2wOLHyjKH842+/9yIrRTBhlTl12FIteWInok1AqYYC+7T2CVW8Hp8ACIK8xHIQku5clHY8HCdOsdCSHLCSAy3t3zKJfi0kx/L8e/E68H170ZvHULTp5lwGGlC09exyupVBRBhJnYm/r6SLe8wAzmg12CfQIDAQAB";

    @Test
    void main1() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(df.format(new Date()));
    }
}