package cn.jarvan.model;

import cn.jarvan.core.generator.WordResolver;
import cn.jarvan.enums.DataType;
import cn.jarvan.exception.WordGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * <b><code>Sqldata</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/24 19:20.
 *
 * @author liuruojing
 * @since poi 0.1.0
 */
public class ConfigData {

    /**
     * word文档中占位符的key.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private String key;

    /**
     * 查询的sql.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private String sql;

    /**
     * 查询出的数据需渲染的类型(文本字段，图片).
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private DataType type;

    /**
     * 列对应的数据库字段名.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private String col;

    /**
     * 行对应的数据库字段名.
     *
     * @author liuruojing
     * @param
     * @return
     * @since ${PROJECT_NAME} 0.1.0
     */
    private String row;

    /**
     * 值对应的数据库字段名.
     *
     * @since ${PROJECT_NAME} 0.1.0
     */
    private String value;

    private ConfigData() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigData{" + "key='" + key + '\'' + ", sql='" + sql + '\''
                + ", type=" + type + ", col='" + col + '\'' + ", row='" + row
                + '\'' + ", value='" + value + '\'' + '}';
    }

    public static class Builder {
        private ConfigData target;

        public Builder() {
            target = new ConfigData();
        }

        public Builder key(String key) {
            target.key = key;
            return this;

        }

        public Builder sql(String sql) {
            target.sql = sql;
            return this;
        }

        public Builder type(DataType type) {
            target.type = type;
            return this;
        }

        public Builder col(String col) {
            target.col = col;
            return this;
        }

        public Builder rowName(String row) {
            target.row = row;
            return this;
        }

        public Builder valueNale(String value) {
            target.value = value;
            return this;
        }

        public Builder builderByString(String string)
                throws WordGeneratorException {
            resolveString(string);
            if (target.key == null || target.sql == null
                    || target.type == null) {
                throw new WordGeneratorException("ConfigData 构建失败，配置文件不符合规范");
            }
            if ((target.type != DataType.STRING && target.type != DataType.TEXT)
                    && (target.col == null || target.row == null
                            || target.value == null)) {
                throw new WordGeneratorException("ConfigData 构建失败，配置文件不符合规范");
            }
            return this;
        }

        public ConfigData build() {
            return target;
        }

        /**
         * 解析String 构建target对象.
         *
         * @param
         * @return
         * @author liuruojing
         * @since ${PROJECT_NAME} 0.1.0
         */

        private void resolveString(String string)
                throws WordGeneratorException {
            String text = string.substring(2, string.length() - 2);
            String[] params = text.split(";");
            Map<String, String> map = initParamsmapByParams(params);
            target.sql = map.get("sql");
            target.col = map.get("col");
            target.row = map.get("row");
            target.value = map.get("value");
            initKeyAndType(params);
            //直接文本的情况
            if (target.type == DataType.STRING && target.sql == null) {
              target.type = DataType.TEXT;
              target.sql="super man sql";
            }
        }

        /**
         * 根据配置填充key和type.
         *
         * @param
         * @return
         * @author liuruojing
         * @since ${PROJECT_NAME} 0.1.0
         */
        private void initKeyAndType(String[] params)
                throws WordGeneratorException {
            String param = params[0];
            String type = param.substring(0, 1);

            switch (type) {
            case "@": { // echart图片
                target.key = param.substring(1, param.length());
                // 得到最后一个配置图片具体类型的数组
                param = params[params.length - 1];
                String concreteType = param.split("=")[1];
                initType(concreteType);
                break;
            }
            case "#": { // word_table
                target.key = param.substring(1, param.length());
                target.type = DataType.WORD_TABLE;
                break;
            }
            default: { // 文本
                target.type = DataType.STRING;
                target.key = param;
                break;
            }
            }

        }

        private Map<String, String> initParamsmapByParams(String[] params)
                throws WordGeneratorException {
            Map<String, String> map = new HashMap<>();
            for (int i = 1; i < params.length; i++) {
                parseToMap(params[i], map);
            }

            return map;
        }

        private void initType(String concreteType)
                throws WordGeneratorException {
            if (concreteType == null || concreteType.equals("")) {
                throw new WordGeneratorException(
                        "ConfigData构建失败，未设置echart图片类型!");
            }
            switch (concreteType) {
            case "bar": {
                target.setType(DataType.BAR);
                break;
            }
            case "line": {
                target.setType(DataType.LINE);
                break;
            }
            case "pie": {
                target.setType(DataType.PIE);
                break;
            }
            case "line_bar": {
                target.setType(DataType.LINE_BAR);
                break;
            }
            case "table": {
                target.setType(DataType.TABLE);
                break;
            }
            case "table_radio": {
                target.setType(DataType.TABLE_RADIO);
                break;
            }
            default: {
                throw new WordGeneratorException(
                        "ConfigData构建失败，设置的echar图片类型出错!");
            }
            }
        }

        private void parseToMap(String param, Map<String, String> map)
                throws WordGeneratorException {
            String[] str = param.split("=");
            if (str.length < 2) {
                throw new WordGeneratorException("配置文件出错");
            }
            for (int i = 1; i < str.length - 1; i++) {
                str[1] = str[i] + "=" + str[i + 1];
            }
            map.put(str[0], str[1]);
        }
    }
}
