/*
 * 广州丰石科技有限公司拥有本软件版权2018并保留所有权利。
 * Copyright 2018, Guangzhou Rich Stone Data Technologies Company Limited,All rights reserved.
 */
package cn.jarvan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b><code>PropertiesUtil</code></b>
 * <p>
 * 读取配置文件工具类.
 * <p>
 * <b>Creation Time:</b> 2018/9/5 10:35.
 *
 * @author liuruojing
 * @since garnet-core-be-fe 0.1.0
 */
public final class PropertiesUtil {
    /**
     * Description.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(PropertiesUtil.class);

    private PropertiesUtil() {

    }

    /**
     * Description.
     *
     * @param fileUrl properties文件相对类加载器的相对路径
     * @param paramName 参数名称
     * @return String
     * @throws IOException e
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static String read(String fileUrl, String paramName)
            throws IOException {
        if (fileUrl == null || "".equals(fileUrl) || paramName == null
                || "".equals(paramName)) {
            throw new NullPointerException(
                    "Parameters are not allowed to be null");
        }
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream(fileUrl);
        // 使用properties对象加载输入流
        try {
            properties.load(in);
            String paramValue = properties.getProperty(paramName);
            return paramValue;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                // e.printStackTrace();
                LOG.debug("erro:", e);

            }
        }
    }
}
