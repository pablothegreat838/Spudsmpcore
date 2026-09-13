package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class EconomyManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Map<UUID, Double> balances = new HashMap<UUID, Double>();
    private final Map<UUID, Map<String, Integer>> stats = new HashMap<UUID, Map<String, Integer>>();
    private final File file;
    private final YamlConfiguration data;
    public EconomyManager(SpudSMPPlugin plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "data.yml"); data = YamlConfiguration.loadConfiguration(file); if (data.getConfigurationSection("players") != null) for (String key : data.getConfigurationSection("players").getKeys(false)) { try { UUID id = UUID.fromString(key); balances.put(id, data.getDouble("players." + key + ".money", 0.0)); Map<String, Integer> values = new HashMap<String, Integer>(); if (data.getConfigurationSection("players." + key + ".stats") != null) for (String stat : data.getConfigurationSection("players." + key + ".stats").getKeys(false)) values.put(stat, data.getInt("players." + key + ".stats." + stat)); stats.put(id, values); } catch (IllegalArgumentException ignored) {} } }
    public double balance(Player player) { return balance(player.getUniqueId()); }
    public double balance(UUID id) { return balances.containsKey(id) ? balances.get(id) : 0.0; }
    public void set(UUID id, double amount) { balances.put(id, Math.max(0, amount)); save(); }
    public void set(Player player, double amount) { set(player.getUniqueId(), amount); }
    public void give(Player player, double amount) { set(player, balance(player) + amount); }
    public boolean take(Player player, double amount) { if (amount < 0 || balance(player) < amount) return false; set(player, balance(player) - amount); return true; }
    public int stat(Player player, String name) { Map<String, Integer> values = stats.get(player.getUniqueId()); return values == null || !values.containsKey(name) ? 0 : values.get(name); }
    public void increment(Player player, String name) { Map<String, Integer> values = stats.get(player.getUniqueId()); if (values == null) { values = new HashMap<String, Integer>(); stats.put(player.getUniqueId(), values); } values.put(name, stat(player, name) + 1); save(); }
    public Map<UUID, Double> balances() { return balances; }
    public void save() { for (UUID id : balances.keySet()) { String path = "players." + id; data.set(path + ".money", balances.get(id)); Map<String, Integer> values = stats.get(id); if (values != null) for (Map.Entry<String, Integer> entry : values.entrySet()) data.set(path + ".stats." + entry.getKey(), entry.getValue()); } try { if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs(); data.save(file); } catch (IOException ex) { plugin.getLogger().warning("Could not save player data: " + ex.getMessage()); } }
    @EventHandler public void join(PlayerJoinEvent event) { if (!balances.containsKey(event.getPlayer().getUniqueId())) { balances.put(event.getPlayer().getUniqueId(), 0.0); save(); } }
    @EventHandler public void breakBlock(BlockBreakEvent event) { increment(event.getPlayer(), "blocks_destroyed"); }
    @EventHandler public void placeBlock(BlockPlaceEvent event) { increment(event.getPlayer(), "blocks_placed"); }
    @EventHandler public void death(EntityDeathEvent event) { if (event.getEntity() instanceof Player) increment((Player) event.getEntity(), "deaths"); if (event.getEntity().getKiller() != null) increment(event.getEntity().getKiller(), "kills"); }
}