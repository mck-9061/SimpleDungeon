package me.mckdev.SimpleDungeon;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonSession {
    private Player player;
    private ItemStack[] oldInventory;
    private Location oldLocation;
    private DungeonMobList spawnedMobs = new DungeonMobList();
    private DifficultyLevel difficulty;
    private int secondsUntilDifficultyIncrease = -1;


    private static Location spawnLocation;
    public static List<DungeonSession> sessions = new ArrayList<>();
    private static ItemStack[] kit;

    public DungeonSession(Player player, Location oldLocation) {
        this.player = player;
        this.oldLocation = oldLocation;
        difficulty = DifficultyLevel.levels.get(0);
    }

    public void begin() throws SQLException {
        sessions.add(this);
        secondsUntilDifficultyIncrease = Main.config.getInt("difficultyIncreaseTime");
        oldInventory = player.getInventory().getContents().clone();
        player.getInventory().clear();
        player.getInventory().setContents(kit.clone());
        player.teleport(spawnLocation);

        DatabaseManager.instance.addSession(player);


        // Hide all other players, and hide all mobs not in the spawned entities list
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player) {
                player.hidePlayer(Main.instance, p);
            }
        }
        for (Entity e : player.getWorld().getEntities()) {
            // Make sure the entity is part of a different session
            if (e instanceof LivingEntity && e != player && DungeonSession.getSession((LivingEntity) e) == null) {
                player.hideEntity(Main.instance, e);
            }
        }

        player.sendMessage("You have entered the dungeon!");
    }

    public void end() throws SQLException {
        sessions.remove(this);

        if (player.isOnline() && player.getHealth() > 0) {
            player.teleport(oldLocation);
            player.getInventory().clear();
            player.getInventory().setContents(oldInventory.clone());
        } else {
            DatabaseManager.instance.savePlayerInventory(player, oldInventory);
            DatabaseManager.instance.savePlayerLocation(player, oldLocation);
        }

        for (DungeonMob mob : spawnedMobs.getMobs()) {
            mob.getSpawned().remove();
        }
    }

    public Player getPlayer() {
        return player;
    }


    // This loop runs at the interval specified in the config.yml, spawning mobs
    public static void mainLoop() {
        Random rand = new Random();
        for (DungeonSession session : sessions) {
            List<DifficultyLevel> levels = new ArrayList<>();
            levels.add(session.difficulty);

            for (int i = 0; i < 10; i++) {
                DifficultyLevel previous = levels.get(levels.size()-1).getPreviousLevel();
                if (previous == null) break;
                levels.add(previous);
            }

            for (DifficultyLevel level : levels) {
                int mobCount = level.getMobCount();
                DungeonMobList mobs = level.getMobs();

                for (int i = 0; i < mobCount; i++) {
                    DungeonMob mob = new DungeonMob(mobs.getMobs().get(rand.nextInt(mobs.getMobs().size())));
                    LivingEntity e = (LivingEntity) mob.spawn(session.player.getWorld(), DungeonSession.getSpawnLocation());

                    e.setInvisible(true);

                    // Teleport mob to a random position in the radius specified in the config.yml
                    int radius = Main.instance.getConfig().getInt("spawnRadius");
                    e.teleport(new Location(spawnLocation.getWorld(), spawnLocation.getX() + rand.nextInt(radius), spawnLocation.getY(), spawnLocation.getZ() + rand.nextInt(radius)));

                    // Teleport mob up 1 block if the block below is solid
                    while (e.getLocation().getBlock().getRelative(0, -1, 0).getType().isSolid()) {
                        e.teleport(new Location(e.getWorld(), e.getLocation().getX(), e.getLocation().getY() + 1, e.getLocation().getZ()));
                    }

                    e.setInvisible(false);

                    // hide the mob from other players
                    for (Player p : session.player.getWorld().getPlayers()) {
                        if (p != session.player) {
                            p.hideEntity(Main.instance, e);
                        }
                    }

                    // set the mob's target so it doesn't attack other players
                    Creature c = (Creature) e;
                    c.setTarget(session.player);

                    session.spawnedMobs.addMob(mob);
                }
            }
        }
    }

    // This next loop runs every second and checks if a session needs a difficulty increase
    public static void difficultyLoop() {
        for (DungeonSession session : sessions) {
            session.secondsUntilDifficultyIncrease--;
            if (session.secondsUntilDifficultyIncrease < 0) continue; // the session hasn't started yet
            if (session.secondsUntilDifficultyIncrease == 0) {
                session.secondsUntilDifficultyIncrease = Main.config.getInt("difficultyIncreaseTime");

                if (session.difficulty.isMax()) continue;

                DifficultyLevel next = session.difficulty.getNextLevel();
                // this is a stupid hacky way of doing it, but it works
                DifficultyLevel nextNext = null;
                if (next != null) {
                    nextNext = next.getNextLevel();
                }

                if (nextNext != null) {
                    session.difficulty = next;
                    session.player.sendMessage("Difficulty increased!");
                } else {
                    session.player.sendMessage("Difficulty increased to maximum!");
                    session.difficulty.setMax(true);
                    if (next != null) {
                        session.difficulty = next;
                    }
                }
            }
        }
    }

    public static Location getSpawnLocation() {
        return spawnLocation;
    }

    public static void setSpawnLocation(Location spawnLocation) {
        DungeonSession.spawnLocation = spawnLocation;
    }

    public static boolean isPlayerInDungeon(Player player) {
        for (DungeonSession session : sessions) {
            if (session.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public static void setKit(Inventory kit) {
        DungeonSession.kit = kit.getContents().clone();
    }

    public static DungeonSession getSession(Player player) {
        for (DungeonSession session : sessions) {
            if (session.getPlayer().equals(player)) {
                return session;
            }
        }
        return null;
    }

    public static DungeonSession getSession(LivingEntity entity) {
        for (DungeonSession session : sessions) {
            for (DungeonMob mob : session.spawnedMobs.getMobs()) {
                if (mob.getSpawned().equals(entity)) {
                    return session;
                }
            }
        }
        return null;
    }

    public DungeonMobList getSpawnedMobs() {
        return spawnedMobs;
    }
}
