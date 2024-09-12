package org.firestorm.deathpro;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.firestorm.deathpro.Commands.CommandManager;
import org.firestorm.deathpro.EventListener.BloodParticles;
import org.firestorm.deathpro.EventListener.PlayerDeath;
import org.firestorm.deathpro.EventListener.Prevent;
import org.firestorm.deathpro.PacketManager.Corpse;
import org.firestorm.deathpro.Utils.Color;
import org.firestorm.deathpro.Utils.ConfigManager;
import org.firestorm.deathpro.Utils.RespawnDelay;

public final class DeathPro extends JavaPlugin implements Listener {
    public static DeathPro instance;
    public Corpse npc;
    public RespawnDelay respawnDelay;
    private ConfigManager configManager;
    private ProtocolManager protocolManager;
    private PlayerManager playerManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(false);
        this.playerManager = PacketEvents.getAPI().getPlayerManager();
        this.protocolManager = PacketEvents.getAPI().getProtocolManager();
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        PacketEvents.getAPI().init();
        configManager = new ConfigManager();
        saveDefaultConfig();
        configManager.setupConfig();
        npc = new Corpse();
        respawnDelay = new RespawnDelay();

        registerCommand();
        registerListeners();

        getServer().getConsoleSender().sendMessage(Color.colorize("&a[DeathPro] has been enabled!"));
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getServer().getConsoleSender().sendMessage(Color.colorize("&c[DeathPro] has been disabled!"));
    }

    private void registerCommand() {
        getCommand("deathpro").setExecutor(new CommandManager());
        getCommand("deathpro").setTabCompleter(new CommandManager());
    }

    private void registerListeners() {
        registerListener(
                new PlayerDeath(),
                new BloodParticles(),
                new Prevent());
    }

    private void registerListener(Listener... listener) {
        for(Listener listener1 : listener)
            getServer().getPluginManager().registerEvents(listener1, this);
    }

    public static DeathPro getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Corpse getNpc() {
        return npc;
    }

    public RespawnDelay getRespawnDelay() {
        return respawnDelay;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
