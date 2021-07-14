package org.cathal02.stackerreborn.data;

import org.bukkit.Location;
import org.cathal02.stackerreborn.EntityManager;

public class SpawnerData extends EntityData {
    public Location location;
    public String mobName;
    public SpawnerData(Location location, int numberOfEntities, String name)
    {
        this.numberOfEntities = numberOfEntities;
        this.location = location;
        this.mobName = name;
    }
}
