package com.spudsmp.core;

import com.spudsmp.core.command.SpudCommand;
import com.spudsmp.core.manager.AmethystManager;
import com.spudsmp.core.manager.AnnouncementManager;
import com.spudsmp.core.manager.AuctionManager;
import com.spudsmp.core.manager.ClearLagManager;
import com.spudsmp.core.manager.CrateManager;
import com.spudsmp.core.manager.EconomyManager;
import com.spudsmp.core.manager.GuiManager;
import com.spudsmp.core.manager.HomesManager;
import com.spudsmp.core.manager.MaintenanceManager;
import com.spudsmp.core.manager.PortalManager;
import com.spudsmp.core.manager.ShopManager;
import com.spudsmp.core.manager.StaffManager;
import com.spudsmp.core.manager.TpaManager;
import com.spudsmp.core.manager.TeamManager;
import com.spudsmp.core.manager.WarpManager;
import com.spudsmp.core.manager.ProtectionManager;
import com.spudsmp.core.placeholder.SpudPlaceholder;
import com.spudsmp.core.util.Message;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpudSMPPlugin extends JavaPlugin {
    private Message messages;
    private GuiManager gui;
    private WarpManager warps;
    private TpaManager tpa;
    private CrateManager crates;
    private AuctionManager auctions;
    private ShopManager shop;
    private StaffManager staff;
    private PortalManager portals;
    private EconomyManager economy;
    private HomesManager homes;
    private TeamManager teams;
    private MaintenanceManager maintenance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new Message(this);
        gui = new GuiManager(this);
        warps = new WarpManager(this);
        tpa = new TpaManager(this);
        crates = new CrateManager(this);
        auctions = new AuctionManager(this);
        shop = new ShopManager(this);
        staff = new StaffManager(this);
        economy = new EconomyManager(this);
        homes = new HomesManager(this);
        teams = new TeamManager(this);
        maintenance = new MaintenanceManager(this);

        getServer().getPluginManager().registerEvents(gui, this);
        getServer().getPluginManager().registerEvents(tpa, this);
        getServer().getPluginManager().registerEvents(crates, this);
        getServer().getPluginManager().registerEvents(auctions, this);
        getServer().getPluginManager().registerEvents(shop, this);
        getServer().getPluginManager().registerEvents(new AmethystManager(this), this);
        getServer().getPluginManager().registerEvents(staff, this);
        portals = new PortalManager(this);
        getServer().getPluginManager().registerEvents(portals, this);
        getServer().getPluginManager().registerEvents(new ProtectionManager(this), this);
        getServer().getPluginManager().registerEvents(economy, this);
        getServer().getPluginManager().registerEvents(homes, this);
        getServer().getPluginManager().registerEvents(teams, this);
        getServer().getPluginManager().registerEvents(maintenance, this);

        new ClearLagManager(this);
        new AnnouncementManager(this);
        SpudCommand command = new SpudCommand(this);
        String[] commands = {"spudsmp", "spud-list", "spud-holders", "crate", "ah", "shop", "amethyst", "tpa", "tpaccept", "tpdeny", "tpacancel",
                "leaderboards", "portal", "shutdown", "tpr", "spudclear", "setspawn", "spawn", "setpvp", "pvp",
            "help", "staff", "ptp", "balance", "pay", "baltop", "eco", "homes", "team"};
        for (String name : commands) {
            if (getCommand(name) != null) {
                getCommand(name).setExecutor(command);
                getCommand(name).setTabCompleter(command);
            }
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SpudPlaceholder(this).register();
        }
        messages.info("Plugin enabled. Modular systems are online.");
    }

    @Override
    public void onDisable() {
        if (auctions != null) auctions.save();
        if (economy != null) economy.save();
    }

    public Message messages() { return messages; }
    public GuiManager gui() { return gui; }
    public WarpManager warps() { return warps; }
    public TpaManager tpa() { return tpa; }
    public CrateManager crates() { return crates; }
    public AuctionManager auctions() { return auctions; }
    public ShopManager shop() { return shop; }
    public StaffManager staff() { return staff; }
    public PortalManager portals() { return portals; }
    public EconomyManager economy() { return economy; }
    public HomesManager homes() { return homes; }
    public TeamManager teams() { return teams; }
    public MaintenanceManager maintenance() { return maintenance; }
}