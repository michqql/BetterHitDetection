package me.michqql.bhd.player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerHandler {

    private final HashMap<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    void registerPlayer(PlayerData data) {
        PLAYER_DATA.put(data.player.getUniqueId(), data);
    }

    public void unregisterPlayer(UUID uuid) {
        PLAYER_DATA.remove(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return PLAYER_DATA.get(uuid);
    }
}
