package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class ProtectionManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Set<String> blocked = new HashSet<String>(Arrays.asList("plugins", "pl", "bukkit:plugins", "bukkit:pl", "version", "bukkit:version"));
    public ProtectionManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    @EventHandler public void command(PlayerCommandPreprocessEvent event) { String command = event.getMessage().substring(1).split(" ")[0].toLowerCase(); if (blocked.contains(command) && !event.getPlayer().hasPermission("spudsmp.bypass")) { event.setCancelled(true); event.getPlayer().sendMessage(plugin.messages().prefix() + ChatColor.RED + "Unknown command."); } }
}