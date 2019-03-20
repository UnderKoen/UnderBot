package nl.underkoen.discordbot;

import nl.underkoen.chatbot.CommandLoader;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;
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
import sx.blah.discord.modules.Configuration;

import java.io.File;
import java.util.Timer;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public class DiscordBot extends Module {
    public static IDiscordClient client;
    public static DCommandHandler handler;
    public static KeyLoaderUtil keys;
    public static ModuleFileUtil moduleFileUtil;
    public static Timer timer;

    static {
        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;
        Configuration.LOAD_EXTERNAL_MODULES = false;
    }

    public static final String VERSION = "0.3.7";

    public static final String PREFIX = "/";

    public static void main(String[] args) throws Exception {
        ModuleInfo info = new ModuleInfo(null, "Discord", null);
        DiscordBot module = new DiscordBot(info, new ModuleFileUtil(info, DiscordBot.class.getClassLoader()));
        module.init();
        module.start();
    }

    public DiscordBot(ModuleInfo moduleInfo, ModuleFileUtil moduleFileUtil) {
        super(moduleInfo, moduleFileUtil);
        DiscordBot.moduleFileUtil = moduleFileUtil;
    }

    public static DMember getSelfMember(DServer server) {
        return DMember.getMember(server, client.getOurUser());
    }

    @Override
    public boolean init() {
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

        handler = new DCommandHandler();

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

        CommandLoader.loadCommands("nl.underkoen.discordbot.commands", handler);
        handler.registerCommand(new MusicCommand());
        handler.registerCommand(new MinesweeperCommand());
        return true;
    }

    @Override
    public void stop() {
        try {
            super.stop();
            status = Status.STOPPED;
            if (timer != null) timer.cancel();
            if (handler != null) handler.teardown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.logout();
    }

    @Override
    public void onCrash() {
        try {
            super.onCrash();
            status = Status.CRASHED;
            if (timer != null) timer.cancel();
            if (handler != null) handler.teardown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.logout();
    }

    private Status status = Status.NONE;

    @Override
    public Status getStatus() {
        return status;
    }
}