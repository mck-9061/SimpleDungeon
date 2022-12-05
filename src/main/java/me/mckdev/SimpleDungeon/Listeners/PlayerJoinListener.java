package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DatabaseManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) throws SQLException, InvalidConfigurationException {
        DatabaseManager.instance.addPlayer(event.getPlayer());

        if (DatabaseManager.instance.hasInventory(event.getPlayer())) {
            DatabaseManager.instance.loadPlayerInventory(event.getPlayer());
            DatabaseManager.instance.loadPlayerLocation(event.getPlayer());
        }
    }
}
