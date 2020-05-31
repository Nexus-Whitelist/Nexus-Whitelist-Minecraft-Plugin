package com.nexuswhitelist.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.*;
import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER;

public class PlayerChecker implements Listener {
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        e.setLoginResult(KICK_WHITELIST);
        Bukkit.getOperators().forEach(p -> {
            if (p.getUniqueId().toString().equals(e.getUniqueId().toString())) {
                e.allow();
                e.setLoginResult(ALLOWED);
            }
        });
        if (e.getLoginResult() == ALLOWED) {
            return;
        }
        Bukkit.getWhitelistedPlayers().forEach(p -> {
            if (p.getUniqueId().toString().equals(e.getUniqueId().toString())) {
                e.allow();
                e.setLoginResult(ALLOWED);
            }
        });
        if (e.getLoginResult() == ALLOWED) {
            return;
        }
        String uuid = e.getUniqueId().toString();
        e.setLoginResult(getWhitelisted(uuid));
        if (e.getLoginResult() == ALLOWED) {
            e.allow();
        }
        else if (e.getLoginResult() == KICK_WHITELIST) {
            e.disallow(KICK_WHITELIST, "You are not whitelisted on this server! If you feel this a mistake, please re-sync your account at https://nexuswhitelist.com");
        }
        else if (e.getLoginResult() == KICK_OTHER) {
            e.disallow(e.getLoginResult(),"Unable to query Nexus Whitelist server");
        } else {
            e.disallow(KICK_WHITELIST, "You are not whitelisted on this server! If you feel this a mistake, please re-sync your account at https://nexuswhitelist.com");
        }
    }

    private AsyncPlayerPreLoginEvent.Result getWhitelisted(String uuid) {
        try {
            URL url = new URL("https://api.nexuswhitelist.com/whitelist/" + Utils.getGameServerId() + "/check?id=" + uuid);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("AUTHORIZATION", "Bearer " + Utils.getAccessToken());
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                System.out.println(content.toString());
                JSONObject myResponse = new JSONObject(content.toString());
                Boolean whitelisted = (Boolean) myResponse.get("whitelisted");
                if (whitelisted) {
                    return ALLOWED;
                } else {
                    return KICK_WHITELIST;
                }

            } else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                JSONObject myResponse = new JSONObject(content.toString());
                String message = myResponse.getJSONObject("error").getString("message");
                System.out.println("Error: " + message);
                return KICK_OTHER;
            }
        } catch (IOException | JSONException error) {
            error.printStackTrace();
            return KICK_OTHER;
        }
    }
}
