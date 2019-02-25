package cn.jarvan.exception.word;

/**
 * <b><code>WordGeneratorException</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/10/22 14:32.
 *
 * @author liuruojing
 * @since poi 0.1.0
 */
public class WordGeneratorException extends Exception {
    static final long serialVersionUID = 1L;
    public WordGeneratorException() {
        super();
    }

    public WordGeneratorException(String message) {
        super(message);
    }

    public WordGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WordGeneratorException(Throwable cause) {
        super(cause);
    }


}
