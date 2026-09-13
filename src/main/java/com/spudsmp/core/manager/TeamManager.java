package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class TeamManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Map<String, Set<UUID>> teams = new HashMap<String, Set<UUID>>();
    private final Map<UUID, String> membership = new HashMap<UUID, String>();
    private final Map<UUID, String> invitations = new HashMap<UUID, String>();
    private final File file;
    private final YamlConfiguration data;
    public TeamManager(SpudSMPPlugin plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "teams.yml"); data = YamlConfiguration.loadConfiguration(file); for (String name : data.getStringList("teams")) { Set<UUID> members = new HashSet<UUID>(); for (String raw : data.getStringList("members." + name)) try { UUID id = UUID.fromString(raw); members.add(id); membership.put(id, name); } catch (IllegalArgumentException ignored) {} teams.put(name, members); } }
    public String team(Player player) { return membership.containsKey(player.getUniqueId()) ? membership.get(player.getUniqueId()) : "None"; }
    public void create(Player player, String name) { if (membership.containsKey(player.getUniqueId()) || teams.containsKey(name)) return; Set<UUID> members = new HashSet<UUID>(); members.add(player.getUniqueId()); teams.put(name, members); membership.put(player.getUniqueId(), name); save(); }
    public boolean invite(Player owner, Player target) { String name = membership.get(owner.getUniqueId()); if (name == null || membership.containsKey(target.getUniqueId())) return false; invitations.put(target.getUniqueId(), name); target.sendMessage(plugin.messages().prefix() + ChatColor.YELLOW + owner.getName() + " has invited you to their team " + name + ". Use /team accept " + owner.getName() + " or /team decline " + owner.getName() + "."); return true; }
    public boolean accept(Player player, String owner) { String name = invitations.remove(player.getUniqueId()); if (name == null || membership.containsKey(player.getUniqueId())) return false; teams.get(name).add(player.getUniqueId()); membership.put(player.getUniqueId(), name); save(); return true; }
    public void decline(Player player) { invitations.remove(player.getUniqueId()); }
    public void leave(Player player) { String name = membership.remove(player.getUniqueId()); if (name != null) { Set<UUID> members = teams.get(name); members.remove(player.getUniqueId()); if (members.isEmpty()) teams.remove(name); save(); } }
    public boolean friendly(Entity first, Entity second) { return first instanceof Player && second instanceof Player && !team((Player) first).equals("None") && team((Player) first).equals(team((Player) second)); }
    private void save() { data.set("teams", new java.util.ArrayList<String>(teams.keySet())); for (Map.Entry<String, Set<UUID>> entry : teams.entrySet()) { java.util.List<String> ids = new java.util.ArrayList<String>(); for (UUID id : entry.getValue()) ids.add(id.toString()); data.set("members." + entry.getKey(), ids); } try { data.save(file); } catch (IOException ex) { plugin.getLogger().warning("Could not save teams: " + ex.getMessage()); } }
    @EventHandler public void damage(EntityDamageByEntityEvent event) { if (friendly(event.getDamager(), event.getEntity())) event.setCancelled(true); }
}