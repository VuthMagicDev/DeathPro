package org.firestorm.deathpro.Utils;

import io.github.retrooper.packetevents.util.SpigotReflectionUtil;

import java.util.UUID;

public class Entityd {
    protected final int entityId;
    protected String name;
    protected final UUID uuid;

    public Entityd() {
        this.entityId = SpigotReflectionUtil.generateEntityId();
        this.name = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.uuid = UUID.randomUUID();
    }

    public int getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
