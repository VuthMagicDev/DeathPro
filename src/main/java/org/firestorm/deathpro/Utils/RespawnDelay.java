package org.firestorm.deathpro.Utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.firestorm.deathpro.DeathPro;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RespawnDelay {
    private final List<Player> spectatePlayer = new ArrayList<>();
    private final ConfigManager config = DeathPro.getInstance().getConfigManager();
    public boolean startDeathSpectate(Player player) {
        if (isSpectating(player))
            return false;
        try {
            if (!setSpectating(player, true))
                return false;

            player.closeInventory();
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 25, 1, 0.5, 1, 0.001);

            startCountdown(player, config.getConfig().getInt("respawn-settings.respawn-delay-seconds"));

            return true;
        } catch (Exception e) {
            DeathPro.getInstance().getLogger().log(Level.SEVERE, "An error occurred while trying to start spectating for " + player.getName());
            DeathPro.getInstance().getLogger().log(Level.SEVERE, "Report the following stacktrace in full:\n");
            e.printStackTrace();
            setSpectating(player, false);
            return false;
        }
    }

    private void startCountdown(Player player, int seconds) {
        new BukkitRunnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    player.sendTitle(
                            config.replacePlaceholders(Color.colorize(config.getConfig().getString("respawn-settings.respawn-title")), timeLeft),
                            config.replacePlaceholders(Color.colorize(config.getConfig().getString("respawn-settings.respawn-subtitle")), timeLeft),
                            0, 50, 10
                    );
                    timeLeft--;
                } else {
                    if (config.getConfig().getBoolean("respawn-settings.respawn-by-command")) {
                        respawnByCommand(player);
                    } else {
                        respawnPlayer(player);
                    }
                    player.sendTitle(
                            Color.colorize(config.getConfig().getString("respawn-settings.respawned-title")),
                            Color.colorize(config.getConfig().getString("respawn-settings.respawned-subtitle")),
                            0, 50, 10
                    );
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    cancel();
                }
            }
        }.runTaskTimer(DeathPro.getInstance(), 0, 20L);
    }

    private void respawnByCommand(Player player) {
        String respawnCommand = config.replacePlaceholders(config.getConfig().getString("respawn-settings.respawn-command"), player.getName());
        if (Bukkit.getPlayer(player.getName()) != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), respawnCommand);
        }
        setSpectating(player, false);
    }

    public boolean respawnPlayer(Player player) {
        if (!isSpectating(player) || player.isDead())
            return false;

        Location spawnLocation = player.getBedSpawnLocation();

        boolean bedSpawn = true;
        if (spawnLocation == null) {
            spawnLocation = player.getWorld().getSpawnLocation();
            bedSpawn = false;
        }

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, spawnLocation, bedSpawn);
        DeathPro.getInstance().getServer().getPluginManager().callEvent(respawnEvent);

        player.teleport(respawnEvent.getRespawnLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
        setSpectating(player, false);
        return true;
    }

    public boolean isSpectating(Player player) {
        return spectatePlayer.contains(player);
    }

    public boolean setSpectating(Player player, boolean spectate) {
        if (spectate) {
            spectatePlayer.add(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setInvisible(true);
            if (!config.getConfig().getBoolean("settings.fly-on-spectator")) player.setFlySpeed(0.0f);
        } else {
            spectatePlayer.remove(player);
            player.setAllowFlight(true);
            player.setFlying(false);
            player.setInvisible(false);
            if (!config.getConfig().getBoolean("settings.fly-on-spectator")) player.setFlySpeed(0.1f);
        }
        return true;
    }
}
