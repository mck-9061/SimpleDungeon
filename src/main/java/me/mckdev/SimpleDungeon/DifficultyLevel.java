package me.mckdev.SimpleDungeon;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class DifficultyLevel {
    private DungeonMobList mobs;
    private int mobCount;
    private boolean max = false;

    public static List<DifficultyLevel> levels = new ArrayList<>();

    public DifficultyLevel(ConfigurationSection config) {
        mobs = new DungeonMobList();

        for (int i = 1; i <= 10; i++) {
            ConfigurationSection mobConfig = config.getConfigurationSection("mobs." + i);
            if (mobConfig != null) {
                mobs.addMob(new DungeonMob(mobConfig));
            } else break;
        }

        mobCount = config.getInt("mobsSpawned");
    }


    public DungeonMobList getMobs() {
        return mobs;
    }

    public int getMobCount() {
        return mobCount;
    }

    public DifficultyLevel getNextLevel() {
        int index = levels.indexOf(this);
        if (index == levels.size() - 1) {
            return null;
        }
        return levels.get(index + 1);
    }

    public DifficultyLevel getPreviousLevel() {
        int index = levels.indexOf(this);
        if (index == 0) {
            return null;
        }
        return levels.get(index - 1);
    }

    public void setMax(boolean max) {
        this.max = max;
    }

    public boolean isMax() {
        return max;
    }
}
