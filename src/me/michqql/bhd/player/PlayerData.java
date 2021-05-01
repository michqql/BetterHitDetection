package me.michqql.bhd.player;

import me.michqql.bhd.presets.Preset;
import org.bukkit.entity.Player;

public class PlayerData {

    public final Player player;
    public final long joinTime;
    public long lastHitTime, lastAttackTime;

    private Preset localPreset;

    public PlayerData(Player player) {
        this.player = player;
        this.joinTime = System.currentTimeMillis();

        this.lastAttackTime = System.currentTimeMillis();

        PlayerHandler.registerPlayer(this);
    }

    public long getTimeSinceLastAttack() {
        return System.currentTimeMillis() - lastAttackTime;
    }

    public long getTimeSinceLastHit() {
        return System.currentTimeMillis() - lastAttackTime;
    }

    public Preset getLocalPreset() {
        return localPreset;
    }

    public void setLocalPreset(Preset preset) {
        this.localPreset = preset;
    }
}
