package com.sgh.demo.general.temp;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [实体类] 语音识别结果
 *
 * @author Song gh
 * @version 2024/12/23
 */
@Data
@NoArgsConstructor
public class AsrResult {

    /** 当前语音识别结果的唯一 id */
    private String key;

    /** 当前语音的全部文字 */
    private String text;

    /** 详细语句信息 */
    @JsonAlias("sentence_info")
    private List<SentenceInfo> sentenceInfo;

//    /**
//     * 当前语音拆分后的时间段(单位: 毫秒)
//     * <pre>
//     *     1. 里层每个 list 包含开始和结束两个时间点
//     *     2. 推荐省略本字段, 可有效减少数据量
//     * </pre>
//     */
//    private List<List<Integer>> timestamp;

    /** 详细语句信息 */
    @Data
    @NoArgsConstructor
    public static class SentenceInfo {

        /** 当前语句的全部文字 */
        private String text;

        /** 当前语句的开始时间(单位: 毫秒) */
        private Integer start;

        /** 当前语句的结束时间(单位: 毫秒) */
        private Integer end;

        /** 当前说话人在本次识别中的代号(0, 1, 2...) */
        private Integer spk;

//        /**
//         * 当前语句拆分后的时间段(单位: 毫秒)
//         * <pre>
//         *     1. 里层每个 list 包含开始和结束两个时间点
//         *     2. 推荐省略本字段, 可有效减少数据量
//         * </pre>
//         */
//        private List<List<Integer>> timestamp;
    }
}
