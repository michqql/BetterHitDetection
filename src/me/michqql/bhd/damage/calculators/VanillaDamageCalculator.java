package me.michqql.bhd.damage.calculators;

import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.damage.DamageUtil;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import me.michqql.bhd.util.BlockUtil;
import me.michqql.bhd.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VanillaDamageCalculator extends AbstractDamageCalculator {

    public VanillaDamageCalculator(Plugin plugin, DamageCalculatorHandler damageCalculatorHandler, PresetHandler presetHandler) {
        super(plugin, damageCalculatorHandler, presetHandler);
    }

    @Override
    public String getId() {
        return "vanilla";
    }

    @Override
    public double calculateDamage(PlayerData attackerData, PlayerData damagedData) {
        final Player attacker = attackerData.player;
        final Player damaged = damagedData.player;
        final ItemStack sword = getSwordInAttackersHand(attacker);

        double baseAttackDamage = PlayerUtil.attribute(attacker, Attribute.GENERIC_ATTACK_DAMAGE);
        int sharpnessLevel = sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        double sharpnessDamage = sharpnessLevel * 0.5 + (sharpnessLevel > 0 ? 0.5 : 0);

        double attackCooldown = attacker.getAttackCooldown();
        baseAttackDamage *= 0.2F + attackCooldown * attackCooldown * 0.8F;
        sharpnessDamage *= attackCooldown;
        if (baseAttackDamage > 0.0F || sharpnessDamage > 0.0F) {
            boolean cooldownExpired = attackCooldown > 0.9F;

            boolean criticalHit = cooldownExpired && attacker.getFallDistance() > 0.0F && !attacker.isOnGround() &&
                    !attacker.isSprinting() && !isClimbing(attacker) && !isInLiquid(attacker) &&
                    !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) && !attacker.isInsideVehicle();
            if (criticalHit) baseAttackDamage *= 1.5F;

            baseAttackDamage += sharpnessDamage;

            if (!damaged.isInvulnerable()) {
                // Blocking
                if(damaged.isBlocking()) {
                    Vector direction = damaged.getLocation().getDirection();
                    Vector towards = attacker.getLocation().subtract(damaged.getLocation()).toVector().normalize();
                    if (direction.distance(towards) > 0.3)
                        baseAttackDamage = 0;
                }

                // Armor modifier
                double armorModifier = DamageUtil.armor(baseAttackDamage, PlayerUtil.attribute(damaged, Attribute.GENERIC_ARMOR),
                        PlayerUtil.attribute(damaged, Attribute.GENERIC_ARMOR_TOUGHNESS));
                baseAttackDamage -= baseAttackDamage - armorModifier;

                // Resistance potion modifier
                PotionEffect resistancePotion = damaged.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                int resistanceLevel = resistancePotion != null ? resistancePotion.getAmplifier() : 0;
                baseAttackDamage = baseAttackDamage - (baseAttackDamage * 0.2 * resistanceLevel);

                // Enchantment protection factor
                double enchantmentProtectionFactor = getEnchantmentProtectionFactor(damaged);
                baseAttackDamage -= baseAttackDamage - DamageUtil.epf(baseAttackDamage, enchantmentProtectionFactor);

                // Final damage value
                return baseAttackDamage;
            }
        }
        return 0.0D;
    }

    @Override
    public double[] calculateKnockback(PlayerData attacker, PlayerData damaged) {
        final Settings settings = presetHandler.getPreset(attacker).getSettings();

        // Base Knockback
        final double yaw = attacker.player.getLocation().getYaw() * Math.PI / 180D;
        final double nsin = -Math.sin(yaw);
        final double cos = Math.cos(yaw);
        double motX = nsin * settings.kbX;
        double motY = 0.1D * settings.kbY;
        double motZ = cos * settings.kbX;

        // Combo Multipliers
        final boolean withinPeriod = attacker.getTimeSinceLastAttack() < settings.comboPeriod * 50;
        double comboX = withinPeriod ? settings.comboX : 1.0D;
        double comboY = withinPeriod ? settings.comboY : 1.0D;

        // Knockback Enchantment
        int knockbackEnchantmentLevel = getSwordInAttackersHand(attacker.player).getEnchantmentLevel(Enchantment.KNOCKBACK);
        if(attacker.player.isSprinting() && attacker.player.getAttackCooldown() > 0.9F)
            knockbackEnchantmentLevel++;

        if(knockbackEnchantmentLevel > 0) {
            double multiplier = (knockbackEnchantmentLevel / 2D) * (1.0D - PlayerUtil.attribute(damaged.player, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
            if(multiplier > 0) {
                motX *= multiplier;
                motY *= multiplier;
                motZ *= multiplier;
            }
        }

        return new double[]{ motX * comboX, motY * comboY, motZ * comboX };
    }

    @Override
    public void applyExtraAffects(PlayerData attacker, PlayerData damaged) {
        final ItemStack sword = getSwordInAttackersHand(attacker.player);

        /* FIRE ASPECT */
        int fireAspectEnchantmentLevel = sword.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
        if(fireAspectEnchantmentLevel > 0) {
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(attacker.player, damaged.player,
                    80 * fireAspectEnchantmentLevel);

            Bukkit.getPluginManager().callEvent(combustEvent);
            if (!combustEvent.isCancelled())
                damaged.player.setFireTicks(combustEvent.getDuration());
        }

        /* SWORD DURABILITY */
        PlayerUtil.damageItem(sword);

        /* ARMOR and SHIELD DURABILITY */
        if(damaged.player.isBlocking()) {
            ItemStack item = getSwordInAttackersHand(damaged.player);
            if(item.getType() == Material.SHIELD)
                PlayerUtil.damageItem(item);
            else {
                item = getOffhand(damaged.player);
                if(item.getType() == Material.SHIELD)
                    PlayerUtil.damageItem(item);
            }
        } else {
            PlayerUtil.damageItem(damaged.player, EquipmentSlot.HEAD);
            PlayerUtil.damageItem(damaged.player, EquipmentSlot.CHEST);
            PlayerUtil.damageItem(damaged.player, EquipmentSlot.LEGS);
            PlayerUtil.damageItem(damaged.player, EquipmentSlot.FEET);
        }
    }

    private ItemStack getSwordInAttackersHand(Player attacker) {
        EntityEquipment equipment = attacker.getEquipment();
        return equipment != null ? equipment.getItemInMainHand() : new ItemStack(org.bukkit.Material.AIR);
    }

    private ItemStack getOffhand(Player player) {
        EntityEquipment equipment = player.getEquipment();
        return equipment != null ? equipment.getItemInOffHand() : new ItemStack(org.bukkit.Material.AIR);
    }

    private ItemStack getHelmetOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new ItemStack(org.bukkit.Material.AIR);

        ItemStack itemStack = equipment.getHelmet();
        if(itemStack == null)
            return new ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private ItemStack getChestplateOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new ItemStack(org.bukkit.Material.AIR);

        ItemStack itemStack = equipment.getChestplate();
        if(itemStack == null)
            return new ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private ItemStack getLeggingsOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new ItemStack(org.bukkit.Material.AIR);

        ItemStack itemStack = equipment.getLeggings();
        if(itemStack == null)
            return new ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private ItemStack getBootsOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new ItemStack(org.bukkit.Material.AIR);

        ItemStack itemStack = equipment.getBoots();
        if(itemStack == null)
            return new ItemStack(Material.AIR);

        return itemStack;
    }

    private boolean isClimbing(Player player) {
        final Block atFeet = player.getWorld().getBlockAt(player.getLocation());
        final Block atHead = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 1, 0));
        return BlockUtil.CLIMBABLE_MATERIALS_LIST.contains(atFeet.getType()) ||
                BlockUtil.CLIMBABLE_MATERIALS_LIST.contains(atHead.getType());
    }

    private boolean isInLiquid(Player player) {
        final Block atFeet = player.getWorld().getBlockAt(player.getLocation());
        final Block atHead = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 1, 0));
        return atFeet.getType() == Material.WATER || atFeet.getType() == Material.LAVA ||
                atHead.getType() == Material.WATER || atHead.getType() == Material.LAVA;
    }

    private int getEnchantmentProtectionFactor(Player player) {
        int helmetProtection = getHelmetOfDamagedPlayer(player).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int chestplateProtection = getChestplateOfDamagedPlayer(player).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int leggingsProtection = getLeggingsOfDamagedPlayer(player).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        int bootsProtection = getBootsOfDamagedPlayer(player).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);

        return helmetProtection + chestplateProtection + leggingsProtection + bootsProtection;
    }
}
