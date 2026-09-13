package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import com.spudsmp.core.util.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class TpaManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Map<UUID, UUID> incoming = new HashMap<UUID, UUID>();
    private final Map<UUID, UUID> confirmations = new HashMap<UUID, UUID>();
    private final Map<UUID, Integer> tasks = new HashMap<UUID, Integer>();
    private final Map<UUID, org.bukkit.Location> positions = new HashMap<UUID, org.bukkit.Location>();
    public TpaManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public boolean enabled() { return plugin.getConfig().getBoolean("tpa.enabled", true); }
    public void request(Player sender, Player target) { confirmations.put(sender.getUniqueId(), target.getUniqueId()); Inventory inventory = Bukkit.createInventory(null, 27, "SpudSMP TPA"); inventory.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&cCancel").build()); Material dimension = target.getWorld().getEnvironment() == World.Environment.NETHER ? Material.NETHERRACK : target.getWorld().getEnvironment() == World.Environment.THE_END ? Material.ENDER_STONE : Material.GRASS; inventory.setItem(13, new ItemBuilder(dimension).name("&e" + target.getWorld().getName()).build()); ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); SkullMeta meta = (SkullMeta) head.getItemMeta(); meta.setOwner(target.getName()); meta.setDisplayName(ChatColor.YELLOW + target.getName()); head.setItemMeta(meta); inventory.setItem(14, head); inventory.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&aConfirm").build()); sender.openInventory(inventory); }
    @EventHandler public void click(InventoryClickEvent event) { if (!(event.getWhoClicked() instanceof Player) || !event.getView().getTitle().equals("SpudSMP TPA")) return; event.setCancelled(true); Player sender = (Player) event.getWhoClicked(); if (event.getRawSlot() == 11) { confirmations.remove(sender.getUniqueId()); sender.closeInventory(); } else if (event.getRawSlot() == 17) { UUID targetId = confirmations.remove(sender.getUniqueId()); sender.closeInventory(); Player target = targetId == null ? null : Bukkit.getPlayer(targetId); if (target != null) sendRequest(sender, target); } }
    private void sendRequest(Player sender, Player target) { incoming.put(target.getUniqueId(), sender.getUniqueId()); sender.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Teleport request sent to " + target.getName() + "."); target.sendMessage(plugin.messages().prefix() + ChatColor.YELLOW + sender.getName() + " wants to teleport to you [Click] to accept or do /tpaccept or /tpacancel"); }
    public void accept(Player target) { UUID requesterId = incoming.remove(target.getUniqueId()); if (requesterId == null) { target.sendMessage(plugin.messages().prefix() + ChatColor.RED + "You have no pending teleport requests."); return; } final Player requester = Bukkit.getPlayer(requesterId); if (requester == null) return; final int[] left = { plugin.getConfig().getInt("tpa.teleport-delay-seconds", 5) }; final org.bukkit.Location start = requester.getLocation().clone(); cancel(requester); int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { public void run() { if (left[0] <= 0) { requester.teleport(target); requester.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Teleported."); cancel(requester); return; } requester.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "Teleporting in " + left[0] + "s...")); left[0]--; } }, 0L, 20L); tasks.put(requester.getUniqueId(), task); positions.put(requester.getUniqueId(), start); }
    public void deny(Player target) { UUID requester = incoming.remove(target.getUniqueId()); target.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Teleport request cancelled."); if (requester != null && Bukkit.getPlayer(requester) != null) Bukkit.getPlayer(requester).sendMessage(plugin.messages().prefix() + ChatColor.RED + "Teleport request denied."); }
    private void cancel(Player player) { Integer task = tasks.remove(player.getUniqueId()); positions.remove(player.getUniqueId()); if (task != null) { Bukkit.getScheduler().cancelTask(task); player.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Teleport cancelled because you moved."); } }
    @EventHandler public void move(PlayerMoveEvent event) { org.bukkit.Location start = positions.get(event.getPlayer().getUniqueId()); if (start != null && (start.getX() != event.getTo().getX() || start.getY() != event.getTo().getY() || start.getZ() != event.getTo().getZ() || start.getYaw() != event.getTo().getYaw() || start.getPitch() != event.getTo().getPitch())) cancel(event.getPlayer()); }
}
