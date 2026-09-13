package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public final class AuctionManager implements Listener {
    public static final class Listing { public final String seller; public final ItemStack item; public final double price; Listing(String seller, ItemStack item, double price) { this.seller = seller; this.item = item; this.price = price; } }
    private final SpudSMPPlugin plugin;
    private final List<Listing> listings = new ArrayList<Listing>();
    public AuctionManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public void list(Player player, double price) { ItemStack item = player.getInventory().getItemInMainHand(); if (item == null || item.getType().name().equals("AIR")) return; listings.add(new Listing(player.getName(), item.clone(), price)); player.getInventory().setItemInMainHand(null); player.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Item listed for $" + price + "."); }
    public List<Listing> listings() { return listings; }
    public void save() {}
}