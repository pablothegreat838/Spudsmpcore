package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class AmethystManager implements Listener {
    private final SpudSMPPlugin plugin;
    public AmethystManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    @EventHandler public void breakBlock(BlockBreakEvent event) { ItemStack tool = event.getPlayer().getInventory().getItemInMainHand(); if (tool == null || !tool.hasItemMeta() || !tool.getItemMeta().hasDisplayName() || !tool.getItemMeta().getDisplayName().contains("Amethyst")) return; if (tool.getType() == Material.DIAMOND_PICKAXE) mine3x3(event.getPlayer(), event.getBlock()); else if (tool.getType() == Material.DIAMOND_AXE) fell(event.getBlock(), 0); }
    private void mine3x3(Player player, Block center) { for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) { Block block = center.getRelative(x, y, z); if (block.getType() != Material.AIR && block.getType().isSolid()) block.breakNaturally(player.getInventory().getItemInMainHand()); } }
    private void fell(Block block, int count) { if (count > 128 || (block.getType() != Material.LOG && block.getType() != Material.LOG_2)) return; block.breakNaturally(); for (int y = 1; y < 32; y++) fell(block.getRelative(0, y, 0), count + 1); }
}