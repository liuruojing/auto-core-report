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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /**
     * LOG.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(DataResolver.class);

    private static SqlSessionFactory sqlSessionFactory = null;

    private static SqlAutoMapper autoMapper;
    static {
        // 配置文件的名称
        String resource = "mybatisConfig.xml";
        // 通过Mybatis包中的Resources获取到配置文件
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            autoMapper = new SqlAutoMapper(sqlSessionFactory.openSession());
        } catch (IOException e) {
            LOG.error("exception:", e);

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
        case NO_SQL_TEXT: {
            return excuteNoSqlText(configData, params);
        }
        case TEXT: {
            return excuteText(configData, params);
        }
        case NO_SQL_WORD_TABLE: {
            return excuteNosqlWordTable(configData, params);
        }
        case WORD_TABLE: {
            return excuteWordTable(configData, params);
        }
        default: {
            return excuteEchart(configData, params);
        }
        }

    }

    private static String excuteNoSqlText(ConfigData configData,
            Map<String, Object> params) {
        String renderData = (String) params.get(configData.getKey());
        return renderData;
    }

    private static String excuteText(ConfigData configData,
            Map<String, Object> params) {
        String renderData = null;
        Map<String, Object> result = autoMapper.selectOne(configData.getSql(),
                params);
        if (result != null && result.size() >= 1) {
            Iterator<Map.Entry<String, Object>> it = result.entrySet()
                    .iterator();
            if (it.hasNext()) {
                renderData = it.next().getValue().toString();
            }
        }
        return renderData;
    }

    private static Object excuteNosqlWordTable(ConfigData configData,
            Map<String, Object> params) {
        List<RenderData> header = new ArrayList<>();
        List<Object> body = new ArrayList<>();
        List<List<String>> tableData = (List<List<String>>) params
                .get(configData.getKey());
        Iterator<String> it;
        int recordNum = 1;
        // 遍历每一行
        for (List<String> record : tableData) {
            StringBuilder recordString = new StringBuilder();
            it = record.iterator();
            while (it.hasNext()) {
                if (recordNum == 1) {
                    header.add(new TextRenderData("d0d0d0", it.next()));
                } else {
                    recordString.append(it.next()).append(";");
                }
            }
            if (recordNum != 1) {
                recordString.deleteCharAt(recordString.length() - 1);
                body.add(recordString);
            }
            recordNum++;
        }
        return new TableRenderData(header, body);
    }

    private static Object excuteWordTable(ConfigData configData,
            Map<String, Object> params) {
        TableRenderData renderData;
        List<Map<String, Object>> result = autoMapper
                .selectList(configData.getSql(), params);
        return null;
    }

    private static Object excuteEchart(ConfigData configData,
            Map<String, Object> params) {

        return null;
    }
}
