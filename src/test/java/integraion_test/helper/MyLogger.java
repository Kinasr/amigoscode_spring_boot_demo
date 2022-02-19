package integraion_test.helper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A Singleton class to create custom logging.
 */
public class MyLogger {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Private Constrictor
     */
    private MyLogger() {}

    // Static initializer to initialize the external log file with the format and to set the logging level.
//    static {
//        logger.setLevel(Level.ALL);
//        try {
//            var fileHandler = new FileHandler("output/logging.log",true);
//            fileHandler.setFormatter(new SimpleFormatter());
//            logger.addHandler(fileHandler);
//        } catch (IOException e) {
//            MyLogger.warning(MyLogger.class.getSimpleName(), "Can not log in the external file.");
//            e.printStackTrace();
//        }
//    }

    /**
     * Logging a Severe log
     * @param title the log title
     * @param msg the log message
     */
    public static void severe(String title, String msg) {
        logger.severe(title+ ": --> "+ msg);
    }

    /**
     * Logging a Warning log
     * @param title the log title
     * @param msg the log message
     */
    public static void warning(String title, String msg) {
        logger.warning(title+ ": --> "+ msg);
    }

    /**
     * Logging an Info log
     * @param title the log title
     * @param msg the log message
     */
    public static void info(String title, String msg) {
        logger.info(title+ ": --> "+ msg);
    }
}
