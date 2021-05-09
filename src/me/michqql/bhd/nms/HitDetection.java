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

    protected AbstractDamageCalculator damageCalculator;

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

    protected boolean handleDamageAndIsCancelled(PlayerData attackerData, PlayerData damagedData) {
        PlayerAttackPlayerEvent playerAttackPlayerEvent =
                new PlayerAttackPlayerEvent(true, attackerData.player, damagedData.player,
                        (float) damageCalculator.calculateDamage(attackerData, damagedData));
        Bukkit.getPluginManager().callEvent(playerAttackPlayerEvent);

        boolean cancelled = playerAttackPlayerEvent.isCancelled();
        if(!cancelled) {
            damagedData.player.setHealth(damagedData.player.getHealth() - playerAttackPlayerEvent.getRawDamage());

            // Extra affects method has to be run from the main thread
            Bukkit.getScheduler().runTask(plugin, () -> damageCalculator.applyExtraAffects(attackerData, damagedData));
        }
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
