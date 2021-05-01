package me.michqql.bhd.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAttackPlayerEvent extends Event implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Player attacker, damaged;
    private float rawDamage;

    private boolean cancelled;

    public PlayerAttackPlayerEvent(boolean isAsync, Player attacker, Player damaged, float rawDamage) {
        super(isAsync);
        this.attacker = attacker;
        this.damaged = damaged;
        this.rawDamage = rawDamage;
    }

    public Player getAttacker() {
        return attacker;
    }

    public Player getDamaged() {
        return damaged;
    }

    public float getRawDamage() {
        return rawDamage;
    }

    public void setRawDamage(float rawDamage) {
        this.rawDamage = rawDamage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandler() {
        return HANDLER_LIST;
    }
}
