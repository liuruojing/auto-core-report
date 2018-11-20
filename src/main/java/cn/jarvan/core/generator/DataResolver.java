package cn.jarvan.core.generator;

import cn.jarvan.enums.DataType;
import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.TextRenderData;
import cn.jarvan.dao.SqlAutoMapper;
import cn.jarvan.model.ConfigData;
import cn.jarvan.model.SeriesData;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * <b><code>DataResolver</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/25 18:09.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class DataResolver {

    private static SqlSessionFactory sqlSessionFactory = null;

    private static SqlAutoMapper autoMapper;
    static {
        // 配置文件的名称
        String resource = "mybatisConfig.xml";
        // 通过Mybatis包中的Resources获取到配置文件
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            autoMapper = new SqlAutoMapper(sqlSessionFactory);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    /**
     * Description.
     *
     * @param configData 配置封装类
     * @param params 参数
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static Object excute(ConfigData configData,
            Map<String, Object> params) {

        DataType type = configData.getType();
        switch (type) {
        case STRING: {
            return excuteString(configData, params);
        }
        case WORD_TABLE: {
            return excuteWordTable(configData, params);
        }
        default: {
            return excuteEchart(configData, params);
        }
        }

    }

    private static Object excuteEchart(ConfigData configData,
            Map<String, Object> params) {

        return null;
    }

    private static Object excuteWordTable(ConfigData configData,
            Map<String, Object> params) {
        TableRenderData renderData;
        List<Map<String, Object>> result = autoMapper
                .selectList(configData.getSql(), params);
        List<SeriesData> seriesData = transferToSeriesData(configData, result);
        renderData = transferDataToTableRenderData(seriesData);
        return renderData;
    }

    private static List<SeriesData> transferToSeriesData(ConfigData configData,
            List<Map<String, Object>> result) {
        Iterator<Map<String, Object>> it = result.iterator();
        Map<String, Object> record;
        String seriesName = configData.getCol();
        String categoriesName = configData.getRow();
        String valueName = configData.getValue();
        List<SeriesData> seriesDataList = new LinkedList<>();
        Map<Object, Map<String, Object>> map = new LinkedHashMap<>();
        Map.Entry<Object, Map<String, Object>> entry;
        while (it.hasNext()) {
            // 得到一行的数据
            record = it.next();
            // 得到系列名
            Object series = record.get(seriesName);
            // 得到类别名
            Object categories = record.get(categoriesName);
            // 得到类别值
            Object value = record.get(valueName);
            if (map.get(series) == null) {
                Map<String, Object> categoriesMap = new LinkedHashMap<>();
                categoriesMap.put(categories.toString(), value);
                map.put(series, categoriesMap);
            } else {
                Map<String, Object> categoriesMap = map.get(series);
                categoriesMap.put(categories.toString(), value);
            }
        }
        // 循环map封装成seriesData对象
        Set<Map.Entry<Object, Map<String, Object>>> entrySet = map.entrySet();
        Iterator<Map.Entry<Object, Map<String, Object>>> itSet = entrySet
                .iterator();
        while (itSet.hasNext()) {
            entry = itSet.next();
            SeriesData seriesData = new SeriesData();
            seriesData.setSeriesName(((String) entry.getKey()));
            seriesData.setCategories(
                    (LinkedHashMap<String, Object>) entry.getValue());
            seriesDataList.add(seriesData);
        }
        return seriesDataList;
    }

    private static TableRenderData transferDataToTableRenderData(
            List<SeriesData> seriesDatas) {
          List<RenderData> renderDate = new ArrayList<>();
          List<Object> categoriesData = new ArrayList<>();
          Map<String,String> map = new LinkedHashMap<>();
          for(SeriesData seriesData :seriesDatas){
              renderDate.add(new TextRenderData(seriesData.getSeriesName()));
              Map<String,Object> categories = seriesData.getCategories();
              Set<Map.Entry<String,Object>> entrySet = categories.entrySet();
              Iterator<Map.Entry<String, Object>>  it = entrySet.iterator();
              while(it.hasNext()){
                  Map.Entry<String,Object> entry = it.next();
                  String categoriesName = entry.getKey();
                  String categoriesvalue = entry.getValue().toString();
                  if(map.get(categoriesName) == null) {
                      map.put(categoriesName,categoriesvalue);
                  } else{
                      map.put(categoriesName,map.get(categoriesName)+";"+categoriesvalue);
                  }

              }

          }

//        new TableRenderData(new ArrayList<RenderData>() {{
//            add(new TextRenderData("d0d0d0", "column1"));
//            add(new TextRenderData("111111", "column2"));
//            add(new TextRenderData("d0d0d0", "column3"));
//        }}, new ArrayList<Object>() {{
//            add("row1;r1c2;");
//            add("row2;;r2c3");
//            add("row3;r3c2;r3c3");
//        }}, "no datas", 10600)
        return null;
    }

    private static String excuteString(ConfigData configData,
            Map<String, Object> params) {

        String renderData = null;
        Map<String, Object> result = autoMapper.selectOne(configData.getSql(),
                params);
        Iterator<Map.Entry<String, Object>> it = result.entrySet().iterator();
        if (it.hasNext()) {
            renderData = it.next().getValue().toString();
        }
        return renderData;
    }

}
