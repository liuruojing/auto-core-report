package cn.jarvan;

import cn.jarvan.core.generator.excel.ExcelGenerator;
import cn.jarvan.exception.excel.ExcelGeneratorException;
import cn.jarvan.model.Data;
import cn.jarvan.model.word.ConfigData;

import java.util.LinkedList;
import java.util.List;

/**
 * <b><code>ExcelClient</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2019/2/15 16:55.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class ExcelClient {

    public static void main(String[] args) throws ExcelGeneratorException, IllegalAccessException {
        //generatorBylist();
        generator();
    }

    public static void generatorBylist() throws ExcelGeneratorException {
        List<List<String>> data = new LinkedList<>();
        List<String> header = new LinkedList();
        header.add("第一列");
        header.add("第二列");
        List<String> record = new LinkedList<>();
        record.add("值1");
        record.add("值2");
        data.add(header);
        data.add(record);
        ExcelGenerator.generatorByList(
                "C:\\wordGenerator\\dest_files\\simple.xlsx", "simple", data);
    }

    public static void generator() throws ExcelGeneratorException, IllegalAccessException {
        List<Data> list = new LinkedList<>();
        Data data = new Data("liuruojing", "man", 23);
        list.add(data);
        ExcelGenerator.generator("C:\\wordGenerator\\dest_files\\simple.xlsx",
                "simple", list);
    }
}
