package me.michqql.bhd.nms;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.player.PlayerHandler;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import me.michqql.bhd.util.PlayerUtil;
import me.michqql.bhd.util.SoundUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class HitDetection_v1_8_R3 extends HitDetection {

    public HitDetection_v1_8_R3(Plugin plugin, PresetHandler presetHandler) {
        super(plugin, presetHandler);
    }

    @Override
    public void inject(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            final CraftPlayer craftPlayer = (CraftPlayer) player;

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
                super.write(context, packet, promise);
            }

            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                // Attacks are handled by the plugin
                if(isAttackPacket(packet))
                    return;

                super.channelRead(context, packet);
            }

            private boolean isAttackPacket(Object objPacket) {
                if(objPacket instanceof Packet) {
                    Packet<?> genericPacket = (Packet<?>) objPacket;

                    if(genericPacket instanceof PacketPlayInUseEntity) {
                        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) genericPacket;
                        Entity damaged = packet.a(craftPlayer.getHandle().world);
                        if(damaged == null)
                            return false;

                        if(packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK &&
                                isDamagedEntityInstanceOfPlayer(craftPlayer, damaged)) {

                            run(craftPlayer.getHandle(), (EntityPlayer) damaged);
                            return true;
                        }
                    }
                }
                return false;
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", CHANNEL_NAME, channelDuplexHandler);
    }

    @Override
    public void deInject(Player player) {

    }

    private void run(EntityPlayer attacker, EntityPlayer damaged) {
        if(!damaged.isAlive())
            return;

        PlayerData attackerData = PlayerHandler.getPlayerData(attacker.getUniqueID());
        PlayerData damagedData = PlayerHandler.getPlayerData(damaged.getUniqueID());
        Settings settings = (attackerData.getLocalPreset() != null ?
                attackerData.getLocalPreset().getSettings() :
                presetHandler.getGlobalPreset().getSettings());

        if(PlayerUtil.failsRangeCheck(attackerData.player, damagedData.player))
            return;

        final long now = System.currentTimeMillis();
        final long attackCooldownMS = attackerData.getTimeSinceLastAttack();

        // If the player has attacked more recently than the cooldown length
        if(settings.cancelOnCooldown && attackCooldownMS < (settings.cooldownLength * 50))
            return;

        // Check if the damaged player is damageable
        if(!isDamageable(damagedData))
            return;

        // The super method will call on the damage calculator
        // to calculate how much damage should be applied to the damaged player.
        // An event, PlayerAttackPlayerEvent, is then called and if not cancelled,
        // the damaged is applied
        // If the event is cancelled, the method returns true, signifying to return here.
        if(handleDamageAndIsCancelled(attacker.getBukkitEntity(), damaged.getBukkitEntity()))
            return;

        attackerData.lastAttackTime = now;
        damagedData.lastHitTime = now;

        // Hit effect]
        SoundUtil.playSound(damagedData.player);

        PacketPlayOutEntityStatus status = new PacketPlayOutEntityStatus(damaged, (byte) 2);
        for(Player online : Bukkit.getOnlinePlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) online;

            if(craftPlayer.getHandle().world.equals(damaged.world))
                craftPlayer.getHandle().playerConnection.sendPacket(status);
        }

        // Knockback
        Vec3D victimMotion = new Vec3D(damaged.motX, damaged.motY, damaged.motZ);

        double motX = -MathHelper.sin((float) (attacker.yaw * Math.PI / 180.0F)) * settings.kbX;
        double motY = 0.1D * settings.kbY;
        double motZ = MathHelper.cos((float) (attacker.yaw * Math.PI / 180.0F)) * settings.kbX;

        double comboX = attackCooldownMS < (settings.comboPeriod * 50) ? settings.comboX : 1.0D;
        double comboY = attackCooldownMS < (settings.comboPeriod * 50) ? settings.comboY : 1.0D;

        Bukkit.getScheduler().runTask(plugin, () -> {
            damaged.velocityChanged = true;
            damaged.motX = motX * comboX;
            damaged.motY = motY * comboY;
            damaged.motZ = motZ * comboX;

            PlayerVelocityEvent velocityEvent = new PlayerVelocityEvent(damaged.getBukkitEntity(), damaged.getBukkitEntity().getVelocity());
            Bukkit.getPluginManager().callEvent(velocityEvent);
            if(velocityEvent.isCancelled()) {
                damaged.motX = victimMotion.a;
                damaged.motY = victimMotion.b;
                damaged.motZ = victimMotion.c;
            } else {
                Vector motion = velocityEvent.getVelocity();
                damaged.motX = motion.getX();
                damaged.motY = motion.getY();
                damaged.motZ = motion.getZ();
            }
        });
    }

    private boolean isDamagedEntityInstanceOfPlayer(CraftPlayer craftPlayer, Entity damaged) {
        if(damaged instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) damaged;
            return ((CraftWorld) craftPlayer.getWorld()).getHandle().players.contains(entityPlayer);
        }
        return false;
    }
}
