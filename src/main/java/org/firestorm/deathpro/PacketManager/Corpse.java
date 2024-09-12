package org.firestorm.deathpro.PacketManager;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.firestorm.deathpro.DeathPro;
import org.firestorm.deathpro.Utils.Color;
import org.firestorm.deathpro.Utils.ConfigManager;
import org.firestorm.deathpro.Utils.Entityd;
import org.firestorm.deathpro.Utils.PlayerNPC;

import java.util.*;

public class Corpse {
    private final Packet packetManager;
    private final int npcRemoveDelay;
    private final PlayerNPC playerNPC;
    private final Map<Object, BukkitRunnable> playerTasks = new HashMap<>();
    private final ConfigManager config;

    public Corpse() {
        this.packetManager = new Packet();
        this.playerNPC = new PlayerNPC();
        this.config = DeathPro.getInstance().getConfigManager();
        this.npcRemoveDelay = config.getConfig().getInt("corpse-settings.npc-remove-delay-seconds");
    }

    public void sendPlayerPacket(Player player) {
        Entityd npc = playerNPC.getOrCreateNPC(player.getUniqueId());
        Object channel = DeathPro.getInstance().getPlayerManager().getUser(player).getChannel();

        Profile.createProfile(player, npc.getUuid(), npc.getName()).thenAcceptAsync(userProfile -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketWrapper<?> playerInfo = packetManager.createPlayerInfoPacket(true, userProfile, npc.getName());
                    PacketWrapper<?> playerPacket = packetManager.createPlayerPacket(true, npc.getEntityId(), npc.getName(), userProfile, player);
                    PacketWrapper<?> playerInfoRemove = packetManager.createPlayerInfoPacket(false, userProfile, npc.getName());
                    PacketWrapper<?> laydownPacket = packetManager.createLaydownPacket(npc.getEntityId());

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        Object channels = DeathPro.getInstance().getPlayerManager().getUser(onlinePlayer).getChannel();
                        showToPlayer(channels, playerInfo, playerPacket, laydownPacket);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                Object channels = DeathPro.getInstance().getPlayerManager().getUser(onlinePlayer).getChannel();
                                showToPlayer(channels, playerInfoRemove);
                            }
                        }
                    }.runTaskLaterAsynchronously(DeathPro.getInstance(), 2);

                    removePacket(channel, userProfile, player, npc);
                }
            }.runTaskAsynchronously(DeathPro.getInstance());
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public void removePacket(Object channel, UserProfile profile, Player player, Entityd npc) {
        if (playerTasks.containsKey(channel)) {
            playerTasks.get(channel).cancel();
        }
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Object channels = DeathPro.getInstance().getPlayerManager().getUser(onlinePlayer).getChannel();
                    PacketWrapper<?> playerPacketRemove = packetManager.createPlayerPacket(false, npc.getEntityId(), npc.getName(), profile, player);
                    showToPlayer(channels, playerPacketRemove);
                    playerTasks.remove(channel);
                }
            }
        };
        timer.runTaskLaterAsynchronously(DeathPro.getInstance(), 20L * npcRemoveDelay);
        playerTasks.put(channel, timer);
    }

    public void showToPlayer(Object channel, PacketWrapper<?>... packets) {
        for (PacketWrapper<?> packet : packets) {
            DeathPro.getInstance().getProtocolManager().sendPacket(channel, packet);
        }
    }
}
