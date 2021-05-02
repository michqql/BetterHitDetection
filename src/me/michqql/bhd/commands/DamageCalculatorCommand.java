package me.michqql.bhd.commands;

import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.nms.HitDetection;
import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class DamageCalculatorCommand implements CommandExecutor {

    private final DamageCalculatorHandler damageCalculatorHandler;
    private final PresetHandler presetHandler;
    private final HitDetection hitDetection;

    public DamageCalculatorCommand(DamageCalculatorHandler damageCalculatorHandler,
                                   PresetHandler presetHandler,
                                   HitDetection hitDetection) {
        this.damageCalculatorHandler = damageCalculatorHandler;
        this.presetHandler = presetHandler;
        this.hitDetection = hitDetection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("bhd.admin")) {
            sender.sendMessage(ChatColor.DARK_RED + "No permission.");
            return true;
        }

        Preset global = presetHandler.getGlobalPreset();
        Settings settings = global.getSettings();

        if(args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "[BetterHitDetection] " + ChatColor.WHITE + "Damage Calculator Command:");
            sender.sendMessage(ChatColor.GRAY + "Currently: " + ChatColor.WHITE + settings.damageHandler);
            sender.sendMessage(ChatColor.AQUA + "/damage set <id> " + ChatColor.WHITE + "Set the damage calculator of current preset");
            sender.sendMessage(ChatColor.AQUA + "/damage list " + ChatColor.WHITE + "Lists all damage calculators");
            sender.sendMessage("");
            return true;
        }

        String subCommand = args[0];

        if(subCommand.equalsIgnoreCase("set")) {
            if(args.length < 2) {
                sender.sendMessage(ChatColor.DARK_RED + "/damage set <id>");
                sender.sendMessage(ChatColor.WHITE + "Sets the damage calculator of the current preset to that specified");
                return true;
            }

            String id = args[1];
            AbstractDamageCalculator adc = damageCalculatorHandler.getDamageCalculator(id);

            settings.damageHandler = adc.getId();
            hitDetection.setDamageCalculator(adc);
            global.save();

            sender.sendMessage(ChatColor.GREEN + "Set damage calculator to " + ChatColor.WHITE + adc.getId());
        }
        else if(subCommand.equalsIgnoreCase("list")) {
            Collection<AbstractDamageCalculator> calculators = damageCalculatorHandler.getDamageCalculators();
            StringBuilder builder = new StringBuilder();
            for(AbstractDamageCalculator adc : calculators) {
                builder.append(adc.getId()).append(", ");
            }
            sender.sendMessage(ChatColor.GREEN + "Listing all damage calculators:");
            sender.sendMessage(builder.substring(0, builder.length() - 2));
        }
        return true;
    }
}
