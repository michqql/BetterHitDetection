package me.michqql.bhd;

import me.michqql.bhd.commands.BetterHitDetectionCommand;
import me.michqql.bhd.commands.DamageCalculatorCommand;
import me.michqql.bhd.commands.PresetCommand;
import me.michqql.bhd.commands.KnockBackCommand;
import me.michqql.bhd.data.ConfigFile;
import me.michqql.bhd.data.InfoFile;
import me.michqql.bhd.nms.HitDetection;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.player.PlayerHandler;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;

public class BetterHitDetectionPlugin extends JavaPlugin implements Listener {

    private static BetterHitDetectionPlugin instance;

    public static BetterHitDetectionPlugin getInstance() {
        return instance;
    }

    private PresetHandler presetHandler;
    private HitDetection hitDetectionHandler;
    private boolean useInventoryLib = false;
    private String craftBukkitVersion;

    @Override
    public void onEnable() {
        instance = this;

        ConfigFile config = new ConfigFile(this, null, "config", "yml");
        new InfoFile(this, null, "info");

        // Create handlers
        setupPresetManager(config.getConfig().getString("global-active-preset"));
        setupHitDetectionManager();
        setupInventoryLib();

        // Register this class as a listener
        getServer().getPluginManager().registerEvents(this, this);

        // Commands
        Objects.requireNonNull(getCommand("betterhitdetection")).setExecutor(new BetterHitDetectionCommand(presetHandler));
        Objects.requireNonNull(getCommand("kb")).setExecutor(new KnockBackCommand(presetHandler));
        Objects.requireNonNull(getCommand("preset")).setExecutor(new PresetCommand(presetHandler));
        Objects.requireNonNull(getCommand("damage")).setExecutor(new DamageCalculatorCommand(presetHandler, hitDetectionHandler));
    }

    @Override
    public void onDisable() {
        // Check if its null in case an error occurred
        // while enabling before this was initialised
        if(presetHandler != null)
            this.presetHandler.savePresets();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new PlayerData(e.getPlayer());
        hitDetectionHandler.inject(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        hitDetectionHandler.deInject(e.getPlayer());

        PlayerHandler.unregisterPlayer(e.getPlayer().getUniqueId());
    }

    private void setupHitDetectionManager() {
        this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        Bukkit.getLogger().log(Level.INFO, "[BHD] Found server version: " + craftBukkitVersion);

        String packageName = HitDetection.class.getPackage().getName();
        String className = HitDetection.class.getSimpleName();
        String versionPath = packageName + "." + className + "_" + craftBukkitVersion;

        try {
            Constructor<?> constructor = Class.forName(versionPath).getDeclaredConstructor(Plugin.class, PresetHandler.class);
            constructor.setAccessible(true);

            this.hitDetectionHandler = (HitDetection) constructor.newInstance(this, presetHandler);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupPresetManager(String globalPreset) {
        this.presetHandler = new PresetHandler();
        presetHandler.loadPresets();
        presetHandler.setGlobalPreset(presetHandler.getPreset(globalPreset));
    }

    private void setupInventoryLib() {
        if(Bukkit.getPluginManager().getPlugin("InventoryLib") != null)
            this.useInventoryLib = true;
        else
            Bukkit.getLogger().log(Level.WARNING, "Dependency not found: InventoryLib - GUI's will not be usable");
    }

    public boolean useInventoryLib() {
        return useInventoryLib;
    }

    public String getCraftBukkitVersion() {
        return craftBukkitVersion;
    }
}
