package nl.underkoen.underbot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class YoutubeUtil {
    public static boolean IsLivestreaming(String userId) {
        return (getLivestream(userId) != "");
    }

    public static String getLivestream(String userId) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=id&channelId="+ userId + "&eventType=live&type=video&key=" + Main.keys.getYoutubeKey();
        String charset = "UTF-8";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(response.toString()).getAsJsonObject();
            if (o.get("items").getAsJsonArray().size() == 0) return "";
            return o.get("items").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getYoutubeVideo(String search) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=1&q=" + search + "&key=" + Main.keys.getYoutubeKey();
        String charset = "UTF-8";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(response.toString()).getAsJsonObject();
            if (o.get("items").getAsJsonArray().size() == 0) return "";
            return o.get("items").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}