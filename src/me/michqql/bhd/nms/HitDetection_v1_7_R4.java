package me.michqql.bhd.nms;

import io.netty.channel.*;
import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.player.PlayerData;
import me.michqql.bhd.player.PlayerHandler;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import me.michqql.bhd.util.PlayerUtil;
import me.michqql.bhd.util.SoundUtil;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class HitDetection_v1_7_R4 extends HitDetection {

    public HitDetection_v1_7_R4(BetterHitDetectionPlugin plugin,
                                DamageCalculatorHandler damageCalculatorHandler,
                                PresetHandler presetHandler,
                                PlayerHandler playerHandler) {
        super(plugin, damageCalculatorHandler, presetHandler, playerHandler);
    }

    @Override
    public void inject(Player player) {
        final CraftPlayer craftPlayer = (CraftPlayer) player;

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

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
                    Packet genericPacket = (Packet) objPacket;

                    if(genericPacket instanceof PacketPlayInUseEntity) {
                        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) genericPacket;
                        Entity damaged = packet.a(craftPlayer.getHandle().world);
                        if(damaged == null)
                            return false;

                        if(packet.c() == EnumEntityUseAction.ATTACK &&
                                isDamagedEntityInstanceOfPlayer(craftPlayer, damaged)) {

                            run(craftPlayer.getHandle(), (EntityPlayer) damaged);
                            return true;
                        }
                    }
                }
                return false;
            }
        };

        NetworkManager networkManager = craftPlayer.getHandle().playerConnection.networkManager;
        Channel channel = getChannel(networkManager);
        if(channel == null)
            return;

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addBefore("packet_handler", CHANNEL_NAME, channelDuplexHandler);
    }

    @Override
    public void deInject(Player player) {

    }

    private void run(EntityPlayer attacker, EntityPlayer damaged) {
        if(!damaged.isAlive())
            return;

        PlayerData attackerData = playerHandler.getPlayerData(attacker.getUniqueID());
        PlayerData damagedData = playerHandler.getPlayerData(damaged.getUniqueID());
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
        if(handleDamageAndIsCancelled(attackerData, damagedData))
            return;

        attackerData.lastAttackTime = now;
        damagedData.lastHitTime = now;

        // Hit effect]
        SoundUtil.playSound(plugin, damagedData.player.getLocation());

        PacketPlayOutEntityStatus status = new PacketPlayOutEntityStatus(damaged, (byte) 2);
        for(Player online : Bukkit.getOnlinePlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) online;

            if(craftPlayer.getHandle().world.equals(damaged.world))
                craftPlayer.getHandle().playerConnection.sendPacket(status);
        }

        // Knockback
        final double initialMotX = damaged.motX;
        final double initialMotY = damaged.motY;
        final double initialMotZ = damaged.motZ;

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
                damaged.motX = initialMotX;
                damaged.motY = initialMotY;
                damaged.motZ = initialMotZ;
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

    private Channel getChannel(NetworkManager networkManager) {
        Channel channel = null;
        try {
            Field field = NetworkManager.class.getDeclaredField("m");
            field.setAccessible(true);

            channel = (Channel) field.get(networkManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return channel;
    }
}
