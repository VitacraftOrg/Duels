package net.vitacraft.gui;

import net.vitacraft.game.DuelMap;
import net.vitacraft.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsMenu {
    public static Inventory getInventory(){
        Inventory inventory = Bukkit.createInventory(null, 9*6, "Duel Maps");
        ItemStack background = createItem("§a", Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, background);
        }

        ItemStack reload = createItem("§aReload ♲", Material.REDSTONE_BLOCK);

        inventory.setItem(4, reload);

        HashMap<DuelMap, Boolean> duelMaps = GameManager.getMaps();
        for (DuelMap map : duelMaps.keySet()){
            List<String> lore = new ArrayList<>();
            if (duelMaps.get(map)){
                lore.add("§eIn use: " + "§aTrue");
            }
            else{
                lore.add("§eIn use: " + "§cFalse");
            }
            StringBuilder items = new StringBuilder();
            int x = 0;
            items.append("§3");
            for (ItemStack itemStack : map.getItems().keySet()){
                if (x >= 3) break;
                items.append(itemStack.getType().toString().replace("_", " ").toUpperCase());
                items.append(", ");
                x++;
            }
            if(items.toString().toLowerCase().contains("air")) items = new StringBuilder().append("§cnone");
            lore.add("§eItems: " + items);
            lore.add("");
            lore.add("§6Coordinates: ");
            Location spawn1 = map.getSpawn1();
            lore.add("§5⬦ 1. Spawn: " + "§ex" + spawn1.getX() + " y" + spawn1.getY() + " z" + spawn1.getZ());
            Location spawn2 = map.getSpawn2();
            lore.add("§5⬦ 2. Spawn: " + "§ex" + spawn2.getX() + " y" + spawn2.getY() + " z" + spawn2.getZ());
            Location vertex1 = map.getCenter();
            lore.add("§5⬦ 1. Vertex: " + "§ex" + vertex1.getX() + " y" + vertex1.getY() + " z" + vertex1.getZ());
            Location vertex2 = map.getCenter();
            lore.add("§5⬦ 2. Vertex: " + "§ex" + vertex2.getX() + " y" + vertex2.getY() + " z" + vertex2.getZ());
            Location center = map.getCenter();
            lore.add("§5⬦ Center: " + "§ex" + center.getX() + " y" + center.getY() + " z" + center.getZ());
            ItemStack mapItem = createItem(map.getType().getDisplayName(), map.getType().getIcon(), lore);
            inventory.addItem(mapItem);
        }
        GUIListener.mapsMenuOpened(inventory);
        return inventory;
    }

    private static ItemStack createItem(String name, Material mat, List<String> lore){
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createItem(String name, Material mat){
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
