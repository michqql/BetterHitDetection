package me.michqql.bhd.nms;

import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.calculators.VanillaDamageCalculator;
import me.michqql.bhd.events.PlayerAttackPlayerEvent;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import me.michqql.bhd.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class HitDetection {

    protected final static String CHANNEL_NAME = "better_hit_detection";

    protected final Plugin plugin;
    protected final PresetHandler presetHandler;

    private AbstractDamageCalculator damageCalculator = new VanillaDamageCalculator();

    public HitDetection(Plugin plugin, PresetHandler presetHandler) {
        this.plugin = plugin;
        this.presetHandler = presetHandler;
    }

    protected boolean handleDamageAndIsCancelled(Player attacker, Player damaged) {
        PlayerAttackPlayerEvent playerAttackPlayerEvent =
                new PlayerAttackPlayerEvent(true, attacker, damaged, (float) damageCalculator.calculateDamage(attacker, damaged));
        Bukkit.getPluginManager().callEvent(playerAttackPlayerEvent);

        boolean cancelled = playerAttackPlayerEvent.isCancelled();
        if(!cancelled) damaged.setHealth(damaged.getHealth() - playerAttackPlayerEvent.getRawDamage());
        return cancelled;
    }

    protected boolean isDamageable(PlayerData data) {
        Settings settings = (data.getLocalPreset() != null ?
                data.getLocalPreset().getSettings() :
                presetHandler.getGlobalPreset().getSettings());

        GameMode gameMode = data.player.getGameMode();
        return (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE)
                && (data.getTimeSinceLastHit() >= settings.immunityTicks * 50);
    }

    public abstract void inject(Player player);

    public abstract void deInject(Player player);

    public void setDamageCalculator(AbstractDamageCalculator damageCalculator) {
        this.damageCalculator = damageCalculator;
    }
}
