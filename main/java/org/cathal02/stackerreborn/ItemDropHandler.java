package org.cathal02.stackerreborn;

import de.dustplanet.util.SilkUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemDropHandler {

    SilkUtil silkUtil;

    public ItemDropHandler(SilkUtil su) {
        silkUtil = su;
    }

    public void dropSpawner(Location location, Block block)
    {
        if(!block.getType().equals(Material.MOB_SPAWNER))return;

        // Creates the Spawener Itemstack and drops it at the correct location.
        String ID = silkUtil.getSpawnerEntityID(block);
        String customName = silkUtil.getCustomSpawnerName(silkUtil.getCreatureName(ID));

        ItemStack spawnerItem = silkUtil.newSpawnerItem(ID, customName, 1, false);

        location.getWorld().dropItem(location, spawnerItem);
    }

    public void decreaseItemInHand(Player player) {
        if(player.getItemInHand() == null)return;
        if(player.getItemInHand().getType().equals(Material.AIR))return;

        int amount = player.getItemInHand().getAmount();
        if(amount == 1)
        {
            player.setItemInHand(null);
        }
        {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }
    }
}
