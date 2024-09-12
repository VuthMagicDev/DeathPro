package org.firestorm.deathpro.PacketManager;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.google.common.collect.Multimap;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Profile {
    public static CompletableFuture<UserProfile> createProfile(Player player, UUID uuid, String name) {
        return CompletableFuture.completedFuture(new UserProfile(uuid, name, SpigotReflectionUtil.getUserProfile(player)));
    }
}

