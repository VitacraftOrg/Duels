package net.vitacraft.utils;

import net.vitacraft.Duels;
import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.creator.Creation;
import net.vitacraft.game.DuelMap;
import net.vitacraft.game.DuelType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MapConfig {
    private static final String file = "maps.yml";
    public static void saveMap(Creation creation){
        DuelType type = creation.getType();
        HashMap<Integer, ItemStack> items = creation.getItems();
        Location center = creation.getCenter();
        Location spawn1 = creation.getSpawn1();
        Location spawn2 = creation.getSpawn2();
        Location vertex1 = creation.getVertex();
        Location vertex2 = creation.getVertex2();

        int id = getAllKeys().size();

        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);

        config.getConfig().set(id + ".type", type.toString());
        for (int slot : items.keySet()){
            config.getConfig().set(id + ".items." + slot, items.get(slot));
        }
        config.getConfig().set(id + ".center", center);
        config.getConfig().set(id + ".spawn1", spawn1);
        config.getConfig().set(id + ".spawn2", spawn2);
        config.getConfig().set(id + ".vertex1", vertex1);
        config.getConfig().set(id + ".vertex2", vertex2);

        config.save();
    }

    public static void removeMap(int id){
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);
        config.getConfig().set(String.valueOf(id), null);
        config.save();
    }

    public static List<DuelMap> getAllMaps(){
        Set<String> keys = getAllKeys();
        List<DuelMap> maps = new ArrayList<>();
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);
        for (String key : keys){
            ConfigurationSection c = config.getConfig().getConfigurationSection(key);
            assert c != null;
            DuelType type = DuelType.valueOf(c.getString("type"));
            HashMap<Integer, ItemStack> items = new HashMap<>();
            try{
                for (String itemSection : Objects.requireNonNull(c.getConfigurationSection("items")).getKeys(false)){
                    items.put(Integer.valueOf(itemSection),Objects.requireNonNull(c.getConfigurationSection("items.")).getItemStack(itemSection));
                }
            } catch (NullPointerException e){
                items.put(1,new ItemStack(Material.AIR,1));
            }
            Location center = c.getLocation("center");
            Location spawn1 = c.getLocation("spawn1");
            Location spawn2 = c.getLocation("spawn2");
            Location vertex1 = c.getLocation("vertex1");
            Location vertex2 = c.getLocation("vertex2");
            DuelMap map = new DuelMap(type,items,center,spawn1,spawn2,vertex1,vertex2);
            maps.add(map);
            System.out.println(map.getType());
        }
        return maps;
    }

    private static Set<String> getAllKeys(){
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);
        return config.getConfig().getKeys(false);
    }
}
