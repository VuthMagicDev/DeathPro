package org.firestorm.deathpro.EventListener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.*;
import org.firestorm.deathpro.DeathPro;
import org.firestorm.deathpro.Utils.Color;

public class Prevent implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(event.getPlayer()) && event.isSneaking())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.colorize("&cConnot use command while in spectator!"));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    void onPlayerQuit(PlayerQuitEvent event) {
        DeathPro.getInstance().getRespawnDelay().respawnPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void onPlayerTakeDamage(EntityDamageByEntityEvent event) {
        if (DeathPro.getInstance().getRespawnDelay().isSpectating((Player) event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void onPlayerRegainingHealth(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getEntityType() != EntityType.PLAYER)
            return;
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(player))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerSwingHand(PlayerAnimationEvent event) {
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        private void onPlayerInteract(PlayerInteractEvent event) {
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onPlayerPickup(EntityPickupItemEvent event) {
        if (event.getEntityType() != EntityType.PLAYER)
            return;
        Player player = (Player)event.getEntity();
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (DeathPro.getInstance().getRespawnDelay().isSpectating(player)) {
            event.setCancelled(true);
        }
    }
}
