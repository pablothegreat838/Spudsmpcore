package com.spudsmp.core.util;

import com.spudsmp.core.SpudSMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Message {
    private final SpudSMPPlugin plugin;
    public Message(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public String color(String value) { return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value); }
    public String prefix() { return color(plugin.getConfig().getString("prefix", "&8[&6SpudSMP&8] &7")); }
    public String text(String path, String fallback) { return prefix() + color(plugin.getConfig().getString(path, fallback)); }
    public void send(CommandSender sender, String path, String fallback) { sender.sendMessage(text(path, fallback)); }
    public void info(String message) { plugin.getServer().getConsoleSender().sendMessage(prefix() + color(message)); }
}