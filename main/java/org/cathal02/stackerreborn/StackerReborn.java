package org.cathal02.stackerreborn;

import de.dustplanet.util.SilkUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.cathal02.stackerreborn.data.EntityDataHandler;
import org.cathal02.stackerreborn.listeners.EntityDropHandler;
import org.cathal02.stackerreborn.listeners.MobHandler;
import org.cathal02.stackerreborn.listeners.SpawnerHandler;

public final class StackerReborn extends JavaPlugin {

    private EntityManager entityManager;
    private ItemDropHandler dropHandler;
    private EntityDropHandler entityDropHandler;
    private static HologramManager hologramManager;



    SilkUtil silkUtil;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        silkUtil = SilkUtil.hookIntoSilkSpanwers();

        EntityDataHandler entityDataHandler = new EntityDataHandler(this);

        entityDropHandler = new EntityDropHandler(this);
        dropHandler = new ItemDropHandler(silkUtil);
        entityManager = new EntityManager(entityDataHandler);
        hologramManager = new HologramManager(this);

        Bukkit.getPluginManager().registerEvents(new SpawnerHandler(this), this);
        Bukkit.getPluginManager().registerEvents(new MobHandler(this),this);
        Bukkit.getPluginManager().registerEvents(entityDropHandler,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        entityManager.saveEntityData();
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ItemDropHandler getDropHandler()
    {
        return dropHandler;
    }

    public SilkUtil getSilkUtil()
    {
        return silkUtil;
    }
    public static HologramManager getHologramManager(){return hologramManager;}


}
