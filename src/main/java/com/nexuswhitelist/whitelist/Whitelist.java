package com.nexuswhitelist.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Whitelist extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Utils.readConfig(getConfig(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChecker(), this);
        getLogger().info("Nexus Whitelist successfully loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
