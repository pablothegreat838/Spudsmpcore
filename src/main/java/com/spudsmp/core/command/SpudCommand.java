package com.spudsmp.core.command;

import com.spudsmp.core.SpudSMPPlugin;
import com.spudsmp.core.manager.AuctionManager.Listing;
import com.spudsmp.core.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class SpudCommand implements CommandExecutor, TabCompleter {
    private final SpudSMPPlugin plugin;
    public SpudCommand(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase();
        if (name.equals("spud-list")) { if (admin(sender)) sender.sendMessage(plugin.messages().prefix() + ChatColor.GOLD + "Commands: /balance /pay /baltop /eco /shop /ah /crate /homes /team /tpa /tpaccept /tpacancel /tpr /leaderboards /portal /shutdown"); return true; }
        if (name.equals("spud-holders")) { if (admin(sender)) { sender.sendMessage(plugin.messages().prefix() + ChatColor.GOLD + "Placeholders:"); sender.sendMessage(ChatColor.GRAY + "%spudsmp_money% %spudsmp_keys_<cratename>% %spudsmp_keys_total% %spudsmp_givekeyall% %spudsmp_team% %spudsmp_autokey_timer%"); } return true; }
        if (name.equals("baltop")) { List<Map.Entry<java.util.UUID, Double>> values = new ArrayList<Map.Entry<java.util.UUID, Double>>(plugin.economy().balances().entrySet()); Collections.sort(values, new Comparator<Map.Entry<java.util.UUID, Double>>() { public int compare(Map.Entry<java.util.UUID, Double> a, Map.Entry<java.util.UUID, Double> b) { return Double.compare(b.getValue(), a.getValue()); } }); sender.sendMessage(plugin.messages().prefix() + ChatColor.GOLD + "Top balances:"); for (int i = 0; i < Math.min(10, values.size()); i++) sender.sendMessage(ChatColor.GRAY + "#" + (i + 1) + " " + values.get(i).getKey() + " $" + values.get(i).getValue()); return true; }
        if (name.equals("eco")) { if (!admin(sender) || args.length < 3) return true; Player target = Bukkit.getPlayer(args[1]); double amount = number(args[2]); if (target == null || amount < 0) return true; if (args[0].equalsIgnoreCase("give")) plugin.economy().give(target, amount); else if (args[0].equalsIgnoreCase("take")) plugin.economy().take(target, amount); else if (args[0].equalsIgnoreCase("set")) plugin.economy().set(target, amount); return true; }
        if (name.equals("homes")) { if (sender instanceof Player) plugin.homes().open((Player) sender); return true; }
        if (name.equals("team")) return team(sender, args);
        if (name.equals("tpa")) { if (!(sender instanceof Player) || !plugin.tpa().enabled()) return true; Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null; if (target != null) plugin.tpa().request((Player) sender, target); return true; }
        if (name.equals("tpaccept") || name.equals("tpdeny")) { if (sender instanceof Player) { if (name.equals("tpaccept")) plugin.tpa().accept((Player) sender); else plugin.tpa().deny((Player) sender); } return true; }
        if (name.equals("setspawn") || name.equals("setpvp")) { if (!admin(sender)) return true; if (sender instanceof Player) plugin.warps().set(name.equals("setspawn") ? "spawn" : "pvp", ((Player) sender).getLocation()); plugin.messages().send(sender, "messages.saved", "&aLocation saved."); return true; }
        if (name.equals("spawn") || name.equals("pvp")) { if (sender instanceof Player) { Location location = plugin.warps().get(name); if (location != null) ((Player) sender).teleport(location); else plugin.messages().send(sender, "messages.not-set", "&cThat location has not been set."); } return true; }
        if (name.equals("help") || name.equals("leaderboards") || name.equals("shop") || name.equals("ah") || name.equals("ptp") || name.equals("staff") || (name.equals("tpr") && args.length == 0)) { if (sender instanceof Player) open((Player) sender, name); return true; }
        if (name.equals("spudclear")) { if (admin(sender)) new com.spudsmp.core.manager.ClearLagManager(plugin).clear(); return true; }
        if (name.equals("amethyst")) { if (sender instanceof Player && admin(sender)) ((Player) sender).getInventory().addItem(new ItemBuilder(Material.DIAMOND_PICKAXE).name("&5Amethyst Pickaxe").build(), new ItemBuilder(Material.DIAMOND_AXE).name("&5Amethyst Axe").build()); return true; }
        if (name.equals("crate")) { if (sender instanceof Player && admin(sender) && args.length >= 2) { Player target = Bukkit.getPlayer(args[0]); if (target != null) plugin.crates().giveKey(target, args[1], args.length > 2 ? Integer.parseInt(args[2]) : 1); } return true; }
        if (name.equals("spudsmp") && args.length > 0 && args[0].equalsIgnoreCase("reload") && admin(sender)) { plugin.reloadConfig(); plugin.messages().send(sender, "messages.reloaded", "&aConfiguration reloaded."); return true; }
        if (name.equals("shutdown") && admin(sender)) { if (args.length > 0 && args[0].equalsIgnoreCase("off")) plugin.maintenance().disable(); else plugin.maintenance().set(args.length == 0 ? "The server is under maintenance." : join(args)); return true; }
        if (name.equals("portal") && admin(sender)) { if (args.length < 2) return true; boolean open = args[1].equalsIgnoreCase("open"); if (!open && !args[1].equalsIgnoreCase("close")) return true; plugin.portals().set(args[0], open); plugin.messages().send(sender, "messages.portal", "&aPortal state updated."); return true; }
            if (name.equals("balance")) { if (sender instanceof Player) sender.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Balance: $" + String.format("%.2f", plugin.economy().balance((Player) sender))); return true; }
            if (name.equals("pay")) { if (sender instanceof Player && args.length >= 2) { Player target = Bukkit.getPlayer(args[0]); double amount = number(args[1]); if (target != null && amount > 0 && plugin.economy().take((Player) sender, amount)) { plugin.economy().give(target, amount); sender.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Paid " + target.getName() + " $" + amount + "."); target.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "You received $" + amount + " from " + sender.getName() + "."); } else sender.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Payment failed."); } return true; }
            if (name.equals("homes")) { if (sender instanceof Player) plugin.homes().open((Player) sender); return true; }
            if (name.equals("team")) return team(sender, args);
            if (name.equals("tpa")) { if (!(sender instanceof Player) || !plugin.tpa().enabled()) return true; Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null; if (target != null && target != sender) plugin.tpa().request((Player) sender, target); return true; }
            if (name.equals("tpaccept")) { if (sender instanceof Player) plugin.tpa().accept((Player) sender); return true; }
            if (name.equals("tpdeny") || name.equals("tpacancel")) { if (sender instanceof Player) plugin.tpa().deny((Player) sender); return true; }
            if (name.equals("tpr") && sender instanceof Player) randomTeleport((Player) sender, args.length > 0 ? args[0] : "overworld");
        return true;
    }
    private void open(Player player, String name) { if (name.equals("shop")) { plugin.shop().open(player); return; } Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "SpudSMP " + name.toUpperCase()); if (name.equals("help")) { inventory.setItem(10, new ItemBuilder(Material.BOOK).name("&eCommands").lore("&7/spawn  /pvp  /tpa", "&7/ah  /shop  /tpr").build()); inventory.setItem(16, new ItemBuilder(Material.BARRIER).name("&cClose").build()); } else if (name.equals("staff")) { if (admin(player)) plugin.staff().open(player); return; } else if (name.equals("ptp")) { if (!admin(player)) return; int slot = 0; for (Player target : Bukkit.getOnlinePlayers()) { inventory.setItem(slot++, new ItemBuilder(Material.SKULL_ITEM).name("&b" + target.getName()).build()); if (slot >= 27) break; } } else if (name.equals("tpr")) { inventory.setItem(11, new ItemBuilder(Material.GRASS).name("&aOverworld").build()); inventory.setItem(13, new ItemBuilder(Material.NETHERRACK).name("&cNether").build()); inventory.setItem(15, new ItemBuilder(Material.ENDER_STONE).name("&eEnd").build()); } else { inventory.setItem(13, new ItemBuilder(Material.CHEST).name("&6Browse " + name).lore("&7Use this menu to browse.").build()); } player.openInventory(inventory); }
    private void randomTeleport(Player player, String type) { if (!plugin.getConfig().getBoolean("tpr.worlds." + type, true)) { player.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Sorry but this tpr is not available right now!"); return; } if (!plugin.getConfig().contains("tpr.limits." + type)) { player.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Sorry but the server admin hasn't added a tpr limit"); return; } World world = player.getWorld(); if (type.equalsIgnoreCase("nether")) for (World candidate : Bukkit.getWorlds()) if (candidate.getEnvironment() == World.Environment.NETHER) world = candidate; else if (type.equalsIgnoreCase("end")) for (World endCandidate : Bukkit.getWorlds()) if (endCandidate.getEnvironment() == World.Environment.THE_END) world = endCandidate; int limit = plugin.getConfig().getInt("tpr.limits." + type, -1); if (limit <= 0) return; Random random = new Random(); player.teleport(new Location(world, random.nextInt(limit * 2) - limit, 100, random.nextInt(limit * 2) - limit)); }
    private double number(String value) { try { return Double.parseDouble(value); } catch (NumberFormatException ex) { return -1; } }
    private String join(String[] args) { StringBuilder result = new StringBuilder(); for (String arg : args) { if (result.length() > 0) result.append(' '); result.append(arg); } return result.toString(); }
    private boolean team(CommandSender sender, String[] args) { if (!(sender instanceof Player) || args.length == 0) return true; Player player = (Player) sender; if (args[0].equalsIgnoreCase("create") && args.length > 1) plugin.teams().create(player, args[1]); else if (args[0].equalsIgnoreCase("invite") && args.length > 1) { Player target = Bukkit.getPlayer(args[1]); if (target != null) plugin.teams().invite(player, target); } else if (args[0].equalsIgnoreCase("accept") && args.length > 1) plugin.teams().accept(player, args[1]); else if (args[0].equalsIgnoreCase("decline")) plugin.teams().decline(player); else if (args[0].equalsIgnoreCase("leave")) plugin.teams().leave(player); else if (args[0].equalsIgnoreCase("info")) player.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Team: " + plugin.teams().team(player)); return true; }
    private boolean admin(CommandSender sender) { return sender.hasPermission("spudsmp.admin") || sender.isOp(); }
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { if (command.getName().equalsIgnoreCase("help")) return Collections.emptyList(); if (command.getName().equalsIgnoreCase("tpa")) { List<String> names = new ArrayList<String>(); for (Player player : Bukkit.getOnlinePlayers()) names.add(player.getName()); return names; } return Arrays.asList("help", "reload"); }
}