package cn.jarvan;

import cn.jarvan.core.generator.word.WordGenerator;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class WordClient {
    public static void main(String[] args) {
        String basePath = System.getProperty("user.dir") + File.separator
                + "resources";
        // 设置模板位置和输出位置
        String templateFile = basePath + File.separator + "template.docx";
        String destFile = basePath + File.separator + "dest.docx";
        // 添加选中指标
        List<Integer> indexs = new ArrayList<>();
        indexs.add(1);
        indexs.add(2);
        // 添加每个指标的参数
        Map<Integer, Map<String, Object>> allParams = new HashMap<>();
        Map<String, Object> params1 = new HashMap<>();
        params1.put("datetime", "2008-09-12");
        params1.put("area_id", "001");
        params1.put("area_id1", "001");
        params1.put("area_id2", "002");
        List<List<String>> tableData = new LinkedList<>();
        List<String> record1 = new LinkedList<>();
        record1.add("col1");
        record1.add("col2");
        record1.add("col3");
        record1.add("col4");
        List<String> record2 = new LinkedList<>();
        record2.add("值1");
        record2.add("值2");
        record2.add("值3");
        record2.add("值4");
        List<String> record3 = new LinkedList<>();
        record3.add("值1");
        record3.add("值2");
        record3.add("值3");
        record3.add("值4");
        List<String> record4 = new LinkedList<>();
        record4.add("值1");
        record4.add("值2");
        record4.add("值3");
        record4.add("值4");
        tableData.add(record1);
        tableData.add(record2);
        tableData.add(record3);
        tableData.add(record4);
        params1.put("no_sql_table", tableData);
        Map<String, Object> params2 = new HashMap<>();
        params2.put("area_id", "002");
        params2.put("area_id1", "001");
        params2.put("area_id2", "002");
        allParams.put(1, params1);
        allParams.put(2, params2);

        try {
            WordGenerator.generator(allParams, indexs, templateFile, destFile,
                    null, null);
        } catch (Exception e) {
        }
    }
}
