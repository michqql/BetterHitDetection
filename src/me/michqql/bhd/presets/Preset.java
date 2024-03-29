package me.michqql.bhd.presets;

import me.michqql.bhd.data.PresetDataFile;
import org.bukkit.plugin.Plugin;

public class Preset {

    private final String id;
    private final PresetDataFile file;
    private Settings settings;

    public Preset(Plugin plugin, PresetHandler presetHandler, String id) {
        this.id = id;
        this.file = new PresetDataFile(plugin, id);
        load();

        presetHandler.registerPreset(this);
    }

    private void load() {
        this.settings = new Settings(id, file);
    }

    public void save() {
        this.settings.save(file);
    }

    public String getId() {
        return id;
    }

    public Settings getSettings() {
        return settings;
    }
}
