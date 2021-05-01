package me.michqql.bhd.damage;


public class DamageUtil {

    // Functions copied from CombatMath
    public static double armor(double damage, double defensePoints, double toughness) {
        double var3 = 2.0F + toughness / 4.0F;
        double var4 = minMax(defensePoints - damage / var3, defensePoints * 0.2F, 20.0F);
        return damage * (1.0F - var4 / 25.0F);
    }

    public static double epf(double damage, double epf) {
        double var2 = minMax(epf, 0.0F, 20.0F);
        return damage * (1.0F - var2 / 25.0F);
    }

    // Function copied from MathHelper
    private static double minMax(double value, double min, double max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }
}