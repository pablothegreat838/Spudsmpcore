package com.spudsmp.core.placeholder;

import com.spudsmp.core.SpudSMPPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public final class SpudPlaceholder extends PlaceholderExpansion {
    private final SpudSMPPlugin plugin;
    public SpudPlaceholder(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public String getIdentifier() { return "spudsmp"; }
    public String getAuthor() { return "SpudSMP"; }
    public String getVersion() { return plugin.getDescription().getVersion(); }
    public String onPlaceholderRequest(Player player, String params) { if (player == null) return ""; if (params.equalsIgnoreCase("keys_total")) return String.valueOf(plugin.crates().totalKeys(player)); if (params.startsWith("keys_")) return String.valueOf(plugin.crates().keys(player, params.substring(5))); if (params.equalsIgnoreCase("givekeyall")) return "0"; if (params.equalsIgnoreCase("money")) return String.format("%.2f", plugin.economy().balance(player)); if (params.equalsIgnoreCase("team")) return plugin.teams().team(player); if (params.equalsIgnoreCase("autokey_timer")) return String.valueOf(plugin.getConfig().getInt("crates.autokeyall.timer", 0)); return null; }
}