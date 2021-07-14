package org.cathal02.stackerreborn;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.cathal02.stackerreborn.data.MobData;
import org.cathal02.stackerreborn.data.SpawnerData;

import java.util.Collection;

import static org.apache.commons.lang.StringUtils.capitalize;

public class HologramManager {

    StackerReborn plugin;
    public HologramManager(StackerReborn plugin)
    {
        this.plugin = plugin;
    }
    public void initHolgorams(Collection<SpawnerData> spawners)
    {
        spawners.forEach(spawnerData -> updateDisplay(spawnerData.location, spawnerData));
    }

    public Location getHologramLocation(Location blockLocation)
    {
        return new Location(blockLocation.getWorld(),blockLocation.getBlockX()+0.5,blockLocation.getBlockY()+2,blockLocation.getBlockZ()+0.5);
    }

    public void removeHologram(Location blockLocation) {
        Location accLoc = getHologramLocation(blockLocation);
        for(Hologram hologram : HologramsAPI.getHolograms(plugin))
        {
            if(hologram.getLocation().equals(accLoc))
            {
                hologram.delete();
                break;
            }
        }
    }
    public void updateDisplay(Location location, SpawnerData spawnerData) {
        Location newLoc = getHologramLocation(location);

        Hologram hologram = null;
        for(Hologram localGram : HologramsAPI.getHolograms(plugin))
        {
            if(localGram.getLocation().equals(newLoc))
            {
                hologram = localGram;
                break;
            }
        }
        if(hologram != null)hologram.removeLine(hologram.size()-1);
        if(hologram == null) hologram = HologramsAPI.createHologram(plugin, newLoc);
        String name = spawnerData.mobName;
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        hologram.appendTextLine(ChatColor.GREEN + name + ChatColor.WHITE +" x" + spawnerData.getEntityCount());

    }

    public void updateMobTitle(Entity entity, MobData data)
    {
        if(data == null)return;
        String name = entity.getType().toString().toLowerCase();
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        entity.setCustomName(ChatColor.GREEN +  name + ChatColor.WHITE + " x" + data.getEntityCount());
        entity.setCustomNameVisible(true);
    }

    public void setItemTitle(Item entity, int amount)
    {
        entity.setCustomNameVisible(true);
        entity.setCustomName(ChatColor.GREEN + capitalize(entity.getItemStack().getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " x" + amount);
    }
}
