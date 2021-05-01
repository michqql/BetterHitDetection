package me.michqql.bhd.damage;

import me.michqql.bhd.damage.calculators.VanillaDamageCalculator;
import me.michqql.bhd.damage.calculators.CustomDamageCalculator;

import java.util.Collection;
import java.util.HashMap;

public class DamageCalculatorHandler {

    private final static HashMap<String, AbstractDamageCalculator> DAMAGE_CALCULATOR_MAP = new HashMap<>();
    private final static AbstractDamageCalculator DEFAULT;

    static void registerCalculator(AbstractDamageCalculator abstractDamageCalculator) {
        DAMAGE_CALCULATOR_MAP.put(abstractDamageCalculator.getId(), abstractDamageCalculator);
    }

    public static AbstractDamageCalculator getDamageCalculator(String id) {
        return DAMAGE_CALCULATOR_MAP.getOrDefault(id, DEFAULT);
    }

    public static Collection<AbstractDamageCalculator> getDamageCalculators() {
        return DAMAGE_CALCULATOR_MAP.values();
    }

    static {
        // Register damage calculators here
        DEFAULT = new VanillaDamageCalculator();
        new CustomDamageCalculator();
    }
}
