package cn.jarvan.enums;

/**
 * <b><code>DataType</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/25 15:11.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public enum DataType {
    NO_SQL_TEXT,         //直接文本,不需要执行sql，直接替换数据就ok
    TEXT,       //普通文本
    NO_SQL_WORD_TABLE, //word内置表格，不需要执行sql，直接替换数据
    WORD_TABLE,   //word内置表格
    TABLE,        //echart表格
    TABLE_RADIO,  //echar占比表格
    LINE,         //echart折线图
    PIE,          //echart饼图
    BAR,          //echart柱状图
    LINE_BAR;      //echar柱状和折线混合图
    DataType(){}
}
