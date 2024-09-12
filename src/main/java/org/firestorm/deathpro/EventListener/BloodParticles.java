package org.firestorm.deathpro.EventListener;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.firestorm.deathpro.DeathPro;

public class BloodParticles implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Location location;
        if (DeathPro.getInstance().getConfigManager().getConfig().getBoolean("blood-effect.enable")) {
            if (event.getEntity() instanceof Player && DeathPro.getInstance().getConfigManager().getConfig().getBoolean("blood-effect.player")) {
                Player player = (Player) event.getEntity();
                if (player.getGameMode() == GameMode.SURVIVAL && event.getDamage() == 0.0) {
                    return;
                }
                location = player.getLocation();
                double middleX = location.getX();
                double middleY = location.getY() + 1.9;
                double middleZ = location.getZ();

                player.getWorld().playEffect(new Location(player.getWorld(), middleX, middleY, middleZ), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            }
            if (!(event.getEntity() instanceof Player && DeathPro.getInstance().getConfigManager().getConfig().getBoolean("blood-effect.mob"))) {
                if (event.getDamage() == 0.0) {
                    return;
                }
                Entity entity = event.getEntity();
                location = entity.getLocation();
                location.getWorld().playEffect(location, Effect.STEP_SOUND, (Object)Material.REDSTONE_BLOCK);
            }
        }
    }
}
