package com.nexuswhitelist.whitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

class Utils {
    private static FileConfiguration cfg;
    private static Plugin plugin;
    private static String token;
    private static String gameServerId;

    static void readConfig(FileConfiguration cfg, Plugin plugin) {
        Utils.cfg = cfg;
        Utils.plugin = plugin;
        Utils.gameServerId = cfg.getString("game-server-id");
        Utils.token = cfg.getString("access-token");
    }

    static String getAccessToken() {
        return Utils.token;
    }

    static String getGameServerId() {
        return Utils.gameServerId;
    }
}
