package dev.siea.duels.gui;

import dev.siea.duels.manager.DuelType;
import dev.siea.duels.manager.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class GUIListener implements Listener {
    private static HashMap<Inventory, Player> openInventories = new HashMap<>();

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e){
        DuelType duelType;
        int duelTypeSlot = e.getSlot()-11;
        try{
            duelType = DuelType.values()[duelTypeSlot];
        } catch (NullPointerException ignore){
            return;
        }
        Player target = openInventories.get(e.getInventory());
        if (target == null){
            Manager.joinQue((Player) e.getWhoClicked(), duelType);
        }
        else{
            Manager.requestDuel((Player) e.getWhoClicked(), target, duelType);
        }
        e.getWhoClicked().closeInventory();
    }




    public static void inventoryOpened(Inventory inventory, Player player){
        openInventories.put(inventory, player);
    }
}
