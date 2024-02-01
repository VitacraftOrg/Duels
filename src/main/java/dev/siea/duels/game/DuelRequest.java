package dev.siea.duels.game;

import dev.siea.base.api.messenger.NotificationReason;
import org.bukkit.entity.Player;
import dev.siea.base.api.messenger.Messenger;

public class DuelRequest {
    private final Player initiator;
    private final Player recipient;

    private final DuelType type;

    private final long startTime = System.currentTimeMillis();

    public DuelRequest(Player initiator, Player recipient, DuelType type){
        this.initiator = initiator;
        this.recipient = recipient;
        this.type = type;
        Messenger.sendMessage(recipient, initiator.getDisplayName() + "§e challenged you in " + type.getDisplayName(), NotificationReason.SOFT_WARNING);
    }

    public Player getInitiator() {
        return initiator;
    }

    public Player getRecipient() {
        return recipient;
    }

    public DuelType getType() {
        return type;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - startTime) < 15000;
    }

    public void deny(){
        Messenger.sendMessage(initiator, "§c" + recipient.getDisplayName() + "§c denied your Duel request", NotificationReason.SOFT_WARNING);
        Messenger.sendMessage(recipient, "§cYou denied a Duel request by " + initiator.getDisplayName(), NotificationReason.SOFT_WARNING);
        GameManager.removeDuelRequest(this);
    }

    public void accept(){
        Messenger.sendMessage(initiator, "§a" + recipient.getDisplayName() + "§a accepted your Duel request", NotificationReason.SOFT_WARNING);
        Messenger.sendMessage(recipient, "§aYou accepted a Duel request by " + initiator.getDisplayName(), NotificationReason.SOFT_WARNING);
        GameManager.removeDuelRequest(this);
        GameManager.startDuel(this);
    }

    public void expired(){
        Messenger.sendMessage(initiator, "§cYour Duel request to " + recipient.getName() + " expired", NotificationReason.SOFT_WARNING);
        GameManager.removeDuelRequest(this);
    }
}
