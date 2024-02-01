package dev.siea.duels.commands;

import dev.siea.base.api.messenger.Messenger;
import dev.siea.base.api.messenger.NotificationReason;
import dev.siea.duels.Duels;
import dev.siea.duels.creator.CreatorManager;
import dev.siea.duels.gui.DuelSelection;
import dev.siea.duels.game.DuelType;
import dev.siea.duels.utils.ConfigUtil;
import dev.siea.duels.utils.getPlayer;
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
            Player player = (Player) sender;
            player.openInventory(DuelSelection.getInventory());
            return true;
        }
        String subcommand = args[0];
        if (subcommand.equalsIgnoreCase("list")){
            for (DuelType duelType : DuelType.values()){
                Messenger.sendMessage(((Player) sender),duelType.getDisplayName() + " §7- " + duelType.getDescription(), NotificationReason.SOFT_WARNING);
            }
            return true;
        }
        if (subcommand.equalsIgnoreCase("spawn")){
            ConfigUtil config = new ConfigUtil(Duels.getPlugin(),"locations.yml");
            config.getConfig().set("spawn", ((Player) sender).getLocation());
            config.save();
            Messenger.sendMessage(((Player) sender),"§eYou successfully set the Duels spawn", NotificationReason.ADMINISTRATIVE);
            return true;
        }
        if (subcommand.equalsIgnoreCase("create")){
            CreatorManager.newCreation((Player) sender);
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 2){
            Player target = getPlayer.onlineFromUsername(args[0]);
            if (target == null){
                Messenger.sendMessage(((Player) sender),"This Player does not exist", NotificationReason.HARD_WARNING);
                return true;
            }
            player.openInventory(DuelSelection.getInventory(target));
        }
        else{
            player.openInventory(DuelSelection.getInventory());
        }
        return true;
    }
}
