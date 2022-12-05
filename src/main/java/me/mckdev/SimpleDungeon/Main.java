package me.mckdev.SimpleDungeon;

import me.mckdev.SimpleDungeon.Commands.*;
import me.mckdev.SimpleDungeon.Listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Mob;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Main extends JavaPlugin {
    public static Main instance;
    @Override
    public void onEnable() {
        instance = this;
        getCommand("start").setExecutor(new StartCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("setstart").setExecutor(new SetStartCommand());
        getCommand("setkit").setExecutor(new SetKitCommand());


        getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);



        createConfig();
        saveResource("configexample.yml", true);

        // add difficulty levels from the config
        for (int i = 1; i <= 10; i++) {
            ConfigurationSection config = getConfig().getConfigurationSection("difficulties." + i);
            if (config == null) {
                break;
            }
            DifficultyLevel.levels.add(new DifficultyLevel(config));
        }

        DungeonSession.setKit(deserializeInventory(config.getConfigurationSection("kit")));




        String dbUrl;
        String dbUser;
        String dbPass;

        DungeonSession.setSpawnLocation(config.getLocation("startPosition"));
        dbUrl = "jdbc:mysql://" + config.getString("databaseHost") + ":" + config.getString("databasePort");
        dbUser = config.getString("databaseUsername");
        dbPass = config.getString("databasePassword");

        System.out.println("Connecting to database...");
        // Connect to database
        try {
            DatabaseManager.instance = new DatabaseManager(dbUrl, dbUser, dbPass, config.getString("databaseName"), "SimpleDungeon");
        } catch (RuntimeException e) {
            System.out.println("Failed to connect to database.");
            getServer().getPluginManager().disablePlugin(this);
        } catch (Exception e) {
            System.out.println("Unknown database error!");
            e.printStackTrace();
        }

        // begin main loop
        new BukkitRunnable() {
            @Override
            public void run() {
                DungeonSession.mainLoop();
            }
        }.runTaskTimer(this, 0L, 20L * config.getInt("secondsBetweenSpawns"));

        // begin difficulty level loop
        new BukkitRunnable() {
            @Override
            public void run() {
                DungeonSession.difficultyLoop();
            }
        }.runTaskTimer(this, 0L, 20L);

        System.out.println("SimpleDungeon enabled!");
    }

    @Override
    public void onDisable() {
        // prevents comodification exception
        DungeonSession[] sessions = DungeonSession.sessions.toArray(new DungeonSession[0]).clone();
        for (DungeonSession session : sessions) {
            try {
                session.end();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        try {
            DatabaseManager.instance.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Config file
    public static File configFile;
    public static FileConfiguration config;

    public static void createConfig() {
        configFile = new File(instance.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            instance.saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveDungeonConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, ItemStack> serializeInventory(org.bukkit.inventory.Inventory inventory) {
        Map<Integer, ItemStack> items = new java.util.HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            items.put(i, inventory.getItem(i));
        }
        return items;
    }

    public static Map<Integer, ItemStack> serializeInventory(ItemStack[] inventory) {
        Map<Integer, ItemStack> items = new java.util.HashMap<>();
        for (int i = 0; i < inventory.length; i++) {
            items.put(i, inventory[i]);
        }
        return items;
    }



    public static Inventory deserializeInventory(ConfigurationSection section) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER);

        for (String key : section.getKeys(false)) {
            inventory.setItem(Integer.parseInt(key), section.getItemStack(key));
        }

        return inventory;
    }
}
