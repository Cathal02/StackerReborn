package org.cathal02.stackerreborn.listeners;

import de.dustplanet.util.SilkUtil;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.cathal02.stackerreborn.data.SpawnerData;
import org.cathal02.stackerreborn.ItemDropHandler;
import org.cathal02.stackerreborn.EntityManager;
import org.bukkit.event.Listener;
import org.cathal02.stackerreborn.StackerReborn;


public class SpawnerHandler implements Listener{

    private EntityManager entityManager;
    private ItemDropHandler dropHandler;
    private SilkUtil silkUtil;

    private StackerReborn plugin;
    int maxStack;
    public SpawnerHandler(StackerReborn plugin) {
        entityManager = plugin.getEntityManager();
        dropHandler = plugin.getDropHandler();
        silkUtil = plugin.getSilkUtil();
        this.plugin = plugin;
        maxStack = plugin.getConfig().getInt("maxSpawnerStack");
        StackerReborn.getHologramManager().initHolgorams(entityManager.getAllSpawners());
    }

    @EventHandler(ignoreCancelled = true)
    public void handleSpawnerStack(BlockPlaceEvent e)
    {
        if(maxStack <0)return;
        Block block = e.getBlock();
        Block placedOnBlock = e.getBlockAgainst();
        BlockState blockReplaced = e.getBlockReplacedState();

        // Checks to ensure we are placing a spawner and that the block we are placing on is also a spawner
        if(!block.getType().equals(Material.MOB_SPAWNER)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(checkNearbyBlocks(e))return;

            if(!checkSpawnerMatch(block,placedOnBlock))return;

            if(!(blockReplaced instanceof CreatureSpawner))return;
            handleSpawnerPlace(e, placedOnBlock);

        },3);




    }

    private void handleSpawnerPlace(BlockPlaceEvent e, Block placedOnBlock) {
        // Adds spawner to database if not already here
        Location location = placedOnBlock.getLocation();
        entityManager.addSpawnerEntity(location, (CreatureSpawner)placedOnBlock.getState());

        // Increases entity count by 1
        SpawnerData spawnerData = entityManager.getSpawnerData(location);
        if(spawnerData.getEntityCount() >= maxStack)return;
        spawnerData.increaseEntityCount();
        if(spawnerData.getEntityCount() > 1)
        {
            location.getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
        }

         modifySpawnRate(placedOnBlock,4);
         StackerReborn.getHologramManager().updateDisplay(placedOnBlock.getLocation(), spawnerData);
    }

    private boolean checkNearbyBlocks(BlockPlaceEvent e) {
        if(plugin.getConfig().getInt("spawnerStackRadius") == -1) return false;
        Block blockToPlace = e.getBlock();

        int radius = plugin.getConfig().getInt("spawnerStackRadius");
        boolean matchFound = false;
        for(int y = radius; y>= -radius; y--)
        {
            if(matchFound)break;;
            for(int x = radius; x>=-radius; x--)
            {
                if(matchFound)break;
                for(int z = radius; z>=-radius;z--)
                {
                    Block block = blockToPlace.getRelative(x,y,z);
                    if(block ==null || block.getLocation().equals(blockToPlace.getLocation()))continue;
                    if(block.getType() != Material.AIR && checkSpawnerMatch(block,blockToPlace))
                    {
                        handleSpawnerPlace(e,block);
                        matchFound = true;
                        break;
                    }
                }
            }
        }
        return matchFound;
    }


    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent e)
    {
        Block block = e.getBlock();
        //Ensures we are breaking a spawner and that it is a stacked spawner
        if(!block.getType().equals(Material.MOB_SPAWNER))return;
        Location location = block.getLocation();
        if(!entityManager.spawnerExists(location))return;

        // Cancels the event, drops the spawner and decreases entity count
        SpawnerData spawnerData = entityManager.getSpawnerData(location);


        boolean hasSilk = silkUtil.isValidItemAndHasSilkTouch(e.getPlayer().getItemInHand());
        //Drop all spawners
        if(plugin.getConfig().getBoolean("dropAllSpawnersOnMine"))
        {
            if(hasSilk)
            {
                for(int i = 0; i<spawnerData.getEntityCount()-1; i++)
                {
                    dropHandler.dropSpawner(location,block);
                }
            }

            entityManager.removeSpawner(block.getLocation());
            return;
        }

        entityManager.handleSpawnerDecrease(spawnerData);

        // If it is not the last spawner in stack, disable break and drop spawner item
        if(spawnerData.getEntityCount()>0)
        {
            e.setCancelled(true);
            if(silkUtil.isValidItemAndHasSilkTouch(e.getPlayer().getItemInHand()))
            {
                dropHandler.dropSpawner(location, block);
            }
            StackerReborn.getHologramManager().updateDisplay(spawnerData.location,spawnerData);
        }

        if(spawnerData.getEntityCount() ==1)
        {
            StackerReborn.getHologramManager().removeHologram(block.getLocation());
        }

        modifySpawnRate(e.getBlock(),-4);
    }


    @EventHandler
    private void blockExplodeEvent(EntityExplodeEvent e)
    {
        for(Block block : e.blockList())
        {
            if(block.getType() == Material.MOB_SPAWNER)
            {
                handleSpawnerExplode(block);
            }
        }
    }

    private void modifySpawnRate(Block block, int amount)
    {
        if(block.getType() != Material.MOB_SPAWNER)return;
        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        NBTTileEntity tileEntity = new NBTTileEntity(spawner);
        tileEntity.setShort("SpawnCount", (short) (tileEntity.getShort("SpawnCount")+ (amount)));
        block.getState().update();

    }

    private boolean checkSpawnerMatch(Block block, Block placedOnBlock)
    {
        if(!(block.getType() == Material.MOB_SPAWNER) || !(placedOnBlock.getType() == Material.MOB_SPAWNER))return false;
        String mob1 = ((CreatureSpawner)block.getState()).getCreatureTypeName();
        String mob2 = ((CreatureSpawner)placedOnBlock.getState()).getCreatureTypeName();
        return mob1.equalsIgnoreCase(mob2);
    }

    private void handleSpawnerExplode(Block block) {
        SpawnerData data = entityManager.getSpawnerData(block.getLocation());
        if(data == null)return;
        if(plugin.getConfig().getBoolean("dropSpawnersOnExplode"))
        {
            for(int i = 0; i< data.getEntityCount(); i++)
            {
                dropHandler.dropSpawner(block.getLocation(), block);
            }
        }

        entityManager.removeSpawner(block.getLocation());

    }



}
