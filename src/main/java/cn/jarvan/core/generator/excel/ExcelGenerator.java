package cn.jarvan.core.generator.excel;

import cn.jarvan.annotation.CellName;
import cn.jarvan.core.generator.word.WordGenerator;
import cn.jarvan.exception.excel.ExcelGeneratorException;
import cn.jarvan.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

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
        List<String> header = new LinkedList<>();
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
                // 得到属性值
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

    /**
     * 将excel中的数据读入model类中
     *
     * @param clazz
     * @param file
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static <T> List<T> read(Class<T> clazz, MultipartFile file)
            throws IOException, IllegalAccessException, InstantiationException,
            NoSuchFieldException {
        // 检查文件
        checkFile(file);
        // 获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        // 创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<T> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook
                    .getNumberOfSheets(); sheetNum++) {
                // 获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                // 获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                // 获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                // 存储列名
                Map<Integer, String> cellNames = new HashMap<>();
                // 存储行记录
                T record = null;
                LOG.debug("从第" + firstRowNum + "开始，到第" + lastRowNum + "结束");
                // 循环所有行
                for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {

                    // 获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }

                    // 获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    // 获得当前行的列数
                    int lastCellNum = row.getLastCellNum();

                    // 循环当前行的列
                    for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {

                        Cell cell = row.getCell(cellNum);
                        // 如果是第一行,将列名放置cells中
                        if (rowNum == firstRowNum) {
                            cellNames.put(cellNum, getCellValue(cell));
                        }
                        // 如果不是第一行，填充record对象
                        else {
                            // 存储行记录信息
                            record = clazz.newInstance();
                            Field[] fields = clazz.getDeclaredFields();
                            for (Field field : fields) {
                                if (cellNames.get(cellNum)
                                        .equals(field
                                                .getAnnotation(CellName.class)
                                                .value())) {
                                    field.setAccessible(true);
                                    if (field != null) {
                                        if (field.getType() == int.class
                                                || field.getType() == Integer.class) {
                                            field.set(record, Integer.parseInt(
                                                    getCellValue(cell)));
                                        }
                                        if (field.getType() == float.class
                                                || field.getType() == Float.class) {
                                            field.set(record, Float.parseFloat(
                                                    getCellValue(cell)));
                                        }
                                        if (field.getType() == double.class
                                                || field.getType() == Double.class) {
                                            field.set(record,
                                                    Double.parseDouble(
                                                            getCellValue(
                                                                    cell)));
                                        }
                                        if (field
                                                .getType() == BigDecimal.class) {
                                            field.set(record, new BigDecimal(
                                                    getCellValue(cell)));
                                        }
                                        if (field.getType() == String.class) {
                                            field.set(record,
                                                    getCellValue(cell));
                                        }
                                    }
                                }

                            }
                        }
                    }
                    if (record != null) {
                        list.add(record);
                    }
                }

            }
            workbook.close();
        }
        return list;
    }

    private static void checkFile(MultipartFile file) throws IOException {
        // 判断文件是否存在
        if (null == file) {
            throw new FileNotFoundException("文件不存在！");
        }
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 判断文件是否是excel文件
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            throw new IOException("文件类型出错");
        }
    }

    private static Workbook getWorkBook(MultipartFile file) throws IOException {
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;

        // 获取excel文件的io流
        InputStream is = file.getInputStream();
        // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
        if (fileName.endsWith("xls")) {
            // 2003
            workbook = new HSSFWorkbook(is);
        } else if (fileName.endsWith("xlsx")) {
            // 2007
            workbook = new XSSFWorkbook(is);
        }

        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC: // 数字
            cellValue = String.valueOf(cell.getNumericCellValue());
            break;
        case Cell.CELL_TYPE_STRING: // 字符串
            cellValue = String.valueOf(cell.getStringCellValue());
            break;
        case Cell.CELL_TYPE_BOOLEAN: // Boolean
            cellValue = String.valueOf(cell.getBooleanCellValue());
            break;
        case Cell.CELL_TYPE_FORMULA: // 公式
            cellValue = String.valueOf(cell.getCellFormula());
            break;
        case Cell.CELL_TYPE_BLANK: // 空值
            cellValue = "";
            break;
        case Cell.CELL_TYPE_ERROR: // 故障
            cellValue = "非法字符";
            break;
        default:
            cellValue = "未知类型";
            break;
        }
        return cellValue;
    }
}
