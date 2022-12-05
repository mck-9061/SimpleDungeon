package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DatabaseManager;
import me.mckdev.SimpleDungeon.DungeonMob;
import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.SQLException;

public class MobKillListener implements Listener {
    @EventHandler
    public void onMobKill(EntityDeathEvent event) throws SQLException {
        if (DungeonSession.getSession(event.getEntity().getKiller()) == null) return;

        for (DungeonMob mob : DungeonSession.getSession(event.getEntity().getKiller()).getSpawnedMobs().getMobs()) {
            if (mob.getSpawned() == event.getEntity()) {
                DungeonSession.getSession(event.getEntity().getKiller()).getSpawnedMobs().getMobs().remove(mob);

                DatabaseManager.instance.addKill(event.getEntity().getKiller());

                break;
            }
        }
    }
}
