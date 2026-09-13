package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class CrateManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Map<UUID, Map<String, Integer>> keys = new HashMap<UUID, Map<String, Integer>>();
    public CrateManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public int keys(Player player, String crate) { Map<String, Integer> map = keys.get(player.getUniqueId()); return map == null ? 0 : map.containsKey(crate) ? map.get(crate) : 0; }
    public int totalKeys(Player player) { int total = 0; Map<String, Integer> map = keys.get(player.getUniqueId()); if (map != null) for (Integer count : map.values()) total += count; return total; }
    public void giveKey(Player player, String crate, int amount) { Map<String, Integer> map = keys.get(player.getUniqueId()); if (map == null) { map = new HashMap<String, Integer>(); keys.put(player.getUniqueId(), map); } map.put(crate, keys(player, crate) + amount); }
    @EventHandler public void interact(PlayerInteractEvent event) { if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) return; Player player = event.getPlayer(); if (keys(player, "common") > 0) { giveKey(player, "common", -1); player.sendMessage(plugin.messages().prefix() + ChatColor.GOLD + "Crate opened! You received a reward."); player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 8)); } }
    public void save() {}
}