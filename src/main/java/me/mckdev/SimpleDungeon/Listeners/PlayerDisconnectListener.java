package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerDisconnectListener implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) throws SQLException {
        // check if player is in dungeon
        // if so, end the session

        Player player = event.getPlayer();
        if (DungeonSession.isPlayerInDungeon(player)) {
            DungeonSession session = DungeonSession.getSession(player);
            if (session == null) return;
            session.end();
        }
    }
}
