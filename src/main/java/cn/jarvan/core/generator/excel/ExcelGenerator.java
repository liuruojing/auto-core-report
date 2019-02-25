package cn.jarvan.core.generator.excel;

import cn.jarvan.annotation.CellName;
import cn.jarvan.core.generator.word.WordGenerator;
import cn.jarvan.exception.excel.ExcelGeneratorException;
import cn.jarvan.util.FileUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <b><code>ExcelGenerator</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2019/2/15 16:33.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class ExcelGenerator {
    private static final Logger LOG = LoggerFactory
            .getLogger(WordGenerator.class);

    /**
     * 将data数据渲染成excel文件,data第一行为列名.
     *
     * @param url 文件保存路径
     * @param sheetName 页名
     * @param data
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static synchronized void generatorByList(String url,
            String sheetName, List<List<String>> data)
            throws ExcelGeneratorException {
        if (url == null || url.trim() == null || data == null
                || data.size() <= 0) {
            throw new IllegalArgumentException("argument erro");
        } else {
            try {
                List<String> header = new LinkedList<>();
                Iterator<List<String>> it = data.iterator();
                if (it.hasNext()) {
                    header = it.next();
                    it.remove();
                }
                SXSSFWorkbook workbook = createXLSXWorkbook(sheetName, header,
                        data);
                write(url, workbook);
                LOG.info("生成excel成功，保存路径为: " + url);
            } catch (Exception e) {
                throw new ExcelGeneratorException("created excel failed:", e);
            }
        }
    }

    /**
     * 将model数据渲染成excel文件.
     *
     * @param
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static synchronized void generator(String url, String sheetName,
            List<?> data)
            throws IllegalAccessException, ExcelGeneratorException {
        generatorHelper(url, sheetName, data);
    }

    private static <E> void generatorHelper(String url, String sheetName,
            List<E> data)
            throws IllegalAccessException, ExcelGeneratorException {
        List<List<String>> list;
        list = copy(data);
        generatorByList(url, sheetName, list);
    }

    /**
     * 将model数组转成list数组.
     *
     * @param data model数组
     * @return list数组
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static <E> List<List<String>> copy(List<E> data)
            throws IllegalAccessException {
        int i = 1;
        List<List<String>> list = new LinkedList<>();
        List<String> header =  new LinkedList<>();
        List<String> record;
        Iterator<E> it = data.iterator();
        while (it.hasNext()) {
            record = new LinkedList<>();
            E current = it.next();
            Class<?> clazz = current.getClass();
            // 获取所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 设置可以通过field.get()访问私有变量
                field.setAccessible(true);
                if (i == 1) {
                    // 读取注解,设置为列名
                    CellName cellName = field.getAnnotation(CellName.class);
                    header.add(cellName.value());
                }
                //得到属性值
                record.add(field.get(current).toString());
            }
            if (i == 1) {
                list.add(header);
            }
            list.add(record);
            i++;
        }
        return list;
    }

    /**
     * 创建一个Excel文件对象.
     *
     * @param sheetName sheet名
     * @param header 列名
     * @param data 表格数据
     * @return SXSSFWorkbook excel文件对象
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static SXSSFWorkbook createXLSXWorkbook(String sheetName,
            List<String> header, List<List<String>> data) {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        SXSSFWorkbook workbook = new SXSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        SXSSFSheet sheet = workbook.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        SXSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        // 声明列对象
        SXSSFCell cell;

        // 创建标题
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(cellStyle);
        }

        // 创建内容
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < data.get(i).size(); j++) {
                // 将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(data.get(i).get(j));
            }
        }
        return workbook;
    }

    /**
     * 将文件写到磁盘.
     *
     * @param url 写入位置
     * @param workbook excel对象
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static void write(String url, SXSSFWorkbook workbook)
            throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(url);
            String destFileDir = url.substring(0,
                    url.lastIndexOf(File.separator));
            FileUtil.mkdirsIfNoExist(destFileDir);
            workbook.write(out);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(workbook);
        }
    }

}
