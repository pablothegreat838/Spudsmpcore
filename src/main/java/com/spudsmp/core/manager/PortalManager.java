package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public final class PortalManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Set<String> closed = new HashSet<String>();
    private final File file;
    private final YamlConfiguration data;
    public PortalManager(SpudSMPPlugin plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "portals.yml"); data = YamlConfiguration.loadConfiguration(file); closed.addAll(data.getStringList("closed")); }
    public void set(String portal, boolean open) { String value = portal.toLowerCase(); if (open) closed.remove(value); else closed.add(value); data.set("closed", new java.util.ArrayList<String>(closed)); try { data.save(file); } catch (IOException ex) { plugin.getLogger().warning("Could not save portal state: " + ex.getMessage()); } }
    public boolean isOpen(String portal) { return !closed.contains(portal.toLowerCase()); }
    @EventHandler public void portal(PlayerPortalEvent event) { String dimension = event.getTo().getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER ? "nether" : event.getTo().getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END ? "end" : "overworld"; if (!isOpen(dimension)) { event.setCancelled(true); event.getPlayer().sendMessage(plugin.messages().prefix() + ChatColor.RED + "The " + dimension + " portal is closed right now."); } }
}