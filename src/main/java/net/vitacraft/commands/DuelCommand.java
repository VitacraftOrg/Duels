package net.vitacraft.commands;

import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.api.messenger.NotificationReason;
import net.vitacraft.Duels;
import net.vitacraft.creator.CreatorManager;
import net.vitacraft.gui.DuelSelection;
import net.vitacraft.game.DuelType;
import net.vitacraft.gui.MapsMenu;
import net.vitacraft.utils.ConfigUtil;
import net.vitacraft.utils.getPlayer;
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
                Messenger.send(((Player) sender),duelType.getDisplayName() + " §7- " + duelType.getDescription(), NotificationReason.IN_GAME);
            }
            return true;
        }
        if (subcommand.equalsIgnoreCase("spawn") && sender.hasPermission("duels.admin")){
            ConfigUtil config = new ConfigUtil(Duels.getPlugin(),"locations.yml");
            config.getConfig().set("spawn", ((Player) sender).getLocation());
            config.save();
            Messenger.send(((Player) sender),"§eYou successfully set the Duels spawn.", NotificationReason.ADMINISTRATIVE);
            return true;
        }
        if (subcommand.equalsIgnoreCase("create") && sender.hasPermission("duels.admin")){
            CreatorManager.newCreation((Player) sender);
            return true;
        }
        if (subcommand.equalsIgnoreCase("maps") && sender.hasPermission("duels.admin")){
            Player player = (Player) sender;
            player.openInventory(MapsMenu.getInventory());
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1){
            Player target = getPlayer.onlineFromUsername(args[0]);
            if (target == null){
                Messenger.send(((Player) sender),"§cThis Player does not exist!", NotificationReason.HARD_WARNING);
                return true;
            }
            if (target == player){
                Messenger.send(((Player) sender),"§cYou can not duel yourself!", NotificationReason.HARD_WARNING);
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
