package nl.underkoen.underbot.hitbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.utils.ColorUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public class HitboxUtil {
    private String token;
    private JsonArray sites;
    private Socket sock;

    private List<Listener> listeners = new ArrayList<>();

    private final String CHANNEL = "makertim";

    private ExecutorService listenersThread;

    public HitboxUtil() throws IOException {
        token = getToken();
        sites = getSites();
        sock = setupSocket();
        listenersThread = Executors.newCachedThreadPool();
        sock.on(Socket.EVENT_MESSAGE, args -> {
            listenersThread.submit(() -> {
                if (getMethod(args[0].toString()).equalsIgnoreCase("chatMsg")) {
                    JsonObject json = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                    //System.out.println(json);
                    if (json.getAsJsonObject("params").get("text").getAsString().startsWith("!linkdiscord")) {
                        Response response = StringToResponseObject(args[0].toString());
                        for (Listener listener : listeners) {
                            listener.onEvent(response);
                        }
                    }
                }
            });
        });
        sock.connect();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private String getToken() throws IOException {
        URL url = new URL("http://api.smashcast.tv/auth/token");
        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
            String content = "login=" + "underbot" + "&pass=makertim123";
            os.writeBytes(content);
            os.flush();
        }
        try (DataInputStream is = new DataInputStream(connection.getInputStream())) {
            return new JSONObject(is.readLine()).get("authToken").toString();
        }
    }

    private JsonArray getSites() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(new HttpGet("https://api.smashcast.tv/chat/servers"));
        HttpEntity entity = response.getEntity();
        return new JsonParser().parse(EntityUtils.toString(entity)).getAsJsonArray();
    }

    private int socketI = -1;

    private Socket setupSocket() {
        socketI = (socketI == 4) ? 0 : socketI + 1;
        IO.Options opts = new IO.Options();
        opts.timeout = 15000;
        opts.transports = new String[]{"websocket"};
        Socket sock;
        try {
            String site = "https://" + sites.get(socketI).getAsJsonObject().get("server_ip").getAsString();
            sock = IO.socket(site, opts);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        sock.on(Socket.EVENT_CONNECT, args -> {
            JSONObject request = new JSONObject();
            request.put("method", "joinChannel");
            JSONObject params = new JSONObject();
            params.put("channel", CHANNEL);
            params.put("name", Main.keys.getHitboxName());
            params.put("token", token);
            params.put("hideBuffered", false);
            request.put("params", params);
            sock.send(request);
            System.out.println("Just connected to the hitbox chat api.");
        }).on(Socket.EVENT_DISCONNECT, args -> {
            sock.disconnect().close();
            this.sock = setupSocket();
        }).on(Socket.EVENT_ERROR, args -> {
            sock.disconnect().close();
            this.sock = setupSocket();
        }).on(Socket.EVENT_RECONNECT_ERROR, args -> {
            sock.disconnect().close();
            this.sock = setupSocket();
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            sock.disconnect().close();
            this.sock = setupSocket();
        });
        return sock;
    }

    public String getMethod(String response) {
        JsonElement jsonEl = new JsonParser().parse(response);
        if (!jsonEl.isJsonObject()) return null;
        JsonObject json = jsonEl.getAsJsonObject();
        return json.get("method").getAsString();
    }

    public Response StringToResponseObject(String response) {
        JsonElement jsonEl = new JsonParser().parse(response);
        if (!jsonEl.isJsonObject()) return null;
        JsonObject json = jsonEl.getAsJsonObject();
        switch (getMethod(response)) {
            case "banList":
                return getBanList(json);
            case "chatLog":
                return getChatLog(json);
            case "chatMsg":
                return getChatMsg(json);
            case "directMsg":
                return getDirectMsg(json);
            case "infoMsg":
                return getInfoMsg(json);
            case "loginMsg":
                return getLoginMsg(json);
            case "mediaLog":
                return getMediaLog(json);
            case "motdMsg":
                return getMotdMsg(json);
            case "pollMsg":
                return getPollMsg(json);
            case "raffleMsg":
                return getRaffleMsg(json);
            case "serverMsg":
                return getServerMsg(json);
            case "slowMsg":
                return getSlowMsg(json);
            case "userInfo":
                return getUserInfo(json);
            case "userList":
                return getUserList(json);
            case "winnerRaffle":
                return getWinnerRaffle(json);
        }
        return null;
    }

    private Map<UUID, UserInfo> userInfos = new HashMap<>();

    public UserInfo getUserInfo(String userName) {
        UUID uuid = UUID.randomUUID();

        sock.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (getMethod(args[0].toString()).equals("userInfo")) {
                    JsonObject json = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                    //System.out.println(json);
                    if (json.getAsJsonObject("params").get("name").getAsString().equalsIgnoreCase(userName)) {
                        UserInfo userInfo = getUserInfo(json);
                        userInfos.put(uuid, userInfo);
                        sock.off(Socket.EVENT_MESSAGE, this);
                    }
                }
            }
        });
        JSONObject request = new JSONObject();
        request.put("method", "getChannelUser");
        JSONObject params = new JSONObject();
        params.put("channel", CHANNEL);
        params.put("name", userName);
        request.put("params", params);
        sock.send(request);
        while (!userInfos.containsKey(uuid)) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        UserInfo userInfo = userInfos.get(uuid);
        userInfos.remove(uuid);
        return userInfo;
    }

    public void sendMessage(String text, Color nameColor) {
        JSONObject request = new JSONObject();
        request.put("method", "chatMsg");
        JSONObject params = new JSONObject();
        params.put("channel", CHANNEL);
        params.put("name", Main.keys.getHitboxName());
        params.put("text", text);
        params.put("nameColor", String.format("%02x%02x%02x", nameColor.getRed(), nameColor.getGreen(), nameColor.getBlue()));
        request.put("params", params);
        sock.send(request);
    }

    private BanList getBanList(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new BanList() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private List<UserInfo> banList = getBans();

            @Override
            public List<UserInfo> getBans() {
                if (banList != null) return banList;
                banList = new ArrayList<>();
                for (JsonElement jsonElement : params.get("data").getAsJsonArray()) {
                    banList.add(getUserInfo(jsonElement.getAsString()));
                }
                return banList;
            }
        };
    }

    private ChatLog getChatLog(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new ChatLog() {
            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private String popover = getPopover();

            @Override
            public String getPopover() {
                if (popover == null) popover = params.get("popover").getAsString();
                return popover;
            }
        };
    }

    private ChatMsg getChatMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new ChatMsg() {
            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private UserInfo user = getUser();

            @Override
            public UserInfo getUser() {
                if (user == null) user = getUserInfo(params.get("name").getAsString());
                return user;
            }

            private Color nameColor = getNameColor();

            @Override
            public Color getNameColor() {
                if (nameColor == null) nameColor = ColorUtil.hexToColor(params.get("nameColor").getAsString());
                return nameColor;
            }

            private RoleUtil.Role role = getRole();

            @Override
            public RoleUtil.Role getRole() {
                if (role == null) role = RoleUtil.getRoleName(params.get("role").getAsString());
                return role;
            }

            private Boolean hasMedia = hasMedia();

            @Override
            public Boolean hasMedia() {
                if (hasMedia == null) hasMedia = params.get("media").getAsBoolean();
                return hasMedia;
            }

            private String image;// = getImage();

            @Override
            public String getImage() {
                if (image == null) image = params.get("image").getAsString();
                return image;
            }
        };
    }

    private DirectMsg getDirectMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new DirectMsg() {
            private UserInfo from = getFrom();

            @Override
            public UserInfo getFrom() {
                if (from == null) from = getUserInfo(params.get("from").getAsString());
                return from;
            }

            private Color nameColor = getNameColor();

            @Override
            public Color getNameColor() {
                return null;
            }

            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String type = getType();

            @Override
            public String getType() {
                if (type == null) type = params.get("type").getAsString();
                return type;
            }
        };
    }

    private InfoMsg getInfoMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new InfoMsg() {
            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String lang = getLang();

            @Override
            public String getLang() {
                if (lang == null) lang = params.get("lang").getAsString();
                return lang;
            }

            private UserInfo subscriber = getSubscriber();

            @Override
            public UserInfo getSubscriber() {
                if (subscriber == null) subscriber = getUserInfo(params.get("name").getAsString());
                return subscriber;
            }

            private JsonObject variables = getVariables();

            @Override
            public JsonObject getVariables() {
                if (variables == null) variables = params.getAsJsonObject("variables");
                return variables;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private UserInfo user = getUser();

            @Override
            public UserInfo getUser() {
                if (user == null) user = getUserInfo(params.get("name").getAsString());
                return user;
            }

            private Action action = getAction();

            @Override
            public Action getAction() {
                if (action == null) action = Action.valueOf(params.get("action").getAsString());
                return action;
            }

            private String type = getType();

            @Override
            public String getType() {
                if (type == null) type = params.get("type").getAsString();
                return type;
            }
        };
    }

    private LoginMsg getLoginMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new LoginMsg() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private UserInfo user = getUser();

            @Override
            public UserInfo getUser() {
                if (user == null) user = getUserInfo(params.get("name").getAsString());
                return user;
            }

            private RoleUtil.Role role = getRole();

            @Override
            public RoleUtil.Role getRole() {
                if (role == null) role = RoleUtil.getRoleName(params.get("role").getAsString());
                return role;
            }
        };
    }

    private MediaLog getMediaLog(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new MediaLog() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String type = getType();

            @Override
            public String getType() {
                if (type == null) type = params.get("type").getAsString();
                return type;
            }

            private List<JsonObject> data = getData();

            @Override
            public List<JsonObject> getData() {
                if (data != null) return data;
                data = new ArrayList<>();
                for (JsonElement jsonElement : params.get("data").getAsJsonArray()) {
                    data.add(jsonElement.getAsJsonObject());
                }
                return data;
            }
        };
    }

    private MotdMsg getMotdMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new MotdMsg() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private UserInfo user = getUser();

            @Override
            public UserInfo getUser() {
                if (user == null) user = getUserInfo(params.get("name").getAsString());
                return user;
            }

            private Color nameColor = getNameColor();

            @Override
            public Color getNameColor() {
                if (nameColor == null) nameColor = ColorUtil.hexToColor(params.get("nameColor").getAsString());
                return nameColor;
            }

            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private String image = getImage();

            @Override
            public String getImage() {
                if (image == null) image = params.get("image").getAsString();
                return image;
            }
        };
    }

    private PollMsg getPollMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new PollMsg() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String question = getQuestion();

            @Override
            public String getQuestion() {
                if (question == null) question = params.get("question").getAsString();
                return question;
            }

            private List<JsonObject> choices = getChoices();

            @Override
            public List<JsonObject> getChoices() {
                if (choices != null) return choices;
                choices = new ArrayList<>();
                for (JsonElement jsonElement : params.get("choices").getAsJsonArray()) {
                    choices.add(jsonElement.getAsJsonObject());
                }
                return choices;
            }

            private Timestamp startTime = getStartTime();

            @Override
            public Timestamp getStartTime() {
                if (startTime == null) startTime = new Timestamp(params.get("start_time").getAsLong());
                return startTime;
            }

            private Integer clienID = getClientID();

            @Override
            public Integer getClientID() {
                if (clienID == null) clienID = params.get("clientID").getAsInt();
                return clienID;
            }

            private PollStatus pollStatus = getPollStatus();

            @Override
            public PollStatus getPollStatus() {
                if (pollStatus == null) pollStatus = PollStatus.valueOf(params.get("status").getAsString());
                return pollStatus;
            }

            private Color nameColor = getNameColor();

            @Override
            public Color getNameColor() {
                if (nameColor == null) nameColor = ColorUtil.hexToColor(params.get("nameColor").getAsString());
                return nameColor;
            }

            private Boolean isSubscriberOnly = isSubscriberOnly();

            @Override
            public Boolean isSubscriberOnly() {
                if (isSubscriberOnly == null) isSubscriberOnly = params.get("subscriberOnly").getAsBoolean();
                return isSubscriberOnly;
            }

            private Boolean isFollowerOnly = isFollowerOnly();

            @Override
            public Boolean isFollowerOnly() {
                if (isFollowerOnly == null) isFollowerOnly = params.get("followerOnly").getAsBoolean();
                return isFollowerOnly;
            }

            private Integer votes = getVotes();

            @Override
            public Integer getVotes() {
                if (votes == null) votes = params.get("votes").getAsInt();
                return votes;
            }

            private List<UserInfo> voters = getVoters();

            @Override
            public List<UserInfo> getVoters() {
                if (voters != null) return voters;
                voters = new ArrayList<>();
                for (JsonElement jsonElement : params.get("voters").getAsJsonArray()) {
                    voters.add(getUserInfo(jsonElement.getAsString()));
                }
                return voters;
            }
        };
    }

    private RaffleMsg getRaffleMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new RaffleMsg() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String question = getQuestion();

            @Override
            public String getQuestion() {
                if (question == null) question = params.get("question").getAsString();
                return question;
            }

            private String prize = getPrize();

            @Override
            public String getPrize() {
                if (prize == null) prize = params.get("prize").getAsString();
                return prize;
            }

            private List<JsonObject> choices = getChoices();

            @Override
            public List<JsonObject> getChoices() {
                if (choices != null) return choices;
                choices = new ArrayList<>();
                for (JsonElement jsonElement : params.get("choices").getAsJsonArray()) {
                    choices.add(jsonElement.getAsJsonObject());
                }
                return choices;
            }

            private Timestamp startTime = getStartTime();

            @Override
            public Timestamp getStartTime() {
                if (startTime == null) startTime = new Timestamp(params.get("start_time").getAsLong());
                return startTime;
            }

            private RaffleStatus raffleStatus = getRaffleStatus();

            @Override
            public RaffleStatus getRaffleStatus() {
                if (raffleStatus == null) raffleStatus = RaffleStatus.valueOf(params.get("status").getAsString());
                return raffleStatus;
            }

            private Color nameColor = getNameColor();

            @Override
            public Color getNameColor() {
                if (nameColor == null) nameColor = ColorUtil.hexToColor(params.get("nameColor").getAsString());
                return nameColor;
            }

            private Boolean isSubscriberOnly = isSubscriberOnly();

            @Override
            public Boolean isSubscriberOnly() {
                if (isSubscriberOnly == null) isSubscriberOnly = params.get("subscriberOnly").getAsBoolean();
                return isSubscriberOnly;
            }

            private Boolean forAdmin = forAdmin();

            @Override
            public Boolean forAdmin() {
                if (forAdmin == null) forAdmin = params.get("forAdmin").getAsBoolean();
                return forAdmin;
            }

            private Boolean isFollowerOnly = isFollowerOnly();

            @Override
            public Boolean isFollowerOnly() {
                if (isFollowerOnly == null) isFollowerOnly = params.get("followerOnly").getAsBoolean();
                return isFollowerOnly;
            }
        };
    }

    private ServerMsg getServerMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new ServerMsg() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private JsonObject text = getText();

            @Override
            public JsonObject getText() {
                if (text == null) text = params.getAsJsonObject("text");
                return text;
            }

            private String type = getType();

            @Override
            public String getType() {
                if (type == null) type = params.get("type").getAsString();
                return type;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private String mode = getMode();

            @Override
            public String getMode() {
                if (mode == null) mode = params.get("mode").getAsString();
                return mode;
            }
        };
    }

    private SlowMsg getSlowMsg(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new SlowMsg() {
            private String text = getText();

            @Override
            public String getText() {
                if (text == null) text = params.get("text").getAsString();
                return text;
            }

            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("time").getAsLong());
                return time;
            }

            private String action = getAction();

            @Override
            public String getAction() {
                if (action == null) action = params.get("action").getAsString();
                return action;
            }

            private Integer slowTime = getSlowTime();

            @Override
            public Integer getSlowTime() {
                if (slowTime == null) slowTime = params.get("slowTime").getAsInt();
                return slowTime;
            }
        };
    }

    private UserInfo getUserInfo(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new UserInfo() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private String name = getName();

            @Override
            public String getName() {
                if (name == null) name = params.get("name").getAsString();
                return name;
            }

            private Timestamp time = getTime();

            @Override
            public Timestamp getTime() {
                if (time == null) time = new Timestamp(params.get("timestamp").getAsLong());
                return time;
            }

            private RoleUtil.Role role = getRole();

            @Override
            public RoleUtil.Role getRole() {
                if (role == null) role = RoleUtil.getRoleName(params.get("role").getAsString());
                return role;
            }

            private Boolean isFollower = isFollower();

            @Override
            public Boolean isFollower() {
                if (isFollower == null) isFollower = params.get("isFollower").getAsBoolean();
                return isFollower;
            }

            private Boolean isSubscriber = isSubscriber();

            @Override
            public Boolean isSubscriber() {
                if (isSubscriber == null) isSubscriber = params.get("isSubscriber").getAsBoolean();
                return isSubscriber;
            }

            private Boolean isOwner = isOwner();

            @Override
            public Boolean isOwner() {
                if (isOwner == null) isOwner = params.get("isOwner").getAsBoolean();
                return isOwner;
            }

            private Boolean isStaff = isStaff();

            @Override
            public Boolean isStaff() {
                if (isStaff == null) isStaff = params.get("isStaff").getAsBoolean();
                return isStaff;
            }

            private Boolean isCommunity = isCommunity();

            @Override
            public Boolean isCommunity() {
                if (isCommunity == null) isCommunity = params.get("isCommunity").getAsBoolean();
                return isCommunity;
            }
        };
    }

    private UserList getUserList(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new UserList() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private Integer guests = getGuests();

            @Override
            public Integer getGuests() {
                if (guests == null) guests = params.get("Guests").getAsInt();
                return guests;
            }

            private List<UserInfo> admins = getAdmins();

            @Override
            public List<UserInfo> getAdmins() {
                if (admins != null) return admins;
                admins = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("admin")) {
                    admins.add(getUserInfo(jsonElement.getAsString()));
                }
                return admins;
            }

            private List<UserInfo> mods = getMods();

            @Override
            public List<UserInfo> getMods() {
                if (mods != null) return mods;
                mods = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("user")) {
                    mods.add(getUserInfo(jsonElement.getAsString()));
                }
                return mods;
            }

            private List<UserInfo> normalUsers = getNormalUsers();

            @Override
            public List<UserInfo> getNormalUsers() {
                if (normalUsers != null) return normalUsers;
                normalUsers = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("anon")) {
                    normalUsers.add(getUserInfo(jsonElement.getAsString()));
                }
                return normalUsers;
            }

            private List<UserInfo> followers = getFollowers();

            @Override
            public List<UserInfo> getFollowers() {
                if (followers != null) return followers;
                followers = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("isFollower")) {
                    followers.add(getUserInfo(jsonElement.getAsString()));
                }
                return followers;
            }

            private List<UserInfo> subscribers = getSubscribers();

            @Override
            public List<UserInfo> getSubscribers() {
                if (subscribers != null) return subscribers;
                subscribers = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("isSubscriber")) {
                    subscribers.add(getUserInfo(jsonElement.getAsString()));
                }
                return subscribers;
            }

            private List<UserInfo> staff = getStaff();

            @Override
            public List<UserInfo> getStaff() {
                if (staff != null) return staff;
                staff = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("isStaff")) {
                    staff.add(getUserInfo(jsonElement.getAsString()));
                }
                return staff;
            }

            private List<UserInfo> community = getCommunity();

            @Override
            public List<UserInfo> getCommunity() {
                if (community != null) return community;
                community = new ArrayList<>();
                for (JsonElement jsonElement : params.getAsJsonObject("data").getAsJsonArray("isCommunity")) {
                    community.add(getUserInfo(jsonElement.getAsString()));
                }
                return community;
            }

            @Override
            public List<UserInfo> getAllUsers() {
                ArrayList<UserInfo> allUsers = new ArrayList<>();
                allUsers.addAll(getAdmins());
                allUsers.addAll(getMods());
                allUsers.addAll(getNormalUsers());
                return allUsers;
            }
        };
    }

    private WinnerRaffle getWinnerRaffle(JsonObject json) {
        JsonObject params = json.getAsJsonObject("params");
        return new WinnerRaffle() {
            private String channel = getChannel();

            @Override
            public String getChannel() {
                if (channel == null) channel = params.get("channel").getAsString();
                return channel;
            }

            private UserInfo winner = getWinner();

            @Override
            public UserInfo getWinner() {
                if (winner == null) winner = getUserInfo(params.get("winner_name").getAsString());
                return winner;
            }

            private String winnerEmail = getWinnerEmail();

            @Override
            public String getWinnerEmail() {
                if (winnerEmail == null) winnerEmail = params.get("winner_email").getAsString();
                return winnerEmail;
            }

            private Boolean winnerPicked = isWinnerPicked();

            @Override
            public Boolean isWinnerPicked() {
                if (winnerPicked == null) winnerPicked = params.get("winner_picked").getAsBoolean();
                return winnerPicked;
            }

            private Boolean forAdmin = isForAdmin();

            @Override
            public Boolean isForAdmin() {
                if (forAdmin == null) forAdmin = params.get("forAdmin").getAsBoolean();
                return forAdmin;
            }

            private String answer = getAnswer();

            @Override
            public String getAnswer() {
                if (answer == null) answer = params.get("answer").getAsString();
                return answer;
            }
        };
    }
}