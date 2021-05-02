package me.michqql.bhd.damage;

import me.michqql.bhd.damage.calculators.VanillaDamageCalculator;
import me.michqql.bhd.damage.calculators.CustomDamageCalculator;

import java.util.Collection;
import java.util.HashMap;

public class DamageCalculatorHandler {

    private final HashMap<String, AbstractDamageCalculator> DAMAGE_CALCULATOR_MAP = new HashMap<>();
    private final AbstractDamageCalculator vanillaDamageCalculator;

    public DamageCalculatorHandler() {
        vanillaDamageCalculator = new VanillaDamageCalculator(this);
        new CustomDamageCalculator(this);
    }

    void registerCalculator(AbstractDamageCalculator abstractDamageCalculator) {
        DAMAGE_CALCULATOR_MAP.put(abstractDamageCalculator.getId(), abstractDamageCalculator);
    }

    public AbstractDamageCalculator getVanillaDamageCalculator() {
        return vanillaDamageCalculator;
    }

    public AbstractDamageCalculator getDamageCalculator(String id) {
        return DAMAGE_CALCULATOR_MAP.getOrDefault(id, vanillaDamageCalculator);
    }

    public Collection<AbstractDamageCalculator> getDamageCalculators() {
        return DAMAGE_CALCULATOR_MAP.values();
    }
}
