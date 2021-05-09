package me.michqql.bhd.presets;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.player.PlayerData;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class PresetHandler {

    private final BetterHitDetectionPlugin plugin;

    private final HashMap<String, Preset> REGISTERED_PRESETS = new HashMap<>();
    private final Preset basePreset;

    private Preset globalPreset;

    public PresetHandler(BetterHitDetectionPlugin plugin) {
        this.plugin = plugin;

        // default presets
        this.basePreset = new Preset(plugin, this, "default");
        new Preset(plugin, this, "combo");
    }

    void registerPreset(Preset preset) {
        if(preset == null) {
            Bukkit.getLogger().log(Level.WARNING, "Preset could not be registered");
            return;
        }

        REGISTERED_PRESETS.putIfAbsent(preset.getId(), preset);
    }

    public Preset getGlobalPreset() {
        return globalPreset;
    }

    public void setGlobalPreset(Preset globalPreset) {
        if(globalPreset == null)
            return;

        this.globalPreset = globalPreset;
    }

    public Preset getPreset(String id) {
        return REGISTERED_PRESETS.getOrDefault(id, basePreset);
    }

    public Preset getPreset(PlayerData data) {
        return data.getLocalPreset() != null ? data.getLocalPreset() : globalPreset;
    }

    public boolean hasPreset(String id) {
        return REGISTERED_PRESETS.containsKey(id);
    }

    public ArrayList<Preset> getPresets() {
        return new ArrayList<>(REGISTERED_PRESETS.values());
    }

    public void loadPresets() {
        File pluginDataFolder = plugin.getDataFolder();
        File presetFolder = new File(pluginDataFolder.getPath(), "presets");
        File[] files = presetFolder.listFiles();
        if(files == null)
            return;

        for(File file : files) {
            if(file.isFile()) {
                String id = file.getName().replaceFirst("[.][^.]+$", "");
                Bukkit.getLogger().log(Level.INFO, "Loading preset: " + id);
                new Preset(plugin, this, id);
            }
        }
    }

    public void savePresets() {
        for(Preset preset : REGISTERED_PRESETS.values()) {
            Bukkit.getLogger().log(Level.INFO, "Saving preset: " + preset.getId());
            preset.save();
        }
    }
}
