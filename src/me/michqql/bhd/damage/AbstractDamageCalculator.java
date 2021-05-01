package me.michqql.bhd.damage;

import org.bukkit.entity.Player;

public abstract class AbstractDamageCalculator {

    public AbstractDamageCalculator() {
        DamageCalculatorHandler.registerCalculator(this);
    }

    /**
     * @return the id that represents this calculator
     */
    public abstract String getId();

    /**
     * Calculates and returns how much damage should be applied to the damaged player
     * @param attacker the attacking player
     * @param damaged the damaged player
     * @return a double value of how much damage should be applied
     */
    public abstract double calculateDamage(Player attacker, Player damaged);
}
