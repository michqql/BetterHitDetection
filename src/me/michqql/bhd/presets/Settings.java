package me.michqql.bhd.presets;

import me.michqql.bhd.data.PresetDataFile;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    // The name of this settings preset
    public String presetId;

    // The length of time the player is on cooldown for
    public long cooldownLength;

    // Should hits while player is on cooldown be cancelled
    // otherwise, the damage is reduced
    public boolean cancelOnCooldown;

    // The length of time the damaged player cannot be hit again for
    public long immunityTicks;

    // Knockback multipliers
    public double kbX, kbY;

    // Combo
    public long comboPeriod;
    public double comboX, comboY, comboDamageMultiplier;

    // Pot multipliers
    public double potX, potY;

    // Damage handler
    public String damageHandler;

    public Settings(String id, PresetDataFile file) {
        this.presetId = id;
        load(file.getConfig());
    }

    private void load(FileConfiguration f) {
        this.cooldownLength = f.getLong("cooldownLength");
        this.cancelOnCooldown = f.getBoolean("cancelOnCooldown");
        this.immunityTicks = f.getLong("immunityTicks");

        this.kbX = f.getDouble("kbX");
        this.kbY = f.getDouble("kbY");

        this.comboPeriod = f.getLong("comboPeriod");
        this.comboX = f.getDouble("comboX");
        this.comboY = f.getDouble("comboY");
        this.comboDamageMultiplier = f.getDouble("comboDamageMultiplier");

        this.potX = f.getDouble("potX");
        this.potY = f.getDouble("potY");

        this.damageHandler = f.getString("damageCalculator");
    }

    public void save(PresetDataFile file) {
        FileConfiguration f = file.getConfig();
        f.set("id", presetId);
        f.set("cooldownLength", cooldownLength);
        f.set("cancelOnCooldown", cancelOnCooldown);
        f.set("immunityTicks", immunityTicks);

        f.set("kbX", kbX);
        f.set("kbY", kbY);

        f.set("comboPeriod", comboPeriod);
        f.set("comboX", comboX);
        f.set("comboY", comboY);
        f.set("comboDamageMultiplier", comboDamageMultiplier);

        f.set("potX", potX);
        f.set("potY", potY);

        f.set("damageCalculator", damageHandler);

        file.save();
    }
}
