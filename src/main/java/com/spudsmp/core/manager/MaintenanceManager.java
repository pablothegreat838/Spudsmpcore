package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class MaintenanceManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final File file;
    private final YamlConfiguration data;
    private boolean active;
    private String message;
    public MaintenanceManager(SpudSMPPlugin plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "maintenance.yml"); data = YamlConfiguration.loadConfiguration(file); active = data.getBoolean("active", false); message = data.getString("message", "The server is under maintenance."); }
    public boolean active() { return active; }
    public void set(String message) { active = true; this.message = message; save(); }
    public void disable() { active = false; save(); }
    private void save() { data.set("active", active); data.set("message", message); try { data.save(file); } catch (IOException ex) { plugin.getLogger().warning("Could not save maintenance state: " + ex.getMessage()); } }
    @EventHandler public void login(PlayerLoginEvent event) { if (active && !event.getPlayer().isOp()) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', message)); }
}