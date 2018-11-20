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
    STRING,       //普通文本
    TABLE,        //echart表格
    TABLE_RADIO,  //echar占比表格
    WORD_TABLE,   //word内置表格
    LINE,         //echart折线图
    PIE,          //echart饼图
    BAR,          //echart柱状图
    LINE_BAR;      //echar柱状和折线混合图

    DataType(){}
}
