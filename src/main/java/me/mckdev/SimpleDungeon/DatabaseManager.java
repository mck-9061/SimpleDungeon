package me.mckdev.SimpleDungeon;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DatabaseManager {
    private Connection connection;
    private String dbName;
    private String tableName;

    public static DatabaseManager instance;

    public DatabaseManager(String uri, String user, String password, String dbName, String tableName) throws SQLException {
        try {
            connection = DriverManager.getConnection(uri, user, password);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: Please make sure your database is running and your credentials are correct.");
            throw new RuntimeException(e);
        }

        this.dbName = dbName;
        this.tableName = tableName;
        init();
    }

    public void init() throws SQLException {
        Statement dbStatement = connection.createStatement();
        dbStatement.execute("CREATE DATABASE IF NOT EXISTS " + dbName);

        Statement useStatement = connection.createStatement();
        useStatement.execute("USE " + dbName);


        Statement tableStatement = connection.createStatement();
        // Average kills per session can be calculated by dividing total kills by total sessions - it doesn't need to be stored in the database
        // This also assumes that deaths and sessions will not be equal - sessions could be greater than deaths as a player could disconnect or leave the dungeon
        tableStatement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (uuid VARCHAR(36) PRIMARY KEY, kills INT, deaths INT, sessions INT, inventory LONGTEXT, location LONGTEXT)");
    }

    public void close() throws SQLException {
        connection.close();
    }

    public void addPlayer(Player player) throws SQLException {
        // Check if player exists, and if not, add them to the database
        Statement checkStatement = connection.createStatement();
        checkStatement.execute("SELECT * FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        if (!checkStatement.getResultSet().next()) {
            Statement addStatement = connection.createStatement();
            addStatement.execute("INSERT INTO " + tableName + " (uuid, kills, deaths, sessions, inventory, location) VALUES ('" + player.getUniqueId().toString() + "', 0, 0, 0, '', '')");
        }
    }

    public void addKill(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET kills = kills + 1 WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public void addDeath(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET deaths = deaths + 1 WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public void addSession(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET sessions = sessions + 1 WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public int getKills(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT kills FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("kills");
    }

    public int getDeaths(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT deaths FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("deaths");
    }

    public int getSessions(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT sessions FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("sessions");
    }

    public float getAverageKillsPerSession(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT kills, sessions FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        return (float)statement.getResultSet().getInt("kills") / (float)statement.getResultSet().getInt("sessions");
    }


    public void savePlayerInventory(Player player, ItemStack[] inventory) throws SQLException {
        Statement statement = connection.createStatement();

        Map<Integer, ItemStack> serializedInventory = Main.serializeInventory(inventory);
        YamlConfiguration config = new YamlConfiguration();
        config.set("inventory", serializedInventory);

        statement.execute("UPDATE " + tableName + " SET inventory = '" + config.saveToString() + "' WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public void savePlayerLocation(Player player, Location location) throws SQLException {
        Statement statement = connection.createStatement();

        YamlConfiguration config = new YamlConfiguration();
        config.set("location", location);

        statement.execute("UPDATE " + tableName + " SET location = '" + config.saveToString() + "' WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public void loadPlayerInventory(Player player) throws SQLException, InvalidConfigurationException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT inventory FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        String inventoryString = statement.getResultSet().getString("inventory");
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(inventoryString);
        ConfigurationSection section = config.getConfigurationSection("inventory");
        player.getInventory().setContents(Main.deserializeInventory(section).getContents());

        Statement resetStatement = connection.createStatement();
        resetStatement.execute("UPDATE " + tableName + " SET inventory = '' WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public void loadPlayerLocation(Player player) throws SQLException, InvalidConfigurationException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT location FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        String locationString = statement.getResultSet().getString("location");
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(locationString);
        Location location = (Location) config.get("location");
        player.teleport(location);

        Statement resetStatement = connection.createStatement();
        resetStatement.execute("UPDATE " + tableName + " SET location = '' WHERE uuid = '" + player.getUniqueId().toString() + "'");
    }

    public boolean hasInventory(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT inventory FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
        statement.getResultSet().next();
        String inventoryString = statement.getResultSet().getString("inventory");
        return !inventoryString.equals("");
    }
}
