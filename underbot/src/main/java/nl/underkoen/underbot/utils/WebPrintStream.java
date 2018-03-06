package nl.underkoen.underbot.utils;

import com.corundumstudio.socketio.SocketIOServer;
import nl.underkoen.underbot.Main;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;

/**
 * Created by Under_Koen on 11/01/2018.
 */
public class WebPrintStream extends PrintStream {
    public static File logFile = getLogFile();

    private static File getLogFile() {
        try {
            File file = Main.assetHandler.fileUtil.getFileInPersonalDir("logs");
            if (!file.exists()) file.mkdir();
            String time = new Timestamp(System.currentTimeMillis()).toString();
            time = time.replace(":", "-");
            file = new File(file.getPath() + "/log_" + time + ".log");
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
                            if (Main.socketHandler.getLoggedIn().get(client.getSessionId())) {
                                client.sendEvent("message", MessageBuilder.getConsoleMessage(line, type));
                            }
                        });
                        try {
                            Main.assetHandler.fileUtil.updateFile(logFile, Main.assetHandler.fileUtil.getContent(logFile) + type.name.toUpperCase() + ": " + line);
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
