package me.michqql.bhd.api;

import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.nms.HitDetection;
import me.michqql.bhd.player.PlayerHandler;
import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.entity.Player;

public class BetterHitDetectionAPI {

    private final DamageCalculatorHandler damageCalculatorHandler;
    private final PresetHandler presetHandler;
    private final HitDetection hitDetectionHandler;
    private final PlayerHandler playerHandler;

    public BetterHitDetectionAPI(DamageCalculatorHandler damageCalculatorHandler,
                                 PresetHandler presetHandler,
                                 HitDetection hitDetectionHandler,
                                 PlayerHandler playerHandler) {
        this.damageCalculatorHandler = damageCalculatorHandler;
        this.presetHandler = presetHandler;
        this.hitDetectionHandler = hitDetectionHandler;
        this.playerHandler = playerHandler;
    }

    public Preset getGlobalPreset() {
        return presetHandler.getGlobalPreset();
    }

    public Preset getLocalPreset(Player player) {
        return playerHandler.getPlayerData(player.getUniqueId()).getLocalPreset();
    }

    public Preset getPresetById(String id) {
        return presetHandler.getPreset(id);
    }

    public void setGlobalPreset(Preset preset) {
        presetHandler.setGlobalPreset(preset);
    }

    public void setLocalPreset(Player player, Preset preset) {
        playerHandler.getPlayerData(player.getUniqueId()).setLocalPreset(preset);
    }
}
