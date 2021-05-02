package me.michqql.bhd.nms;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.events.PlayerAttackPlayerEvent;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.player.PlayerHandler;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public abstract class HitDetection {

    protected final static String CHANNEL_NAME = "better_hit_detection";

    protected final BetterHitDetectionPlugin plugin;

    /* HANDLERS */
    protected final DamageCalculatorHandler damageCalculatorHandler;
    protected final PresetHandler presetHandler;
    protected final PlayerHandler playerHandler;

    private AbstractDamageCalculator damageCalculator;

    public HitDetection(BetterHitDetectionPlugin plugin,
                        DamageCalculatorHandler damageCalculatorHandler,
                        PresetHandler presetHandler,
                        PlayerHandler playerHandler) {
        this.plugin = plugin;
        this.damageCalculatorHandler = damageCalculatorHandler;
        this.presetHandler = presetHandler;
        this.playerHandler = playerHandler;

        this.damageCalculator = damageCalculatorHandler.getDamageCalculator(presetHandler.getGlobalPreset().getSettings().damageHandler);
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
