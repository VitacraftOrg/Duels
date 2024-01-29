package dev.siea.duels.commands;

import dev.siea.duels.manager.Manager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("You must be a player to execute this Command");
            return true;
        }
        if (args.length < 1){
            return false;
        }
        if (args.length == 1){
            Manager.joinQue((Player) sender, null);
        }
        Manager.requestDuel((Player) sender, null, null);
        return true;
    }
}
