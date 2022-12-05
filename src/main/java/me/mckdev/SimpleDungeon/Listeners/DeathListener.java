package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DatabaseManager;
import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class DeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        if (DungeonSession.isPlayerInDungeon(event.getEntity())) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            DungeonSession session = DungeonSession.getSession(event.getEntity());
            DatabaseManager.instance.addDeath(session.getPlayer());
            session.end();
        }
    }
}
