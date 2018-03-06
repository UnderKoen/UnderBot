package nl.underkoen.discordbot;

import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.Member;
import nl.underkoen.discordbot.entities.impl.MemberImpl;
import nl.underkoen.discordbot.minesweeper.commands.MinesweeperCommand;
import nl.underkoen.discordbot.music.commands.MusicCommand;
import nl.underkoen.discordbot.utils.KeyLoaderUtil;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.ModuleInfo;
import nl.underkoen.underbot.models.Status;
import nl.underkoen.underbot.utils.ModuleFileUtil;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.modules.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Timer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public class Main extends Module {
    public static IDiscordClient client;
    public static CommandHandler handler;

    public static KeyLoaderUtil keys;

    public static String version = "0.3.6";

    public static Main main;

    public Main(ModuleInfo moduleInfo, ModuleFileUtil moduleFileUtil) {
        super(moduleInfo, moduleFileUtil);
        Main.main = this;
    }

    public static Member getSelfMember(IGuild guild) {
        return new MemberImpl(guild, client.getOurUser());
    }

    public static Timer timer;

    private static void initializeAllCommands(String pckgname, CommandHandler handler) {
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
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (InstantiationException | IllegalAccessException ignored) {
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
        String name = pckgname;
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        name = name.replace('.', '/');
        URL url = Main.class.getResource(name);
        File directory = new File(url.getFile());
        if (directory.exists()) {
            String[] files = directory.list();
            if (files != null) {
                for (String className : files) {
                    if (!className.contains(".")) {
                        initializeAllCommands(pckgname + "." + className, handler);
                        continue;
                    }
                    if (className.endsWith(".class")) {
                        String classname = className.substring(0, className.length() - 6);
                        try {
                            // Try to create an instance of the object
                            Object o = Class.forName(pckgname + "." + classname).newInstance();
                            if (o instanceof Command) {
                                Command command = (Command) o;
                                handler.initializeCommand(command);
                            }
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (InstantiationException | IllegalAccessException ignored) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean init() throws Exception {
        status = Status.INITIALIZING;

        //TODO not hardcoded
        //String[] args = {"/Users/koen/Desktop/Programmeren/Java/UnderBot/keys/test_discord_keys.json"};

        try {
            //if (args.length == 0) {
            File file = getModuleFileUtil().getFileInPersonalDir("Keys.json");
            if (!file.exists()) {
                file.createNewFile();
                try {
                    getModuleFileUtil().copyResourceToPersonalDir("Keys.json");
                    System.out.println("You need to hava a Keys.json file or a path to a Keys.json file as arg.");
                    System.out.println("Created a Keys.json.");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            keys = new KeyLoaderUtil(getModuleFileUtil().getContent(file));
            /*} else {
                File file = new File(args[0]);
                if (!file.exists()) {
                    System.out.print("You need to hava a Keys.json file or a path to a Keys.json file as arg.");
                    return false;
                }
                keys = new KeyLoaderUtil(FileUtils.readFileToString(file, Charset.defaultCharset()));
            }*/
        } catch (Exception e) {
            return false;
        }

        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;
        Configuration.LOAD_EXTERNAL_MODULES = false;

        handler = new CommandHandler("/");

        timer = new Timer();
        return true;
    }

    @Override
    public boolean start() throws Exception {
        super.start();
        status = Status.RUNNING;

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(keys.getDiscordKey());
        client = clientBuilder.login();
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(handler);

        initializeAllCommands("nl.underkoen.discordbot.commands", handler);
        handler.initializeCommand(new MusicCommand());
        handler.initializeCommand(new MinesweeperCommand());

        //for (Role role : jda.getGuilds().getFileInput(0).getRoles()) {
        //    System.out.println(role.getName() + " -=- " + role.getPosition());
        //}
        return true;
    }

    @Override
    public void stop() throws Exception {
        try {
            super.stop();
            status = Status.CRASHED;
            if (timer != null) timer.cancel();
            if (handler != null) handler.stopAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.logout();
    }

    @Override
    public void onCrash() throws Exception {
        try {
            super.onCrash();
            status = Status.CRASHED;
            if (timer != null) timer.cancel();
            if (handler != null) handler.stopAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.logout();
    }

    private Status status = Status.NONE;

    @Override
    public Status getStatus() {
        if (status == Status.RUNNING) {
            status = (client.isLoggedIn()) ? Status.RUNNING : Status.STOPPED;
        }
        return status;
    }
}