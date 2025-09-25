package com.smarterp.order.tools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.*;

public class CollectionLetterGenerator {

    public static void main(String[] args) {
        String excelFilePath = "/Users/lijuntao/Desktop/第三批替补.xlsx";  // Excel文件路径
        String wordTemplatePath = "/Users/lijuntao/Desktop/催收函_模板.docx"; // Word模板路径
        String outputDir = "/Users/lijuntao/Downloads/output";               // 输出文件夹路径

        try {
            // 读取Excel文件，获取100个业主的欠费信息
            List<Map<String, String>> customerData = readExcel(excelFilePath);

            // 为每个业主生成催收函
            for (int i = 0; i < customerData.size(); i++) {
                Map<String, String> customer = customerData.get(i);
                String outputFilePath = outputDir + "/催收函_" + customer.get("name") + ".docx";

                // 生成催收函
                generateCollectionLetter(wordTemplatePath, outputFilePath, customer);
            }

            System.out.println("催收函生成完毕!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读取Excel文件
    private static List<Map<String, String>> readExcel(String filePath) throws IOException {
        List<Map<String, String>> customerData = new ArrayList<>();

        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(file);
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

        for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {  // 从第二行开始，跳过标题行
            Row row = sheet.getRow(i);
            if (row != null) {
                Map<String, String> customer = new HashMap<>();
                customer.put("address", getCellValueAsString(row.getCell(0), formulaEvaluator));
                customer.put("name", getCellValueAsString(row.getCell(2), formulaEvaluator));

                customer.put("k", getCellValueAsString(row.getCell(10), formulaEvaluator));

                customer.put("g", getCellValueAsString(row.getCell(6), formulaEvaluator));
                customer.put("h", getCellValueAsString(row.getCell(7), formulaEvaluator));
                customer.put("j", getCellValueAsString(row.getCell(9), formulaEvaluator));

                customer.put("nsm", getCellValueAsString(row.getCell(13), formulaEvaluator));


                customerData.add(customer);
            }
        }

        workbook.close();
        file.close();
        return customerData;
    }

    // 根据模板生成催收函
    private static void generateCollectionLetter(String templatePath, String outputFilePath, Map<String, String> customer) throws IOException {
        FileInputStream templateFile = new FileInputStream(new File(templatePath));
        XWPFDocument document = new XWPFDocument(templateFile);

        // 遍历文档的每一段，替换占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // 获取段落中的所有文本运行（XWPFRun）
            for (XWPFRun run : paragraph.getRuns()) {
                String runText = run.getText(0); // 获取文本内容
                if (runText != null) {
                    System.out.println(customer.get("nsm"));
                    // 替换占位符
                    runText = runText.replace("#{address}", customer.get("address"))
                            .replace("#{name}", customer.get("name"))
                            .replace("#{k}", customer.get("k"))
                            .replace("#{g}", customer.get("g"))
                            .replace("#{h}", customer.get("h"))
                            .replace("#{j}", customer.get("j"))
                            .replace("#{nsm}", customer.get("nsm"));
                    run.setText(runText, 0); // 更新文本
                }
            }
        }

        // 保存新的文档
        FileOutputStream out = new FileOutputStream(outputFilePath);
        document.write(out);
        out.close();
        templateFile.close();
    }

    // 读取单元格值并确保转换为字符串
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static String getCellValueAsString(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 使用 FormulaEvaluator 评估公式
                return evaluateFormula(cell, formulaEvaluator);
            default:
                return "";
        }
    }

    private static String evaluateFormula(Cell cell, FormulaEvaluator formulaEvaluator) {
        // 通过 FormulaEvaluator 来评估公式单元格的值
        switch (cell.getCachedFormulaResultType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

}
