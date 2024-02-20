package net.vitacraft.gui;

import net.vitacraft.game.DuelType;
import net.vitacraft.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIListener implements Listener {
    private static final HashMap<Inventory, Player> duelSelection = new HashMap<>();
    private static final List<Inventory> mapMenus = new ArrayList<>();

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e){
        if (duelSelection.containsKey(e.getInventory())) handleDuelSelection(e);
        if (mapMenus.contains(e.getInventory())) handleMapsMenu(e);
    }

    private static void handleMapsMenu(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    private static void handleDuelSelection(InventoryClickEvent e){
        e.setCancelled(true);
        DuelType duelType;
        int duelTypeSlot = e.getSlot()-11;
        try{
            duelType = DuelType.values()[duelTypeSlot];
        } catch (IndexOutOfBoundsException ignore){
            return;
        }
        Player target = duelSelection.get(e.getInventory());
        if (target == null){
            GameManager.joinQue((Player) e.getWhoClicked(), duelType);
        }
        else{
            GameManager.requestDuel((Player) e.getWhoClicked(), target, duelType);
        }
        e.getWhoClicked().closeInventory();
    }


    public static void duelSelectionOpened(Inventory inventory, Player player){
        duelSelection.put(inventory, player);
    }

    public static void mapsMenuOpened(Inventory inventory){
        mapMenus.add(inventory);
    }
}
