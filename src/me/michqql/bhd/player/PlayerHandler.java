package me.michqql.bhd.player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerHandler {

    private final static HashMap<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    static void registerPlayer(PlayerData data) {
        PLAYER_DATA.put(data.player.getUniqueId(), data);
    }

    public static void unregisterPlayer(UUID uuid) {
        PLAYER_DATA.remove(uuid);
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return PLAYER_DATA.get(uuid);
    }
}
