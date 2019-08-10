package com.crowsofwar.avatar.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerViewRegistry {
    private static Map<UUID, Integer> player_view = new HashMap<UUID, Integer>();

    public static void setPlayerViewInRegistry(UUID uuid, int mode) {
        if (player_view.containsKey(uuid)) {
            player_view.replace(uuid, mode);
        } else {
            player_view.put(uuid, mode);
        }
    }

    public static int getPlayerViewMode(UUID uuid) {
        if (player_view.containsKey(uuid)) {
            return player_view.get(uuid);
        } else {
            return -1;
        }
    }
}