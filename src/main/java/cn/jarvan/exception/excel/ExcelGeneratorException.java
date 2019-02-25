package cn.jarvan.exception.excel;

/**
 * <b><code>ExcelGeneratorException</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2019/2/25 10:59.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class ExcelGeneratorException extends Exception{
    static final long serialVersionUID = 1L;

    public ExcelGeneratorException() {
        super();
    }

    public ExcelGeneratorException(String message) {
        super(message);
    }

    public ExcelGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelGeneratorException(Throwable cause) {
        super(cause);
    }

}
