package me.michqql.bhd;

import me.michqql.bhd.commands.BetterHitDetectionCommand;
import me.michqql.bhd.commands.DamageCalculatorCommand;
import me.michqql.bhd.commands.PresetCommand;
import me.michqql.bhd.commands.KnockBackCommand;
import me.michqql.bhd.damage.DamageCalculatorHandler;
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

    /* HANDLERS */
    private HitDetection hitDetectionHandler;
    private PresetHandler presetHandler;
    private DamageCalculatorHandler damageCalculatorHandler;
    private PlayerHandler playerHandler;

    /* HOOKS */
    private boolean useInventoryLib = false;
    private String craftBukkitVersion;

    @Override
    public void onEnable() {
        final ConfigFile config = new ConfigFile(this, null, "config", "yml");
        new InfoFile(this, null, "info");

        /* SETUP HANDLERS */
        this.damageCalculatorHandler = new DamageCalculatorHandler();
        setupPresetHandler(config.getConfig().getString("global-active-preset"));
        setupHitDetectionHandler();
        setupInventoryLib();
        this.playerHandler = new PlayerHandler();

        // Register this class as a listener
        getServer().getPluginManager().registerEvents(this, this);

        // Commands
        registerCommands();
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
        new PlayerData(playerHandler, e.getPlayer());
        hitDetectionHandler.inject(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        hitDetectionHandler.deInject(e.getPlayer());
        playerHandler.unregisterPlayer(e.getPlayer().getUniqueId());
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("betterhitdetection"))
                .setExecutor(new BetterHitDetectionCommand(this, presetHandler));

        Objects.requireNonNull(getCommand("kb"))
                .setExecutor(new KnockBackCommand(presetHandler));

        Objects.requireNonNull(getCommand("preset"))
                .setExecutor(new PresetCommand(this, presetHandler, playerHandler));

        Objects.requireNonNull(getCommand("damageCalculator"))
                .setExecutor(new DamageCalculatorCommand(damageCalculatorHandler, presetHandler, hitDetectionHandler));
    }

    private void setupHitDetectionHandler() {
        this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        Bukkit.getLogger().log(Level.INFO, "[BHD] Found server version: " + craftBukkitVersion);

        String packageName = HitDetection.class.getPackage().getName();
        String className = HitDetection.class.getSimpleName();
        String versionPath = packageName + "." + className + "_" + craftBukkitVersion;

        try {
            Constructor<?> constructor = Class.forName(versionPath).getDeclaredConstructor(
                    BetterHitDetectionPlugin.class,
                    DamageCalculatorHandler.class,
                    PresetHandler.class,
                    PlayerHandler.class
            );
            constructor.setAccessible(true);

            this.hitDetectionHandler = (HitDetection) constructor.newInstance(this, damageCalculatorHandler, presetHandler, playerHandler);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error has occurred while setting up hit detection handler");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupPresetHandler(String globalPreset) {
        this.presetHandler = new PresetHandler(this);
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
