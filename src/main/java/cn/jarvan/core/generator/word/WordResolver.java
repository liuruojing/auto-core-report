package cn.jarvan.core.generator.word;

import java.util.ArrayList;
import java.util.List;

import cn.jarvan.enums.DataType;
import cn.jarvan.exception.word.WordGeneratorException;
import cn.jarvan.model.word.ConfigData;
import cn.jarvan.util.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b><code>WordResolver</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/24 12:11.
 *
 * @author liuruojing
 * @since poi 0.1.0
 */
public final class WordResolver {
    /**
     * private construct.
     *
     * @param
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private WordResolver() {
        throw new UnsupportedOperationException();
    }

    /**
     * LOG.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(WordResolver.class);

    /**
     * 判断是否是指标段<index=1></index>开头，如果是,那么返回指标id，如果不是返回-1.
     *
     * @param paragraph 段落
     * @return int
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    protected static int isParagraphStart(XWPFParagraph paragraph) {
        String text = paragraph.getText() == null ? "" : paragraph.getText();
        // 匹配形如<index=0>,<index=1>,<index=12>...，并返回指标id
        int index = StringUtil.findIndex(text);
        return index;
    }

    /**
     * 判断段落的内容是否是结尾标志.
     *
     * @param paragraph 段落
     * @return true or false
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    protected static boolean isParagraphEnd(XWPFParagraph paragraph) {
        String text = paragraph.getText() == null ? "" : paragraph.getText();
        String regex = "</index=[0-9]+?>";
        return StringUtil.hasRegexString(regex, text);
    }

    /**
     * 删除掉这个paragraph的所有文本run.
     *
     * @param paragraph 段落
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    protected static void remove(XWPFParagraph paragraph) {
        List<XWPFRun> textRuns = paragraph.getRuns();
        int size = textRuns.size();
        // 删除所有run
        for (int i = 0; i < size; i++) {
            paragraph.removeRun(0);
        }
    }

    /**
     * {{dataName;sql=sql1,sql2;imagetype=pie}
     * 找出段落中配置的占位符，并根据标签返回sql，占位符名称，type(替换类型：图片，表格，字段).
     * @param paragraph 段落
     * @return ConfigData
     * @author liuruojing
     * @throws WordGeneratorException e
     * @since ${PROJECT_NAME} 0.1.0
     */
    protected static List<ConfigData> findConfig(XWPFParagraph paragraph)
            throws WordGeneratorException {
        List<ConfigData> configDatas = new ArrayList<>();
        ConfigData configdata;
        String text = paragraph.getText();
        String regex = "\\{\\{.*?\\}\\}";
        List<String> matchStrings = StringUtil.findAllString(regex, text);
        for (String matchString : matchStrings) {
            configdata = new ConfigData.Builder().builderByString(matchString)
                    .build();
            // 替换掉sql等配置，只留取占位符
            text = replaceMatchString(text, matchString, configdata);
            LOG.debug("Make config to Object:" + configdata);
            configDatas.add(configdata);
        }
        if (configDatas.size() > 0) {
            refreshDoc(paragraph, text);
        }
        return configDatas;
    }

    /**
     * 将段落内容替换为指定字符串.
     *
     * @param paragraph 段落
     * @param text 替换的文本
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static void refreshDoc(XWPFParagraph paragraph, String text) {
        List<XWPFRun> textRuns = paragraph.getRuns();
        int size = textRuns.size();
        // 保留一个run
        for (int i = 0; i < size - 1; i++) {
            paragraph.removeRun(0);
        }

        // 插入文本
        paragraph.createRun().setText(text);
        // 設置樣式
        XWPFRun run0 = paragraph.getRuns().get(0);
        XWPFRun run1 = paragraph.getRuns().get(1);
        run1.setFontFamily(run0.getFontFamily());
        run1.setColor(run0.getColor());
        int fontSize = run0.getFontSize();
        if (fontSize != -1) {
            run1.setFontSize(run0.getFontSize());
        }
        // 删除run0
        paragraph.removeRun(0);
    }

    /**
     * 替换指定内容.
     *
     * @param text text
     * @param matchString matchString
     * @param configdata configdata
     * @return string
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    private static String replaceMatchString(String text, String matchString,
            ConfigData configdata) {
        StringBuilder str = new StringBuilder();
        str.append("{{");
        DataType type = configdata.getType();
        if (type == DataType.WORD_TABLE || type == DataType.NO_SQL_WORD_TABLE) {
            str.append("#").append(configdata.getKey()).append("}}");
        } else {
            if (type == DataType.TEXT || type == DataType.NO_SQL_TEXT) {
                str.append(configdata.getKey()).append("}}");
            } else {
                str.append("@").append(configdata.getKey()).append("}}");
            }
        }
        text = text.replace(matchString, str);
        return text;
    }

}
