package org.cathal02.stackerreborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.cathal02.stackerreborn.HologramManager;
import org.cathal02.stackerreborn.StackerReborn;

public class EntityDropHandler implements Listener {

    StackerReborn plugin;
    boolean enabled;

    public EntityDropHandler(StackerReborn stackerReborn) {
    plugin = stackerReborn;
    enabled = plugin.getConfig().getBoolean("displayItemStackAmount");
    }


    @EventHandler
    public void onItemMerge(ItemMergeEvent e)
    {
        if(!enabled)return;
        StackerReborn.getHologramManager().setItemTitle(e.getTarget(),e.getTarget().getItemStack().getAmount()+e.getEntity().getItemStack().getAmount());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        if(!enabled)return;
        StackerReborn.getHologramManager().setItemTitle(e.getItemDrop(), e.getItemDrop().getItemStack().getAmount());
    }



}
