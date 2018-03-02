package nl.underkoen.underbot.utils;

import com.corundumstudio.socketio.SocketIOServer;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Under_Koen on 11/01/2018.
 */
public class WebPrintStream extends PrintStream {
    public static File logFile = getLogFile();

    private static File getLogFile() {
        try {
            File file = FileUtilOld.getFile("logs");
            file.mkdir();
            file = FileUtilOld.getFile("logs/log_" + new Timestamp(new Date().getTime()) + ".log");
            file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebPrintStream(SocketIOServer server, MessageBuilder.ConsoleType type) {
        super(
                new OutputStream() {
                    PrintStream old = getPrintStream();

                    private PrintStream getPrintStream() {
                        switch (type) {
                            case LOG:
                                return System.out;
                            case ERR:
                                return System.err;
                        }
                        return null;
                    }

                    public void logLine(String line) {
                        server.getAllClients().forEach(client -> {
                            client.sendEvent("message", MessageBuilder.getConsoleMessage(line, type));
                        });
                        try {
                            FileUtilOld.updateFile(logFile, FileUtilOld.getFileInput(logFile) + type.name.toUpperCase() + ": " + line);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void log(char c) {
                        old.print(c);
                    }

                    StringBuilder str;

                    @Override
                    public void write(int b) {
                        if (str == null) {
                            str = new StringBuilder();
                        }
                        char c = (char) b;
                        str.append(c);
                        if (c == '\n') {
                            logLine(str.toString());
                            str = null;
                        }
                        log(c);
                    }
                }
        );
    }
}
