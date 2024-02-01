package dev.siea.duels.creator;

import dev.siea.base.api.messenger.Messenger;
import dev.siea.base.api.messenger.NotificationReason;
import dev.siea.duels.game.DuelType;
import dev.siea.duels.utils.MapConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.HashMap;

public class CreatorManager implements Listener {
    private static final HashMap<Player, Creation> activeCreations = new HashMap<>();


    public static void newCreation(Player player) {
        if (activeCreations.containsKey(player)){
            Messenger.sendMessage(player, "§cYou are already creating a Duel Map" , NotificationReason.HARD_WARNING);
            return;
        }
        activeCreations.put(player, new Creation(player));
    }

    public static void finishCreation(Creation creation, Player player){
        activeCreations.remove(player);
        MapConfig.saveMap(creation);
        Messenger.sendMessage(player, "§eSuccessfully created new Map", NotificationReason.ADMINISTRATIVE);
    }

    public static void cancelCreation(Creation creation, Player player){
        activeCreations.remove(player);
    }

    @EventHandler
    public static void onAsyncChatEvent(AsyncPlayerChatEvent e){
        Creation creation = activeCreations.get(e.getPlayer());

        if (creation != null){
            e.setCancelled(true);
            creation.chatInput(e.getMessage());
        }
    }
}
