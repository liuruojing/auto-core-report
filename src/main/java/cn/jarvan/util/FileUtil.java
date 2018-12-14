package cn.jarvan.util;

import java.io.File;

/**
 * <b><code>FileUtil</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2018/12/14 16:08.
 *
 * @author liuruojing
 * @since nile-core-autoreport 0.1.0
 */
public final class FileUtil {

    /**
     * 递归创建文件夹.
     *
     * @param fileDir dir
     * @author liuruojing
     * @since ${PROJECT_NAME} 0.1.0
     */
    public static synchronized void mkdirsIfNoExist(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
