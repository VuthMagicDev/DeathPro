package org.firestorm.deathpro.PacketManager;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class Packet {
    public PacketWrapper<?> createPlayerInfoPacket(boolean add, UserProfile profile, String name) {
        PacketWrapper<?> packet;
        if (add) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                packet = new WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, getModernPlayerInfoData(profile, name));
            } else {
                packet = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, getLegacyPlayerInfoData(profile, name));
            }
        } else {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                packet = new WrapperPlayServerPlayerInfoRemove(profile.getUUID());
            } else {
                packet = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, getLegacyPlayerInfoData(profile, name));
            }
        }
        return packet;
    }

    public PacketWrapper<?> createPlayerPacket(boolean add, int entityId, String entityName, UserProfile profile, Player player) {
        PacketWrapper<?> packet;
        Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        location.setY(location.getBlockY());
        while (location.getBlock().getType() == Material.AIR) {
            location.subtract(0, 1, 0);
        }
        location.add(0, 1, 0);
        if (add) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
                packet = new WrapperPlayServerSpawnEntity(entityId, profile.getUUID(), EntityTypes.PLAYER, SpigotConversionUtil.fromBukkitLocation(location), SpigotConversionUtil.fromBukkitLocation(location).getYaw(), 0, null);
            } else {
                packet = new WrapperPlayServerSpawnPlayer(entityId, profile.getUUID(), SpigotConversionUtil.fromBukkitLocation(location));
            }

            hideNameTag(player, entityName);
        } else {
            packet = new WrapperPlayServerDestroyEntities(entityId);
        }
        return packet;
    }

    public PacketWrapper<?> createLaydownPacket(int entityId) {
        byte b = (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40 | 0x80);
        int skinIndex = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17) ? 17 : 16;
        EntityData pose = new EntityData(6, EntityDataTypes.ENTITY_POSE, EntityPose.SLEEPING);
        EntityData skin = new EntityData(skinIndex, EntityDataTypes.BYTE, b);
        ArrayList<EntityData> entityData = new ArrayList<>();
        entityData.add(pose);
        entityData.add(skin);
        return new WrapperPlayServerEntityMetadata(entityId, entityData);
    }

    public void hideNameTag(Player player, String name) {
        String TEAM_NAME = "DeathPro";
        Scoreboard scoreboard = player.getScoreboard();
        Team npcs = null;
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().equals(TEAM_NAME)) {
                npcs = team;
                break;
            }
        }
        if (npcs == null) {
            npcs = scoreboard.registerNewTeam(TEAM_NAME);
        }
        npcs.setNameTagVisibility(NameTagVisibility.NEVER);
        npcs.addEntry(name);
    }

    public WrapperPlayServerPlayerInfo.PlayerData getLegacyPlayerInfoData(UserProfile profile, String name) {
        return new WrapperPlayServerPlayerInfo.PlayerData(Component.text(name),
                profile, GameMode.CREATIVE,
                1);
    }

    public WrapperPlayServerPlayerInfoUpdate.PlayerInfo getModernPlayerInfoData(UserProfile profile, String name) {
        return new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile, true, 1, GameMode.CREATIVE, Component.text(name), null);
    }
}
