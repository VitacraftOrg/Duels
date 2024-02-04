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
        Messenger.send(recipient, initiator.getDisplayName() + "§e challenged you in " + type.getDisplayName(), NotificationReason.IN_GAME);
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
        Messenger.send(initiator, "§c" + recipient.getDisplayName() + "§c denied your Duel request", NotificationReason.IN_GAME);
        Messenger.send(recipient, "§cYou denied a Duel request by " + initiator.getDisplayName(), NotificationReason.IN_GAME);
        GameManager.removeDuelRequest(this);
    }

    public void accept(){
        Messenger.send(initiator, "§a" + recipient.getDisplayName() + "§a accepted your Duel request", NotificationReason.IN_GAME);
        Messenger.send(recipient, "§aYou accepted a Duel request by " + initiator.getDisplayName(), NotificationReason.IN_GAME);
        GameManager.removeDuelRequest(this);
        GameManager.startDuel(this);
    }

    public void expired(){
        Messenger.send(initiator, "§cYour Duel request to " + recipient.getName() + " expired", NotificationReason.IN_GAME);
        GameManager.removeDuelRequest(this);
    }
}
