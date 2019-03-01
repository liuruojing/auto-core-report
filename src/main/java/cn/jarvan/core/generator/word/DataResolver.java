package cn.jarvan.core.generator.word;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jarvan.dao.SqlAutoMapper;
import cn.jarvan.enums.DataType;
import cn.jarvan.model.word.ConfigData;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.TextRenderData;


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

    /**
     * 单例句柄.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static volatile DataResolver instance = null;

    /**
     * mybatis配置文件流.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static Reader reader;

    /**
     * sqlSessionFactory.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static SqlSessionFactory sqlSessionFactory = null;

    /**
     * autoMapper.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static SqlAutoMapper autoMapper;

    /**
     * 私有化构造器.
     *
     * @param mybatisConfigUrl 配置文件路径
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private DataResolver(String mybatisConfigUrl) {
        super();
        try {
            if (mybatisConfigUrl == null
                    || "".equals(mybatisConfigUrl.trim())) {
                // 采用默认的配置文件
                String resource = "mybatisConfig.xml";
                reader = Resources.getResourceAsReader(resource);
            } else {
                // 使用客户端传递来的配置文件
                reader = new InputStreamReader(
                        new FileInputStream(mybatisConfigUrl));
            }
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            autoMapper = new SqlAutoMapper(sqlSessionFactory.openSession(true));
        } catch (IOException e) {
            throw new RuntimeException("exception:", e);
        }
    }

    /**
     * 获取对象实例，使用双重校验锁.
     *
     * @param mybatisConfigUrl 配置文件路径
     * @return DataResolver
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static DataResolver getInstance(String mybatisConfigUrl) {
        // 双重校验，减少同步控制代码的范围，防止每次都进行同步控制判断是否为空
        if (instance == null) {
            synchronized (DataResolver.class) {
                if (instance == null) {
                    instance = new DataResolver(mybatisConfigUrl);
                }
            }
        }
        return instance;
    }

    /**
     * Description.
     *
     * @param configData 配置封装类
     * @param params 参数
     * @return 待填充poi-tl数据
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    protected static Object excute(ConfigData configData,
            Map<String, Object> params) {

        DataType type = configData.getType();
        switch (type) {
        case NO_SQL_TEXT: {
            return excuteNoSqlText(configData, params);
        }
        case TEXT: {
            try {
                return excuteText(configData, params);
            } catch (Exception e) {
                LOG.debug("连接数据库异常，重试一次：", e);
                // 数据库连接超时异常 设置新的连接重试一次
                autoMapper = new SqlAutoMapper(
                        sqlSessionFactory.openSession(true));
                return excuteText(configData, params);
            }

        }
        case NO_SQL_WORD_TABLE: {
            return excuteNosqlWordTable(configData, params);
        }
        case WORD_TABLE: {
            try {
                return excuteWordTable(configData, params);
            } catch (Exception e) {
                LOG.debug("连接数据库异常，重试一次:", e);
                // 数据库连接超时异常 设置新的连接重试一次
                autoMapper = new SqlAutoMapper(
                        sqlSessionFactory.openSession(true));
                excuteWordTable(configData, params);
            }
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

    private static synchronized String excuteText(ConfigData configData,
            Map<String, Object> params) {
        String renderData = null;
        LOG.debug(
                "----------从数据库查询 " + configData.getKey() + "指标-------------");
        Map<String, Object> result = autoMapper.selectOne(configData.getSql(),
                params);
        LOG.debug("----------从数据库查询 " + configData.getKey() + "指标成功---------");
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

    private static synchronized Object excuteWordTable(ConfigData configData,
            Map<String, Object> params) {
        //todo
        return null;
    }

    private static synchronized Object excuteEchart(ConfigData configData,
            Map<String, Object> params) {
        //todo
        return null;
    }

    /**
     * 对象被gc回收时调用此方法释放掉文件流.
     *
     * @param
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        IOUtils.closeQuietly(reader);
        LOG.info("shit! I'm killed by gc ");
    }
}
