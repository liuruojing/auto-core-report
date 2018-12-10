package cn.jarvan;

import cn.jarvan.core.generator.WordGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class Client
{
    public static void main(String[] args) {
        String basePath = System.getProperty("user.dir") + File.separator
                + "resources";
        //设置模板位置和输出位置
        String templateFile = basePath + File.separator + "template.docx";
        String destFile = basePath + File.separator + "dest.docx";
        //添加选中指标
        List<Integer> indexs = new ArrayList<>();
        indexs.add(1);
        indexs.add(2);
        //添加每个指标的参数
        Map<Integer, Map<String,Object>> allParams = new HashMap<>();
        Map<String,Object> params1 = new HashMap<>();
        params1.put("datetime","2008-09-12");
        params1.put("area_id","001");
        params1.put("area_id1","001");
        params1.put("area_id2","002");
        Map<String,Object> params2 = new HashMap<>();
        params2.put("area_id","001");
        params2.put("area_id1","001");
        params2.put("area_id2","002");
        allParams.put(1,params1);
        allParams.put(2,params2);

        try {
            WordGenerator.generator(allParams, indexs,
                    templateFile, destFile);
        } catch (Exception e) {
        }
    }
}
