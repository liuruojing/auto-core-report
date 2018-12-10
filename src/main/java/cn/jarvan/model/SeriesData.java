package cn.jarvan.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * <b><code>SeriesData</code></b>
 * <p>
 * .构建echart、table需要的数据实体
 * <p>
 * <b>Creation Time:</b> 2018/10/25 17:46.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class SeriesData implements Serializable{
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Series name.
     */
    private String seriesName;

    /**
     * The Categories.
     */
    private LinkedHashMap<String, Object> categories;


    public SeriesData(){

    }
    /**
     * Instantiates a new Chart series.
     *
     * @param seriesName the series name
     * @param categories the categories
     */
    public SeriesData(String seriesName, LinkedHashMap<String, Object> categories) {
        this.seriesName = seriesName;
        this.categories = categories;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public LinkedHashMap<String, Object> getCategories() {
        return categories;
    }

    /**
     * Sets categories.
     *
     * @param categories the categories
     */
    public void setCategories(LinkedHashMap<String, Object> categories) {
        this.categories = categories;
    }

    /**
     * Gets series name.
     *
     * @return the series name
     */
    public String getSeriesName() {
        return seriesName;
    }

    /**
     * Sets series name.
     *
     * @param seriesName the series name
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }


}
