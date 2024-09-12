package org.firestorm.deathpro.Utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerNPC {
    private final Map<UUID, Entityd> playerNPCMap = new ConcurrentHashMap<>();

    public Entityd getOrCreateNPC(UUID playerUUID) {
        return playerNPCMap.computeIfAbsent(playerUUID, k -> new Entityd());
    }

    public Entityd getNPC(UUID playerUUID) {
        return playerNPCMap.get(playerUUID);
    }

    public void removeNPC(UUID playerUUID) {
        playerNPCMap.remove(playerUUID);
    }

    public boolean hasNPC(UUID playerUUID) {
        return playerNPCMap.containsKey(playerUUID);
    }
}
