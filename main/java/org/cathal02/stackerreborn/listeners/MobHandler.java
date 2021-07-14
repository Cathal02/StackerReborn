package org.cathal02.stackerreborn.listeners;

import de.dustplanet.util.SilkUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;
import org.cathal02.stackerreborn.ItemDropHandler;
import org.cathal02.stackerreborn.EntityManager;
import org.cathal02.stackerreborn.StackerReborn;
import org.cathal02.stackerreborn.data.MobData;

import java.util.Collection;

public class MobHandler implements Listener{

    EntityManager entityManager;
    ItemDropHandler entityDropHandler;
    SilkUtil silkUtil;
    StackerReborn plugin;

    int maxStack;
    public MobHandler(StackerReborn stackerReborn) {
        entityManager = stackerReborn.getEntityManager();
        entityDropHandler = stackerReborn.getDropHandler();
        silkUtil = stackerReborn.getSilkUtil();
    this.plugin = stackerReborn;

    maxStack = plugin.getConfig().getInt("maxMobStack");
    }

    @EventHandler
    public void entitySpawnEvent(CreatureSpawnEvent e)
    {
        if(maxStack <0)return;
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))
        {
            return;
        }

        Collection<Entity> nearbyEntities = e.getLocation().getWorld().getNearbyEntities(e.getLocation(),5,5,5);

        Entity firstMatch = null;
        for(Entity entity : nearbyEntities)
        {
            if(entity.getType() == e.getEntityType())
            {
                if(entityManager.mobExists(entity.getUniqueId()))
                {
                    MobData data = entityManager.getMobData(entity.getUniqueId());
                    if(data.getEntityCount() >= maxStack)continue;

                    data.increaseEntityCount();
                    StackerReborn.getHologramManager().updateMobTitle(entity, data);

                    e.setCancelled(true);
                    break;
                }else {
                    firstMatch = entity;
                }
            }
        }

        if(firstMatch != null)
        {
            entityManager.addMobEntity(firstMatch.getUniqueId());
            MobData data = entityManager.getMobData(firstMatch.getUniqueId());

            data.increaseEntityCount();;
            StackerReborn.getHologramManager().updateMobTitle(firstMatch, data);

            e.setCancelled(true);
        }


        }

    @EventHandler
    public void onEntityDeath2(EntityDamageEvent e)
    {
        if(maxStack <0)return;
        if(!(e.getEntity() instanceof LivingEntity))return;
        if(e.getCause() == EntityDamageEvent.DamageCause.CUSTOM)return;
        if(handleEntityTakeDamage(e.getEntity(), e.getDamage()))
            {
                LivingEntity ent = (LivingEntity)e.getEntity();
                Location loc = ent.getLocation();
                MobData data = entityManager.getMobData(ent.getUniqueId());
                if(plugin.getConfig().getBoolean("killEntireStack") && data.getEntityCount()>1)
                {
                    int amount = data.getEntityCount();
                        for(int i = 0; i < amount-1;i++)
                        {
                            spawnAndKillEntity(ent,e,loc);
                            data.decreaseEntityCount();
                        }
                        ent.damage(ent.getHealth());
                        return;
                }

                if(data.getEntityCount()>1)
                {
                    data.decreaseEntityCount();
                    StackerReborn.getHologramManager().updateMobTitle(ent,data);
                    e.setCancelled(true);
                    spawnAndKillEntity(ent,e,loc);

                }
                else {
                    if(!ent.isDead())
                    {
                        ent.damage(ent.getHealth());
                    }

                    //Remove entity from database
                    entityManager.removeMob(ent.getUniqueId());
                }
            }
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent e)
    {
        if(maxStack <0)return;
        entityManager.removeMob(e.getEntity().getUniqueId());
    }



    // Returns true if entity dies
    private boolean handleEntityTakeDamage(Entity entity, double damage) {
        if(entity == null) return false;
        if(!(entity instanceof LivingEntity)) return false;
        // Ensures our mob has died
        LivingEntity mob = (LivingEntity)entity;
        if(mob.getHealth() - damage >0) return false;

        // Ensures our entity is a stacked entity
        MobData data = entityManager.getMobData(entity.getUniqueId());
        if(data == null) { return false; }


        // Ensures we don't kill our original entity if not killing entire stack
        if(data.getEntityCount() > 0)
        {
            if(!plugin.getConfig().getBoolean("killEntireStack"))
            {
                mob.setHealth(mob.getMaxHealth());
            }
        }

        return true;
    }



    private void spawnAndKillEntity(Entity ent, EntityDamageEvent e, Location loc)
    {
        LivingEntity newEntity = (LivingEntity)ent.getWorld().spawnEntity(loc, ent.getType());
        if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
        {
            newEntity.damage(50, ((EntityDamageByEntityEvent)e).getDamager());
        }
        else
        {
            newEntity.damage(50);
        }
    }
}
