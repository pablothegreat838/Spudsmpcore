package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.util.List;
import org.bukkit.ChatColor;

public final class AnnouncementManager {
    public AnnouncementManager(SpudSMPPlugin plugin) { if (plugin.getConfig().getBoolean("announcements.enabled", true)) { long ticks = plugin.getConfig().getLong("announcements.interval-seconds", 180) * 20L; plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() { private int index; public void run() { List<String> messages = plugin.getConfig().getStringList("announcements.messages"); if (!messages.isEmpty()) { plugin.getServer().broadcastMessage(plugin.messages().prefix() + ChatColor.translateAlternateColorCodes('&', messages.get(index++ % messages.size()))); } } }, ticks, ticks); } }
}