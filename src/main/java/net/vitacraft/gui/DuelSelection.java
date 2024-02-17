package net.vitacraft.gui;

import net.vitacraft.game.DuelType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuelSelection {
    public static Inventory getInventory(){
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Select a Duel");
        ItemStack background = createItem("§a", Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 9*3; i++) {
            inventory.setItem(i, background);
        }
        int[] slots = {11,12,13,14,15};
        DuelType[] duelTypes = DuelType.values();

        for (int i = 0; i < Math.min(slots.length, duelTypes.length); i++) {
            DuelType duelType = duelTypes[i];
            int slot = slots[i];
            ItemStack itemStack = createItem(duelType.getDisplayName(), duelType.getIcon(), duelType.getDescription());
            inventory.setItem(slot, itemStack);
        }
        GUIListener.inventoryOpened(inventory, null);
        return inventory;
    }

    public static Inventory getInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Playing against - " + player.getName());
        ItemStack background = createItem("§a", Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 9*3; i++) {
            inventory.setItem(i, background);
        }
        int[] slots = {11,12,13,14,15};
        DuelType[] duelTypes = DuelType.values();

        for (int i = 0; i < Math.min(slots.length, duelTypes.length); i++) {
            DuelType duelType = duelTypes[i];
            int slot = slots[i];
            ItemStack itemStack = createItem(duelType.getDisplayName(), duelType.getIcon());
            inventory.setItem(slot, itemStack);
        }
        GUIListener.inventoryOpened(inventory, player);
        return inventory;
    }

    private static ItemStack createItem(String name, Material mat, String lore){
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        String[] loreArray = lore.split("\n");
        List<String> loreList = new ArrayList<>(Arrays.asList(loreArray));
        meta.setLore(loreList);
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
