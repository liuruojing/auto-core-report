package cn.jarvan.core.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.render.RenderAPI;
import cn.jarvan.exception.WordGeneratorException;
import cn.jarvan.model.ConfigData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b><code>WordGenerator</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/24 11:01.
 *
 * @author liuruojing
 * @since poi 0.1.0
 */
public class WordGenerator {
    /**
     * LOG.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(WordGenerator.class);

    /**
     * 1、删除不需要的指标段paragraph
     * 2、分成指标段的paragraph集合.
     * 3、循环每个指标段，获取sql和待填充的指标
     * 4、填充sql，查出数据，存储
     * 5、删除分段指标标签<index></index>
     * 6、根据数据调用poi-tl生成word文档
     * 
     * @param params 前台参数
     * @param indexs 选中的指标id
     * @param templateFilePath 模板文件位置
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static void generator(Map<Integer, Map<String, Object>> params,
            List<Integer> indexs, String templateFilePath, String destFilePath)
            throws WordGeneratorException {
        String basePath = System.getProperty("user.dir") + File.separator
                + "resources";
        // 设置中间文档位置
        String semi_finished_file = basePath + File.separator
                + "semi_finished_file.docx";
        XWPFDocument document = null;
        FileInputStream fileInputStream = null;
        FileOutputStream outputStream = null;
        Map<String, Object> renderData;
        try {
            fileInputStream = new FileInputStream(templateFilePath);
            document = new XWPFDocument(fileInputStream);
            // 对整个文本进行过滤，key为指标id，value为该指标的段落们
            Map<Integer, List<XWPFParagraph>> index_paragraphs_map = paragraphFilter(
                    document, indexs);
            // 将每页的指标映射重配置对象们
            Map<Integer, List<ConfigData>> configDatas = resolverConfig(
                    index_paragraphs_map);
            // 开始拼接sql,查询数据库，获取填充word的
            renderData = ConfigExcutor.excute(params, configDatas);
            // 写出到中间文档
            outputStream = new FileOutputStream(semi_finished_file);
            document.write(outputStream);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            throw new WordGeneratorException("构建word失败:" + e.getMessage(), e);
        } finally {
            try {
                if (document != null) {
                    document.close();

                }
                if (outputStream != null) {
                    outputStream.close();

                }
                if (fileInputStream != null) {

                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //利用poi-tl渲染中间文档
        XWPFTemplate template = XWPFTemplate.create(semi_finished_file);
        RenderAPI.render(template, renderData);
        // 输出到文件系统
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destFilePath);
            template.write(out);
            out.flush();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            throw new WordGeneratorException("构建word失败:" + e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new WordGeneratorException(
                            "构建word失败:" + e.getMessage(), e);
                }
            }
        }

    }

    /**
     * 将整个文本进行筛选，删除未选择的指标段，并筛选出有用的需填充的指标段.
     * 
     * @param document 模板文档
     * @param indexs 选中指标集合
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static Map<Integer, List<XWPFParagraph>> paragraphFilter(
            XWPFDocument document, List<Integer> indexs) throws Exception {
        List<XWPFParagraph> originParagraphs = document.getParagraphs();
        XWPFParagraph paragraph;
        Iterator<XWPFParagraph> it = originParagraphs.iterator();
        Map<Integer, List<XWPFParagraph>> index_paragraphs_map = new HashMap<>();
        List<XWPFParagraph> currentParagraphs = new LinkedList<>();
        while (it.hasNext()) {
            paragraph = it.next();
            int index = WordResolver.isParagraphStart(paragraph);
            // 是指标段落的开始
            if (index != -1) {
                LOG.debug("检查" + index + "指标段");
                int i = 0;
                // 如果不是选定指标,删掉他直到结尾指标段
                if (!indexs.contains(index)) {
                    do {
                        if (i != 0) {
                            paragraph = it.next();
                        }
                        if (WordResolver.isParagraphEnd(paragraph)) {
                            LOG.debug(index + "指标段是未勾选的，删除掉该无用指标段");
                            WordResolver.remove(paragraph);
                            break;
                        } else {
                            WordResolver.remove(paragraph);
                        }
                        i++;
                    } while (it.hasNext());
                } else {
                    LOG.debug(index + "指标段是被勾选的指标段，将其添加到待渲染指标段集合中");
                    do {
                        if (i != 0) {
                            paragraph = it.next();
                        }
                        // 如果不是指标最后一页
                        if (!WordResolver.isParagraphEnd(paragraph)) {
                            currentParagraphs.add(paragraph);
                        } else { // 如果是最后一页
                            currentParagraphs.add(paragraph);
                            index_paragraphs_map.put(index, currentParagraphs);
                            currentParagraphs = new LinkedList<>();
                            break;
                        }
                        i++;
                    } while (it.hasNext());
                }
            }
        }
        return index_paragraphs_map;

    }

    /**
     * 读取出配置，封装成对象.
     *
     * @param indexsParagraph
     * @return map map
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static Map<Integer, List<ConfigData>> resolverConfig(
            Map<Integer, List<XWPFParagraph>> indexsParagraph)
            throws WordGeneratorException {
        // 整个文档的配置
        Map<Integer, List<ConfigData>> configDatas = new HashMap<>();
        // 每一个指标的配置
        List<ConfigData> indexConfigData = new ArrayList<>();
        Set<Map.Entry<Integer, List<XWPFParagraph>>> set = indexsParagraph
                .entrySet();
        Iterator<Map.Entry<Integer, List<XWPFParagraph>>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<XWPFParagraph>> entry = it.next();
            for (XWPFParagraph paragraph : entry.getValue()) {
                // 找到每个段落的配置文件，封装成对象返回
                for (ConfigData configData : WordResolver
                        .findConfig(paragraph)) {
                    indexConfigData.add(configData);
                }
            }
            configDatas.put(entry.getKey(), indexConfigData);
            indexConfigData = new ArrayList<>();
        }
        return configDatas;

    }
}