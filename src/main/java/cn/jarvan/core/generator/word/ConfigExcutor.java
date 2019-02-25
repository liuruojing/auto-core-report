package cn.jarvan.core.generator.word;

import cn.jarvan.model.word.ConfigData;

import java.util.*;

/**
 * <b><code>ConfigExcutor</code></b>
 * <p>
 * 执行配置文件sql生成不同数据类型的关键对象.
 * <p>
 * <b>Creation Time:</b> 2018/10/25 17:24.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class ConfigExcutor {

    /**
     * 根据配置和参数构建poi-tl需要的数据.
     *
     * @param params
     * @param configDatas
     * @return Map
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static Map<String, Object> excute(Map<Integer, Map<String, Object>> params, Map<Integer, List<ConfigData>> configDatas,String mybatisConfigUrl) {
        Map<String, Object> renderData = new HashMap();
        Set<Map.Entry<Integer,List<ConfigData>>> entrySet = configDatas.entrySet();
        Iterator<Map.Entry<Integer,List<ConfigData>>> it = entrySet.iterator();
        Map.Entry<Integer,List<ConfigData>> entry;
        int index;
        List<ConfigData> indexConfigdata;
        DataResolver dataResolver = DataResolver.getInstance(mybatisConfigUrl);
        while(it.hasNext()){
            entry = it.next();
            index = entry.getKey();
            indexConfigdata = entry.getValue();
            //开始循环具体指标配置 拼接传过来的参数开始构建数据
            for (ConfigData configData : indexConfigdata) {
                 Object obj= dataResolver.excute(configData,params.get(index));
                 renderData.put(configData.getKey(),obj);
            }
        }
        return renderData;
    }


}
