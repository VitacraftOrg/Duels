package dev.siea.duels.utils;

import dev.siea.duels.Duels;
import dev.siea.duels.creator.Creation;
import dev.siea.duels.game.DuelMap;
import dev.siea.duels.game.DuelType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.util.*;

public class MapConfig {
    private static final String file = "maps.yml";
    public static void saveMap(Creation creation){
        DuelType type = creation.getType();
        Inventory items = creation.getItems();
        Location center = creation.getCenter();
        Location spawn1 = creation.getSpawn1();
        Location spawn2 = creation.getSpawn2();
        Location vertex1 = creation.getVertex();
        Location vertex2 = creation.getVertex2();

        int id = getAllKeys().size();

        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);

        config.getConfig().set(id + ".type", type.toString());
        config.getConfig().set(id + ".items", InventoryBase64DeSerializer.toBase64(items));
        config.getConfig().set(id + ".center", center);
        config.getConfig().set(id + ".spawn1", spawn1);
        config.getConfig().set(id + ".spawn1", spawn2);
        config.getConfig().set(id + ".vertex1", vertex1);
        config.getConfig().set(id + ".vertex2", vertex2);

        config.save();
    }

    public static void removeMap(String id){
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);
        config.getConfig().set(id, null);
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
            Inventory items;
            try {
                items = InventoryBase64DeSerializer.fromBase64( c.getString("items"));
            } catch (IOException e) {
                items = Bukkit.createInventory(null, 3*9);
            }
            Location center = c.getLocation("center");
            Location spawn1 = c.getLocation("center");
            Location spawn2 = c.getLocation("center");
            Location vertex1 = c.getLocation("center");
            Location vertex2 = c.getLocation("center");
            DuelMap map = new DuelMap(type,items,center,spawn1,spawn2,vertex1,vertex2);
            maps.add(map);
        }
        return maps;
    }

    private static Set<String> getAllKeys(){
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),file);
        return config.getConfig().getKeys(false);
    }
}
