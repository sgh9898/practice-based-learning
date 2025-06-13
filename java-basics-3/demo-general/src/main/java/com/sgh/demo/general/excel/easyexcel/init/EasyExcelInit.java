package com.sgh.demo.general.excel.easyexcel.init;

import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import com.sgh.demo.general.excel.easyexcel.EasyExcelUtils;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelExportDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * EasyExcel 初始化(主动触发可以减少初次使用的加载时间)
 *
 * @author Song gh
 * @version 2025/1/7
 */
@Component
class EasyExcelInit {

    /** 导出至空白输出流, 完成初始化 */
    @PostConstruct
    public void init() {
        EasyExcelUtils excel = new EasyExcelUtils(new NullOutputStream());
        EasyExcelExportDTO exportDto = new EasyExcelExportDTO();
        excel.writeSheet(EasyExcelClassTemplate.class, null, exportDto);
        excel.closeExcel();
    }

    /** 空白输出流(仅用于主动触发 EasyExcel 初始化) */
    static class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) {
        }
    }
}

