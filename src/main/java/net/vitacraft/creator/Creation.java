package net.vitacraft.creator;

import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.api.messenger.NotificationReason;
import net.vitacraft.game.DuelType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class Creation {
    private final Player player;
    private CreationState creationState = CreationState.TYPE;
    private DuelType type;
    private final HashMap<Integer, ItemStack> items = new HashMap<>();

    private Location center;
    private Location spawn1;
    private Location spawn2;
    private Location vertex;
    private Location vertex2;

    public Creation(Player player){
        this.player = player;
        messageInstructions();
    }

    public void chatInput(String input){
        input = input.toLowerCase();
        switch (input){
            case "done":
                handleDone();
                break;
            case "back":
                handleBack();
                break;
            case "cancel":
                Messenger.send(player, "§cYou canceled the creation.", NotificationReason.ADMINISTRATIVE);
                CreatorManager.cancelCreation(this, player);
                break;
            default:
                handleDefault(input);
                break;
        }
    }

    private void handleDefault(String string){
        if (creationState == CreationState.TYPE){
            switch (string){
                case "classic":
                    type = DuelType.CLASSIC;
                    break;
                case "sumo":
                    type = DuelType.SUMO;
                    break;
                case "bow":
                    type = DuelType.BOW;
                    break;
                case "boxing":
                    type = DuelType.BOXING;
                    break;
                case "uhc":
                    type = DuelType.UHC;
                    break;
                default:
                    Messenger.send(player, "§c\"" + string.toUpperCase() + "\" is not a valid Duel type. Valid types are: " + Arrays.toString(DuelType.values()).replace("[", "").replace("]",""), NotificationReason.HARD_WARNING);
                    return;
            }
            Messenger.send(player, "§eYou successfully set the Duel type to " + type.getDisplayName(), NotificationReason.ADMINISTRATIVE);
            next();
        }
    }

    private void handleDone(){
        switch (creationState){
            case TYPE:
                Messenger.send(player, "§c" + "\"DONE\"" + " is not a valid Duel type. Valid types are: " + Arrays.toString(DuelType.values()).replace("[", "").replace("]",""), NotificationReason.HARD_WARNING);
                return;
            case ITEMS:
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                ItemStack itemStack = player.getInventory().getItem(slot);
                if (itemStack != null && !itemStack.getType().isAir()) {
                    items.put(slot, itemStack);
                }
            }
                next();
                break;
            case CENTER:
                center = player.getLocation();
                next();
                break;
            case SPAWN_1:
                spawn1 = player.getLocation();
                next();
                break;
            case SPAWN_2:
                spawn2 = player.getLocation();
                next();
                break;
            case VERTEX_1:
                vertex = player.getLocation();
                next();
                break;
            case VERTEX_2:
                vertex2 = player.getLocation();
                finish();
                break;
        }
    }

    private void handleBack(){
        switch (creationState){
            case ITEMS:
                creationState = CreationState.TYPE;
                break;
            case CENTER:
                creationState = CreationState.ITEMS;
                break;
            case SPAWN_1:
                creationState = CreationState.CENTER;
                break;
            case SPAWN_2:
                creationState = CreationState.SPAWN_1;
                break;
            case VERTEX_1:
                creationState = CreationState.SPAWN_2;
                break;
            case VERTEX_2:
                creationState = CreationState.VERTEX_1;
                break;
            default:
                break;
        }
        String message = "§eMade a mistake? You went back to §6" + creationState.toString().replace("_" , " ");
        Messenger.send(player, message, NotificationReason.ADMINISTRATIVE);
        messageInstructions();
    }
    private void finish(){
        CreatorManager.finishCreation(this, player);
    }

    private void next(){
        switch (creationState){
            case TYPE:
                creationState = CreationState.ITEMS;
                break;
            case ITEMS:
                creationState = CreationState.CENTER;
                break;
            case CENTER:
                creationState = CreationState.SPAWN_1;
                break;
            case SPAWN_1:
                creationState = CreationState.SPAWN_2;
                break;
            case SPAWN_2:
                creationState = CreationState.VERTEX_1;
                break;
            case VERTEX_1:
                creationState = CreationState.VERTEX_2;
                break;
            case VERTEX_2:
                finish();
                break;
        }
        messageInstructions();
    }

    private void messageInstructions(){
        String message;
        switch (creationState){
            case TYPE:
                message = "§eWhat §6Type §eof Duel are you creating? Valid types are: " + Arrays.toString(DuelType.values()).replace("[", "").replace("]","");
                break;
            case ITEMS:
                message = "§aPerfect! §eNow choose the §6Items§e players shall receive in this duel. Edit your Inventory then type §6\"done\"§e to copy it.";
                break;
            case CENTER:
                message = "§aGood choice! §eLets move on to the Locations. First lets establish the §6Center §eof your Map. Stand in the middle of your Map and type §6\"done\"§e to lock it in.";
                break;
            case SPAWN_1:
                message = "§aExcellent! §eNow choose the §6first Player spawn§e. Stand on your preferred spot and type §6\"done\"§e to lock it in.";
                break;
            case SPAWN_2:
                message = "§aWell done! §eSame goes for the §6second Player spawn§e. Stand on your preferred spot and type §6\"done\"§e to lock it in.";
                break;
            case VERTEX_1:
                message = "§aAlmost there! §eNow for the §6Area §eof your Map. We need 2 positions in each corner. Stand in one of them and type §6\"done\"§e to lock it in.";
                break;
            case VERTEX_2:
                message = "§eand for the other corner do the same. Stand in it and type §6\"done\"§e to lock it in.";
                break;
            default:
                message = "§cA fetal error occurred";
        }
        Messenger.send(player, message, NotificationReason.ADMINISTRATIVE);
    }

    public DuelType getType() {
        return type;
    }

    public HashMap<Integer, ItemStack> getItems(){
        return items;
    }
    public Location getCenter(){
        return center;
    }
    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public Location getVertex() {
        return vertex;
    }

    public Location getVertex2() {
        return vertex2;
    }
}
