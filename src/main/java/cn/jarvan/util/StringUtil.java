package cn.jarvan.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b><code>StringUtil</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/24 12:25.
 *
 * @author liuruojing
 * @since poi 0.1.0
 */
public final class StringUtil {
    /**
     * 返回包含<index=*>子串的指标id，如果不存在该子串，则返回-1.
     *
     * @param string
     * @return int
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static int findIndex(String string) {
        String regex = "<index=[0-9]+?>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String matchString = matcher.group();
            return Integer.parseInt(
                    matchString.substring(7, matchString.length() - 1));
        } else {
            return -1;
        }
    }

    /**
     * 判断是否含有符合regex正则表达式的子串.
     *
     * @param regex
     * @param string
     * @return boolean
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static boolean hasRegexString(String regex, String string) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回符合正则表达式的所有子串.
     *
     * @param
     * @return
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static List<String> findAllString(String regex, String string){
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;

    }
}
