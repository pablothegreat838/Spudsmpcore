package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import com.spudsmp.core.util.ItemBuilder;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.block.Action;

public final class HomesManager implements Listener {
    private static final String MENU = "SpudSMP Homes";
    private final SpudSMPPlugin plugin;
    private final Map<UUID, Map<Integer, Home>> homes = new HashMap<UUID, Map<Integer, Home>>();
    private final Map<UUID, Integer> pendingBreak = new HashMap<UUID, Integer>();
    private final Map<UUID, Integer> pendingRename = new HashMap<UUID, Integer>();
    private final File file;
    private final YamlConfiguration data;
    public HomesManager(SpudSMPPlugin plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "homes.yml"); data = YamlConfiguration.loadConfiguration(file); load(); }
    private void load() { if (data.getConfigurationSection("homes") == null) return; for (String owner : data.getConfigurationSection("homes").getKeys(false)) { try { UUID id = UUID.fromString(owner); Map<Integer, Home> values = new HashMap<Integer, Home>(); for (String number : data.getConfigurationSection("homes." + owner).getKeys(false)) { int slot = Integer.parseInt(number); String path = "homes." + owner + "." + number; WorldLocation location = new WorldLocation(data.getString(path + ".world"), data.getInt(path + ".x"), data.getInt(path + ".y"), data.getInt(path + ".z")); values.put(slot, new Home(data.getString(path + ".name", "Home " + (slot + 1)), location, data.getBoolean(path + ".respawn", false))); } homes.put(id, values); } catch (Exception ignored) {} } }
    public void open(Player player) { Inventory inventory = Bukkit.createInventory(null, 27, MENU); Map<Integer, Home> values = homes.get(player.getUniqueId()); for (int i = 0; i < 20; i++) { Home home = values == null ? null : values.get(i); inventory.setItem(i, new ItemBuilder(Material.BED).name(home == null ? "&fHome " + (i + 1) : "&a" + home.name).lore(home == null ? "&7Click to receive a special bed." : "&7Placed: " + (home.location.world == null ? "unknown" : home.location.world)).build()); } player.openInventory(inventory); }
    private ItemStack specialBed(int slot, String name) { return new ItemBuilder(Material.BED).name("&a" + name).lore("&8SpudSMP home:" + slot).build(); }
    private boolean isSpecial(ItemStack item) { return item != null && item.getType() == Material.BED && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().toString().contains("SpudSMP home:"); }
    private int slot(ItemStack item) { for (String line : item.getItemMeta().getLore()) if (line.startsWith("SpudSMP home:")) try { return Integer.parseInt(line.substring(14)); } catch (NumberFormatException ignored) {} return -1; }
    @EventHandler public void click(InventoryClickEvent event) { if (!(event.getWhoClicked() instanceof Player) || !event.getView().getTitle().equals(MENU)) return; event.setCancelled(true); if (event.getRawSlot() < 0 || event.getRawSlot() >= 20) return; Player player = (Player) event.getWhoClicked(); int slot = event.getRawSlot(); Map<Integer, Home> values = homes.get(player.getUniqueId()); Home home = values == null ? null : values.get(slot); if (home != null && home.location.toLocation() != null) { player.closeInventory(); player.teleport(home.location.toLocation()); } else { player.getInventory().addItem(specialBed(slot, "Home " + (slot + 1))); player.closeInventory(); } }
    @EventHandler public void place(BlockPlaceEvent event) { if (!isSpecial(event.getItemInHand())) return; Player player = event.getPlayer(); int slot = slot(event.getItemInHand()); Map<Integer, Home> values = homes.get(player.getUniqueId()); if (values == null) { values = new HashMap<Integer, Home>(); homes.put(player.getUniqueId(), values); } values.put(slot, new Home("Home " + (slot + 1), new WorldLocation(event.getBlock().getLocation()), false)); save(); }
    @EventHandler public void interact(PlayerInteractEvent event) { if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return; HomeRef ref = find(event.getClickedBlock()); if (ref == null) return; event.setCancelled(true); Player player = event.getPlayer(); Inventory gui = Bukkit.createInventory(null, 36, "SpudSMP Home " + ref.home.name); gui.setItem(13, new ItemBuilder(Material.SIGN).name("&eRename").build()); gui.setItem(25, new ItemBuilder(Material.COMPASS).name("&bSet Respawn Point").build()); player.openInventory(gui); }
    @EventHandler public void breakBlock(BlockBreakEvent event) { HomeRef ref = find(event.getBlock()); if (ref == null) return; event.setCancelled(true); if (!ref.owner.equals(event.getPlayer().getUniqueId())) { event.getPlayer().sendMessage(plugin.messages().prefix() + ChatColor.RED + "You cannot break another player's home."); return; } pendingBreak.put(event.getPlayer().getUniqueId(), ref.slot); Inventory gui = Bukkit.createInventory(null, 27, "SpudSMP Break Home " + ref.home.name); gui.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&cCancel").build()); gui.setItem(14, new ItemBuilder(Material.BED).name("&cBreak Home " + (ref.slot + 1)).build()); gui.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&aConfirm").build()); event.getPlayer().openInventory(gui); }
    @EventHandler public void gui(InventoryClickEvent event) { if (!(event.getWhoClicked() instanceof Player)) return; Player player = (Player) event.getWhoClicked(); String title = event.getView().getTitle(); if (title.equals("SpudSMP Home " + (pendingBreak.containsKey(player.getUniqueId()) ? "" : ""))) return; if (title.startsWith("SpudSMP Break Home ")) { event.setCancelled(true); if (event.getRawSlot() == 12) { pendingBreak.remove(player.getUniqueId()); player.closeInventory(); } else if (event.getRawSlot() == 16) { Integer slot = pendingBreak.remove(player.getUniqueId()); if (slot != null) { Map<Integer, Home> values = homes.get(player.getUniqueId()); if (values != null) values.remove(slot); save(); } player.closeInventory(); } } else if (title.startsWith("SpudSMP Home ")) { event.setCancelled(true); Integer slot = null; Map<Integer, Home> values = homes.get(player.getUniqueId()); if (values != null) for (Map.Entry<Integer, Home> entry : values.entrySet()) if (title.endsWith(entry.getValue().name)) slot = entry.getKey(); if (slot != null && event.getRawSlot() == 13) { pendingRename.put(player.getUniqueId(), slot); player.closeInventory(); player.sendMessage(plugin.messages().prefix() + ChatColor.YELLOW + "Type the new home name in chat."); } else if (slot != null && event.getRawSlot() == 25) { values.get(slot).respawn = true; save(); player.closeInventory(); player.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Respawn point set."); } } }
    @EventHandler public void rename(AsyncPlayerChatEvent event) { Integer slot = pendingRename.remove(event.getPlayer().getUniqueId()); if (slot == null) return; event.setCancelled(true); Map<Integer, Home> values = homes.get(event.getPlayer().getUniqueId()); if (values != null && values.containsKey(slot)) { values.get(slot).name = ChatColor.stripColor(event.getMessage()); save(); event.getPlayer().sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Home renamed."); } }
    private HomeRef find(Block block) { for (Map.Entry<UUID, Map<Integer, Home>> owner : homes.entrySet()) for (Map.Entry<Integer, Home> entry : owner.getValue().entrySet()) { Location location = entry.getValue().location.toLocation(); if (location != null && location.getBlock().equals(block)) return new HomeRef(owner.getKey(), entry.getKey(), entry.getValue()); } return null; }
    private void save() { for (Map.Entry<UUID, Map<Integer, Home>> owner : homes.entrySet()) for (Map.Entry<Integer, Home> entry : owner.getValue().entrySet()) { String path = "homes." + owner.getKey() + "." + entry.getKey(); Home home = entry.getValue(); data.set(path + ".name", home.name); data.set(path + ".world", home.location.world); data.set(path + ".x", home.location.x); data.set(path + ".y", home.location.y); data.set(path + ".z", home.location.z); data.set(path + ".respawn", home.respawn); } try { data.save(file); } catch (IOException ex) { plugin.getLogger().warning("Could not save homes: " + ex.getMessage()); } }
    private static final class Home { String name; WorldLocation location; boolean respawn; Home(String name, WorldLocation location, boolean respawn) { this.name = name; this.location = location; this.respawn = respawn; } }
    private static final class HomeRef { UUID owner; int slot; Home home; HomeRef(UUID owner, int slot, Home home) { this.owner = owner; this.slot = slot; this.home = home; } }
    private static final class WorldLocation { String world; int x; int y; int z; WorldLocation(Location location) { this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()); } WorldLocation(String world, int x, int y, int z) { this.world = world; this.x = x; this.y = y; this.z = z; } Location toLocation() { return Bukkit.getWorld(world) == null ? null : new Location(Bukkit.getWorld(world), x + .5, y, z + .5); } }
}