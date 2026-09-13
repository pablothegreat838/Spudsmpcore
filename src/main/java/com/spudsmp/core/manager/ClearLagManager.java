package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;

public final class ClearLagManager {
    private final SpudSMPPlugin plugin;
    public ClearLagManager(SpudSMPPlugin plugin) { this.plugin = plugin; if (plugin.getConfig().getBoolean("clearlag.enabled", true)) schedule(); }
    private void schedule() { long interval = plugin.getConfig().getLong("clearlag.interval-seconds", 600) * 20L; plugin.getServer().getScheduler().runTaskTimer(plugin, this::clear, interval, interval); }
    public int clear() { int removed = 0; for (org.bukkit.World world : plugin.getServer().getWorlds()) for (Entity entity : world.getEntities()) if ((entity instanceof Item && plugin.getConfig().getBoolean("clearlag.clear-items", true)) || (entity instanceof ExperienceOrb && plugin.getConfig().getBoolean("clearlag.clear-xp", true))) { entity.remove(); removed++; } plugin.getServer().broadcastMessage(plugin.messages().prefix() + ChatColor.YELLOW + "Cleared " + removed + " dropped entities."); return removed; }
}