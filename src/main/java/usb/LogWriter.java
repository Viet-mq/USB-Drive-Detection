package usb;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogWriter {

    public void writeLog(String s){
        Logger logger = Logger.getLogger(LogWriter.class.getName());
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("Events.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.info(s);
            for (Handler h : logger.getHandlers()){
                h.close();
            }

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
