package me.michqql.bhd.damage;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.damage.calculators.VanillaDamageCalculator;
import me.michqql.bhd.damage.calculators.CustomDamageCalculator;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;

public class DamageCalculatorHandler {

    private final HashMap<String, AbstractDamageCalculator> DAMAGE_CALCULATOR_MAP = new HashMap<>();
    private final AbstractDamageCalculator vanillaDamageCalculator;

    public DamageCalculatorHandler(BetterHitDetectionPlugin plugin, PresetHandler presetHandler) {
        vanillaDamageCalculator = new VanillaDamageCalculator(plugin, this, presetHandler);
        new CustomDamageCalculator(plugin, this, presetHandler);
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
