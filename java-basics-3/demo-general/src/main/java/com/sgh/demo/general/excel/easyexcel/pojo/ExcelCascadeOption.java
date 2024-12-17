package com.sgh.demo.general.excel.easyexcel.pojo;

import jakarta.annotation.Nonnull;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Excel 多级联动菜单的单个选项
 */
@Data
public class ExcelCascadeOption {

    /** 当前选项名 */
    private String name;

    /** (选择当前选项之后的)下一层选项, 没有下一层选项可以不填 */
    @Nonnull
    private List<ExcelCascadeOption> childList = new LinkedList<>();

    public ExcelCascadeOption(String name) {
        this.name = name;
    }
}
