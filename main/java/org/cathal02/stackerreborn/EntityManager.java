package org.cathal02.stackerreborn;

import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.cathal02.stackerreborn.data.SpawnerData;
import org.cathal02.stackerreborn.data.EntityDataHandler;
import org.cathal02.stackerreborn.data.MobData;

import java.util.*;

public class EntityManager {

    private Map<Location, SpawnerData> spawners = new HashMap<>();
    private Map<UUID, MobData> stackedMobs = new HashMap<>();

    private EntityDataHandler entityDataHandler;

    public EntityManager(EntityDataHandler dataHandler)
    {
        this.entityDataHandler = dataHandler;

        for(SpawnerData data : entityDataHandler.getSpawners())
        {
            spawners.put(data.location,data);
        }

        for(MobData data : entityDataHandler.getMobs())
        {
            stackedMobs.put(data.entityID, data);
        }

    }

    public void saveEntityData()
    {
        entityDataHandler.saveEntities(spawners.values(), stackedMobs.values());
    }

    public SpawnerData getSpawnerData(Location location)
    {
        return spawners.get(location);
    }
    public MobData getMobData(UUID uuid)
    {
        if(!stackedMobs.containsKey(uuid))return null;
        return stackedMobs.get(uuid);
    }
    public void addSpawnerEntity( Location location,CreatureSpawner spawner)
    {
        if(spawners.containsKey(location))return;
        spawners.put(location, new SpawnerData(location,1,spawner.getCreatureTypeName()));
    }

    public boolean spawnerExists(Location location) {
        return spawners.containsKey(location);
    }
    public boolean mobExists(UUID uuid) {return stackedMobs.containsKey(uuid); }

    public void handleSpawnerDecrease(SpawnerData spawnerData) {
        spawnerData.decreaseEntityCount();
        if(spawnerData.getEntityCount() <=0)
        {
            spawners.remove(spawnerData.location);
        }
    }

    public void addMobEntity(UUID mob) {
        if(stackedMobs.containsKey(mob))return;
        stackedMobs.put(mob, new MobData(1,mob));
    }

    public Collection<SpawnerData> getAllSpawners()
    {
        return spawners.values();
    }

    public void removeMob(UUID mob) {
        stackedMobs.remove(mob);
    }

    public void removeSpawner(Location loc)
    {
        spawners.remove(loc);
        StackerReborn.getHologramManager().removeHologram(loc);
    }
}
