package org.firestorm.deathpro.EventListener;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.firestorm.deathpro.DeathPro;
import org.firestorm.deathpro.Utils.Color;

public class PlayerDeath implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Location location = p.getLocation();
        location.setY(location.getY() + 1);
        String playerName = p.getName();
        String deathMessage;

        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            String killerName = killer.getName();
            deathMessage = DeathPro.getInstance().getConfigManager().getPrefix() + DeathPro.getInstance().getConfigManager().replacePlaceholders(DeathPro.getInstance().getConfigManager().getKillerMessage(), playerName, killerName, coordinate(p));
        } else {
            deathMessage = DeathPro.getInstance().getConfigManager().getPrefix() + DeathPro.getInstance().getConfigManager().replacePlayerCoordinate(DeathPro.getInstance().getConfigManager().getDeathMessage(), playerName, coordinate(p));
        }

        e.setDeathMessage(Color.colorize(deathMessage));

        Bukkit.getScheduler().runTask(DeathPro.getInstance(), () -> {
            p.spigot().respawn();
            if (location.getY() > -120) p.teleport(location);

            startSpectate(p);
        });
    }

    private void startSpectate(Player p) {
        Location location = p.getLocation();
        Bukkit.getScheduler().runTaskLater(DeathPro.getInstance(), () -> {
            if (DeathPro.getInstance().getConfigManager().getConfig().getBoolean("respawn-settings.respawn-spectator")) {
                if (DeathPro.getInstance().getRespawnDelay().isSpectating(p)) return;

                loadChunks(location);

                if (p.getLocation().getY() > -120) {
                    if (DeathPro.getInstance().getConfigManager().getConfig().getBoolean("corpse-settings.npc-spawn")) DeathPro.getInstance().getNpc().sendPlayerPacket(p);
                }

                DeathPro.getInstance().getRespawnDelay().startDeathSpectate(p);
            }
        }, 1);
    }

    private String coordinate(Player player) {
        return player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
    }

    private void loadChunks(Location location) {
        int chunkX = location.getChunk().getX();
        int chunkZ = location.getChunk().getZ();
        int radius = 2;
        World world = location.getWorld();

        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                if (!chunk.isLoaded()) {
                    chunk.load(true);
                }
            }
        }
    }
}
