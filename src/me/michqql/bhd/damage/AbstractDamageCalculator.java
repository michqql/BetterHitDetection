package me.michqql.bhd.damage;

import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.plugin.Plugin;

public abstract class AbstractDamageCalculator {

    protected final Plugin plugin;
    protected final PresetHandler presetHandler;

    public AbstractDamageCalculator(Plugin plugin, DamageCalculatorHandler damageCalculatorHandler, PresetHandler presetHandler) {
        this.plugin = plugin;
        this.presetHandler = presetHandler;

        damageCalculatorHandler.registerCalculator(this);
    }

    /**
     * @return the id that represents this calculator
     */
    public abstract String getId();

    public String getAuthor() {
        return "n/a";
    }

    /**
     * Calculates and returns how much damage should be applied to the damaged player
     * @param attackerData the attacking player
     * @param damagedData the damaged player
     * @return a double value of how much damage should be applied
     */
    public abstract double calculateDamage(PlayerData attackerData, PlayerData damagedData);

    /**
     * Calculates and returns how much knockback should be applied to the damaged player
     * Should take into consideration:
     * - Current knockback settings
     * - Knockback enchantment
     *
     * Should return an array of length 2, a[0] = kbX, a[1] = kbY
     * @param attackerData the attacking player
     * @param damagedData the damaged player
     * @return a double array of length 2
     */
    public abstract double[] calculateKnockback(PlayerData attackerData, PlayerData damagedData);

    /**
     * Applies extra affects, such as
     * - Fire Aspect Enchantment
     *
     * This method is called from the main thread,
     * and therefore is thread-safe
     * @param attackerData the attacking player
     * @param damagedData the damaged player
     */
    public abstract void applyExtraAffects(PlayerData attackerData, PlayerData damagedData);
}
