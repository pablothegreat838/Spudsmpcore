package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class GuiManager implements Listener {
    public static final String HELP = "&8SpudSMP Help";
    private final SpudSMPPlugin plugin;
    public GuiManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    @EventHandler public void click(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("SpudSMP")) event.setCancelled(true);
    }
    @EventHandler public void drag(InventoryDragEvent event) {
        if (event.getView().getTitle().contains("SpudSMP")) event.setCancelled(true);
    }
}