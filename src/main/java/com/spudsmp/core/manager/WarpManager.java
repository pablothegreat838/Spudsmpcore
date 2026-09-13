package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public final class WarpManager {
    private final SpudSMPPlugin plugin;
    public WarpManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public Location get(String name) { return plugin.getConfig().getSerializable(name, Location.class); }
    public void set(String name, Location location) { plugin.getConfig().set(name, location); plugin.saveConfig(); }
    public ConfigurationSection data() { return plugin.getConfig().getConfigurationSection("spawn"); }
}