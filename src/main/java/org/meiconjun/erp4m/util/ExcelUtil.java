package org.meiconjun.erp4m.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: excel操作工具类
 * @date 2020/6/26 14:12
 */
public class ExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    // 列宽度
    private static int columnWidth = 4000;
    // 行高
    private static short rowHeight = 400;

    // 设置列宽
    public static void setColumnWidth(int columnWidth) {
        ExcelUtil.columnWidth = columnWidth;
    }
    //设置行高
    public static void setRowHeight(short rowHeight) {
        ExcelUtil.rowHeight = rowHeight;
    }

    /**
     * 生成excel对象
     * @param cellHeads 表头数据
     * @param dataList  表格内容
     * @return excel对象
     */
    public static Workbook exportExcel(List<String> cellHeads, List<String[]> dataList) {
        Workbook workbook = new SXSSFWorkbook();
        // 设置列头
        Sheet sheet = workbook.createSheet();// 创建工作簿
        //设置列头宽度
        for (int i = 0; i < cellHeads.size(); i++) {
            sheet.setColumnWidth(i, columnWidth);
        }
        // 设置行高
        sheet.setDefaultRowHeight(rowHeight);
        // 构建单元格样式
        CellStyle cellStyle = buildHeadCellStyle(workbook);
        // 写入列头
        Row head = sheet.createRow(0);
        for (int i = 0; i< cellHeads.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(cellHeads.get(i));
            cell.setCellStyle(cellStyle);
        }
        // 写入数据
        for (int i = 0; i < dataList.size(); i++) {
            String[] data = dataList.get(i);
            if (data == null) {
                continue;
            }
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(CommonUtil.isStrBlank(data[i]) ? "" : data[i]);
            }
        }
        return workbook;
    }

    /**
     * 设置列头的样式
     * @param workbook
     * @return
     */
    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 对齐方式设置
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框颜色和宽度设置
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());// 下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());// 左边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        //设置背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }
}
