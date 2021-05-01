package me.michqql.bhd.damage.calculators;

import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.DamageUtil;
import me.michqql.bhd.util.BlockUtil;
import me.michqql.bhd.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanillaDamageCalculator extends AbstractDamageCalculator {

    @Override
    public String getId() {
        return "vanilla";
    }

    @Override
    public double calculateDamage(Player attacker, Player damaged) {
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

//            int fireAspectEnchantmentLevel = sword.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
//            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(attacker, damaged, 80 * fireAspectEnchantmentLevel);
//            Bukkit.getPluginManager().callEvent(combustEvent);
//            if (!combustEvent.isCancelled()) {
//                damaged.setFireTicks(combustEvent.getDuration());
//            }

            if (!damaged.isInvulnerable()) {
                // Temporary solution to blocking
                if (damaged.isBlocking()) baseAttackDamage = 0;

                double armorModifier = DamageUtil.armor(baseAttackDamage, PlayerUtil.attribute(damaged, Attribute.GENERIC_ARMOR),
                        PlayerUtil.attribute(damaged, Attribute.GENERIC_ARMOR_TOUGHNESS));
                baseAttackDamage -= baseAttackDamage - armorModifier;

                PotionEffect resistancePotion = damaged.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                int resistanceLevel = resistancePotion != null ? resistancePotion.getAmplifier() : 0;
                baseAttackDamage = baseAttackDamage - (baseAttackDamage * 0.2 * resistanceLevel);

                double enchantmentProtectionFactor = getEnchantmentProtectionFactor(damaged);
                baseAttackDamage -= baseAttackDamage - DamageUtil.epf(baseAttackDamage, enchantmentProtectionFactor);

                // todo: do damage to armor & shields

                return baseAttackDamage;
            }
        }
        return 0.0D;
    }

    private org.bukkit.inventory.ItemStack getSwordInAttackersHand(Player attacker) {
        EntityEquipment equipment = attacker.getEquipment();
        return equipment != null ? equipment.getItemInMainHand() : new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
    }

    private org.bukkit.inventory.ItemStack getHelmetOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        org.bukkit.inventory.ItemStack itemStack = equipment.getHelmet();
        if(itemStack == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private org.bukkit.inventory.ItemStack getChestplateOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        org.bukkit.inventory.ItemStack itemStack = equipment.getChestplate();
        if(itemStack == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private org.bukkit.inventory.ItemStack getLeggingsOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        org.bukkit.inventory.ItemStack itemStack = equipment.getLeggings();
        if(itemStack == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        return itemStack;
    }

    private org.bukkit.inventory.ItemStack getBootsOfDamagedPlayer(Player damaged) {
        EntityEquipment equipment = damaged.getEquipment();
        if(equipment == null)
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);

        org.bukkit.inventory.ItemStack itemStack = equipment.getBoots();
        if(itemStack == null)
            return new org.bukkit.inventory.ItemStack(Material.AIR);

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
