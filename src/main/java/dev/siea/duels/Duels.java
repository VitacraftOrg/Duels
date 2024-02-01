package dev.siea.duels;

import dev.siea.base.Base;
import dev.siea.duels.commands.DuelCommand;
import dev.siea.duels.creator.CreatorManager;
import dev.siea.duels.game.GameManager;
import dev.siea.duels.gui.GUIListener;
import dev.siea.duels.listeners.PlayerConnectionListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Duels extends JavaPlugin {
    private static final Base base = (Base) Bukkit.getServer().getPluginManager().getPlugin("Base");
    private static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;
        Objects.requireNonNull(getCommand("duel")).setExecutor(new DuelCommand());
        getServer().getPluginManager().registerEvents(new PlayerConnectionListeners(), this);
        getServer().getPluginManager().registerEvents(new GameManager(), this);
        getServer().getPluginManager().registerEvents(new CreatorManager(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
    }

    @Override
    public void onDisable() {
        GameManager.onDisable();
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
