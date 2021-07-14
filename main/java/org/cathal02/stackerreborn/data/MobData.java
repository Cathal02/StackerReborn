package org.cathal02.stackerreborn.data;

import org.bukkit.Location;

import java.util.UUID;

public class MobData extends EntityData {
    public MobData(int numberOfEntities, UUID entityID) {
        this.entityID = entityID;
        this.numberOfEntities = numberOfEntities;
    }

    public UUID entityID;
}
