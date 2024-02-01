package dev.siea.duels.listeners;

import dev.siea.duels.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListeners implements Listener {
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e){
        GameManager.joinLobby(e.getPlayer());
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        GameManager.playerDied(player);
    }
}
