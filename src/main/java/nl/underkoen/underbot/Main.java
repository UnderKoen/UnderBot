package nl.underkoen.underbot;

import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.commands.hitbox.LinkDiscord;
import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.entities.impl.MemberImpl;
import nl.underkoen.underbot.hitbox.HitboxUtil;
import nl.underkoen.underbot.minesweeper.commands.MinesweeperCommand;
import nl.underkoen.underbot.music.commands.MusicCommand;
import nl.underkoen.underbot.utils.KeyLoaderUtil;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public class Main {

    public static IDiscordClient client;
    public static CommandHandler handler;
    public static HitboxUtil hitboxUtil;

    public static KeyLoaderUtil keys;

    public static String version = "0.3.2";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/Keys.json");
            if (!file.exists()) {
                file.createNewFile();
                try {
                    InputStream input = Main.class.getClassLoader().getResourceAsStream("Keys.json");
                    FileUtils.copyInputStreamToFile(input, file);
                    System.out.println("You need to hava a Keys.json file or a path to a Keys.json file as arg.");
                    System.out.println("Created a Keys.json.");
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            keys = new KeyLoaderUtil(FileUtils.readFileToString(file, Charset.defaultCharset()));
        } else {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.out.print("You need to hava a Keys.json file or a path to a Keys.json file as arg.");
                return;
            }
            keys = new KeyLoaderUtil(FileUtils.readFileToString(file, Charset.defaultCharset()));
        }
        hitboxUtil = new HitboxUtil();

        handler = new CommandHandler("/");

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(keys.getDiscordKey());
        client = clientBuilder.login();
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(handler);

        //for (Role role : jda.getGuilds().get(0).getRoles()) {
        //    System.out.println(role.getName() + " -=- " + role.getPosition());
        //}

        initializeAllCommands("nl.underkoen.underbot.commands", handler);
        handler.initializeCommand(new MusicCommand());
        handler.initializeCommand(new MinesweeperCommand());

        hitboxUtil.addListener(new LinkDiscord());
    }

    public static Member getSelfMember(IGuild guild) {
        return  new MemberImpl(guild, client.getOurUser());
    }

    public static void initializeAllCommands(String pckgname, CommandHandler handler) {
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() &&
                        entry.getName().endsWith(".class") &&
                        entry.getName().replace('/', '.').startsWith(pckgname)) {
                    String className = entry.getName().replace('/', '.');
                    if (className.endsWith(".class")) {
                        String classname = className.substring(0, className.length() - 6);
                        try {
                            // Try to create an instance of the object
                            Object o = Class.forName(classname).newInstance();
                            if (o instanceof Command) {
                                Command command = (Command) o;
                                handler.initializeCommand(command);
                            }
                        } catch (ClassNotFoundException cnfex) {
                            System.err.println(cnfex);
                        } catch (InstantiationException iex) {
                            // We try to instantiate an interface
                            // or an object that does not have a
                            // default constructor
                        } catch (IllegalAccessException iaex) {
                            // The class is not public
                        }
                    }
                }
            }
        } catch (Exception e) {
            initializeAllCommandsInDir(pckgname, handler);
        }
    }

    //ugly code so i don't need to initialize every command by hand
    private static void initializeAllCommandsInDir(String pckgname, CommandHandler handler) {
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        name = name.replace('.', '/');
        URL url = Main.class.getResource(name);
        File directory = new File(url.getFile());
        if (directory.exists()) {
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                String className = files[i];
                if (!files[i].contains(".")) {
                    initializeAllCommands(pckgname + "." + className, handler);
                    continue;
                }
                if (files[i].endsWith(".class")) {
                    String classname = className.substring(0, files[i].length() - 6);
                    try {
                        // Try to create an instance of the object
                        Object o = Class.forName(pckgname + "." + classname).newInstance();
                        if (o instanceof Command) {
                            Command command = (Command) o;
                            handler.initializeCommand(command);
                        }
                    } catch (ClassNotFoundException cnfex) {
                        System.err.println(cnfex);
                    } catch (InstantiationException iex) {
                        // We try to instantiate an interface
                        // or an object that does not have a
                        // default constructor
                    } catch (IllegalAccessException iaex) {
                        // The class is not public
                    }
                }
            }
        }
    }
}